package dynamite.reldb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.core.IInstance;
import dynamite.datalog.ast.DatalogFact;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.reldb.RelFactGenVisitor;
import dynamite.reldb.RelInstToTextVisitor;
import dynamite.reldb.RelToDocInstVisitor;
import dynamite.reldb.RelValueCollectingVisitor;
import dynamite.util.SetMultiMap;

/**
 * Data structure for relational database instances.
 */
public final class RelationalInstance implements IInstance {

    public final List<RITable> tables;
    // set representation for equality comparison
    private final Set<RITable> tableSet;

    public RelationalInstance(List<RITable> tables) {
        this.tables = Collections.unmodifiableList(tables);
        this.tableSet = new HashSet<>(tables);
    }

    public RelationalSchema getSchema() {
        List<RSTable> schemaTables = tables.stream()
                .map(table -> table.tableSchema)
                .collect(Collectors.toList());
        return new RelationalSchema(schemaTables);
    }

    @Override
    public List<DatalogFact> toDatalogFacts() {
        return this.accept(new RelFactGenVisitor());
    }

    @Override
    public DocumentInstance toDocumentInstance() {
        return this.accept(new RelToDocInstVisitor());
    }

    @Override
    public SetMultiMap<String, Object> collectValuesByAttr() {
        return this.accept(new RelValueCollectingVisitor());
    }

    public <T> T accept(IRelInstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toInstanceString() {
        return this.accept(new RelInstToTextVisitor());
    }

    @Override
    public String toString() {
        return tables.stream()
                .map(RITable::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tableSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RelationalInstance)) return false;
        RelationalInstance o = (RelationalInstance) obj;
        return Objects.equals(tableSet, o.tableSet);
    }

}
