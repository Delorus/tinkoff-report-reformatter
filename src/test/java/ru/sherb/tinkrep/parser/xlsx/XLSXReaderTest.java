package ru.sherb.tinkrep.parser.xlsx;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author maksim
 * @since 14.03.2020
 */
public class XLSXReaderTest {

    @Test
    public void testReadNoLine() throws IOException {
        // Setup
        var builder = XLSXReader.from(getTableWithScrupSheet());

        // When
        List<String> rows = builder.read();

        // Then
        assertTrue(rows.isEmpty());
    }

    @Test
    public void testReadAllLineInCSV() throws IOException {
        // Setup
        var builder = XLSXReader.from(getTableWithScrupSheet());

        // When
        List<String> lines = builder
                .newRule().regex(".*", ".*").result(List::add)
                .read();

        // Then
        assertRowsEquals(lines,
                "First Table",
                "Date\tNumber\tString",
                "41307.0\t42.0\thello",
                "43864.0\t\tworld",
                "some scrup",
                "\t66.0");
    }

    @Test
    public void testReadOnlyOneLineByPrefix() throws IOException {
        // Setup
        XLSXReaderBuilder<List<String>, String> builder = XLSXReader.from(getTableWithScrupSheet());

        // When
        List<String> list = builder
                .newRule().startWith("some").result(List::add).read();

        // Then
        assertRowsEquals(list, "some scrup");
    }

    @Test
    public void testIgnoreRule() throws IOException {
        // Setup
        XLSXReaderBuilder<List<String>, String> builder = XLSXReader.from(getTableWithScrupSheet());

        // When
        List<String> list = builder.newGlobalRule().startWith("some").ignore()
                .newRule().regex(".*", ".*").result(List::add)
                .read();

        // Then
        assertRowsEquals(list,
                "First Table",
                "Date\tNumber\tString",
                "41307.0\t42.0\thello",
                "43864.0\t\tworld",
                "\t66.0");
    }

    @Test
    public void testFromRule() throws IOException {
        // Setup
        XLSXReaderBuilder<List<String>, String> builder = XLSXReader.from(getTableWithScrupSheet());

        // When
        List<String> list = builder.newRule().from("First Table").result(List::addAll).read();

        // Then
        assertRowsEquals(list,
                "Date\tNumber\tString",
                "41307.0\t42.0\thello",
                "43864.0\t\tworld",
                "some scrup",
                "\t66.0");
    }

    @Test
    public void testFromWithIgnoreRule() throws IOException {
        // Setup
        XLSXReaderBuilder<List<String>, String> builder = XLSXReader.from(getTableWithScrupSheet());

        // When
        List<String> list = builder
                .newGlobalRule().startWith("some").ignore()
                .newRule().from("First Table").result(List::addAll).read();

        // Then
        assertRowsEquals(list,
                "Date\tNumber\tString",
                "41307.0\t42.0\thello",
                "43864.0\t\tworld",
                "\t66.0");
    }

    @Test
    public void testFromToRule() throws IOException {
        // Setup
        XLSXReaderBuilder<List<String>, String> builder = XLSXReader.from(getTableWithScrupSheet());

        // When
        List<String> list = builder
                .newRule().from("First Table").to("some").result(List::addAll).read();

        // Then
        assertRowsEquals(list,
                "Date\tNumber\tString",
                "41307.0\t42.0\thello",
                "43864.0\t\tworld");
    }

    @Test
    public void testReadToDto() throws IOException {
        // Setup
        XLSXReaderBuilder<List<String>, String> builder = XLSXReader.from(getTableWithScrupSheet());

        // When
        TableDto table = builder.readTo(TableDto::new)
                .newGlobalRule().startWith("some").ignore()
                .newRule().from("First Table").result(TableDto::addRows)
                .read();

        // Then
        assertTableEquals(table,
                "Date",    "Number", "String",
                "41307.0", "42.0",   "hello",
                "43864.0", ""    ,   "world",
                "",        "66.0",   "");
    }

    private void assertTableEquals(TableDto actual, String... expected) {
        assertFalse(actual.rows.isEmpty());

        for (int i = 0; i < actual.rows.size(); i++) {
            TableDto.RowDto actualRow = actual.rows.get(i);
            int offset = i * 3;
            assertEquals(expected[offset], actualRow.date);
            assertEquals(expected[offset+1], actualRow.number);
            assertEquals(expected[offset+2], actualRow.string);
        }
    }

    private void assertRowsEquals(List<String> actual, String... expected) {
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }
    }

    private Sheet getTableWithScrupSheet() throws IOException {
        File file = new File("./src/test/resources/parser/test_table_with_scrup.xlsx");
        Workbook workbook = WorkbookFactory.create(file);
        return workbook.getSheetAt(0);
    }

    private static class TableDto {
        private List<RowDto> rows = new ArrayList<>();

        public void addRows(List<String> rows) {
            for (String row : rows) {
                String[] cells = row.split("\t");
                this.rows.add(new RowDto(
                        getOrDefault(cells, 0, ""),
                        getOrDefault(cells, 1, ""),
                        getOrDefault(cells, 2, "")));
            }
        }

        private String getOrDefault(String[] cells, int index, String def) {
            if (index >= cells.length) {
                return def;
            }

            return cells[index];
        }

        private static class RowDto {
            private final String date;
            private final String number;
            private final String string;


            private RowDto(String date, String number, String string) {
                this.date = date;
                this.number = number;
                this.string = string;
            }
        }
    }
}