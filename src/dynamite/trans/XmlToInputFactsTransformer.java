package dynamite.trans;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.HashMap;

import dynamite.docdb.DocXmlInstanceParser;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.util.FileUtils;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class XmlToInputFactsTransformer extends DocToInputFactsTransformer {
    public static void parseToFactsCsv(DocumentSchema schema, Reader inputReader, String tmpPath) {
        FileUtils.createDirectory(Paths.get(tmpPath));
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEventReader eventReader = createEventReader(inputFactory, inputReader);
        HashMap<String, Writer> writers = new HashMap<>();
        DocXmlInstanceParser xmlParser = new DocXmlInstanceParser(schema);

        if (!(nextNontrivialEvent(eventReader).isStartDocument())) { throw new IllegalStateException("Expected beginning of XML object"); }
        XMLEvent tableStartEvent = nextNontrivialEvent(eventReader);
        if (!tableStartEvent.isStartElement()) { throw new IllegalStateException("Expected beginning of XML object"); }
        while (eventReader.hasNext()) {
            XMLEvent xmlEvent = nextNontrivialEvent(eventReader);
            if (xmlEvent.isStartElement()) {
                String objectName = xmlEvent.asStartElement().getName().getLocalPart();
                Reader xmlReader = parseOneXmlObject(eventReader, outputFactory, eventFactory, xmlEvent);
                DIDocument doc = xmlParser.parseToDIDocument(xmlReader);
                emitFactToFileFromDIDocument(objectName, doc, writers, tmpPath);
            } else if (xmlEvent.isEndElement()) { // done with document
                XMLEvent documentClosing = nextNontrivialEvent(eventReader);
                if (!documentClosing.isEndDocument()) { throw new IllegalStateException("Unexpected tag while parsing XML" + documentClosing); }
            } else {
                throw new IllegalStateException("Unexpected tag while parsing XML" + xmlEvent);
            }
        }
        closeResources(writers, eventReader);
    }

    private static XMLEventReader createEventReader(XMLInputFactory inputFactory, Reader inputReader) {
        try {
            return inputFactory.createXMLEventReader(inputReader);
        } catch (XMLStreamException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create reader");
        }
    }

    private static void closeResources(HashMap<String, Writer> writers, XMLEventReader eventReader) {
        try {
            for (String relation : writers.keySet()) {
                writers.get(relation).close();
            }
            eventReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not close all resources");
        }
    }

    private static Reader parseOneXmlObject(XMLEventReader reader, XMLOutputFactory outputFactory, XMLEventFactory eventFactory, XMLEvent xmlEvent) {
        try {
            StringWriter intermediateXmlBuffer = new StringWriter();
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(intermediateXmlBuffer);
            eventWriter.add(eventFactory.createStartDocument());
            eventWriter.add(xmlEvent);
            XMLEvent intermediateEvent = nextNontrivialEvent(reader);
            while (!(intermediateEvent.isEndElement() &&
                    intermediateEvent.asEndElement().getName().equals(xmlEvent.asStartElement().getName()))) {
                eventWriter.add(intermediateEvent);
                intermediateEvent = nextNontrivialEvent(reader);
            }
            eventWriter.add(intermediateEvent);
            eventWriter.flush();
            eventWriter.close();
            return new StringReader(intermediateXmlBuffer.toString());
        } catch (XMLStreamException e) {
            e.printStackTrace();
            throw new RuntimeException("Xml stream exception");
        }
    }

    private static XMLEvent nextNontrivialEvent(XMLEventReader reader) {
        try {
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isCharacters()) {
                    Characters characters = event.asCharacters();
                    if (characters.isIgnorableWhiteSpace() || characters.isWhiteSpace()) {
                        continue;
                    } else {
                        return event;
                    }
                } else {
                    return event;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
            throw new RuntimeException("Xml reading exception");
        }
        throw new IllegalStateException("Reached end of document before finished parsing");
    }
}
