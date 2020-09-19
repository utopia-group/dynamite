package dynamite.reldb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RITable {

    public final RSTable tableSchema;
    public final List<RIRow> rows;
    // set representation for equality comparison
    private final Set<RIRow> rowSet;

    public RITable(RSTable tableSchema, List<RIRow> rows) {
        this.tableSchema = tableSchema;
        this.rows = Collections.unmodifiableList(rows);
        this.rowSet = new HashSet<>(rows);
    }

    public String getName() {
        return tableSchema.name;
    }

    public int getSize() {
        return rows.size();
    }

    public <T> T accept(IRIAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        Stream<String> stream = Stream.of(getName());
        return Stream.concat(stream, rows.stream().map(RIRow::toString))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableSchema, rowSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RITable)) return false;
        RITable o = (RITable) obj;
        return Objects.equals(tableSchema, o.tableSchema) && Objects.equals(rowSet, o.rowSet);
    }

}
