package ru.nsu.fit.mazhuga;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Comparator;

public class Main {

    private static final String DEFAULT_FILE_PATH = "RU-NVS.osm.bz2";

    private static final int PRINT_ENTRIES_LIMIT = 10;

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

            if (pathString == null) {
                pathString = DEFAULT_FILE_PATH;
            }

            String outputPathString = pathString.replace(".bz2", "");

            System.out.println(pathString);

            final var decompressor = new Decompressor();
            final var parser = new NodeParser();

            decompressor.decompress(pathString, outputPathString);

            var parsingResult = parser.parse(outputPathString);

            System.out.println();
            System.out.println("=== Top " + PRINT_ENTRIES_LIMIT + " USERS by edits ===");
            parsingResult.getUserDataList().stream()
                    .sorted(Comparator.comparing(ParsingResult.UserData::getEditsCount).reversed())
                    .limit(PRINT_ENTRIES_LIMIT)
                    .forEachOrdered(it -> System.out.println("Name: " + it.getName() + " Edits: " + it.getEditsCount()));

            System.out.println();
            System.out.println("=== Top " + PRINT_ENTRIES_LIMIT + " NODES by edits ===");
            parsingResult.getNodeDataList().stream()
                    .sorted(Comparator.comparing(ParsingResult.NodeData::getEditsCount).reversed())
                    .limit(PRINT_ENTRIES_LIMIT)
                    .forEachOrdered(it -> System.out.println("Id: " + it.getId() + " Edits: " + it.getEditsCount()));
        }

    }
}
