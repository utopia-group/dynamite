package dynamite.datalog.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class ASTVisitorTest implements
        IProgramVisitor<String>, IStatementVisitor<String>,
        IPredicateVisitor<String>, IExpressionVisitor<String> {

    @Override
    public String visit(Identifier identifier) {
        return identifier.name;
    }

    @Override
    public String visit(IntLiteral literal) {
        return String.valueOf(literal.value);
    }

    @Override
    public String visit(StringLiteral literal) {
        return String.format("\"%s\"", literal.value);
    }

    @Override
    public String visit(ExpressionHole hole) {
        return String.format("?{%s}", hole.domain.stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.joining(", ")));
    }

    @Override
    public String visit(ConcatFunction concatFunc) {
        return String.format("cat(%s, %s)", concatFunc.lhs.accept(this), concatFunc.rhs.accept(this));
    }

    @Override
    public String visit(PlaceHolder placeHolder) {
        return "_";
    }

    @Override
    public String visit(RelationPredicate predicate) {
        return String.format("%s(%s)", predicate.relation, predicate.arguments.stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.joining(", ")));
    }

    @Override
    public String visit(BinaryPredicate predicate) {
        return String.format("%s %s %s",
                predicate.lhs.accept(this),
                BinaryPredicate.operatorToString(predicate.op),
                predicate.rhs.accept(this));
    }

    @Override
    public String visit(TypeDeclaration declaration) {
        return String.format(".type %s", declaration.typeName);
    }

    @Override
    public String visit(InputDeclaration declaration) {
        return String.format(".input %s", declaration.relation);
    }

    @Override
    public String visit(OutputDeclaration declaration) {
        return String.format(".output %s", declaration.relation);
    }

    @Override
    public String visit(RelationDeclaration declaration) {
        return String.format(".decl %s(%s)", declaration.relation, declaration.attributes.stream()
                .map(TypedAttribute::toSouffle) // typed attribute does not have any visitor
                .collect(Collectors.joining(", ")));
    }

    @Override
    public String visit(DatalogRule rule) {
        return String.format("%s :-%s.",
                rule.heads.stream().map(pred -> pred.accept(this)).collect(Collectors.joining(", ")),
                rule.bodies.stream().map(pred -> "\n    " + pred.accept(this)).collect(Collectors.joining(",")));
    }

    @Override
    public String visit(DatalogFact fact) {
        return String.format("%s.", fact.relationPred.accept(this));
    }

    @Override
    public String visit(DatalogProgram program) {
        StringBuilder builder = new StringBuilder();
        for (DatalogStatement decl : program.declarations) {
            builder.append(decl.accept(this)).append("\n");
        }
        builder.append("\n");
        for (DatalogRule rule : program.rules) {
            builder.append(rule.accept(this)).append("\n");
        }
        builder.append("\n");
        for (DatalogFact fact : program.facts) {
            builder.append(fact.accept(this)).append("\n");
        }
        return builder.toString();
    }

    @Test
    public void test() {
        List<DatalogStatement> decls = new ArrayList<>();
        {
            decls.add(new TypeDeclaration("Attr"));
            decls.add(new RelationDeclaration("A", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("x", "number"), new TypedAttribute("y", "Attr"), new TypedAttribute("z", "Attr")
            })));
            decls.add(new RelationDeclaration("B", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("x", "number"), new TypedAttribute("y", "Attr")
            })));
            decls.add(new OutputDeclaration("B"));
            decls.add(new RelationDeclaration("C", Arrays.asList(new TypedAttribute[] {
                    new TypedAttribute("y", "Attr"), new TypedAttribute("z", "Attr")
            })));
            decls.add(new OutputDeclaration("C"));
        }
        List<DatalogRule> rules = new ArrayList<>();
        {
            List<RelationPredicate> heads = Collections.singletonList(
                    new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                            new Identifier("x"), new Identifier("y"), new Identifier("z")
                    })));
            List<DatalogPredicate> bodies = new ArrayList<>();
            {
                bodies.add(new RelationPredicate("B", Arrays.asList(new DatalogExpression[] {
                        new Identifier("x"), new Identifier("y")
                })));
                bodies.add(new RelationPredicate("C", Arrays.asList(new DatalogExpression[] {
                        new Identifier("y"), new Identifier("z")
                })));
            }
            rules.add(new DatalogRule(heads, bodies));
        }
        List<DatalogFact> facts = new ArrayList<>();
        {
            facts.add(new DatalogFact(
                    new RelationPredicate("A", Arrays.asList(new DatalogExpression[] {
                            new IntLiteral(99), new StringLiteral("name"), new StringLiteral("addr")
                    }))));
        }
        DatalogProgram program = new DatalogProgram(decls, rules, facts);
        ASTVisitorTest souffleDumpVisitor = new ASTVisitorTest();
        String souffleText = program.accept(souffleDumpVisitor);
        Assert.assertEquals(program.toSouffle(), souffleText);
    }

}
