package dynamite.smt.ast;

/**
 * Abstract class for SMT formulas.
 */
public abstract class LFormula {

    public abstract <T> T accept(ILFormulaVisitor<T> visitor);

}
