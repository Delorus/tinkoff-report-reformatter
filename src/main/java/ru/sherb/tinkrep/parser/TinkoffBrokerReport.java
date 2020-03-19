package ru.sherb.tinkrep.parser;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ru.sherb.tinkrep.model.Count;
import ru.sherb.tinkrep.model.Operation;
import ru.sherb.tinkrep.model.OperationType;
import ru.sherb.tinkrep.model.TradingTool;
import ru.sherb.tinkrep.parser.xlsx.XLSXReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.sherb.tinkrep.parser.ParseUtils.parseLocalDate;
import static ru.sherb.tinkrep.parser.ParseUtils.parseMoney;

/**
 * @author maksim
 * @since 14.03.2020
 */
public class TinkoffBrokerReport {

    private static final String PERIOD_PREFIX = "Отчет о сделках и операциях за период";
    private static final String COMPLETE_DEALS_TABLE = "1.1 Информация о совершенных и исполненных сделках на конец отчетного периода";
    private static final String INCOMPLETE_DEALS_TABLE = "1.2 Информация о неисполненных сделках на конец отчетного периода";
    private static final String CASH_TRANSACTIONS_TABLE = "2. Операции с денежными средствами";

    private static final String DELIMITER = ";";

    public static TinkoffBrokerReport readFromFile(File file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);
        // @formatter:off
        return XLSXReader.from(sheet)
                .readTo(TinkoffBrokerReport::new)
                .transformLine(strings -> String.join(DELIMITER, strings))
                .newGlobalRule()
                    .regex("^\\d", "^\\d+ из \\d+$")
                    .ignore()
                .newRule()
                    .startWith(PERIOD_PREFIX)
                    .result(TinkoffBrokerReport::parsePeriod)
                .newRule()
                    .from(COMPLETE_DEALS_TABLE).to(INCOMPLETE_DEALS_TABLE)
                    .result(TinkoffBrokerReport::parseRawCompleteDealsTable)
                .newRule()
                    .from(CASH_TRANSACTIONS_TABLE).to("^R", "^RUB$")
                    .result(TinkoffBrokerReport::parseRawCashTransTable)
                .newRule()
                    .from("^R", "^RUB$").to("^U", "^USD$")
                    .result(TinkoffBrokerReport::parseRawRubOperationTable)
                .newRule()
                    .from("^U", "^USD$").to("^E", "^EUR$")
                    .result(TinkoffBrokerReport::parseRawUsdOperationTable)
                .newRule()
                    .from("^E", "^EUR$").to("3. Движение финансовых активов инвестора")
                    .result(TinkoffBrokerReport::parseRawEurOperationTable)
                .newRule()
                    .from("3. Движение финансовых активов инвестора")
                    .to("4.1 Информация о ценных бумагах")
                    .result(TinkoffBrokerReport::parseRawFinancialAssetsTable)
                .newRule()
                    .from("4.1 Информация о ценных бумагах")
                    .to("4.2 Информация об инструментах, не квалифицированных в качестве ценной бумаги")
                    .result(TinkoffBrokerReport::parseRawStocksInfoTable)
                .read();
        // @formatter:on
    }

    private LocalDatePeriod period;

    private List<Operation> operations = new ArrayList<>();
    private List<TradingTool> toolInfos = new ArrayList<>();

    public LocalDatePeriod period() {
        if (period != null) {
            return period;
        }
        return LocalDatePeriod.ZERO;
    }

    public List<Operation> operations() {
        return operations;
    }

    public List<TradingTool> toolInfos() {
        return toolInfos;
    }

    private void parsePeriod(String rawPeriod) {
        this.period = LocalDatePeriod.parse(rawPeriod.replace(PERIOD_PREFIX, "").trim());
    }

    private enum CompleteDealsTableField implements TableField {
        TYPE("Вид сделки"),
        TOOL_ID("Код актива"),
        PRICE("Цена за единицу"),
        CURRENCY("Валюта цены"),
        COUNT("Количество"),
        SUMMARY("Сумма сделки"),
        COMMISSION("Комиссия брокера"),
        SETTLEMENT_DATE("Дата расчетов");

        private static Map<String, CompleteDealsTableField> titleToField = new HashMap<>();
        static {
            for (CompleteDealsTableField value : CompleteDealsTableField.values()) {
                titleToField.put(value.title, value);
            }
        }

        private final String title;

        CompleteDealsTableField(String title) {
            this.title = title;
        }

        public static TableField getField(String title) {
            return titleToField.get(title);
        }
    }

    private static class TableIndex {

        public static TableIndex findFields(String row, TableFields fields) {
            String[] cells = row.split(DELIMITER);

            TableIndex tableIndex = new TableIndex();
            for (int i = 0; i < cells.length; i++) {
                TableField field = fields.getField(cells[i]);
                if (field != null) {
                    tableIndex.indexes.put(field, i);
                }
            }

            return tableIndex;
        }

        private final Map<TableField, Integer> indexes = new HashMap<>();

        public String get(String[] cells, TableField field) {
            Integer indx = indexes.get(field);
            if (indx == null) {
                throw new IllegalArgumentException("Unexpected field: " + field);
            }
            return cells[indx];
        }
    }

    private void parseRawCompleteDealsTable(List<String> rows) {
        if (rows.isEmpty()) {
            return;
        }

        String titleRow = rows.get(0);
        TableIndex fields = TableIndex.findFields(titleRow, CompleteDealsTableField::getField);

        for (String row : rows.subList(1, rows.size())) {
            String[] cells = row.split(DELIMITER);

            String currency = fields.get(cells, CompleteDealsTableField.CURRENCY);
            var builder = Operation.builder()
                    .type(OperationType.parse(fields.get(cells, CompleteDealsTableField.TYPE)))
                    .toolId(fields.get(cells, CompleteDealsTableField.TOOL_ID))
                    .price(parseMoney(fields.get(cells, CompleteDealsTableField.PRICE), currency))
                    .count(new Count(Integer.parseInt(fields.get(cells, CompleteDealsTableField.COUNT))))
                    .summary(parseMoney(fields.get(cells, CompleteDealsTableField.SUMMARY), currency))
                    .commission(parseMoney(fields.get(cells, CompleteDealsTableField.COMMISSION), currency))
                    .date(parseLocalDate(fields.get(cells, CompleteDealsTableField.SETTLEMENT_DATE)));

            operations.add(builder.build());
        }
    }

    private void parseRawCashTransTable(List<String> strings) {
    }

    private void parseRawRubOperationTable(List<String> strings) {
    }

    private void parseRawUsdOperationTable(List<String> strings) {
    }

    private void parseRawEurOperationTable(List<String> strings) {
    }

    private void parseRawFinancialAssetsTable(List<String> strings) {
    }

    private enum StocksInfoTableField implements TableField {
        NAME("Сокращенное наименование актива"), CODE("Код актива");

        private static Map<String, StocksInfoTableField> titleToField = new HashMap<>();
        static {
            for (StocksInfoTableField value : StocksInfoTableField.values()) {
                titleToField.put(value.title, value);
            }
        }

        private final String title;

        StocksInfoTableField(String title) {
            this.title = title;
        }

        public static TableField getField(String title) {
            return titleToField.get(title);
        }
    }

    private void parseRawStocksInfoTable(List<String> rows) {
        if (rows.isEmpty()) {
            return;
        }

        String titleRow = rows.get(0);
        TableIndex fields = TableIndex.findFields(titleRow, StocksInfoTableField::getField);

        for (String row : rows.subList(1, rows.size())) {
            String[] cells = row.split(DELIMITER);

            String name = fields.get(cells, StocksInfoTableField.NAME);
            String code = fields.get(cells, StocksInfoTableField.CODE);
            toolInfos.add(new TradingTool(name, code));
        }
    }
}
