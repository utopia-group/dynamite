package dynamite.reldb;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import dynamite.reldb.ast.RIFloatValue;
import dynamite.reldb.ast.RIIntValue;
import dynamite.reldb.ast.RINullValue;
import dynamite.reldb.ast.RIRow;
import dynamite.reldb.ast.RIStrValue;
import dynamite.reldb.ast.RITable;
import dynamite.reldb.ast.RIValue;
import dynamite.reldb.ast.RSColumn;
import dynamite.reldb.ast.RSTable;
import dynamite.reldb.ast.RelationalInstance;
import dynamite.util.CSVHelper;

public final class RelInstanceParser {

    /**
     * Delimiter for values in a row.
     */
    public static final char VALUE_DELIMITER = ',';
    /**
     * Null literal
     */
    public static final String NULL_LITERAL = "null";

    /**
     * Parse an instance string to RelationalInstance, assuming the first line
     * is the schema string and other lines are data in the relation.
     *
     * @param instString the instance string
     * @return the relational instance
     */
    public static RelationalInstance parse(String instString) {
        String[] lines = instString.split("\\R+"); // split by new-lines
        assert lines.length > 1;
        List<RITable> tables = new ArrayList<>();
        List<String> tableLines = new ArrayList<>();
        tableLines.add(lines[0]); // add the first table schema string
        for (int i = 1; i < lines.length; ++i) {
            if (isSchemaString(lines[i])) {
                tables.add(parseTable(tableLines));
                tableLines = new ArrayList<>();
            }
            tableLines.add(lines[i]);
        }
        tables.add(parseTable(tableLines));
        return new RelationalInstance(tables);
    }

    private static RITable parseTable(List<String> lines) {
        String tableString = lines.get(0);
        RSTable tableSchema = RelSchemaParser.parseTable(tableString);
        List<RIRow> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); ++i) {
            rows.add(parseRow(tableSchema, lines.get(i)));
        }
        return new RITable(tableSchema, rows);
    }

    private static RIRow parseRow(RSTable tableSchema, String rowString) {
        rowString = rowString.trim();
        return parseRow(tableSchema, new StringReader(rowString));
    }

    public static RIRow parseRow(RSTable tableSchema, Reader r) {
        try {
            List<String> valueStrs = CSVHelper.parseLine(r);
            if (valueStrs == null) { return null; } // this means we are done with the file
            assert valueStrs.size() == tableSchema.columns.size();
            // parse value strings
            List<RIValue> values = new ArrayList<>();
            for (int i = 0; i < valueStrs.size(); ++i) {
                String valueString = valueStrs.get(i).trim();
                RIValue value = parseValue(tableSchema.columns.get(i), valueString);
                values.add(value);
            }
            return new RIRow(values);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("CSV Parsing error");
        }
    }

    private static RIValue parseValue(RSColumn column, String valueString) {
        if (valueString.equals(NULL_LITERAL)) return RINullValue.getInstance();
        if (valueString.equals("")) {
            return buildMissingAtomicValue(column.type);
        }
        switch (column.type) {
        case INT:
            try {
                return new RIIntValue(Integer.parseInt(valueString));
            } catch (NumberFormatException e) {
                return new RIIntValue(0); // replace overflowing int with zero
            }
        case STRING:
            return new RIStrValue(valueString);
        case FLOAT:
            return new RIFloatValue(valueString);
        default:
            throw new IllegalArgumentException("Unknown column type for " + column);
        }
    }

    private static RIValue buildMissingAtomicValue(RSColumn.ColumnType type) {
        switch (type) {
        case INT:
            return new RIIntValue(0);
        case FLOAT:
            return new RIFloatValue("0.0");
        case STRING:
            return RINullValue.getInstance();
        default:
            throw new IllegalArgumentException("Unknown attribute type: " + type.name());
        }
    }

    private static boolean isSchemaString(String line) {
        return line.trim().endsWith(")");
    }

}
