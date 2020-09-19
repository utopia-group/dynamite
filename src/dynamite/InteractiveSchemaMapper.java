package dynamite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import dynamite.core.IInstance;
import dynamite.core.ISchema;
import dynamite.core.InteractiveSynthesizer;
import dynamite.core.InteractiveSynthesizer.SynthStatus;
import dynamite.core.SchemaMapping;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.util.FileUtils;

public class InteractiveSchemaMapper {

    public static final Logger LOGGER = LoggerWrapper.getFileLogger("interaction.log");

    public static long RANDOM_SEED;

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: InteractiveSchemaMapper <src-schema-path> <tgt-schema-path> <src-instance-path> <random-seed>");
            System.exit(1);
        }
        String srcSchemaPath = args[0];
        String tgtSchemaPath = args[1];
        String srcInstancePath = args[2];
        RANDOM_SEED = Integer.parseInt(args[3]);

        long startTime = System.currentTimeMillis();
        LOGGER.info("=== Random Seed: " + RANDOM_SEED);
        LOGGER.info("=== Start: " + new Timestamp(startTime));
        ISchema srcSchema = SchemaMapper.parseSchema(srcSchemaPath);
        ISchema tgtSchema = SchemaMapper.parseSchema(tgtSchemaPath);
        IInstance srcInstance = SchemaMapper.parseExample(srcSchema, srcInstancePath);
        SchemaMapping schemaMapping = mapSchema(srcSchema, tgtSchema, srcInstance);
        String output = schemaMapping == null ? "Synthesis Failed" : schemaMapping.toString();
        System.out.println("Synthesis succeeded with the following program:");
        System.out.println(output);
        long endTime = System.currentTimeMillis();
        int ellapsedTime = (int) ((endTime - startTime) / 1000);
        System.out.println("Ellapsed Time: " + ellapsedTime + " s");
        LOGGER.info("=== End: " + new Timestamp(endTime));
        LOGGER.info("=== Ellapsed Time: " + ellapsedTime + " s");
        LOGGER.info("=== Result:\n" + output);
    }

    public static SchemaMapping mapSchema(ISchema srcSchema, ISchema tgtSchema, IInstance srcInst) {
        DocumentSchema tgtDocSchema = tgtSchema.toDocumentSchema();
        List<DatalogProgram> programs = new ArrayList<>();
        for (DSCollection tgtCollection : tgtDocSchema.collections) {
            DatalogProgram program = mapOneTarget(srcSchema, srcInst, tgtSchema, tgtCollection);
            if (program == null) {
                return null;
            }
            programs.add(program);
        }
        SchemaMapping schemaMapping = SchemaMapping.fromDatalogPrograms(programs);
        return schemaMapping;
    }

    public static DatalogProgram mapOneTarget(ISchema srcSchema, IInstance srcInst, ISchema tgtSchema, DSCollection tgtCollection) {
        InteractiveSynthesizer synthesizer = new InteractiveSynthesizer(srcSchema, srcInst, tgtCollection, RANDOM_SEED);
        IInstance srcExample = synthesizer.sampleInitialInstance();
        outputSourceExample(srcExample);
        IInstance tgtExample = readTargetExample(tgtSchema);
        synthesizer.loadTargetExample(tgtExample);
        SynthStatus status = synthesizer.synthesizeNext();
        while (status == SynthStatus.NEED_INPUT) {
            srcExample = synthesizer.getCurrSourceExample();
            outputSourceExample(srcExample);
            tgtExample = readTargetExample(tgtSchema);
            synthesizer.loadTargetExample(tgtExample);
            status = synthesizer.synthesizeNext();
        }
        // synthesis finished
        switch (status) {
        case FAIL:
            return null;
        case REACH_LIMIT:
            return synthesizer.getBestProgram();
        case SUCCEED:
            return synthesizer.getCurrentProgram();
        default:
            throw new RuntimeException("Unknown synthesizer status: " + status.name());
        }
    }

    private static int seqNumber = 0;

    private static void outputSourceExample(IInstance srcExample) {
        String extension = SchemaMapper.computeFileExtension(srcExample);
        String filename = "src-example-" + (++seqNumber) + "." + extension;
        String exampleString = srcExample.toInstanceString();
        String prettyString = (srcExample instanceof DocumentInstance || srcExample instanceof GraphInstance)
                ? prettifyJson(exampleString)
                : exampleString;
        FileUtils.writeStringToFile(filename, prettyString);
        System.out.println("Source example is stored in " + filename);
    }

    private static IInstance readTargetExample(ISchema tgtSchema) {
        IInstance tgtExample = null;
        while (tgtExample == null) {
            String examplePath = readFileNameFromConsole();
            tgtExample = parseTargetExample(tgtSchema, examplePath);
        }
        System.out.println("Target loaded. Now synthesizing the Datalog program ...");
        return tgtExample;
    }

    private static String readFileNameFromConsole() {
        System.out.print("Please enter the filename for target instance: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String name = reader.readLine();
            return name;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while reading the filename");
        }
    }

    private static IInstance parseTargetExample(ISchema tgtSchema, String examplePath) {
        try {
            return SchemaMapper.parseExample(tgtSchema, examplePath);
        } catch (Throwable e) {
            System.out.println("Cannot parse the instance in " + examplePath + ". Check and try again.");
            return null;
        }
    }

    private static String prettifyJson(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(jsonString);
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        return gson.toJson(json);
    }

}
