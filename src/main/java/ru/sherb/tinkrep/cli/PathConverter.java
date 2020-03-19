package ru.sherb.tinkrep.cli;

import picocli.CommandLine;

import java.nio.file.Path;

/**
 * @author maksim
 * @since 18.05.19
 */
final class PathConverter implements CommandLine.ITypeConverter<Path> {

    @Override
    public Path convert(String value) throws Exception {
        return Path.of(value);
    }
}
