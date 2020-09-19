package dynamite.core;

import java.util.List;

import dynamite.datalog.ast.DatalogFact;
import dynamite.docdb.ast.DocumentInstance;
import dynamite.util.SetMultiMap;

/**
 * Interface for database instances.
 */
public interface IInstance {

    /**
     * Convert the instance to a list of Datalog facts.
     *
     * @return a list of Datalog facts
     */
    public List<DatalogFact> toDatalogFacts();

    /**
     * Convert the instance to the corresponding document instance.
     *
     * @return the document instance
     */
    public DocumentInstance toDocumentInstance();

    /**
     * Collect the value set for all the attributes.
     *
     * @return a map from attribute canonical name to its corresponding value set
     */
    public SetMultiMap<String, Object> collectValuesByAttr();

    /**
     * Convert the instance to a human-readable string that can also be parsed.
     *
     * @return the instance string
     */
    public String toInstanceString();

}
