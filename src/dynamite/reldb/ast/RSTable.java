package dynamite.reldb.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RSTable {

    public final String name;
    public final List<RSColumn> columns;

    public RSTable(String name, List<RSColumn> columns) {
        this.name = name;
        this.columns = Collections.unmodifiableList(columns);
        setCanonicalColumnNames();
    }

    private void setCanonicalColumnNames() {
        for (RSColumn column : columns) {
            column.setCanonicalName(name + "?" + column.name);
        }
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", name, columns.stream()
                .map(RSColumn::toString)
                .collect(Collectors.joining("\n")));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, columns);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RSTable)) return false;
        RSTable o = (RSTable) obj;
        return Objects.equals(name, o.name) && Objects.equals(columns, o.columns);
    }

}
