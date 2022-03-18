package ru.nsu.fit.mazhuga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;

@Slf4j
@RequiredArgsConstructor
public class Service {

    private static final String DEFAULT_FILE_PATH = "RU-NVS.osm.bz2";
    private static final int PRINT_ENTRIES_LIMIT = 10;

    private final NodeParser parser;

    void parseData(String pathString) throws Exception {

        if (pathString == null) {
            pathString = DEFAULT_FILE_PATH;
        }

        var parsingResult = parser.parseCompressed(pathString);

        log.info("=== Top " + PRINT_ENTRIES_LIMIT + " USERS by edits ===");
        parsingResult.getUserDataList().stream()
                .sorted(Comparator.comparing(ParsingResult.UserData::getEditsCount).reversed())
                .limit(PRINT_ENTRIES_LIMIT)
                .forEachOrdered(it -> log.info("Name: " + it.getName() + " Edits: " + it.getEditsCount()));

        log.info("=== Top " + PRINT_ENTRIES_LIMIT + " NODES by edits ===");
        parsingResult.getNodeDataList().stream()
                .sorted(Comparator.comparing(ParsingResult.NodeData::getEditsCount).reversed())
                .limit(PRINT_ENTRIES_LIMIT)
                .forEachOrdered(it -> log.info("Name: " + it.getName() + " Edits: " + it.getEditsCount()));

    }
}
