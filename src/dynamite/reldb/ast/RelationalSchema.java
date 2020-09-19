package dynamite.reldb.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.core.ISchema;
import dynamite.docdb.SchemaCanonicalNameVisitor;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSAtomicAttr.AttrType;
import dynamite.docdb.ast.DSAttribute;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.reldb.ast.RSColumn.ColumnType;

/**
 * Data structure for relational database schemas.
 */
public final class RelationalSchema implements ISchema {

    public final List<RSTable> tables;
    // set representation for equality comparison
    private final Set<RSTable> tableSet;

    public RelationalSchema(List<RSTable> tables) {
        this.tables = Collections.unmodifiableList(tables);
        this.tableSet = new HashSet<>(tables);
    }

    /**
     * Find the schema for a specific relation
     *
     * @param name of the relation
     * @return the RSTable for that relation, or null if it doesn't exist
     */
    public RSTable getSubSchema(String name) {
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).name.equals(name)) {
                return tables.get(i);
            }
        }
        return null;
    }

    @Override
    public DocumentSchema toDocumentSchema() {
        List<DSCollection> collections = new ArrayList<>();
        for (RSTable table : tables) {
            List<DSAttribute> attributes = new ArrayList<>();
            for (RSColumn column : table.columns) {
                attributes.add(new DSAtomicAttr(column.name, columnTypeToAttrType(column.type)));
            }
            collections.add(new DSCollection(table.name, new DSDocument(attributes)));
        }
        DocumentSchema schema = new DocumentSchema(collections);
        schema.accept(new SchemaCanonicalNameVisitor());
        return schema;
    }

    public <T> T accept(IRelSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public static AttrType columnTypeToAttrType(ColumnType columnType) {
        switch (columnType) {
        case INT:
            return AttrType.INT;
        case STRING:
            return AttrType.STRING;
        case FLOAT:
            return AttrType.FLOAT;
        default:
            throw new IllegalArgumentException("Unknown column type: " + columnType.name());
        }
    }

    @Override
    public String toString() {
        return tables.stream()
                .map(RSTable::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tableSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RelationalSchema)) return false;
        RelationalSchema o = (RelationalSchema) obj;
        return Objects.equals(tableSet, o.tableSet);
    }

}
