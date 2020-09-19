package dynamite.trans;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Updates.set;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.TypedAttribute;
import dynamite.util.FileUtils;
import dynamite.util.Node;

public class Postprocessor {

    private static final String IMPORT_STDOUT = "tmp/import_out.txt";
    private static final String IMPORT_STDERR = "tmp/import_err.txt";
    private static final String EXPORT_STDOUT = "tmp/export_out.txt";
    private static final String EXPORT_STDERR = "tmp/export_err.txt";

    public static List<Node<String>> constructMongoInstance(DatalogProgram program, String outputPath) {
        Set<String> outputDeclNames = computOutputDeclNames(program);
        List<RelationDeclaration> relationDecls = computeRelationDecls(program, outputDeclNames);
        emitSchemas(relationDecls, outputPath);
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("test");
        database.drop(); // begin by dropping the database to remove stale data
        System.out.println("Start to import");
        long importStartTime = System.currentTimeMillis();
        mongoimportFlatFiles(outputPath, relationDecls);
        System.out.println("=== Import Time: " + (System.currentTimeMillis() - importStartTime) / 1000);
        System.out.println("Start to create index");
        long indexStartTime = System.currentTimeMillis();
        createMongoIndices(database, relationDecls);
        System.out.println("=== Index Time: " + (System.currentTimeMillis() - indexStartTime) / 1000);

        System.out.println("Start to embed relations");
        long embedStartTime = System.currentTimeMillis();
        List<Node<String>> forest = relationDecls.stream().map(Postprocessor::constructTreeFromRelationDecl).collect(Collectors.toList());
        for (Node<String> tree : forest) {
            embedAllRelations(database, tree);
        }
        System.out.println("=== Embed Time: " + (System.currentTimeMillis() - embedStartTime) / 1000);
        return forest;
    }

    private static void embedAllRelations(MongoDatabase db, Node<String> node) {
        if (node.getChildren() != null) {
            for (Node<String> curr : node.getChildren()) {
                embedAllRelations(db, curr);
            }
        }
        if (!node.isRoot()) {
            String parentDB = node.getParent().getData();
            String childDB = node.getData();
            String[] fields = childDB.split("\\?");
            String parentKey = fields[fields.length - 1];
            String childKey = "__id";
            System.out.println(String.format("Embedding parentDB: %s, childDB: %s, parentKey: %s, childKey: %s",
                    parentDB, childDB, parentKey, childKey));
            long embedRelStartTime = System.currentTimeMillis();
            embedRelation(db, parentDB, childDB, parentKey, childKey);
            System.out.println("=== Sub Embed Time: " + (System.currentTimeMillis() - embedRelStartTime) / 1000);
        }
    }

    private static void embedRelation(MongoDatabase db, String parentDB, String childDB, String parentKey, String childKey) {
        MongoCollection<Document> parentColl = db.getCollection(parentDB);
        MongoCollection<Document> childColl = db.getCollection(childDB);

        for (Document doc : parentColl.find()) {
            String hashValue = doc.getString(parentKey);
            FindIterable<Document> innerDoc = childColl
                    .find(eq(childKey, hashValue))
                    .projection(fields(exclude("_id", childKey))); // don't need the key and the mongoid anymore
            ObjectId id = doc.getObjectId("_id");
            parentColl.updateOne(eq("_id", id), set(parentKey, innerDoc));
        }
    }

    private static Node<String> constructTreeFromRelationDecl(RelationDeclaration outputDecl) {
        Node<String> root = new Node<>(null);
        String string = outputDecl.relation;
        List<String> path = new ArrayList<>();
        String[] split = string.split("\\?");
        StringBuilder currPath = new StringBuilder(split[0]);
        path.add(currPath.toString());
        for (int i = 1; i < split.length; i++) {
            currPath.append("?").append(split[i]);
            path.add(currPath.toString());
        }
        root = root.insert(path);

        return root;
    }

    private static void createMongoIndices(MongoDatabase database, List<RelationDeclaration> outputDecls) {
        for (RelationDeclaration predicate : outputDecls) {
            String collectionName = predicate.relation;
            for (TypedAttribute attr : predicate.attributes) {
                if (attr.typeName.equals("Rel") && attr.attrName.equals("__id")) {
                    String keyName = attr.attrName;
                    MongoCollection<Document> collection = database.getCollection(collectionName);
                    collection.createIndex(Indexes.ascending(keyName));
                }
            }
        }

    }

    private static void mongoimportFlatFiles(String outputPath, List<RelationDeclaration> outputDecls) {
        for (RelationDeclaration predicate : outputDecls) {
            String schemaPath = outputPath + predicate.relation + ".schema";
            String instancePath = outputPath + predicate.relation + ".csv";
            executeMongoimport(schemaPath, instancePath);
        }
    }

    /**
     * Execute mongoimport assuming command `mongoimport` exists
     */
    private static void executeMongoimport(String fieldFile, String file) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("mongoimport", "--type", "tsv", "--fieldFile", fieldFile, "--file", file);
        processBuilder.redirectOutput(Redirect.to(new File(IMPORT_STDOUT)));
        processBuilder.redirectError(Redirect.to(new File(IMPORT_STDERR)));
        try {
            Process process = processBuilder.start();
            int exitValue = process.waitFor();
            String outMsg = FileUtils.readFromFile(IMPORT_STDOUT);
            String errMsg = FileUtils.readFromFile(IMPORT_STDERR);
            String exceptionMsg = buildExceptionMessage(exitValue, outMsg, errMsg);
            if (exitValue != 0) throw new RuntimeException("[Import]" + exceptionMsg);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Cannot execute mongoimport on " + file);
        }
    }

    /**
     * Execute mongoimport assuming command `mongoexport` exists
     */
    public static void executeMongoexport(String relationName, String outputPath) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String outputFilepath = outputPath + relationName + ".json";
        processBuilder.command("mongoexport", "--db", "test", "--collection", relationName, "--out", outputFilepath, "--jsonArray");
        processBuilder.redirectOutput(Redirect.to(new File(EXPORT_STDOUT)));
        processBuilder.redirectError(Redirect.to(new File(EXPORT_STDERR)));
        try {
            Process process = processBuilder.start();
            int exitValue = process.waitFor();
            String outMsg = FileUtils.readFromFile(EXPORT_STDOUT);
            String errMsg = FileUtils.readFromFile(EXPORT_STDERR);
            String exceptionMsg = buildExceptionMessage(exitValue, outMsg, errMsg);
            if (exitValue != 0) throw new RuntimeException("[Export]" + exceptionMsg);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Cannot execute mongoexport");
        }
    }

    private static void emitSchemas(List<RelationDeclaration> outputDecls, String outputPath) {
        for (RelationDeclaration predicate : outputDecls) {
            String schemaPath = outputPath + predicate.relation + ".schema";
            StringBuilder schemaString = new StringBuilder();
            for (TypedAttribute e : predicate.attributes) {
                schemaString.append((e.attrName));
                schemaString.append("\n");
            }
            FileUtils.writeStringToFile(schemaPath, schemaString.toString());
        }
    }

    public static Set<String> computOutputDeclNames(DatalogProgram program) {
        return program.declarations.stream()
                .filter(s -> s instanceof OutputDeclaration)
                .map(s -> ((OutputDeclaration) s).relation)
                .collect(Collectors.toSet());
    }

    public static List<RelationDeclaration> computeRelationDecls(DatalogProgram program, Set<String> outputDeclNames) {
        return program.declarations.stream()
                .filter(statement -> statement instanceof RelationDeclaration)
                .filter(statement -> outputDeclNames.contains(((RelationDeclaration) statement).relation))
                .map(statement -> (RelationDeclaration) statement)
                .collect(Collectors.toList());
    }

    private static String buildExceptionMessage(int exitCode, String outMsg, String errMsg) {
        StringBuilder builder = new StringBuilder();
        builder.append("Exit value: ").append(exitCode).append("\n");
        builder.append("Stdout:\n").append(outMsg).append("\n");
        builder.append("Stderr:\n").append(errMsg);
        return builder.toString();
    }

}
