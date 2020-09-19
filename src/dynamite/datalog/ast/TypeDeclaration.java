package dynamite.datalog.ast;

public final class TypeDeclaration extends DatalogStatement {

    public final String typeName;

    public static final String TYPE_ATTR_INT = "IntAttr";
    public static final String TYPE_ATTR_STR = "StrAttr";
    public static final String TYPE_RELATION = "Rel";

    private static TypeDeclaration intAttrTypeInstance = null;
    private static TypeDeclaration strAttrTypeInstance = null;
    private static TypeDeclaration relTypeInstance = null;

    public TypeDeclaration(String typeName) {
        this.typeName = typeName;
    }

    public static synchronized TypeDeclaration intAttrTypeDecl() {
        if (intAttrTypeInstance == null) {
            intAttrTypeInstance = new TypeDeclaration(TYPE_ATTR_INT);
        }
        return intAttrTypeInstance;
    }

    public static synchronized TypeDeclaration strAttrTypeDecl() {
        if (strAttrTypeInstance == null) {
            strAttrTypeInstance = new TypeDeclaration(TYPE_ATTR_STR);
        }
        return strAttrTypeInstance;
    }

    public static synchronized TypeDeclaration relTypeDecl() {
        if (relTypeInstance == null) {
            relTypeInstance = new TypeDeclaration(TYPE_RELATION);
        }
        return relTypeInstance;
    }

    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toSouffle() {
        return String.format(".type %s", typeName);
    }

    @Override
    public String toString() {
        return String.format("TypeDecl(%s)", typeName);
    }

}
