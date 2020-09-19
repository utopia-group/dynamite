package dynamite.datalog;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data structure for Datalog evaluation results.
 */
public final class DatalogOutput {

    /**
     * Name of the universe relation for Datalog outputs.
     */
    public static final String UNIVERSE_NAME = "__Universe";

    public final List<RelationOutput> relationOutputs;
    // relation name -> relation output
    private final Map<String, RelationOutput> relNameToOutput;
    // set representation for equality check
    private final Set<RelationOutput> relationOutputSet;

    public DatalogOutput(final List<RelationOutput> relationOutputs) {
        Objects.requireNonNull(relationOutputs, "Relation outputs cannot be null");
        this.relationOutputs = Collections.unmodifiableList(relationOutputs);
        this.relNameToOutput = buildRelNameToOutputMap(relationOutputs);
        this.relationOutputSet = new HashSet<>(relationOutputs);
    }

    public RelationOutput getRelationOutputByName(String name) {
        if (!relNameToOutput.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Cannot find relation %s", name));
        }
        return relNameToOutput.get(name);
    }

    public RelationOutput getUnveriseOutput() {
        return getRelationOutputByName(UNIVERSE_NAME);
    }

    public DatalogOutput getOutputWithoutUniverse() {
        List<RelationOutput> outputsWithoutUniverse = relationOutputs.stream()
                .filter(relOutput -> !relOutput.relation.equals(UNIVERSE_NAME))
                .collect(Collectors.toList());
        return new DatalogOutput(outputsWithoutUniverse);
    }

    private static Map<String, RelationOutput> buildRelNameToOutputMap(List<RelationOutput> outputs) {
        Map<String, RelationOutput> map = new HashMap<>();
        outputs.forEach(output -> map.put(output.relation, output));
        return map;
    }

    @Override
    public String toString() {
        return relationOutputs.stream()
                .map(RelationOutput::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(relationOutputSet);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DatalogOutput)) return false;
        DatalogOutput o = (DatalogOutput) obj;
        return Objects.equals(relationOutputSet, o.relationOutputSet);
    }
}
