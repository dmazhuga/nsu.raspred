package ru.nsu.fit.mazhuga;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NodeParser {

    private static final String NODE_ELEMENT_NAME = "node";
    private static final String NODE_ELEMENT_TAG = "tag";

    private static final String TAG_KEY_ATTRIBUTE_NAME = "k";
    private static final String TAG_VALUE_ATTRIBUTE_NAME = "v";
    private static final String NODE_ID_ATTRIBUTE_NAME = "id";
    private static final String USERNAME_ATTRIBUTE_NAME = "user";

    private static final String TAG_KEY_NAME = "name";

    // TODO: should be injected
    private final XMLInputFactory xmlInputFactory;

    public NodeParser() {
        xmlInputFactory = XMLInputFactory.newInstance();
    }

    public ParsingResult parseCompressed(String pathString) throws Exception {
        return parse(new BZip2CompressorInputStream(
                new BufferedInputStream(Files.newInputStream(Paths.get(pathString)))));
    }

    public ParsingResult parse(InputStream inputStream) throws Exception {

        final var userEditsMap = new HashMap<String, Long>();
        final var nodeEditsMap = new HashMap<String, Long>();

        final var reader = xmlInputFactory.createXMLEventReader(inputStream);

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
                                         Map<String, Long> nodeEditsMap) throws Exception {

        if (element.getName().getLocalPart().equals(NODE_ELEMENT_NAME)) {

            var nodeIdAttribute = element.getAttributeByName(QName.valueOf(NODE_ID_ATTRIBUTE_NAME));
            var userNameAttribute = element.getAttributeByName(QName.valueOf(USERNAME_ATTRIBUTE_NAME));

            if (nodeIdAttribute != null && userNameAttribute != null) {

                String userName = userNameAttribute.getValue();

                if (userEditsMap.containsKey(userName)) {
                    Long editsValue = userEditsMap.get(userName);
                    userEditsMap.put(userName, ++editsValue);
                } else {
                    userEditsMap.put(userName, 1L);
                }

            } else {
                throw new Exception("Node does not have required attributes");
            }
        } else if (element.getName().getLocalPart().equals(NODE_ELEMENT_TAG)) {

            var keyAttribute = element.getAttributeByName(QName.valueOf(TAG_KEY_ATTRIBUTE_NAME));
            var valueAttribute = element.getAttributeByName(QName.valueOf(TAG_VALUE_ATTRIBUTE_NAME));

            if (keyAttribute != null && valueAttribute != null &&
                    keyAttribute.getValue().equals(TAG_KEY_NAME)) {

                String nodeName = valueAttribute.getValue();

                if (nodeEditsMap.containsKey(nodeName)) {
                    Long editsValue = nodeEditsMap.get(nodeName);
                    nodeEditsMap.put(nodeName, ++editsValue);
                } else {
                    nodeEditsMap.put(nodeName, 1L);
                }
            }
        }
    }

    private ParsingResult createResult(Map<String, Long> userEditsMap,
                                       Map<String, Long> nodeEditsMap) {

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
