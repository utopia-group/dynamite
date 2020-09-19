package dynamite.exp;

import dynamite.core.IInstance;
import dynamite.core.ISchema;
import dynamite.docdb.RandomDocInstGenVisitor;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.graphdb.RandomGraphInstGenVisitor;
import dynamite.graphdb.ast.GraphInstance;
import dynamite.graphdb.ast.GraphSchema;
import dynamite.reldb.RandomRelInstGenVisitor;
import dynamite.reldb.ast.RelationalInstance;
import dynamite.reldb.ast.RelationalSchema;

public class RandomInstGenerator {

    /**
     * Generate a random instance given a schema and its integrity constraints.
     *
     * @param schema     the specified schema
     * @param constraint the specified integrity constraint
     * @param size       size of the instance to be generated
     * @param randomSeed random seed
     * @return a random instance
     */
    public static IInstance randomInstance(ISchema schema, IntegrityConstraint constraint, int size, long randomSeed) {
        if (schema instanceof RelationalSchema) {
            return genRelInst((RelationalSchema) schema, constraint, size, randomSeed);
        } else if (schema instanceof DocumentSchema) {
            return genDocInst((DocumentSchema) schema, constraint, size, randomSeed);
        } else if (schema instanceof GraphSchema) {
            return genGraphInst((GraphSchema) schema, constraint, size, randomSeed);
        } else {
            throw new RuntimeException("Unknown schema type");
        }
    }

    public static RelationalInstance genRelInst(RelationalSchema schema, IntegrityConstraint constraint, int size, long randomSeed) {
        RandomRelInstGenVisitor visitor = new RandomRelInstGenVisitor(size, constraint, randomSeed);
        return schema.accept(visitor);
    }

    public static DocumentInstance genDocInst(DocumentSchema schema, IntegrityConstraint constraint, int size, long randomSeed) {
        RandomDocInstGenVisitor visitor = new RandomDocInstGenVisitor(size, constraint, randomSeed);
        return schema.accept(visitor);
    }

    public static GraphInstance genGraphInst(GraphSchema schema, IntegrityConstraint constraint, int size, long randomSeed) {
        RandomGraphInstGenVisitor visitor = new RandomGraphInstGenVisitor(size, constraint, randomSeed);
        return schema.accept(visitor);
    }

}
