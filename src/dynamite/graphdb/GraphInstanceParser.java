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

import dynamite.graphdb.ast.GIAstNode;
import dynamite.graphdb.ast.GIAtomicProp;
import dynamite.graphdb.ast.GICollection;
import dynamite.graphdb.ast.GICollectionProp;
import dynamite.graphdb.ast.GIEdge;
import dynamite.graphdb.ast.GIFloatValue;
import dynamite.graphdb.ast.GIIntValue;
import dynamite.graphdb.ast.GINullValue;
import dynamite.graphdb.ast.GIPropList;
import dynamite.graphdb.ast.GIProperty;
import dynamite.graphdb.ast.GIStrValue;
import dynamite.graphdb.ast.GIValue;
import dynamite.graphdb.ast.GIVertex;
import dynamite.graphdb.ast.GSAtomicProp;
import dynamite.graphdb.ast.GSAtomicProp.PropType;
import dynamite.graphdb.ast.GSCollectionProp;
import dynamite.graphdb.ast.GSEdge;
import dynamite.graphdb.ast.GSPropList;
import dynamite.graphdb.ast.GSProperty;
import dynamite.graphdb.ast.GSVertex;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.GraphSchema;
import dynamite.graphdb.ast.VertexInfo;

public final class GraphInstanceParser {

    private static final String ID_KEY_NAME = "id";
    private static final String TYPE_KEY_NAME = "type";
    private static final String LABELS_KEY_NAME = "labels";
    private static final String LABEL_KEY_NAME = "label";
    private static final String PROP_KEY_NAME = "properties";
    private static final String START_KEY_NAME = "start";
    private static final String END_KEY_NAME = "end";
    private static final String VERTEX_TYPE = "node";
    private static final String EDGE_TYPE = "relationship";
    private static final String BOX_KEY_NAME = "value";

    public static GraphInstance parse(String jsonString, GraphSchema schema) {
        List<GIVertex> vertices = new ArrayList<>();
        List<GIEdge> edges = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
        for (JsonElement aJsonArray : jsonArray) {
            JsonObject jsonObject = aJsonArray.getAsJsonObject();
            GIAstNode giAstNode = parseObj(jsonObject, schema);
            if (giAstNode instanceof GIVertex) {
                vertices.add((GIVertex) giAstNode);
            } else if (giAstNode instanceof GIEdge) {
                edges.add((GIEdge) giAstNode);
            } else {
                throw new IllegalStateException("Unexpected return value when parsing JSON object");
            }
        }
        GraphInstance instance = new GraphInstance(vertices, edges);
        return instance;
    }

    public static GIAstNode parseObj(JsonObject jsonObject, GraphSchema schema) {
        String type = jsonObject.get(TYPE_KEY_NAME).getAsString();
        switch (type) {
        case VERTEX_TYPE:
            return parseVertex(jsonObject, getVertexSchema(jsonObject, schema));
        case EDGE_TYPE:
            return parseEdge(jsonObject, getEdgeSchema(jsonObject, schema));
        default:
            throw new RuntimeException("Unknown type of json object: " + type);
        }
    }

    private static GSVertex getVertexSchema(JsonObject vertexObject, GraphSchema schema) {
        List<GSVertex> vertexSchemas = schema.getVertexSchemas();
        List<String> vertexLabels = parseLabels(vertexObject.get(LABELS_KEY_NAME).getAsJsonArray());
        List<String> resLabels = new ArrayList<>();
        List<GSProperty> resProps = new ArrayList<>();
        for (GSVertex vertexSchema : vertexSchemas) {
            if (vertexSchema.labels.size() != 1) {
                throw new IllegalArgumentException("Expected schema to have singleton label but found: " + vertexSchema.labels);
            }
            String schemaLabel = vertexSchema.getLabels().get(0);
            if (vertexLabels.contains(schemaLabel)) {
                resLabels.add(schemaLabel);
                resProps.addAll(vertexSchema.getPropList().getProperties());
            }
        }
        if (resLabels.size() == 0) {
            throw new RuntimeException("Please add at least one of the following to the graph schema: " + vertexLabels.toString());
        } else {
            return new GSVertex(resLabels, new GSPropList(resProps));
        }
    }

    private static GSEdge getEdgeSchema(JsonObject edgeObject, GraphSchema schema) {
        List<GSEdge> edgeSchemas = schema.getEdgeSchemas();
        String edgeLabel = edgeObject.get(LABEL_KEY_NAME).getAsString();
        Iterator<GSEdge> itr = edgeSchemas.iterator();
        GSEdge edgeSchema;
        while (itr.hasNext()) {
            edgeSchema = itr.next();
            if (edgeLabel.equals(edgeSchema.getLabel())) {
                return edgeSchema;
            }
        }
        throw new RuntimeException("Unknown edge type found: " + edgeLabel);
    }

    private static GIVertex parseVertex(JsonObject vertexObject, GSVertex schema) {
        String id = vertexObject.get(ID_KEY_NAME).getAsString();
        JsonArray labelArray = vertexObject.get(LABELS_KEY_NAME).getAsJsonArray();
        List<String> labels = parseLabels(labelArray);
        JsonObject propObject = vertexObject.get(PROP_KEY_NAME).getAsJsonObject();
        GIPropList propList = parsePropList(propObject, schema.getPropList());
        GIVertex vertex = new GIVertex(id, labels, propList);
        return vertex;
    }

    private static GIEdge parseEdge(JsonObject edgeObject, GSEdge schema) {
        String id = edgeObject.get(ID_KEY_NAME).getAsString();
        String label = edgeObject.get(LABEL_KEY_NAME).getAsString();
        VertexInfo start = parseVertexInfo(edgeObject.get(START_KEY_NAME).getAsJsonObject());
        VertexInfo end = parseVertexInfo(edgeObject.get(END_KEY_NAME).getAsJsonObject());
        GIPropList propList;
        if (edgeObject.has(PROP_KEY_NAME)) {
            JsonObject propObject = edgeObject.get(PROP_KEY_NAME).getAsJsonObject();
            propList = parsePropList(propObject, schema.getPropList());
        } else {
            List<GIProperty> emptyList = new ArrayList<>();
            propList = new GIPropList(emptyList);
        }
        return new GIEdge(id, label, propList, start, end);
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

    private static VertexInfo parseVertexInfo(JsonObject vertexInfoObject) {
        String id = vertexInfoObject.get(ID_KEY_NAME).getAsString();
        JsonArray labelArray = vertexInfoObject.get(LABELS_KEY_NAME).getAsJsonArray();
        List<String> labels = parseLabels(labelArray);
        return new VertexInfo(id, labels);
    }

    private static GIPropList parsePropList(JsonObject propObject, GSPropList propList) {
        List<GIProperty> properties = new ArrayList<>();
        for (GSProperty property : propList.getProperties()) {
            String key = property.getName();
            if (propObject.keySet().contains(key)) {
                JsonElement value = propObject.get(key);
                if (value.isJsonPrimitive()) {
                    GSAtomicProp atomicProp = (GSAtomicProp) property;
                    properties.add(new GIAtomicProp(key, parseAtomicValue(value, atomicProp.type)));
                } else if (value.isJsonArray()) {
                    List<GIPropList> nestedPropLists = parsePropArray(value.getAsJsonArray(), ((GSCollectionProp) property).getCollection().getPropList());
                    GICollection collection = new GICollection(key, nestedPropLists);
                    properties.add(new GICollectionProp(collection));
                } else if (value.isJsonObject()) {
                    // ill case handling: wrap it with an array
                    JsonArray array = new JsonArray();
                    array.add(value.getAsJsonObject());
                    List<GIPropList> nestedPropLists = parsePropArray(array, ((GSCollectionProp) property).getCollection().getPropList());
                    GICollection collection = new GICollection(key, nestedPropLists);
                    properties.add(new GICollectionProp(collection));
                } else if (value.isJsonNull()) {
                    properties.add(new GIAtomicProp(key, GINullValue.getInstance()));
                } else {
                    throw new IllegalArgumentException("Unknown element type: " + value);
                }
            } else {
                properties.add(buildMissingProperty(property));
            }
        }
        Collections.sort(properties, (x, y) -> x.getName().compareTo(y.getName()));
        return new GIPropList(properties);
    }

    private static List<GIPropList> parsePropArray(JsonArray propArray, GSPropList schema) {
        List<GIPropList> propLists = new ArrayList<>();
        for (Iterator<JsonElement> iter = propArray.iterator(); iter.hasNext();) {
            JsonElement element = iter.next();
            if (element.isJsonObject()) {
                propLists.add(parsePropList(element.getAsJsonObject(), schema));
            } else if (element.isJsonPrimitive() || element.isJsonNull()) {
                // ill case handling: wrap it with an object
                JsonObject box = new JsonObject();
                box.add(BOX_KEY_NAME, element);
                propLists.add(parsePropList(box, schema));
            } else {
                throw new IllegalArgumentException("The element of JsonArray should be a JsonObject");
            }
        }
        return propLists;
    }

    private static GIProperty buildMissingProperty(GSProperty propSchema) {
        if (propSchema instanceof GSAtomicProp) {
            return buildMissingAtomicProp((GSAtomicProp) propSchema);
        } else if (propSchema instanceof GSCollectionProp) {
            return buildMissingCollectionAttr((GSCollectionProp) propSchema);
        } else {
            throw new IllegalArgumentException("Unknown attribute type: " + propSchema);
        }
    }

    private static GIAtomicProp buildMissingAtomicProp(GSAtomicProp propSchema) {
        return new GIAtomicProp(propSchema.getName(), buildMissingAtomicValue(propSchema.type));
    }

    private static GICollectionProp buildMissingCollectionAttr(GSCollectionProp propSchema) {
        List<GIProperty> properties = new ArrayList<>();
        for (GSProperty nestedProp : propSchema.getCollection().getPropList().getProperties()) {
            properties.add(buildMissingProperty(nestedProp));
        }
        Collections.sort(properties, (x, y) -> x.getName().compareTo(y.getName()));
        GIPropList propList = new GIPropList(properties);
        return new GICollectionProp(new GICollection(propSchema.getName(), Collections.singletonList(propList)));
    }

    private static GIValue parseAtomicValue(JsonElement valueElement, PropType valueType) {
        if (valueElement.isJsonNull()) return GINullValue.getInstance();
        JsonPrimitive valuePrimitive = valueElement.getAsJsonPrimitive();
        String valueStr = valuePrimitive.getAsString();
        switch (valueType) {
        case INT:
            return new GIIntValue(valuePrimitive.getAsInt());
        case FLOAT:
            return new GIFloatValue(valueStr);
        case STRING:
            return new GIStrValue(valueStr);
        default:
            throw new IllegalArgumentException(String.format("Unknown type %s for value %s" + valueType.name(), valueStr));
        }
    }

    private static GIValue buildMissingAtomicValue(PropType type) {
        switch (type) {
        case INT:
            return new GIIntValue(0);
        case FLOAT:
            return new GIFloatValue("0.0");
        case STRING:
            return GINullValue.getInstance();
        default:
            throw new IllegalArgumentException("Unknown attribute type: " + type.name());
        }
    }

}
