package dynamite.datalog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.IProgramVisitor;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;

/**
 * A visitor to build a Datalog program that removes the universe declarations and rules
 * from the original program.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class UniverseRemovalVisitor implements
        IProgramVisitor<DatalogProgram> {

    private static final String UNIVERSE_NAME = "__Universe";

    @Override
    public DatalogProgram visit(DatalogProgram program) {
        List<DatalogStatement> decls = new ArrayList<>(program.declarations);
        for (Iterator<DatalogStatement> iter = decls.iterator(); iter.hasNext();) {
            DatalogStatement decl = iter.next();
            if (decl instanceof RelationDeclaration) {
                RelationDeclaration relDecl = (RelationDeclaration) decl;
                if (relDecl.relation.equals(UNIVERSE_NAME)) iter.remove();
            } else if (decl instanceof OutputDeclaration) {
                OutputDeclaration outputDecl = (OutputDeclaration) decl;
                if (outputDecl.relation.equals(UNIVERSE_NAME)) iter.remove();
            }
        }
        List<DatalogRule> rules = new ArrayList<>(program.rules);
        for (Iterator<DatalogRule> iter = rules.iterator(); iter.hasNext();) {
            DatalogRule rule = iter.next();
            if (rule.heads.size() == 1 && rule.heads.get(0).relation.equals(UNIVERSE_NAME)) {
                iter.remove();
            }
        }
        return new DatalogProgram(decls, rules, program.facts);
    }

}
