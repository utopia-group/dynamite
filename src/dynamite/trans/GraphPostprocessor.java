package dynamite.trans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import dynamite.datalog.ast.DatalogProgram;
import dynamite.graphdb.ast.GSEdge;
import dynamite.graphdb.ast.GSVertex;
import dynamite.graphdb.ast.GraphSchema;

public class GraphPostprocessor extends Postprocessor {

    private static final int BATCH_SIZE = 1000;

    public static void emitGraph(GraphSchema schema, DatalogProgram program, String outputPath) {
        constructMongoInstance(program, outputPath);
        System.out.println("Start to build the graph wrapper");
        long wrapStartTime = System.currentTimeMillis();
        String compiledCollectionName = compileWrapper(schema);
        System.out.println("=== Wrap Time: " + (System.currentTimeMillis() - wrapStartTime) / 1000);
        System.out.println("Start to export");
        long exportStartTime = System.currentTimeMillis();
        executeMongoexport(compiledCollectionName, outputPath);
        System.out.println("=== Export Time: " + (System.currentTimeMillis() - exportStartTime) / 1000);
    }

    private static String compileWrapper(GraphSchema schema) {
        MongoDatabase database = MongoClients.create().getDatabase("test");
        String wrapperCollectionName = "wrapper";
        MongoCollection<Document> wrapperCollection = database.getCollection(wrapperCollectionName);
        for (GSVertex vertex : schema.vertices) {
            insertAllVertices(database, wrapperCollection, vertex);
        }
        for (GSEdge edge : schema.edges) {
            insertAllEdges(database, wrapperCollection, edge);
        }
        return wrapperCollectionName;
    }

    private static void insertAllEdges(MongoDatabase database, MongoCollection<Document> wrapperCollection, GSEdge edgeSchema) {
        List<Document> documents = new ArrayList<>((int) 1.5 * BATCH_SIZE);
        String edgeName = edgeSchema.label;
        MongoCollection<Document> edgeCollection = database.getCollection(edgeName);
        for (Document d : edgeCollection.find().projection(Projections.excludeId())) {
            Object startKey = d.get("_start");
            Object endKey = d.get("_end");
            d.remove("_start");
            d.remove("_end");
            Document document = new Document("type", "relationship")
                    .append("label", edgeName)
                    // .append("id", "null")
                    .append("start", new Document("id", startKey))
                    .append("end", new Document("id", endKey))
                    .append("properties", d);
            documents.add(document);
            if (documents.size() >= BATCH_SIZE) {
                wrapperCollection.insertMany(documents);
                documents.clear();
            }
        }
        if (!documents.isEmpty()) {
            wrapperCollection.insertMany(documents);
            documents.clear();
        }
    }

    private static void insertAllVertices(MongoDatabase database, MongoCollection<Document> wrapperCollection, GSVertex vertex) {
        List<Document> documents = new ArrayList<>((int) 1.5 * BATCH_SIZE);
        for (String vertexName : vertex.labels) {
            MongoCollection<Document> vertexCollection = database.getCollection(vertexName);
            for (Document d : vertexCollection.find()) {
                Object k = d.get("_id");
                d.remove("_id");
                Document document = new Document("type", "node")
                        .append("labels", Collections.singletonList(vertexName))
                        .append("id", k)
                        .append("properties", d);
                documents.add(document);
                if (documents.size() >= BATCH_SIZE) {
                    wrapperCollection.insertMany(documents);
                    documents.clear();
                }
            }
        }
        if (!documents.isEmpty()) {
            wrapperCollection.insertMany(documents);
            documents.clear();
        }
    }
}
