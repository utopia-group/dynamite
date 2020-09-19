package dynamite.reldb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import dynamite.exp.CompoundFK;
import dynamite.exp.ForeignKey;
import dynamite.exp.IntegrityConstraint;
import dynamite.reldb.ast.IRelSchemaVisitor;
import dynamite.reldb.ast.RIFloatValue;
import dynamite.reldb.ast.RIIntValue;
import dynamite.reldb.ast.RIRow;
import dynamite.reldb.ast.RIStrValue;
import dynamite.reldb.ast.RITable;
import dynamite.reldb.ast.RIValue;
import dynamite.reldb.ast.RSColumn;
import dynamite.reldb.ast.RSTable;
import dynamite.reldb.ast.RelationalInstance;
import dynamite.reldb.ast.RelationalSchema;
import dynamite.util.NameUtils;
import dynamite.util.RandomValueFactory;

/**
 * A relational schema visitor that generates random relational instances.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class RandomRelInstGenVisitor
        implements IRelSchemaVisitor<RelationalInstance> {

    // probability in percentage to use existing values for foreign keys
    public static final int PROBABILITY = 90;

    // random value factory
    private final RandomValueFactory randFactory;
    // size of the table
    private final int size;
    // integrity constraints
    private final IntegrityConstraint constraint;

    public RandomRelInstGenVisitor(int size, IntegrityConstraint constraint, long randomSeed) {
        this.size = size;
        this.constraint = constraint;
        this.randFactory = new RandomValueFactory(randomSeed);
    }

    @Override
    public RelationalInstance visit(RelationalSchema schema) {
        // generate random table instances
        Map<String, RITable> nameToTables = schema.tables.stream()
                .map(table -> visit(table))
                .collect(Collectors.toMap(RITable::getName, Function.identity()));
        // update the values based on foreign keys
        updateByFKs(schema, nameToTables, constraint.foreignKeys);
        // update the values based on compound foreign keys
        updateByCompoundFKs(schema, nameToTables, constraint.compoundFKs);
        // build the relational instance
        return new RelationalInstance(schema.tables.stream()
                .map(table -> nameToTables.get(table.name))
                .collect(Collectors.toList()));
    }

    public RITable visit(RSTable table) {
        List<RIRow> rows = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            List<RIValue> values = new ArrayList<>();
            for (RSColumn column : table.columns) {
                values.add(visit(column));
            }
            rows.add(new RIRow(values));
        }
        return new RITable(table, rows);
    }

    public RIValue visit(RSColumn column) {
        switch (column.type) {
        case INT:
            return new RIIntValue(randFactory.mkInt());
        case STRING:
            return new RIStrValue(randFactory.mkString());
        case FLOAT:
            return new RIFloatValue(randFactory.mkFloatString());
        default:
            throw new IllegalStateException("Unknown column type: " + column.type.name());
        }
    }

    private void updateByFKs(RelationalSchema schema, Map<String, RITable> nameToTables, List<ForeignKey> fks) {
        for (ForeignKey fk : fks) {
            String fromTableName = NameUtils.computeRecordTypeName(fk.from);
            RITable fromTableInst = nameToTables.get(fromTableName);
            RSTable fromTable = schema.getSubSchema(fromTableName);
            String fromAttr = NameUtils.computeSimpleAttrName(fk.from);
            int fromIndex = computeAttrIndex(fromTable, fromAttr);

            String toTableName = NameUtils.computeRecordTypeName(fk.to);
            RITable toTableInst = nameToTables.get(toTableName);
            RSTable toTable = schema.getSubSchema(toTableName);
            String toAttr = NameUtils.computeSimpleAttrName(fk.to);
            int toIndex = computeAttrIndex(toTable, toAttr);

            RITable newInst = updateFKValuesForTable(fromTableInst, fromIndex, toTableInst, toIndex);
            nameToTables.put(fromTableName, newInst);
        }
    }

    private void updateByCompoundFKs(RelationalSchema schema, Map<String, RITable> nameToTables, List<CompoundFK> fks) {
        for (CompoundFK fk : fks) {
            // NOTE: assume all attributes are in the same table
            String fromTableName = NameUtils.computeRecordTypeName(fk.fromAttrs.get(0));
            RITable fromTableInst = nameToTables.get(fromTableName);
            RSTable fromTable = schema.getSubSchema(fromTableName);
            List<Integer> fromIndices = fk.fromAttrs.stream()
                    .map(from -> NameUtils.computeSimpleAttrName(from))
                    .map(fromAttr -> computeAttrIndex(fromTable, fromAttr))
                    .collect(Collectors.toList());

            // NOTE: assume all attributes are in the same table
            String toTableName = NameUtils.computeRecordTypeName(fk.toAttrs.get(0));
            RITable toTableInst = nameToTables.get(toTableName);
            RSTable toTable = schema.getSubSchema(toTableName);
            List<Integer> toIndices = fk.toAttrs.stream()
                    .map(to -> NameUtils.computeSimpleAttrName(to))
                    .map(toAttr -> computeAttrIndex(toTable, toAttr))
                    .collect(Collectors.toList());

            if (toIndices.size() != fromIndices.size()) throw new IllegalStateException();

            RITable newInst = updateManyFKValues(fromTableInst, fromIndices, toTableInst, toIndices);
            nameToTables.put(fromTableName, newInst);
        }
    }

    private RITable updateManyFKValues(RITable fromTableInst, List<Integer> fromIndices, RITable toTableInst, List<Integer> toIndices) {
        List<RIRow> rows = new ArrayList<>();
        for (int i = 0; i < fromTableInst.rows.size(); ++i) {
            RIRow row = fromTableInst.rows.get(i);
            if (toApplyCompoundFK()) {
                int index = getRandomIndex(toTableInst.getSize());
                RIRow toRow = toTableInst.rows.get(index);
                rows.add(updateManyFKValuesForRow(row, fromIndices, toRow, toIndices));
            } else {
                rows.add(row);
            }
        }
        return new RITable(fromTableInst.tableSchema, rows);
    }

    private RIRow updateManyFKValuesForRow(RIRow fromRow, List<Integer> fromIndices, RIRow toRow, List<Integer> toIndices) {
        List<RIValue> values = new ArrayList<>(fromRow.values);
        for (int i = 0; i < fromIndices.size(); ++i) {
            int fromIndex = fromIndices.get(i);
            int toIndex = toIndices.get(i);
            values.set(fromIndex, toRow.values.get(toIndex));
        }
        return new RIRow(values);
    }

    // compute the column index in the table schema given a column name
    private static int computeAttrIndex(RSTable table, String columnName) {
        for (int i = 0; i < table.columns.size(); ++i) {
            if (table.columns.get(i).name.equals(columnName)) {
                return i;
            }
        }
        throw new IllegalStateException(String.format("Cannot find %s in %s", columnName, table.name));
    }

    private RITable updateFKValuesForTable(RITable fromTableInst, int fromIndex, RITable toTableInst, int toIndex) {
        List<RIRow> rows = new ArrayList<>();
        for (int i = 0; i < fromTableInst.rows.size(); ++i) {
            RIRow row = fromTableInst.rows.get(i);
            if (toBeReplaced()) {
                int index = getRandomIndex(toTableInst.getSize());
                RIRow toRow = toTableInst.rows.get(index);
                rows.add(updateFKValuesForRow(row, fromIndex, toRow, toIndex));
            } else {
                rows.add(row);
            }
        }
        return new RITable(fromTableInst.tableSchema, rows);
    }

    private RIRow updateFKValuesForRow(RIRow fromRow, int fromIndex, RIRow toRow, int toIndex) {
        List<RIValue> values = new ArrayList<>(fromRow.values);
        values.set(fromIndex, toRow.values.get(toIndex));
        return new RIRow(values);
    }

    private boolean toApplyCompoundFK() {
        // NOTE: always apply the compound foreign keys
        return true;
    }

    private boolean toBeReplaced() {
        int num = randFactory.mkBoundedInt(100);
        return num < PROBABILITY;
    }

    private int getRandomIndex(int bound) {
        return randFactory.mkBoundedInt(bound);
    }

}
