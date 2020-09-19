package dynamite.docdb;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.OutputDeclaration;
import dynamite.docdb.ast.DocumentSchema;

public class OutputDeclGenVisitorTest {

    @Test
    public void testSchema1() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        List<OutputDeclaration> declarations = schema.accept(new OutputDeclGenVisitor());
        Assert.assertEquals(2, declarations.size());
        Assert.assertEquals("Computers?parts", declarations.get(0).relation);
        Assert.assertEquals("Computers", declarations.get(1).relation);
    }

    @Test
    public void testSchema2() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        List<OutputDeclaration> declarations = schema.accept(new OutputDeclGenVisitor());
        Assert.assertEquals(2, declarations.size());
        Assert.assertEquals("Computers?parts", declarations.get(0).relation);
        Assert.assertEquals("Computers", declarations.get(1).relation);
    }

    @Test
    public void testSchema3() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        List<OutputDeclaration> declarations = schema.accept(new OutputDeclGenVisitor());
        Assert.assertEquals(3, declarations.size());
        Assert.assertEquals("A?B?C", declarations.get(0).relation);
        Assert.assertEquals("A?B", declarations.get(1).relation);
        Assert.assertEquals("A", declarations.get(2).relation);
    }

    @Test
    public void testSchema4() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        List<OutputDeclaration> declarations = schema.accept(new OutputDeclGenVisitor());
        Assert.assertEquals(2, declarations.size());
        Assert.assertEquals("A", declarations.get(0).relation);
        Assert.assertEquals("B", declarations.get(1).relation);
    }

}
