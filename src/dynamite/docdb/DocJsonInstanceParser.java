package dynamite.docdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

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
import dynamite.docdb.ast.DSAtomicAttr.AttrType;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;

public final class DocJsonInstanceParser {

    public static final String BOX_KEY_NAME = "value";

    public static DocumentInstance parse(String jsonString, DocumentSchema schema) {
        List<DICollection> collections = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonObject rootObject = parser.parse(jsonString).getAsJsonObject();
        for (String key : rootObject.keySet()) {
            JsonElement value = rootObject.get(key);
            assert schema.hasCollection(key);
            DSCollection collectionSchema = schema.getCollectionByName(key);
            if (value.isJsonArray()) {
                List<DIDocument> documents = parseDocArray(value.getAsJsonArray(), collectionSchema);
                collections.add(new DICollection(key, documents));
            } else if (value.isJsonObject()) {
                // ill case handling: wrap it with an array
                JsonArray array = new JsonArray();
                array.add(value.getAsJsonObject());
                List<DIDocument> documents = parseDocArray(array, collectionSchema);
                collections.add(new DICollection(key, documents));
            } else {
                throw new IllegalArgumentException("Unknown value type in root object: " + value);
            }
        }
        DocumentInstance instance = new DocumentInstance(collections);
        // set canonical names to all documents and attributes
        instance.accept(new InstCanonicalNameVisitor());
        return instance;
    }

    public static DIDocument parseDocument(JsonObject docObject, DSDocument docSchema) {
        List<DIAttrValue> attributes = new ArrayList<>();
        for (DSAttribute attrSchema : docSchema.attributes) {
            String attrName = attrSchema.getName();
            DIAttrValue attribute = docObject.has(attrName)
                    ? parseAttribute(docObject.get(attrName), attrSchema)
                    : buildMissingAttribute(attrSchema);
            attributes.add(attribute);
        }
        Collections.sort(attributes, (x, y) -> x.getAttributeName().compareTo(y.getAttributeName()));
        return new DIDocument(attributes);
    }

    private static List<DIDocument> parseDocArray(JsonArray docArray, DSCollection collectionSchema) {
        List<DIDocument> documents = new ArrayList<>();
        for (Iterator<JsonElement> iter = docArray.iterator(); iter.hasNext();) {
            JsonElement element = iter.next();
            if (element.isJsonObject()) {
                documents.add(parseDocument(element.getAsJsonObject(), collectionSchema.document));
            } else if (element.isJsonPrimitive() || element.isJsonNull()) {
                // ill case handling: wrap it with an object
                JsonObject box = new JsonObject();
                box.add(BOX_KEY_NAME, element);
                documents.add(parseDocument(box, collectionSchema.document));
            } else {
                throw new IllegalArgumentException("Unknown element type: " + element);
            }
        }
        return documents;
    }

    private static DIAttrValue parseAttribute(JsonElement valueElement, DSAttribute attrSchema) {
        if (attrSchema instanceof DSAtomicAttr) {
            return parseAtomicAttr(valueElement, (DSAtomicAttr) attrSchema);
        } else if (attrSchema instanceof DSCollectionAttr) {
            return parseCollectionAttr(valueElement, (DSCollectionAttr) attrSchema);
        } else {
            throw new IllegalArgumentException("Unknown attribute type: " + attrSchema);
        }
    }

    private static DIAtomicAttrValue parseAtomicAttr(JsonElement valueElement, DSAtomicAttr attrSchema) {
        DIValue value = parseAtomicValue(valueElement, attrSchema.type);
        return new DIAtomicAttrValue(attrSchema.name, value);
    }

    private static DICollectionAttrValue parseCollectionAttr(JsonElement valueElement, DSCollectionAttr attrSchema) {
        if (valueElement.isJsonArray()) {
            List<DIDocument> nestedDocs = parseDocArray(valueElement.getAsJsonArray(), attrSchema.collection);
            return new DICollectionAttrValue(new DICollection(attrSchema.getName(), nestedDocs));
        } else if (valueElement.isJsonObject()) {
            // ill case handling: wrap it with an array
            JsonArray array = new JsonArray();
            array.add(valueElement.getAsJsonObject());
            List<DIDocument> nestedDocs = parseDocArray(array, attrSchema.collection);
            return new DICollectionAttrValue(new DICollection(attrSchema.getName(), nestedDocs));
        } else if (valueElement.isJsonNull()) {
            // add missing default values
            return buildMissingCollectionAttr(attrSchema);
        } else {
            throw new IllegalArgumentException("Unknown type of Json element: " + valueElement);
        }
    }

    private static DIAttrValue buildMissingAttribute(DSAttribute attrSchema) {
        if (attrSchema instanceof DSAtomicAttr) {
            return buildMissingAtomicAttr((DSAtomicAttr) attrSchema);
        } else if (attrSchema instanceof DSCollectionAttr) {
            return buildMissingCollectionAttr((DSCollectionAttr) attrSchema);
        } else {
            throw new IllegalArgumentException("Unknown attribute type: " + attrSchema);
        }
    }

    private static DIAtomicAttrValue buildMissingAtomicAttr(DSAtomicAttr attrSchema) {
        return new DIAtomicAttrValue(attrSchema.name, buildMissingAtomicValue(attrSchema.type));
    }

    private static DICollectionAttrValue buildMissingCollectionAttr(DSCollectionAttr attrSchema) {
        List<DIAttrValue> attributes = new ArrayList<>();
        for (DSAttribute nestedAttr : attrSchema.collection.document.attributes) {
            attributes.add(buildMissingAttribute(nestedAttr));
        }
        Collections.sort(attributes, (x, y) -> x.getAttributeName().compareTo(y.getAttributeName()));
        DIDocument document = new DIDocument(attributes);
        return new DICollectionAttrValue(new DICollection(attrSchema.getName(), Collections.singletonList(document)));
    }

    private static DIValue parseAtomicValue(JsonElement valueElement, AttrType valueType) {
        if (valueElement.isJsonNull()) return DINullValue.getInstance();
        JsonPrimitive valuePrimitive = valueElement.getAsJsonPrimitive();
        String valueStr = valuePrimitive.getAsString();
        switch (valueType) {
        case INT:
            return new DIIntValue(valuePrimitive.getAsInt());
        case FLOAT:
            return new DIFloatValue(valueStr);
        case STRING:
            return new DIStrValue(valueStr);
        default:
            throw new IllegalArgumentException(String.format("Unknown type %s for value %s" + valueType.name(), valueStr));
        }
    }

    private static DIValue buildMissingAtomicValue(AttrType type) {
        switch (type) {
        case INT:
            return new DIIntValue(0);
        case FLOAT:
            return new DIFloatValue("0.0");
        case STRING:
            return DINullValue.getInstance();
        default:
            throw new IllegalArgumentException("Unknown attribute type: " + type.name());
        }
    }

}
