package dynamite.core;

/**
 * Interface for schema mapping synthesizers.
 */
public interface ISynthesizer {

    /**
     * Synthesize a schema mapping that is consistent with the provided example.
     *
     * @param srcSchema source schema
     * @param tgtSchema target schema
     * @param example   example of database instances
     * @return a schema mapping from source to target such that it is consistent with the example, or
     *         {@code null} for synthesis failure
     */
    public SchemaMapping synthesize(ISchema srcSchema, ISchema tgtSchema, InstanceExample example);

}
