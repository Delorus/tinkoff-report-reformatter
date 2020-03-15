package ru.sherb.tinkrep.parser;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

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

//    @Test
//    public void testParseCompleteDealsTable() throws IOException {
//        // Setup
//        var report = TinkoffBrokerReport.readFromFile(getTestReport());
//
//        // Expect
//        assertEquals();
//    }

    private File getTestReport() {
        return new File("./src/test/resources/test_broker_report.xlsx");
    }
}