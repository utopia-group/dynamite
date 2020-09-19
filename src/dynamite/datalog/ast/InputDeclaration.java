package dynamite.datalog.ast;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class InputDeclaration extends DatalogStatement {

    public final String relation;
    public final Map<String, String> parameterMap;

    public InputDeclaration(String relation) {
        this.relation = relation;
        this.parameterMap = Collections.emptyMap();
    }

    public InputDeclaration(String relation, final Map<String, String> parameterMap) {
        Objects.requireNonNull(parameterMap);
        this.relation = relation;
        this.parameterMap = Collections.unmodifiableMap(parameterMap);
    }

    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        if (parameterMap.isEmpty()) return ".input " + relation;
        return String.format(".input %s(%s)", relation,
                parameterMap.keySet().stream()
                        .sorted()
                        .map(key -> String.format("%s=%s", key, parameterMap.get(key)))
                        .collect(Collectors.joining(", ")));
    }

    @Override
    public String toString() {
        return String.format("InputDecl(%s, %s)", relation, parameterMap);
    }

}
