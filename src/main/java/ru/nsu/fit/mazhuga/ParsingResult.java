package ru.nsu.fit.mazhuga;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ParsingResult {

    private List<UserData> userDataList;

    private List<NodeData> nodeDataList;

    @Data
    @AllArgsConstructor
    static class UserData {

        private String name;

        private long editsCount;
    }

    @Data
    @AllArgsConstructor
    static class NodeData {

        private long id;

        private long editsCount;
    }
}
