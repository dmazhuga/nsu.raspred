package ru.nsu.fit.mazhuga;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class Main {

    public static void main(String[] args) throws Exception {

        final var options = new Options();
        options.addOption(
                Option.builder("file")
                        .argName("path")
                        .hasArg()
                        .desc("load an archived file with osm data")
                        .build());

        final var commandParser = new DefaultParser();
        final var commandLine = commandParser.parse(options, args);

        if (commandLine.hasOption("file")) {

            String pathString = commandLine.getOptionValue("file");

            final var decompressor = new Decompressor();
            final var parser = new NodeParser();
            final var service = new Service(decompressor, parser);

            service.parseData(pathString);
        }

    }
}
