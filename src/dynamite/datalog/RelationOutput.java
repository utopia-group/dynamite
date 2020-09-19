package dynamite.datalog;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Data structure for relation produced by datalog evaluation.
 */
public final class RelationOutput {

    public final String relation;
    public final List<List<String>> data;
    // set representation of the data for equality check
    private final Set<List<String>> dataSet;

    public RelationOutput(String relation, final List<List<String>> data) {
        Objects.requireNonNull(data, String.format("Data of %s is null", relation));
        this.relation = relation;
        this.data = Collections.unmodifiableList(data);
        this.dataSet = new HashSet<>(data);
    }

    public RelationOutput project(List<Integer> colIndices) {
        Objects.requireNonNull(colIndices);
        if (colIndices.isEmpty()) {
            throw new IllegalArgumentException("Empty column indices");
        }
        String projRelation = relation + colIndices.toString();
        List<List<String>> projData = data.stream()
                .map(row -> colIndices.stream()
                        .flatMap(colIndex -> Stream.of(row.get(colIndex)))
                        .collect(Collectors.toList()))
                .distinct() // not necessary because the equality check is based on dataSet
                .collect(Collectors.toList());
        return new RelationOutput(projRelation, projData);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(relation).append(":\n");
        data.forEach(row -> builder.append(row).append("\n"));
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(relation, dataSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RelationOutput)) return false;
        RelationOutput o = (RelationOutput) obj;
        return Objects.equals(relation, o.relation) && Objects.equals(dataSet, o.dataSet);
    }

}
