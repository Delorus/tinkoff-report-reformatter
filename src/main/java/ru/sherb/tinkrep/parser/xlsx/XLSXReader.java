package ru.sherb.tinkrep.parser.xlsx;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author maksim
 * @since 14.03.2020
 */
public class XLSXReader<T, L> {

    public static XLSXReaderBuilder<List<String>, String> from(Sheet sheet) {
        return new XLSXReaderBuilder<>(sheet, ArrayList::new, cells -> String.join("\t", cells));
    }

    private final Sheet sheet;
    private final Supplier<T> entityGen;
    private final Function<List<String>, L> lineTransformer;
    private final List<ParseRule<T, L>> rules;
    private final List<ParseRule<T, L>> globalRules;

    public XLSXReader(Sheet sheet, Supplier<T> entityGen, Function<List<String>, L> lineTransformer, List<ParseRule<T, L>> rules, List<ParseRule<T, L>> globalRules) {
        this.sheet = sheet;
        this.entityGen = entityGen;
        this.lineTransformer = lineTransformer;
        this.rules = rules;
        this.globalRules = globalRules;
    }


    public T read() {
        T entity = entityGen.get();

        for (Row row : sheet) {
            ArrayList<String> cells = findCellsInRow(row);

            L line = lineTransformer.apply(cells);

            boolean handleByGlobalRule = false;
            for (ParseRule<T, L> gRule : globalRules) {
                if (gRule.fastEquals(cells) && gRule.fullEquals(cells)) {
                    gRule.handle(line);
                    handleByGlobalRule = true;
                }
            }

            if (handleByGlobalRule) {
                continue;
            }

            for (ParseRule<T, L> rule : rules) {
                if (rule.fastEquals(cells) && rule.fullEquals(cells)) {
                    rule.handle(line);
                }
            }
        }

        for (ParseRule<T, L> rule : rules) {
            rule.fill(entity);
        }

        return entity;
    }

    private ArrayList<String> findCellsInRow(Row row) {
        ArrayList<String> result = new ArrayList<>(row.getPhysicalNumberOfCells());
        for (Cell cell : row) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    result.add(Double.toString(cell.getNumericCellValue()));
                    break;
                case STRING:
                    result.add(cell.getStringCellValue());
                    break;
                case BOOLEAN:
                    result.add(Boolean.toString(cell.getBooleanCellValue()));
                    break;
                case BLANK:
                    result.add("");
                    break;
                case FORMULA:
                case ERROR:
                case _NONE:
                    break;
            }
        }

        return trimRight(result);
    }

    private ArrayList<String> trimRight(ArrayList<String> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).equals("")) {
                list.remove(i);
            } else {
                return list;
            }
        }

        return list;
    }
}
