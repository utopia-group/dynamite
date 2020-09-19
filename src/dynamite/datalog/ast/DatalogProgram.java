package dynamite.datalog.ast;

import java.util.List;
import java.util.Objects;

public final class DatalogProgram extends DatalogAstNode {

    public final List<DatalogStatement> declarations;
    public final List<DatalogRule> rules;
    public final List<DatalogFact> facts;

    public DatalogProgram(
            final List<DatalogStatement> declarations,
            final List<DatalogRule> rules,
            final List<DatalogFact> facts) {
        Objects.requireNonNull(declarations, "Declarations cannot be null");
        Objects.requireNonNull(rules, "Rules cannot be null");
        Objects.requireNonNull(facts, "Facts cannot be null");
        this.declarations = declarations;
        this.rules = rules;
        this.facts = facts;
    }

    public <T> T accept(IProgramVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        StringBuilder builder = new StringBuilder();
        for (DatalogStatement decl : declarations) {
            builder.append(decl.toSouffle()).append("\n");
        }
        builder.append("\n");
        for (DatalogRule rule : rules) {
            builder.append(rule.toSouffle()).append("\n");
        }
        builder.append("\n");
        for (DatalogFact fact : facts) {
            builder.append(fact.toSouffle()).append("\n");
        }
        return builder.toString();
    }

}
