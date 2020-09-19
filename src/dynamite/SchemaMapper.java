package dynamite;

import dynamite.core.EnumSynthesizer;
import dynamite.core.IInstance;
import dynamite.core.ISchema;
import dynamite.core.ISynthesizer;
import dynamite.core.InstanceExample;
import dynamite.core.MDPSynthesizer;
import dynamite.core.SchemaMapping;
import dynamite.datalog.SimplificationVisitor;
import dynamite.docdb.DocJsonInstanceParser;
import dynamite.docdb.DocSchemaParser;
import dynamite.docdb.DocXmlInstanceParser;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.graphdb.GraphInstanceParser;
import dynamite.graphdb.GraphSchemaParser;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.GraphSchema;
import dynamite.reldb.RelInstanceParser;
import dynamite.reldb.RelSchemaParser;
import dynamite.reldb.ast.RelationalInstance;
import dynamite.util.FileUtils;

public class SchemaMapper {

    // file extension for relational schemas and examples
    public static final String REL_EXTENSION = "txt";
    // file extension for json schemas and examples
    public static final String JSON_EXTENSION = "json";
    // file extension for xml schemas and examples
    public static final String XML_EXTENSION = "xml";
    // file extension for graph schemas and examples
    public static final String GRAPH_EXTENSION = "graph";

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: SchemaMapper <src-schema-path> <tgt-schema-path> <src-example-path> <tgt-example-path>");
            System.exit(1);
        }
        String srcSchemaPath = args[0];
        String tgtSchemaPath = args[1];
        String srcExamplePath = args[2];
        String tgtExamplePath = args[3];
        ISchema srcSchema = parseSchema(srcSchemaPath);
        ISchema tgtSchema = parseSchema(tgtSchemaPath);
        IInstance srcExample = parseExample(srcSchema, srcExamplePath);
        IInstance tgtExample = parseExample(tgtSchema, tgtExamplePath);
        SchemaMapping schemaMapping = mapSchema(srcSchema, tgtSchema, srcExample, tgtExample);
        String output = schemaMapping == null ? "Synthesis Failed" : schemaMapping.toString();
        System.out.println(output);
    }

    public static SchemaMapping mapSchema(ISchema srcSchema, ISchema tgtSchema, IInstance srcExample, IInstance tgtExample) {
        InstanceExample example = new InstanceExample(srcExample, tgtExample);
        ISynthesizer synthesizer = Config.MDP_SYNTH ? new MDPSynthesizer() : new EnumSynthesizer();
        SchemaMapping schemaMapping = synthesizer.synthesize(srcSchema, tgtSchema, example);
        if (schemaMapping == null) return null;
        // simplify the Datalog program
        SchemaMapping simplified = new SchemaMapping(schemaMapping.program.accept(new SimplificationVisitor()));
        return simplified;
    }

    static ISchema parseSchema(String schemaPath) {
        String schemaText = FileUtils.readFromFile(schemaPath);
        String extension = FileUtils.getFileExtension(schemaPath);
        if (extension.equals(REL_EXTENSION)) {
            return RelSchemaParser.parse(schemaText);
        } else if (extension.equals(JSON_EXTENSION) || extension.equals(XML_EXTENSION)) {
            return DocSchemaParser.parse(schemaText);
        } else if (extension.equals(GRAPH_EXTENSION)) {
            return GraphSchemaParser.parse(schemaText);
        } else {
            throw new IllegalArgumentException("Cannot infer schema type by extension: " + schemaPath);
        }
    }

    static IInstance parseExample(ISchema schema, String examplePath) {
        String exampleText = FileUtils.readFromFile(examplePath);
        String extension = FileUtils.getFileExtension(examplePath);
        if (extension.equals(REL_EXTENSION)) {
            return RelInstanceParser.parse(exampleText);
        } else if (extension.equals(JSON_EXTENSION)) {
            if (!(schema instanceof DocumentSchema)) {
                throw new IllegalArgumentException("Must provide a document schema");
            }
            return DocJsonInstanceParser.parse(exampleText, (DocumentSchema) schema);
        } else if (extension.equals(XML_EXTENSION)) {
            if (!(schema instanceof DocumentSchema)) {
                throw new IllegalArgumentException("Must provide a document schema");
            }
            DocXmlInstanceParser parser = new DocXmlInstanceParser((DocumentSchema) schema);
            return parser.parse(exampleText);
        } else if (extension.equals(GRAPH_EXTENSION)) {
            if (!(schema instanceof GraphSchema)) {
                throw new IllegalArgumentException("Must provide a graph schema");
            }
            return GraphInstanceParser.parse(exampleText, (GraphSchema) schema);
        } else {
            throw new IllegalArgumentException("Cannot infer example type by extension: " + examplePath);
        }
    }

    static String computeFileExtension(IInstance instance) {
        if (instance instanceof RelationalInstance) {
            return REL_EXTENSION;
        } else if (instance instanceof DocumentInstance) {
            // NOTE: currently does not handle XML here
            return JSON_EXTENSION;
        } else if (instance instanceof GraphInstance) {
            return GRAPH_EXTENSION;
        } else {
            throw new IllegalArgumentException("Cannot recognize the instance type");
        }
    }

}
