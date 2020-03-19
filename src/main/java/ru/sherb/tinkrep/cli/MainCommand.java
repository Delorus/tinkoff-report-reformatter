package ru.sherb.tinkrep.cli;

import picocli.CommandLine;

import java.nio.file.Path;

/**
 * @author maksim
 * @since 18.03.2020
 */
@CommandLine.Command(mixinStandardHelpOptions = true, abbreviateSynopsis = true, helpCommand = true)
public final class MainCommand {

    @CommandLine.Parameters(description = "Path to report", converter = PathConverter.class)
    public Path root;
}