package ru.sherb.tinkrep.parser;

import org.junit.jupiter.api.Test;
import ru.sherb.tinkrep.model.Count;
import ru.sherb.tinkrep.model.Operation;
import ru.sherb.tinkrep.model.OperationType;
import ru.sherb.tinkrep.model.TradingTool;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author maksim
 * @since 15.03.2020
 */
class TinkoffBrokerReportTest {

    @Test
    public void testParsePeriod() throws IOException {
        // Setup
        var report = TinkoffBrokerReport.readFromFile(getTestReport());

        // Expect
        assertEquals(LocalDatePeriod.parse("01.01.2020 - 31.01.2020"), report.period());
    }

    @Test
    public void testParseCompleteDealsTable() throws IOException {
        // Setup
        var report = TinkoffBrokerReport.readFromFile(getTestReport());

        // When
        List<Operation> operations = report.operations();

        // Then
        assertEquals(List.of(
                op("02.01.2020", "Продажа", "32,25", "USD", "TWTR", 2, "64,5", "0,19"),
                op("06.01.2020", "Покупка", "19338", "RUB", "GMKN", 2, "38676", "116,03"),
                op("23.01.2020", "Покупка", "252,05", "RUB", "GAZP", 50, "12602,5", "6,3")
        ), operations);
    }

    @Test
    public void testParseStocksInfoTable() throws IOException {
        // Setup
        var report = TinkoffBrokerReport.readFromFile(getTestReport());

        // When
        List<TradingTool> tradingTools = report.toolInfos();

        // Then
        assertEquals(List.of(
                new TradingTool("Сбербанк", "SBER"),
                new TradingTool("ФосАгро ао", "PHOR"),
                new TradingTool("ММК", "MAGN")
        ), tradingTools);
    }

    private Operation op(String date, String type, String price, String currency, String toolId, int count, String summary, String commission) {
        return Operation.builder()
                .date(ParseUtils.parseLocalDate(date))
                .type(OperationType.parse(type))
                .price(ParseUtils.parseMoney(price, currency))
                .toolId(toolId)
                .count(new Count(count))
                .summary(ParseUtils.parseMoney(summary, currency))
                .commission(ParseUtils.parseMoney(commission, currency))
                .build();
    }

    private File getTestReport() {
        return new File("./src/test/resources/test_broker_report.xlsx");
    }
}