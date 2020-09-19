package dynamite.docdb.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.core.IInstance;
import dynamite.datalog.DatalogOutput;
import dynamite.datalog.SouffleEvaluator;
import dynamite.datalog.UniverseGenVisitor;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.TypeDeclaration;
import dynamite.docdb.DocInstToJsonVisitor;
import dynamite.docdb.FactGenVisitor;
import dynamite.docdb.OutputDeclGenVisitor;
import dynamite.docdb.RelationDeclGenVisitor;
import dynamite.docdb.SchemaCanonicalNameVisitor;
import dynamite.docdb.SchemaExtractionVisitor;
import dynamite.docdb.ValueCollectingVisitor;
import dynamite.util.SetMultiMap;

/**
 * Data structure for document database instances.
 */
public final class DocumentInstance implements IInstance {

    public final List<DICollection> collections;
    // set representation for equality comparison
    private final Set<DICollection> collectionSet;
    // collection name -> collection
    private final Map<String, DICollection> nameToCollectionMap;

    /**
     * The corresponding document schema of this instance.
     * Lazily instantiated in {@code getSchema}.
     */
    private DocumentSchema schema = null;

    public DocumentInstance(final List<DICollection> collections) {
        Objects.requireNonNull(collections, "Collections cannot be null");
        this.collections = Collections.unmodifiableList(collections);
        this.collectionSet = new HashSet<>(collections);
        this.nameToCollectionMap = buildNameToCollectionMap(collections);
    }

    /**
     * Infer the schema of this document instance.
     *
     * @return the inferred schema
     */
    public DocumentSchema getSchema() {
        if (schema == null) {
            SchemaExtractionVisitor visitor = new SchemaExtractionVisitor();
            schema = this.accept(visitor);
            schema.accept(new SchemaCanonicalNameVisitor());
        }
        return schema;
    }

    @Override
    public List<DatalogFact> toDatalogFacts() {
        return this.accept(new FactGenVisitor());
    }

    @Override
    public DocumentInstance toDocumentInstance() {
        return this;
    }

    public DatalogOutput toDatalogOutput() {
        DatalogProgram program = buildDatalogProgramWithNoRules();
        DatalogProgram progWithUniv = program.accept(new UniverseGenVisitor());
        return new SouffleEvaluator().evaluate(progWithUniv);
    }

    public DatalogOutput toDatalogOutputWithoutUniverse() {
        DatalogProgram program = buildDatalogProgramWithNoRules();
        return new SouffleEvaluator().evaluate(program);
    }

    private DatalogProgram buildDatalogProgramWithNoRules() {
        DocumentSchema schema = getSchema();
        List<DatalogStatement> decls = new ArrayList<>();
        decls.add(TypeDeclaration.intAttrTypeDecl());
        decls.add(TypeDeclaration.strAttrTypeDecl());
        decls.add(TypeDeclaration.relTypeDecl());
        decls.addAll(schema.accept(new RelationDeclGenVisitor()));
        decls.addAll(schema.accept(new OutputDeclGenVisitor()));
        List<DatalogRule> rules = Collections.emptyList();
        List<DatalogFact> facts = toDatalogFacts();
        return new DatalogProgram(decls, rules, facts);
    }

    @Override
    public SetMultiMap<String, Object> collectValuesByAttr() {
        return this.accept(new ValueCollectingVisitor());
    }

    public DICollection findTopLevelCollectionByName(String name) {
        if (!nameToCollectionMap.containsKey(name)) {
            throw new IllegalArgumentException("Cannot find collection: " + name);
        }
        return nameToCollectionMap.get(name);
    }

    public <T> T accept(IDocInstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toInstanceString() {
        return this.accept(new DocInstToJsonVisitor());
    }

    @Override
    public String toString() {
        return String.format("DocInst(\n%s)", collections.stream()
                .map(DICollection::toString)
                .collect(Collectors.joining("\n")));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(collectionSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DocumentInstance)) return false;
        DocumentInstance o = (DocumentInstance) obj;
        return Objects.equals(collectionSet, o.collectionSet);
    }

    private static Map<String, DICollection> buildNameToCollectionMap(List<DICollection> collections) {
        return collections.stream().collect(Collectors.toMap(c -> c.name, c -> c));
    }

}
