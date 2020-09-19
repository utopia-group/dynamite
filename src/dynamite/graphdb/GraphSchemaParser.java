package dynamite.graphdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import dynamite.graphdb.ast.GSAtomicProp;
import dynamite.graphdb.ast.GSAtomicProp.PropType;
import dynamite.graphdb.ast.GSCollection;
import dynamite.graphdb.ast.GSCollectionProp;
import dynamite.graphdb.ast.GSEdge;
import dynamite.graphdb.ast.GSPropList;
import dynamite.graphdb.ast.GSProperty;
import dynamite.graphdb.ast.GSVertex;
import dynamite.graphdb.ast.GraphSchema;

public final class GraphSchemaParser {

    private static final String TYPE_KEY_NAME = "type";
    private static final String LABELS_KEY_NAME = "labels";
    private static final String LABEL_KEY_NAME = "label";
    private static final String PROP_KEY_NAME = "properties";
    private static final String VERTEX_TYPE = "node";
    private static final String EDGE_TYPE = "relationship";
    private static final String BOX_KEY_NAME = "value";

    /**
     * Parse a schema string to a {@link #GraphSchema} data structure.
     *
     * @param schemaString the schema string
     * @return the graph schema data structure
     */
    public static GraphSchema parse(String jsonString) {
        List<GSVertex> vertices = new ArrayList<>();
        List<GSEdge> edges = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
        for (Iterator<JsonElement> iter = jsonArray.iterator(); iter.hasNext();) {
            JsonObject jsonObject = iter.next().getAsJsonObject();
            String type = jsonObject.get(TYPE_KEY_NAME).getAsString();
            switch (type) {
            case VERTEX_TYPE:
                vertices.add(parseVertex(jsonObject));
                break;
            case EDGE_TYPE:
                edges.add(parseEdge(jsonObject));
                break;
            default:
                throw new RuntimeException("Unknown type of json object: " + type);
            }
        }
        GraphSchema schema = new GraphSchema(vertices, edges);
        return schema;
    }

    private static GSVertex parseVertex(JsonObject vertexObject) {
        JsonArray labelArray = vertexObject.get(LABELS_KEY_NAME).getAsJsonArray();
        List<String> labels = parseLabels(labelArray);
        JsonObject propObject = vertexObject.get(PROP_KEY_NAME).getAsJsonObject();
        GSPropList propList = parsePropList(propObject);
        return new GSVertex(labels, propList);
    }

    private static GSEdge parseEdge(JsonObject edgeObject) {
        String label = edgeObject.get(LABEL_KEY_NAME).getAsString();
        JsonObject propObject = edgeObject.get(PROP_KEY_NAME).getAsJsonObject();
        GSPropList propList = parsePropList(propObject);
        return new GSEdge(label, propList);
    }

    private static List<String> parseLabels(JsonArray labelArray) {
        List<String> labels = new ArrayList<>();
        for (Iterator<JsonElement> iter = labelArray.iterator(); iter.hasNext();) {
            String label = iter.next().getAsString();
            labels.add(label);
        }
        assert !labels.isEmpty() : "Labels cannot be empty";
        return labels;
    }

    private static GSPropList parsePropList(JsonObject propObject) {
        List<GSProperty> properties = new ArrayList<>();
        for (String key : propObject.keySet()) {
            JsonElement value = propObject.get(key);
            if (value.isJsonPrimitive()) {
                GSProperty property = new GSAtomicProp(key, parseAtomicPropType(value.getAsJsonPrimitive()));
                properties.add(property);
            } else if (value.isJsonArray()) {
                GSPropList nestedPropList = parsePropArray(value.getAsJsonArray());
                GSCollection collection = new GSCollection(key, nestedPropList);
                properties.add(new GSCollectionProp(collection));
            } else {
                throw new IllegalArgumentException("Unknown element type: " + value);
            }
        }
        Collections.sort(properties, (x, y) -> x.getName().compareTo(y.getName()));
        return new GSPropList(properties);
    }

    private static PropType parseAtomicPropType(JsonPrimitive typePrimitive) {
        return GSAtomicProp.stringToPropType(typePrimitive.getAsString());
    }

    private static GSPropList parsePropArray(JsonArray propArray) {
        Iterator<JsonElement> iterator = propArray.iterator();
        assert iterator.hasNext();
        JsonElement element = iterator.next();
        if (element.isJsonObject()) {
            return parsePropList(element.getAsJsonObject());
        } else if (element.isJsonPrimitive()) {
            JsonObject box = new JsonObject();
            box.add(BOX_KEY_NAME, element);
            return parsePropList(box);
        } else {
            throw new IllegalArgumentException("The element of JsonArray should be a JsonObject");
        }
    }

}
