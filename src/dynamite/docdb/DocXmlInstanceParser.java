package dynamite.docdb;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DICollectionAttrValue;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DIFloatValue;
import dynamite.docdb.ast.DIIntValue;
import dynamite.docdb.ast.DINullValue;
import dynamite.docdb.ast.DIStrValue;
import dynamite.docdb.ast.DIValue;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static dynamite.docdb.DocJsonInstanceParser.BOX_KEY_NAME;

public final class DocXmlInstanceParser {

    private static final String INTERNAL_TEXT_NODE_NAME = "nestedContent";
    private static final String ROOT_CANONICAL_NAME = "?ROOT?";
    // current prefix for canonical names
    private static Stack<String> nameStack = new Stack<>();

    private DocumentSchema schema;

    public DocXmlInstanceParser(DocumentSchema schema) {
        this.schema = schema;
    }

    public DocumentInstance parse(String xmlString) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlString)));
            doc.getDocumentElement().normalize();
            removeXmlWhitespaceNodes(doc);
            Node root = doc.getChildNodes().item(0);
            List<DICollection> collections = parseRootXmlObject(root);
            DocumentInstance instance = new DocumentInstance(collections);
            // set canonical names to all documents and attributes
            instance.accept(new InstCanonicalNameVisitor());
            return instance;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<DICollection> parseRootXmlObject(Node root) {
        List<DICollection> collections = new ArrayList<>();
        nameStack.push(ROOT_CANONICAL_NAME);
        DIAttrValue rootedValue = parseXmlObject(root);
        if (!(rootedValue instanceof DICollectionAttrValue)) { throw new IllegalArgumentException("Primitive object cannot be root"); }
        DICollectionAttrValue rootedCollection = (DICollectionAttrValue) rootedValue;
        if (rootedCollection.collection.documents.size() != 1) { throw new IllegalStateException("Valid XML must be rooted at exactly one XML object"); }
        for (DIAttrValue attrValue : rootedCollection.collection.documents.get(0).attributes) {
            if (!(attrValue instanceof DICollectionAttrValue)) {
                throw new IllegalArgumentException("Primitive JsonObject should not be in root: " + attrValue.getCanonicalName());
            }
            DICollectionAttrValue cAttrValue = (DICollectionAttrValue) attrValue;
            collections.add(cAttrValue.collection);
        }
        return collections;
    }

    public DIDocument parseToDIDocument(Reader r) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(r));
            doc.getDocumentElement().normalize();
            removeXmlWhitespaceNodes(doc);
            Node root = doc.getChildNodes().item(0);
            DIAttrValue res = parseXmlObject(root);
            if (res == null) {
                return null; // this means this object isn't in our schema, so we can safely ignore it
            } else if (!(res instanceof DICollectionAttrValue)) {
                throw new IllegalStateException("Xml can't be rooted at a primitive");
            }
            DICollectionAttrValue collectionVal = (DICollectionAttrValue) res;
            if (collectionVal.collection.documents.size() != 1) {
                throw new IllegalStateException("Unexpected number of documents in collection");
            }
            return new DIDocument(collectionVal.collection.documents.get(0).attributes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private DIAttrValue parseXmlObject(Node obj) {
        String canonicalName = constructCanonicalName(obj.getNodeName());
        boolean isRoot = canonicalName.equals(ROOT_CANONICAL_NAME);
        if (!isRoot) {
            if (getExpectedTypeStructure(canonicalName).size() == 0) {
                return null; // value not in schema, so safe to ignore
            }
            nameStack.push(canonicalName);
        }
        DIAttrValue returnValue;
        if (isXmlPrimitive(obj)) {
            returnValue = parseXmlPrimitive(obj, canonicalName);
        } else if (isXmlNull(obj)) {
            returnValue = new DIAtomicAttrValue(obj.getNodeName(), DINullValue.getInstance());
        } else {
            Map<String, DIAttrValue> attrMap = constructAttrMap(obj);
            if (!isRoot) {
                addMissingValuesToAttrMap(attrMap, canonicalName);
            }
            DIDocument doc = new DIDocument(new ArrayList<>(attrMap.values()));
            List<DIDocument> docs = new ArrayList<>();
            docs.add(doc);
            returnValue = new DICollectionAttrValue(new DICollection(obj.getNodeName(), docs));
        }
        if (!isRoot) {
            nameStack.pop();
        }
        return returnValue;
    }

    private DIAttrValue parseXmlPrimitive(Node obj, String canonicalName) {
        DSAttribute expectedType = getExpectedType(canonicalName);
        if (expectedType == null) {
            return null;
        } else if (expectedType instanceof DSCollectionAttr) {
            DSCollectionAttr collectionAttr = (DSCollectionAttr) expectedType;
            List<DIDocument> docs = new ArrayList<>();
            DIAttrValue newValue = null;
            if (collectionAttr.collection.document.attributes.size() > 1) {
                for (DSAttribute innerExpectedType : collectionAttr.collection.document.attributes) {
                    String innerName = innerExpectedType.getName();
                    if (innerName.equals(INTERNAL_TEXT_NODE_NAME)) {
                        DIValue value = parseXmlPrimitiveContent(obj, (DSAtomicAttr) innerExpectedType);
                        newValue = new DIAtomicAttrValue(BOX_KEY_NAME, value);
                    } else {
                        DIValue value = getAtomicMissingValue((DSAtomicAttr) innerExpectedType);
                        newValue = new DIAtomicAttrValue(innerName, value);
                    }
                }
            } else {
                expectedType = collectionAttr.collection.document.attributes.get(0);
                DIValue value = parseXmlPrimitiveContent(obj, (DSAtomicAttr) expectedType);
                newValue = new DIAtomicAttrValue(BOX_KEY_NAME, value);
            }
            docs.add(new DIDocument(Collections.singletonList(newValue)));
            return new DICollectionAttrValue(new DICollection(obj.getNodeName(), docs));
        } else if (expectedType instanceof DSAtomicAttr) {
            return new DIAtomicAttrValue(obj.getNodeName(), parseXmlPrimitiveContent(obj, (DSAtomicAttr) expectedType));
        } else {
            throw new IllegalStateException("Unexpected type");
        }
    }

    private Map<String, DIAttrValue> constructAttrMap(Node obj) {
        Map<String, DIAttrValue> attrMap = new TreeMap<>();
        for (int i = 0; i < obj.getAttributes().getLength(); i++) {
            Node item = obj.getAttributes().item(i);
            addItemToAttrMap(attrMap, item);
        }
        for (int i = 0; i < obj.getChildNodes().getLength(); i++) {
            Node item = obj.getChildNodes().item(i);
            addItemToAttrMap(attrMap, item);
        }

        return attrMap;
    }

    private void addItemToAttrMap(Map<String, DIAttrValue> attrMap, Node item) {
        if (item.getNodeType() == Node.TEXT_NODE) { // special case for singular text node inside element with attributes
            String canonicalName = constructCanonicalName(INTERNAL_TEXT_NODE_NAME);
            DSAttribute expectedType = getExpectedType(canonicalName);
            if (expectedType == null) {
                return;
            }
            if (!(expectedType instanceof DSAtomicAttr)) {
                throw new IllegalStateException("Expected internal node to be atomic");

            }
            DIAttrValue val = new DIAtomicAttrValue(INTERNAL_TEXT_NODE_NAME, stringToDIValue(item.getTextContent(), (DSAtomicAttr) expectedType));
            attrMap.put(INTERNAL_TEXT_NODE_NAME, val);
            return;
        }
        String key = item.getNodeName();
        DIAttrValue value = parseXmlObject(item);
        if (value == null) { return; } // not in the schema, so don't need to add it at all
        if (attrMap.containsKey(key)) {
            DIAttrValue oldVal = attrMap.get(key);
            if (oldVal instanceof DIAtomicAttrValue) {
                throw new IllegalStateException(
                        "Found array of primitives where schema claimed there should be atomic primitive: " + ((DIAtomicAttrValue) oldVal).name);
            } else if (oldVal instanceof DICollectionAttrValue) { // we need to add the current DIAttrValue to it's existing list in the map
                DICollectionAttrValue oldCollectionVal = (DICollectionAttrValue) oldVal;
                List<DIDocument> newList = new ArrayList<>(oldCollectionVal.collection.documents);
                if (value instanceof DIAtomicAttrValue) { // primitives need singleton wrappers
                    DIAtomicAttrValue atomicValue = (DIAtomicAttrValue) value;
                    DIAttrValue newValue = new DIAtomicAttrValue(BOX_KEY_NAME, atomicValue.value);
                    newList.add(new DIDocument(Collections.singletonList(newValue)));
                } else if (value instanceof DICollectionAttrValue) {
                    DICollectionAttrValue collectionVal = (DICollectionAttrValue) value;
                    newList.addAll(collectionVal.collection.documents);
                }
                DIAttrValue newVal = new DICollectionAttrValue(new DICollection(oldCollectionVal.collection.name, newList));
                attrMap.put(key, newVal);
            }
        } else {
            if (value instanceof DIAtomicAttrValue) {
                DIAtomicAttrValue atomicValue = (DIAtomicAttrValue) value;
                String canonicalName = constructCanonicalName(atomicValue.name);
                DSAttribute expectedType = getExpectedType(canonicalName);
                if (expectedType instanceof DSAtomicAttr) {
                    attrMap.put(key, value);
                } else if (expectedType instanceof DSCollectionAttr) {
                    DIAttrValue newValue = new DIAtomicAttrValue(BOX_KEY_NAME, atomicValue.value);
                    DIDocument doc = new DIDocument(Collections.singletonList(newValue));
                    List<DIDocument> docs = new ArrayList<>();
                    docs.add(doc);
                    attrMap.put(atomicValue.name, new DICollectionAttrValue(new DICollection(atomicValue.name, docs)));
                }
            } else if (value instanceof DICollectionAttrValue) {
                attrMap.put(key, value);
            }
        }
    }

    private DSAttribute getExpectedType(String canonicalName) {
        String[] path = canonicalName.split("\\?");
        String tableName = path[0];
        int collIndex = indexInCollections(schema.collections, tableName);
        if (collIndex == -1) { return null; }
        DSCollection table = schema.collections.get(collIndex);
        List<DSAttribute> attributes = table.document.attributes;
        DSAttribute attr = null;
        for (int i = 1; i < path.length; i++) {
            String currTarget = path[i];
            int attrIndex = indexInAttributes(attributes, currTarget);
            if (attrIndex == -1) { return null; }
            attr = attributes.get(attrIndex);
            if (attr instanceof DSCollectionAttr) {
                attributes = ((DSCollectionAttr) attr).collection.document.attributes;
            } else if (attr instanceof DSAtomicAttr && i == path.length - 1) {
                return attr;
            }
        }
        return attr;
    }

    private List<DSAttribute> getExpectedTypeStructure(String canonicalName) {
        String[] path = canonicalName.split("\\?");
        String tableName = path[0];
        int collIndex = indexInCollections(schema.collections, tableName);
        if (collIndex == -1) { return new ArrayList<>(); }
        DSCollection table = schema.collections.get(collIndex);
        List<DSAttribute> attributes = table.document.attributes;
        for (int i = 1; i < path.length; i++) {
            String currTarget = path[i];
            int index = indexInAttributes(attributes, currTarget);
            if (index == -1) { return new ArrayList<>(); }
            DSAttribute attr = attributes.get(index);
            if (attr instanceof DSCollectionAttr) {
                attributes = ((DSCollectionAttr) attr).collection.document.attributes;
            } else if (attr instanceof DSAtomicAttr && i == path.length - 1) {
                return Collections.singletonList(attr);
            }
        }
        return attributes;
    }

    private void addMissingValuesToAttrMap(Map<String, DIAttrValue> attrMap, String canonicalName) {
        List<DSAttribute> expectedTypes = getExpectedTypeStructure(canonicalName);
        for (DSAttribute expectedType : expectedTypes) {
            String name = expectedType.getName();
            if (attrMap.get(name) == null) {
                attrMap.put(name, constructNestedMissingValue(expectedType));
            }
        }
    }

    private DIAttrValue constructNestedMissingValue(DSAttribute expectedType) {
        String name = expectedType.getName();
        if (expectedType instanceof DSAtomicAttr) {
            DSAtomicAttr atomicAttr = (DSAtomicAttr) expectedType;
            return new DIAtomicAttrValue(name, getAtomicMissingValue(atomicAttr));
        } else if (expectedType instanceof DSCollectionAttr) {
            DSCollectionAttr collectionAttr = (DSCollectionAttr) expectedType;
            List<DIDocument> docs = new ArrayList<>();
            List<DIAttrValue> attrs = new ArrayList<>();
            for (DSAttribute attr : collectionAttr.collection.document.attributes) {
                attrs.add(constructNestedMissingValue(attr));
            }
            docs.add(new DIDocument(attrs));
            return new DICollectionAttrValue(new DICollection(name, docs));
        } else {
            throw new IllegalArgumentException("Unexpected DSAttribute type");
        }
    }

    private DIValue getAtomicMissingValue(DSAtomicAttr atomicAttr) {
        if (atomicAttr.type == DSAtomicAttr.AttrType.INT) {
            return new DIIntValue(0);
        } else if (atomicAttr.type == DSAtomicAttr.AttrType.FLOAT) {
            return new DIFloatValue("0.0");
        } else if (atomicAttr.type == DSAtomicAttr.AttrType.STRING) {
            return DINullValue.getInstance();
        } else {
            throw new IllegalStateException("Unexpected type of missing value");
        }
    }

    private int indexInCollections(List<DSCollection> collections, String name) {
        for (int i = 0; i < collections.size(); i++) {
            if (collections.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private int indexInAttributes(List<DSAttribute> attributes, String name) {
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private static String constructCanonicalName(String name) {
        if (nameStack.isEmpty()) {
            return name;
        } else if (nameStack.peek().equals(ROOT_CANONICAL_NAME)) {
            nameStack.pop();
            return ROOT_CANONICAL_NAME;
        } else {
            return nameStack.peek() + InstCanonicalNameVisitor.DELIMITER + name;
        }
    }

    private boolean isXmlPrimitive(Node obj) {
        return !hasNontrivialAttributes(obj) &&
                (obj.getChildNodes().getLength() == 1 && obj.getFirstChild().getNodeType() == Node.TEXT_NODE);
    }

    private boolean hasNontrivialAttributes(Node obj) {
        if (obj.getAttributes() == null) { return false; }
        for (int i = 0; i < obj.getAttributes().getLength(); i++) {
            String canonicalName = constructCanonicalName(obj.getAttributes().item(i).getNodeName());
            if (getExpectedType(canonicalName) != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean isXmlNull(Node obj) {
        return obj.getNodeType() == Node.ELEMENT_NODE && obj.getFirstChild() == null && obj.getAttributes().getLength() == 0;
    }

    private static DIValue parseXmlPrimitiveContent(Node obj, DSAtomicAttr expectedType) {
        return stringToDIValue(obj.getFirstChild().getNodeValue(), expectedType);
    }

    private static DIValue stringToDIValue(String s, DSAtomicAttr type) {
        String val = s.trim();
        switch (type.type) {
        case INT:
            if (isInteger(val)) {
                try {
                    return new DIIntValue(Integer.parseInt(val));
                } catch (NumberFormatException e) { // overflow error
                    return new DIIntValue(-1);
                }
            } else {
                throw new IllegalArgumentException("Expected int but got " + s + " for attribute " + type.getName());
            }
        case FLOAT:
            return new DIFloatValue(val);
        case STRING:
            return new DIStrValue(val.trim());
        default:
            throw new IllegalArgumentException("Expected type to be int, float, or string");
        }
    }

    private static void removeXmlWhitespaceNodes(Document doc) {
        try {
            XPathFactory xpathFactory = XPathFactory.newInstance();
            // XPath to find empty text nodes.
            XPathExpression xpathExp = xpathFactory.newXPath().compile(
                    "//text()[normalize-space(.) = '']");
            NodeList emptyTextNodes = (NodeList)
                    xpathExp.evaluate(doc, XPathConstants.NODESET);

            // Remove each empty text node from document.
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            throw new RuntimeException("Xpath issue");
        }
    }

    private static boolean isInteger(String str) {
        return str.matches("-?\\d+");
    }
}
