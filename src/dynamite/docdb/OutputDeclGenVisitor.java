package dynamite.docdb;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dynamite.datalog.ast.OutputDeclaration;
import dynamite.docdb.ast.DSAtomicAttr;
import dynamite.docdb.ast.DSCollection;
import dynamite.docdb.ast.DSCollectionAttr;
import dynamite.docdb.ast.DSDocument;
import dynamite.docdb.ast.DocumentSchema;
import dynamite.docdb.ast.IDSAstVisitor;
import dynamite.docdb.ast.IDocSchemaVisitor;
import dynamite.util.DatalogAstUtils;

/**
 * Visitor for generating Datalog output declarations for document schemas.
 * This visitor does not modify the schema AST.
 */
public final class OutputDeclGenVisitor implements
        IDocSchemaVisitor<List<OutputDeclaration>>,
        IDSAstVisitor<List<OutputDeclaration>> {

    @Override
    public List<OutputDeclaration> visit(DSCollection collection) {
        List<OutputDeclaration> outputDecls = collection.document.accept(this);
        outputDecls.add(new OutputDeclaration(
                collection.getCanonicalName(),
                DatalogAstUtils.buildDelimiterParamMap()));
        return outputDecls;
    }

    @Override
    public List<OutputDeclaration> visit(DSDocument document) {
        return document.attributes.stream()
                .flatMap(attr -> attr.accept(this).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<OutputDeclaration> visit(DSAtomicAttr atomicAttr) {
        return Collections.emptyList();
    }

    @Override
    public List<OutputDeclaration> visit(DSCollectionAttr collectionAttr) {
        return collectionAttr.collection.accept(this);
    }

    @Override
    public List<OutputDeclaration> visit(DocumentSchema schema) {
        return schema.collections.stream()
                .flatMap(collection -> collection.accept(this).stream())
                .collect(Collectors.toList());
    }

}
