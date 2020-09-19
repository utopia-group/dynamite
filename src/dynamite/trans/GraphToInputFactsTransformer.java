package dynamite.trans;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import dynamite.datalog.ast.DatalogFact;
import dynamite.graphdb.GraphInstanceParser;
import dynamite.graphdb.ast.GIAstNode;
import dynamite.graphdb.ast.GIEdge;
import dynamite.graphdb.ast.GIVertex;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.GraphSchema;
import dynamite.util.FileUtils;

import static dynamite.trans.InputFactsTransformer.emitFactsToFile;

public class GraphToInputFactsTransformer {

    public static void parseToFactsCsv(GraphSchema schema, Reader inputReader, String tmpPath) {
        FileUtils.createDirectory(Paths.get(tmpPath));
        JsonReader reader = new JsonReader(inputReader);
        reader.setLenient(true);
        JsonParser parser = new JsonParser();
        try {
            reader.beginArray();
            HashMap<String, Writer> writers = new HashMap<>();
            while (reader.hasNext()) {
                JsonObject obj = parser.parse(reader).getAsJsonObject();
                GIAstNode astNode = GraphInstanceParser.parseObj(obj, schema); // either a vertex or an edge
                emitFactToFileFromGIAstNode(astNode, writers, tmpPath);
            }
            for (String relation : writers.keySet()) {
                writers.get(relation).close();
            }
            reader.endArray();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reading exception");
        }
    }

    public static void emitFactToFileFromGIAstNode(GIAstNode astNode, HashMap<String, Writer> writers, String tmpPath) {
        List<GIVertex> vertices = new ArrayList<>();
        List<GIEdge> edges = new ArrayList<>();
        if (astNode instanceof GIVertex) {
            vertices.add((GIVertex) astNode);
        } else if (astNode instanceof GIEdge) {
            edges.add((GIEdge) astNode);
        } else {
            throw new IllegalArgumentException("Unexpected subtype for astNode");
        }
        GraphInstance singleton = new GraphInstance(vertices, edges);
        List<DatalogFact> facts = singleton.toDatalogFacts();
        emitFactsToFile(facts, writers, tmpPath);
    }
}
