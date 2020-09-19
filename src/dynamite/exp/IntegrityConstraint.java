package dynamite.exp;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class IntegrityConstraint {

    public final List<ForeignKey> foreignKeys;
    public final List<CompoundFK> compoundFKs;
    public final List<Selection> selections;
    private final Map<String, Selection> attrToSelectionMap;
    public final List<Equality> equalities;

    public IntegrityConstraint(final List<ForeignKey> foreignKeys) {
        this(foreignKeys, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public IntegrityConstraint(
            final List<ForeignKey> foreignKeys,
            final List<CompoundFK> compoundFKs,
            final List<Selection> selections,
            final List<Equality> equalities) {
        Objects.requireNonNull(foreignKeys);
        Objects.requireNonNull(compoundFKs);
        Objects.requireNonNull(selections);
        Objects.requireNonNull(equalities);
        this.foreignKeys = Collections.unmodifiableList(foreignKeys);
        this.compoundFKs = Collections.unmodifiableList(compoundFKs);
        this.selections = Collections.unmodifiableList(selections);
        this.attrToSelectionMap = buildAttrToSelectionMap(selections);
        this.equalities = Collections.unmodifiableList(equalities);
    }

    public boolean isSelectionAttr(String attribute) {
        return attrToSelectionMap.containsKey(attribute);
    }

    public Selection getSelectionByAttr(String attribute) {
        if (!attrToSelectionMap.containsKey(attribute)) {
            throw new IllegalArgumentException(attribute);
        }
        return attrToSelectionMap.get(attribute);
    }

    private static Map<String, Selection> buildAttrToSelectionMap(List<Selection> selections) {
        return selections.stream()
                .collect(Collectors.toMap(sel -> sel.attribute, Function.identity()));
    }

    @Override
    public String toString() {
        return Stream.concat(Stream.concat(foreignKeys.stream(), selections.stream()), equalities.stream())
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

}
