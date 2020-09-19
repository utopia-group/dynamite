package dynamite.reldb;

import java.util.ArrayList;
import java.util.List;

import dynamite.reldb.ast.RSColumn;
import dynamite.reldb.ast.RSColumn.ColumnType;
import dynamite.reldb.ast.RSTable;
import dynamite.reldb.ast.RelationalSchema;

public final class RelSchemaParser {

    /**
     * Parse a schema string to a {@link #RelationalSchema} data structure.
     *
     * @param schemaString the schema string
     * @return the relational schema data structure
     */
    public static RelationalSchema parse(String schemaString) {
        String[] tableStrs = schemaString.split("\\R+"); // split by new-lines
        List<RSTable> tables = new ArrayList<>();
        for (String tableStr : tableStrs) {
            tables.add(parseTable(tableStr));
        }
        return new RelationalSchema(tables);
    }

    static RSTable parseTable(String tableString) {
        String[] tokens = tableString.split("\\(|\\)");
        assert tokens.length == 2 : tableString;
        String tableName = tokens[0].trim();
        String columnStr = tokens[1];
        String[] columnTokens = columnStr.split("\\s*,\\s*");
        List<RSColumn> columns = new ArrayList<>();
        for (String columnToken : columnTokens) {
            columns.add(parseColumn(columnToken));
        }
        return new RSTable(tableName, columns);
    }

    private static RSColumn parseColumn(String columnString) {
        String[] tokens = columnString.split("\\s*:\\s*");
        assert tokens.length == 2 : columnString;
        String columnName = tokens[0].trim();
        ColumnType columnType = RSColumn.stringToColumnType(tokens[1]);
        return new RSColumn(columnName, columnType);
    }

}
