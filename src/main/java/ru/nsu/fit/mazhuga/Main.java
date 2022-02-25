package ru.nsu.fit.mazhuga;

import org.apache.commons.cli.*;

public class Main {

    public static final String DEFAULT_FILE_PATH = "RU-NVS.osm.bz2";

    public static void main(String[] args) throws ParseException {

        Options options = new Options();
        options.addOption(
                Option.builder("file")
                        .argName("path")
                        .hasArg()
                        .desc("load an archived file with osm data")
                        .build());

        CommandLineParser parser = new DefaultParser();

        CommandLine line = parser.parse(options, args);

        if (line.hasOption("file")) {

            String path = line.getOptionValue("file");

            if (path == null) {
                path = DEFAULT_FILE_PATH;
            }

            System.out.println(path);
        }

    }
}
