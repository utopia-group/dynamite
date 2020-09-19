package dynamite.reldb;

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
import dynamite.util.SetMultiMap;

/**
 * A relational instance visitor that collects the value set for all columns.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class RelValueCollectingVisitor implements
        IRelInstVisitor<SetMultiMap<String, Object>>,
        IRIAstVisitor<SetMultiMap<String, Object>>,
        IRIValueVisitor<Object> {

    public static final String NULL_LITERAL = "null";
    // current table that the visitor is visiting
    private RITable currTable;

    @Override
    public Integer visit(RIIntValue value) {
        return Integer.valueOf(value.value);
    }

    @Override
    public String visit(RIStrValue value) {
        return value.value;
    }

    @Override
    public String visit(RIFloatValue value) {
        return value.valueString;
    }

    @Override
    public String visit(RINullValue value) {
        return NULL_LITERAL;
    }

    @Override
    public SetMultiMap<String, Object> visit(RIRow row) {
        SetMultiMap<String, Object> ret = new SetMultiMap<>();
        for (int i = 0; i < row.values.size(); ++i) {
            String columnName = currTable.tableSchema.columns.get(i).getCanonicalName();
            Object value = row.values.get(i).accept(this);
            ret.put(columnName, value);
        }
        return ret;
    }

    @Override
    public SetMultiMap<String, Object> visit(RITable table) {
        currTable = table;
        return table.rows.stream()
                .map(row -> row.accept(this))
                .reduce(new SetMultiMap<>(), SetMultiMap::unionSetMultiMaps);
    }

    @Override
    public SetMultiMap<String, Object> visit(RelationalInstance instance) {
        return instance.tables.stream()
                .map(table -> table.accept(this))
                .reduce(new SetMultiMap<>(), SetMultiMap::unionSetMultiMaps);
    }

}
