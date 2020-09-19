package dynamite.reldb;

import java.util.stream.Collectors;

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
 * A visitor that converts a relational instance to text.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class RelInstToTextVisitor implements
        IRelInstVisitor<String>,
        IRIAstVisitor<String>,
        IRIValueVisitor<String> {

    @Override
    public String visit(RIIntValue value) {
        return value.toString();
    }

    @Override
    public String visit(RIStrValue value) {
        return value.toString();
    }

    @Override
    public String visit(RIFloatValue value) {
        return value.toString();
    }

    @Override
    public String visit(RINullValue value) {
        return value.toString();
    }

    @Override
    public String visit(RITable table) {
        StringBuilder builder = new StringBuilder();
        builder.append(table.tableSchema.toString().replaceAll("\n", ", "));
        builder.append("\n");
        builder.append(table.rows.stream()
                .map(row -> row.accept(this))
                .collect(Collectors.joining("\n")));
        return builder.toString();
    }

    @Override
    public String visit(RIRow row) {
        return row.values.stream()
                .map(value -> value.accept(this))
                .collect(Collectors.joining(", "));
    }

    @Override
    public String visit(RelationalInstance instance) {
        return instance.tables.stream()
                .map(table -> table.accept(this))
                .collect(Collectors.joining("\n"));
    }

}
