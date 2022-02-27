package ru.nsu.fit.mazhuga;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NodeParser {

    private static final String NODE_ELEMENT_NAME = "node";
    private static final String NODE_ID_ATTRIBUTE_NAME = "id";
    private static final String USERNAME_ATTRIBUTE_NAME = "user";

    private final XMLInputFactory xmlInputFactory;
    private XMLEventReader reader;

    public NodeParser() {
        xmlInputFactory = XMLInputFactory.newInstance();
    }

    public ParsingResult parse(String pathString) throws Exception {

        final var userEditsMap = new HashMap<String, Long>();
        final var nodeEditsMap = new HashMap<Long, Long>();

        final var reader = xmlInputFactory.createXMLEventReader(new FileInputStream(pathString));

        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()) {

                StartElement element = nextEvent.asStartElement();
                parseElementAndFillMaps(element, userEditsMap, nodeEditsMap);
            }
        }

        return createResult(userEditsMap, nodeEditsMap);
    }

    private void parseElementAndFillMaps(StartElement element,
                                         Map<String, Long> userEditsMap,
                                         Map<Long, Long> nodeEditsMap) throws Exception {

        if (element.getName().getLocalPart().equals(NODE_ELEMENT_NAME)) {

            var nodeIdAttribute = element.getAttributeByName(QName.valueOf(NODE_ID_ATTRIBUTE_NAME));
            var userNameAttribute = element.getAttributeByName(QName.valueOf(USERNAME_ATTRIBUTE_NAME));

            if (nodeIdAttribute != null && userNameAttribute != null) {

                Long nodeId = Long.valueOf(nodeIdAttribute.getValue());
                String userName = userNameAttribute.getValue();

                if (userEditsMap.containsKey(userName)) {
                    Long editsValue = userEditsMap.get(userName);
                    userEditsMap.put(userName, ++editsValue);
                } else {
                    userEditsMap.put(userName, 1L);
                }

                if (nodeEditsMap.containsKey(nodeId)) {
                    Long editsValue = nodeEditsMap.get(nodeId);
                    nodeEditsMap.put(nodeId, ++editsValue);
                } else {
                    nodeEditsMap.put(nodeId, 1L);
                }

            } else {
                throw new Exception("Node does not have required attributes");
            }
        }
    }

    private ParsingResult createResult(Map<String, Long> userEditsMap,
                                       Map<Long, Long> nodeEditsMap) {

        final var userDataList = new ArrayList<ParsingResult.UserData>();
        final var nodeDataList = new ArrayList<ParsingResult.NodeData>();

        for (var mapEntry : userEditsMap.entrySet()) {
            var userData = new ParsingResult.UserData(mapEntry.getKey(), mapEntry.getValue());
            userDataList.add(userData);
        }

        for (var mapEntry : nodeEditsMap.entrySet()) {
            var nodeData = new ParsingResult.NodeData(mapEntry.getKey(), mapEntry.getValue());
            nodeDataList.add(nodeData);
        }

        return new ParsingResult(userDataList, nodeDataList);
    }
}
