package dynamite.docdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAtomicAttr.AttrType;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentSchema;

public final class DocSchemaParser {

    private static final String BOX_KEY_NAME = "value";

    public static DocumentSchema parse(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        List<DSCollection> collections = parseRootJsonObject(jsonObject);
        DocumentSchema schema = new DocumentSchema(collections);
        // set canonical names to all documents and attributes
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    private static List<DSCollection> parseRootJsonObject(JsonObject jsonObject) {
        List<DSCollection> collections = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonElement value = entry.getValue();
            if (value instanceof JsonArray) {
                collections.add(new DSCollection(entry.getKey(), parseJsonArray((JsonArray) value)));
            } else {
                throw new IllegalArgumentException("Each entry of the root JsonObject should be an array");
            }
        }
        return collections;
    }

    private static DSDocument parseJsonObject(JsonObject jsonObject) {
        List<DSAttribute> attributes = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonElement value = entry.getValue();
            if (value instanceof JsonArray) {
                DSDocument document = parseJsonArray((JsonArray) value);
                DSCollection collection = new DSCollection(entry.getKey(), document);
                attributes.add(new DSCollectionAttr(collection));
            } else if (value instanceof JsonPrimitive) {
                attributes.add(new DSAtomicAttr(entry.getKey(), parseJsonPrimitive((JsonPrimitive) value)));
            } else {
                throw new IllegalArgumentException("Unknown element type: " + value);
            }
        }
        Collections.sort(attributes, (x, y) -> x.getName().compareTo(y.getName()));
        return new DSDocument(attributes);
    }

    private static DSDocument parseJsonArray(JsonArray jsonArray) {
        Iterator<JsonElement> iterator = jsonArray.iterator();
        assert iterator.hasNext();
        JsonElement element = iterator.next();
        if (element instanceof JsonObject) {
            return parseJsonObject((JsonObject) element);
        } else if (element instanceof JsonPrimitive) {
            JsonObject box = new JsonObject();
            box.add(BOX_KEY_NAME, element);
            return parseJsonObject(box);
        } else {
            throw new IllegalArgumentException("The element of JsonArray should be a JsonObject");
        }
    }

    private static AttrType parseJsonPrimitive(JsonPrimitive jsonPrimitive) {
        if (!jsonPrimitive.isString()) {
            throw new IllegalArgumentException("Non-string attribute type: " + jsonPrimitive);
        }
        return DSAtomicAttr.stringToAttrType(jsonPrimitive.getAsString());
    }

}
