package dynamite;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;

import dynamite.core.IInstance;
import dynamite.core.ISchema;
import dynamite.core.SchemaMapping;
import dynamite.datalog.AggressiveSimplVisitor;
import dynamite.datalog.InputDeclGenVisitor;
import dynamite.datalog.SouffleEvaluator;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.graphdb.ast.GraphSchema;
import dynamite.reldb.ast.RSTable;
import dynamite.reldb.ast.RelationalSchema;
import dynamite.trans.GraphPostprocessor;
import dynamite.trans.GraphToInputFactsTransformer;
import dynamite.trans.JsonPostprocessor;
import dynamite.trans.JsonToInputFactsTransformer;
import dynamite.trans.RelToInputFactsTransformer;
import dynamite.trans.XmlToInputFactsTransformer;
import dynamite.util.FileUtils;

public class DataMigration {

    public static void main(String[] args) {
        if (args.length != 7) {
            String errMsg = ""
                    + "Usage: DataMigration <src-schema-path> <tgt-schema-path> <src-example-path> "
                    + "<tgt-examle-path> <src-instance-path> <src-instance-schema-path> <output-path>";
            System.out.println(errMsg);
            System.exit(1);
        }
        String srcSchemaPath = args[0];
        String tgtSchemaPath = args[1];
        String srcExamplePath = args[2];
        String tgtExamplePath = args[3];
        String srcInstancePath = args[4];
        String srcInstanceSchemaPath = args[5];
        String outputPath = args[6];
        ISchema srcSchema = SchemaMapper.parseSchema(srcSchemaPath);
        ISchema tgtSchema = SchemaMapper.parseSchema(tgtSchemaPath);
        IInstance srcExample = SchemaMapper.parseExample(srcSchema, srcExamplePath);
        IInstance tgtExample = SchemaMapper.parseExample(tgtSchema, tgtExamplePath);
        SchemaMapping schemaMapping = SchemaMapper.mapSchema(srcSchema, tgtSchema, srcExample, tgtExample);
        if (schemaMapping == null) {
            System.out.println("Cannot synthesize a schema mapping");
            System.exit(2);
        }
        System.out.println("Schema Mapping:\n" + schemaMapping.toString());
        dataMigration(schemaMapping, tgtSchema, srcInstancePath, srcInstanceSchemaPath, outputPath);
        System.out.println("New instance is written to: " + outputPath);
    }

    public static void generateFacts(String srcInstPath, String srcInstSchemaPath, String tmpPath) {
        String extension = FileUtils.getFileExtension(srcInstPath);
        try (Reader reader = new BufferedReader(new FileReader(srcInstPath))) {
            ISchema srcInstSchema = SchemaMapper.parseSchema(srcInstSchemaPath);
            switch (extension) {
            case SchemaMapper.REL_EXTENSION:
                if (!(srcInstSchema instanceof RelationalSchema)) {
                    throw new IllegalArgumentException("Expected Relational Schema");
                }
                RelationalSchema rSchema = (RelationalSchema) srcInstSchema;
                RSTable table = rSchema.getSubSchema(FileUtils.getRelationName(srcInstPath));
                if (table == null) {
                    throw new RuntimeException("Mismatch between filename (" + srcInstPath + ") and the schema relation names");
                }
                RelToInputFactsTransformer.parseToFactsCsv(table, reader, tmpPath);
                break;
            case SchemaMapper.JSON_EXTENSION:
                if (!(srcInstSchema instanceof DocumentSchema)) {
                    throw new IllegalArgumentException("Expected Document Schema");
                }
                JsonToInputFactsTransformer.parseToFactsCsv((DocumentSchema) srcInstSchema, reader, tmpPath);
                break;
            case SchemaMapper.XML_EXTENSION:
                if (!(srcInstSchema instanceof DocumentSchema)) {
                    throw new IllegalArgumentException("Expected Document Schema");
                }
                XmlToInputFactsTransformer.parseToFactsCsv((DocumentSchema) srcInstSchema, reader, tmpPath);
                break;
            case SchemaMapper.GRAPH_EXTENSION:
                if (!(srcInstSchema instanceof GraphSchema)) {
                    throw new UnsupportedOperationException("Expected Graph Schema");
                }
                GraphToInputFactsTransformer.parseToFactsCsv((GraphSchema) srcInstSchema, reader, tmpPath);
                break;
            default:
                throw new IllegalArgumentException("Cannot infer instance type by extension: " + srcInstPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException for " + srcInstPath);
        }
    }

    public static void evaluateProgram(SchemaMapping schemaMapping, String outputPath, String tmpPath) {
        DatalogProgram instanceProgram = schemaMapping.program
                .accept(new AggressiveSimplVisitor())
                .accept(new InputDeclGenVisitor(tmpPath));
        System.out.println("Evaluating Souffle program on fact files");
        System.out.println(instanceProgram.toSouffle());
        new SouffleEvaluator().evaluateToFolder(instanceProgram, Paths.get(outputPath));
    }

    public static void postprocessProgramOutput(ISchema tgtSchema, SchemaMapping schemaMapping, String outputPath) {
        if (tgtSchema instanceof DocumentSchema) {
            JsonPostprocessor.emitJson(schemaMapping.program, outputPath);
        } else if (tgtSchema instanceof GraphSchema) {
            GraphPostprocessor.emitGraph((GraphSchema) tgtSchema, schemaMapping.program, outputPath);
        }
    }

    public static void dataMigration(SchemaMapping schemaMapping, ISchema tgtSchema, String srcInstPath, String srcInstSchemaPath, String outputPath) {
        String tmpPath = "tmp/Souffle_input/";
        generateFacts(srcInstPath, srcInstSchemaPath, tmpPath);
        evaluateProgram(schemaMapping, outputPath, tmpPath);
        postprocessProgramOutput(tgtSchema, schemaMapping, outputPath);
    }
}
