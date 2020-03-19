package ru.sherb.tinkrep;

import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import picocli.CommandLine;
import ru.sherb.tinkrep.cli.MainCommand;
import ru.sherb.tinkrep.model.Operation;
import ru.sherb.tinkrep.parser.TinkoffBrokerReport;
import ru.sherb.tinkrep.report.DetailedDoubleEntryReport;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author maksim
 * @since 14.03.2020
 */
public class Launch {

    public static void main(String[] args) throws IOException {
        MainCommand cmd = tryParseArgs(args);

        List<TinkoffBrokerReport> reports = new ArrayList<>();
        if (Files.isDirectory(cmd.root)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(cmd.root)) {
                for (Path path : stream) {
                    if (!Files.isDirectory(path)) {
                        System.out.println("Read: " + path);
                        try {
                            reports.add(TinkoffBrokerReport.readFromFile(path.toFile()));
                        } catch (NotOLE2FileException exception) {
                            System.out.println("WARN: it is not excel file: " + path.getFileName());
                        }
                    }
                }
            }
        } else {
            reports.add(TinkoffBrokerReport.readFromFile(cmd.root.toFile()));
        }

        List<Operation> operations = reports.stream()
                .map(TinkoffBrokerReport::operations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        DetailedDoubleEntryReport detailedReport = DetailedDoubleEntryReport.build(operations);
        toCsv(detailedReport, "detailed.csv");
    }

    private static MainCommand tryParseArgs(String[] args) {
        var cli = new CommandLine(new MainCommand());
        try {
            cli.parseArgs(args);
        } catch (CommandLine.PicocliException e) {
            System.out.println(e.getMessage());
            cli.usage(System.out);
            System.exit(-1);
        }

        if (cli.isUsageHelpRequested()) {
            cli.usage(System.out);
            System.exit(0);
        } else if (cli.isVersionHelpRequested()) {
            cli.printVersionHelp(System.out);
            System.exit(0);
        }

        return cli.getCommand();
    }

    private static final String DELIMITER = ";";

    private static void toCsv(DetailedDoubleEntryReport report, String path) throws IOException {
        try (var writer = new PrintWriter(Files.newBufferedWriter(Paths.get(path)))) {
            writer.println("Код Актива;Продажа;Покупка;Комиссия;Результат;Валюта");
            for (var row : report.getRows()) {
                writer.println(row.getTradingToolCode()
                        + DELIMITER + row.getSell()
                        + DELIMITER + row.getBuy()
                        + DELIMITER + row.getCommission()
                        + DELIMITER + row.getResult()
                        + DELIMITER + row.getCurrency());
            }
            writer.flush();
        }
    }
}
