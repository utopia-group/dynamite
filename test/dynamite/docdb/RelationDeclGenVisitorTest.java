package dynamite.docdb;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.RelationDeclaration;
import dynamite.docdb.ast.DocumentSchema;

public class RelationDeclGenVisitorTest {

    @Test
    public void testSchema1() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema1();
        List<RelationDeclaration> declarations = schema.accept(new RelationDeclGenVisitor());
        Assert.assertEquals(2, declarations.size());
        Assert.assertEquals(".decl Computers?parts(__id: Rel, value: IntAttr)", declarations.get(0).toSouffle());
        Assert.assertEquals(".decl Computers(cid: IntAttr, name: StrAttr, manufacturer: StrAttr, catalog: StrAttr, parts: Rel)",
                declarations.get(1).toSouffle());
    }

    @Test
    public void testSchema2() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema2();
        List<RelationDeclaration> declarations = schema.accept(new RelationDeclGenVisitor());
        Assert.assertEquals(2, declarations.size());
        Assert.assertEquals(".decl Computers?parts(__id: Rel, value: IntAttr)", declarations.get(0).toSouffle());
        Assert.assertEquals(".decl Computers(cid: IntAttr, name: StrAttr, parts: Rel, catalog: StrAttr, manufacturer: StrAttr)",
                declarations.get(1).toSouffle());
    }

    @Test
    public void testSchema3() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema3();
        List<RelationDeclaration> declarations = schema.accept(new RelationDeclGenVisitor());
        Assert.assertEquals(3, declarations.size());
        Assert.assertEquals(".decl A?B?C(__id: Rel, c1: IntAttr, c2: IntAttr)", declarations.get(0).toSouffle());
        Assert.assertEquals(".decl A?B(__id: Rel, b1: IntAttr, C: Rel)", declarations.get(1).toSouffle());
        Assert.assertEquals(".decl A(a1: IntAttr, B: Rel)", declarations.get(2).toSouffle());
    }

    @Test
    public void testSchema4() {
        DocumentSchema schema = DocDbTestUtils.buildDocumentSchema4();
        List<RelationDeclaration> declarations = schema.accept(new RelationDeclGenVisitor());
        Assert.assertEquals(2, declarations.size());
        Assert.assertEquals(".decl A(a1: IntAttr, a2: IntAttr)", declarations.get(0).toSouffle());
        Assert.assertEquals(".decl B(b1: IntAttr, b2: IntAttr)", declarations.get(1).toSouffle());
    }
}
