package dynamite.core;

/**
 * Data structure for examples to infer schema mappings.
 */
public class InstanceExample {

    // source database instance
    public final IInstance sourceInstance;
    // target database instance
    public final IInstance targetInstance;

    public InstanceExample(IInstance sourceInstance, IInstance targetInstance) {
        this.sourceInstance = sourceInstance;
        this.targetInstance = targetInstance;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Source:\n");
        builder.append(sourceInstance);
        builder.append("\nTarget:\n");
        builder.append(targetInstance);
        return builder.toString();
    }

}
