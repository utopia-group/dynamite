package dynamite.core;

import dynamite.docdb.ast.DocumentSchema;

/**
 * Interface for database schemas.
 */
public interface ISchema {

    /**
     * Convert the current schema to a document database schema.
     *
     * @return the document database schema
     */
    public DocumentSchema toDocumentSchema();

}
