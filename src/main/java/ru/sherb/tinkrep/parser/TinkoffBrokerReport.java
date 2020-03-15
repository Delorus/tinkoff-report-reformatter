package ru.sherb.tinkrep.parser;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import ru.sherb.tinkrep.parser.xlsx.XLSXReader;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author maksim
 * @since 14.03.2020
 */
public class TinkoffBrokerReport {

    public static final String PERIOD_PREFIX = "Отчет о сделках и операциях за период";
    public static final String COMPLETE_DEALS_TABLE = "1.1 Информация о совершенных и исполненных сделках на конец отчетного периода";
    public static final String INCOMPLETE_DEALS_TABLE = "1.2 Информация о неисполненных сделках на конец отчетного периода";
    public static final String CASH_TRANSACTIONS_TABLE = "2. Операции с денежными средствами";

    public static TinkoffBrokerReport readFromFile(File file) throws IOException {
        Workbook workbook = XSSFWorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);
        return XLSXReader.from(sheet)
                .readTo(TinkoffBrokerReport::new)
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
                    .result(TinkoffBrokerReport::parseRawStoksInfoTable)
                .read();
    }

    private LocalDatePeriod period;

    public TinkoffBrokerReport() { }

    public LocalDatePeriod period() {
        if (period != null) {
            return period;
        }
        return LocalDatePeriod.ZERO;
    }

    private void parsePeriod(String rawPeriod) {
        this.period = LocalDatePeriod.parse(rawPeriod.replace(PERIOD_PREFIX, "").trim());
    }

    private void parseRawCompleteDealsTable(List<String> strings) {

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

    private void parseRawStoksInfoTable(List<String> strings) {
    }
}
