package dynamite.reldb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dynamite.docdb.InstCanonicalNameVisitor;
import dynamite.docdb.ast.DIAstNode;
import dynamite.docdb.ast.DIAtomicAttrValue;
import dynamite.docdb.ast.DIAttrValue;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DIFloatValue;
import dynamite.docdb.ast.DIIntValue;
import dynamite.docdb.ast.DINullValue;
import dynamite.docdb.ast.DIStrValue;
import dynamite.docdb.ast.DocumentInstance;
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
 * A relational instance visitor that builds the corresponding document instance.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class RelToDocInstVisitor implements
        IRelInstVisitor<DocumentInstance>,
        IRIAstVisitor<DIAstNode>,
        IRIValueVisitor<DIAtomicAttrValue> {

    // current table that the visitor is visiting
    private RITable currTable;
    // column name of the current value
    private String currColumnName;

    @Override
    public DIAtomicAttrValue visit(RIIntValue value) {
        return new DIAtomicAttrValue(currColumnName, new DIIntValue(value.value));
    }

    @Override
    public DIAtomicAttrValue visit(RIStrValue value) {
        return new DIAtomicAttrValue(currColumnName, new DIStrValue(value.value));
    }

    @Override
    public DIAtomicAttrValue visit(RIFloatValue value) {
        return new DIAtomicAttrValue(currColumnName, new DIFloatValue(value.valueString));
    }

    @Override
    public DIAtomicAttrValue visit(RINullValue value) {
        return new DIAtomicAttrValue(currColumnName, DINullValue.getInstance());
    }

    @Override
    public DIDocument visit(RIRow row) {
        List<DIAttrValue> attributes = new ArrayList<>();
        for (int i = 0; i < row.values.size(); ++i) {
            currColumnName = currTable.tableSchema.columns.get(i).name;
            attributes.add(row.values.get(i).accept(this));
        }
        return new DIDocument(attributes);
    }

    @Override
    public DICollection visit(RITable table) {
        currTable = table;
        List<DIDocument> documents = table.rows.stream()
                .map(row -> row.accept(this))
                .map(DIDocument.class::cast)
                .collect(Collectors.toList());
        return new DICollection(table.getName(), documents);
    }

    @Override
    public DocumentInstance visit(RelationalInstance instance) {
        List<DICollection> collections = instance.tables.stream()
                .map(table -> table.accept(this))
                .map(DICollection.class::cast)
                .collect(Collectors.toList());
        DocumentInstance docInst = new DocumentInstance(collections);
        docInst.accept(new InstCanonicalNameVisitor());
        return docInst;
    }

}
