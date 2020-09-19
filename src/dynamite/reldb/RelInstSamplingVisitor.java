package dynamite.reldb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dynamite.reldb.ast.IRelInstVisitor;
import dynamite.reldb.ast.RIRow;
import dynamite.reldb.ast.RITable;
import dynamite.reldb.ast.RelationalInstance;

/**
 * A relational instance visitor that samples the instance.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class RelInstSamplingVisitor implements
        IRelInstVisitor<RelationalInstance> {

    // indices of rows to sample in each table
    private final BitSet indicesBitSet;

    public RelInstSamplingVisitor(BitSet indicesBitSet) {
        Objects.requireNonNull(indicesBitSet);
        this.indicesBitSet = indicesBitSet;
    }

    public RITable visit(RITable table) {
        List<RIRow> rows = new ArrayList<>();
        for (int i = 0; i < table.rows.size(); ++i) {
            if (indicesBitSet.get(i)) {
                rows.add(table.rows.get(i));
            }
        }
        return new RITable(table.tableSchema, rows);
    }

    @Override
    public RelationalInstance visit(RelationalInstance instance) {
        return new RelationalInstance(instance.tables.stream()
                .map(table -> visit(table))
                .collect(Collectors.toList()));
    }

}
