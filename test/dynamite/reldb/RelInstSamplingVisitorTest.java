package dynamite.reldb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.reldb.ast.RIIntValue;
import dynamite.reldb.ast.RIRow;
import dynamite.reldb.ast.RITable;
import dynamite.reldb.ast.RIValue;
import dynamite.reldb.ast.RSTable;
import dynamite.reldb.ast.RelationalInstance;
import dynamite.reldb.ast.RelationalSchema;

public class RelInstSamplingVisitorTest {

    private static RelationalInstance buildExpectedRelInstance1() {
        RelationalSchema schema = RelDbTestUtils.buildRelationalSchema2();
        List<RITable> tables = new ArrayList<>();
        {
            RSTable tableSchema = schema.tables.get(0);
            List<RIRow> rows = new ArrayList<>();
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(1), new RIIntValue(2),
            })));
            tables.add(new RITable(tableSchema, rows));
        }
        {
            RSTable tableSchema = schema.tables.get(1);
            List<RIRow> rows = new ArrayList<>();
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(5), new RIIntValue(6),
            })));
            tables.add(new RITable(tableSchema, rows));
        }
        return new RelationalInstance(tables);
    }

    private static RelationalInstance buildExpectedRelInstance2() {
        RelationalSchema schema = RelDbTestUtils.buildRelationalSchema2();
        List<RITable> tables = new ArrayList<>();
        {
            RSTable tableSchema = schema.tables.get(0);
            List<RIRow> rows = new ArrayList<>();
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(3), new RIIntValue(4),
            })));
            tables.add(new RITable(tableSchema, rows));
        }
        {
            RSTable tableSchema = schema.tables.get(1);
            List<RIRow> rows = new ArrayList<>();
            rows.add(new RIRow(Arrays.asList(new RIValue[] {
                    new RIIntValue(7), new RIIntValue(8),
            })));
            tables.add(new RITable(tableSchema, rows));
        }
        return new RelationalInstance(tables);
    }

    @Test
    public void testSample() {
        RelationalInstance instance = RelDbTestUtils.buildRelationalInstance2();
        BitSet indices1 = BitSet.valueOf(new long[] { 1 });
        RelationalInstance actual1 = instance.accept(new RelInstSamplingVisitor(indices1));
        RelationalInstance expected1 = buildExpectedRelInstance1();
        Assert.assertEquals(expected1, actual1);
        BitSet indices2 = BitSet.valueOf(new long[] { 2 });
        RelationalInstance actual2 = instance.accept(new RelInstSamplingVisitor(indices2));
        RelationalInstance expected2 = buildExpectedRelInstance2();
        Assert.assertEquals(expected2, actual2);
    }

}
