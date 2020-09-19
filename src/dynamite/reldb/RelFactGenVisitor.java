package dynamite.reldb;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.StringLiteral;
import dynamite.reldb.ast.IRIAstVisitor;
import dynamite.reldb.ast.IRIValueVisitor;
import dynamite.reldb.ast.IRelInstVisitor;
import dynamite.reldb.ast.RIFloatValue;
import dynamite.reldb.ast.RIIntValue;
import dynamite.reldb.ast.RINullValue;
import dynamite.reldb.ast.RIRow;
import dynamite.reldb.ast.RIStrValue;
import dynamite.reldb.ast.RITable;
import dynamite.reldb.ast.RelationalInstance;

/**
 * A relational instance visitor that generates all Datalog facts.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class RelFactGenVisitor implements
        IRelInstVisitor<List<DatalogFact>>,
        IRIAstVisitor<List<DatalogFact>>,
        IRIValueVisitor<StringLiteral> {

    public static final String NULL_LITERAL = "null";
    // current table that the visitor is visiting
    private RITable currTable;

    @Override
    public StringLiteral visit(RIIntValue value) {
        return new StringLiteral(String.valueOf(value.value));
    }

    @Override
    public StringLiteral visit(RIStrValue value) {
        return new StringLiteral(value.value);
    }

    @Override
    public StringLiteral visit(RIFloatValue value) {
        return new StringLiteral(value.valueString);
    }

    @Override
    public StringLiteral visit(RINullValue value) {
        return new StringLiteral(NULL_LITERAL);
    }

    @Override
    public List<DatalogFact> visit(RIRow row) {
        String tableName = currTable.getName();
        List<DatalogExpression> arguments = row.values.stream()
                .map(value -> value.accept(this))
                .collect(Collectors.toList());
        return Collections.singletonList(new DatalogFact(new RelationPredicate(tableName, arguments)));
    }

    @Override
    public List<DatalogFact> visit(RITable table) {
        currTable = table;
        return table.rows.stream()
                .flatMap(row -> row.accept(this).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<DatalogFact> visit(RelationalInstance instance) {
        return instance.tables.stream()
                .flatMap(table -> table.accept(this).stream())
                .collect(Collectors.toList());
    }

}
