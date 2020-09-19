package dynamite.datalog.ast;

/**
 * Note this class does not have a corresponding visitor for the time being.
 * The operations on this class should be handled in {@code RelationDeclaration}.
 */
public final class TypedAttribute extends DatalogAstNode {

    public final String attrName;
    public final String typeName;

    public TypedAttribute(String attrName, String typeName) {
        this.attrName = attrName;
        this.typeName = typeName;
    }

    @Override
    public String toSouffle() {
        return String.format("%s: %s", attrName, typeName);
    }

    @Override
    public String toString() {
        return String.format("TypedAttr(%s, %s)", attrName, typeName);
    }

}
