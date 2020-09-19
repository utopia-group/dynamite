package dynamite.trans;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import dynamite.docdb.DocJsonInstanceParser;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.util.FileUtils;

public class JsonToInputFactsTransformer extends DocToInputFactsTransformer {

    public static void parseToFactsCsv(DocumentSchema schema, Reader inputReader, String tmpPath) {
        FileUtils.createDirectory(Paths.get(tmpPath));
        JsonReader reader = new JsonReader(inputReader);
        reader.setLenient(true);
        JsonParser parser = new JsonParser();
        try {
            reader.beginObject();
            String name = reader.nextName();
            DSCollection collection = schema.getCollectionByName(name);
            reader.beginArray();
            HashMap<String, Writer> writers = new HashMap<>();
            while (reader.hasNext()) {
                JsonObject obj = parser.parse(reader).getAsJsonObject();
                DIDocument doc = DocJsonInstanceParser.parseDocument(obj, collection.document);
                emitFactToFileFromDIDocument(name, doc, writers, tmpPath);
            }
            for (String relation : writers.keySet()) {
                writers.get(relation).close();
            }
            reader.endArray();
            reader.endObject();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Json reading exception");
        }
    }
}
