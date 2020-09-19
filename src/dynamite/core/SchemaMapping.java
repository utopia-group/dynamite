package dynamite.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.util.DatalogAstUtils;

/**
 * Data structure for schema mappings.
 */
public class SchemaMapping {

    // a schema mapping is essentially a datalog program
    public final DatalogProgram program;
    // Datalog program without facts
    public final DatalogProgram programWithoutFacts;

    public SchemaMapping(DatalogProgram program) {
        this.program = program;
        this.programWithoutFacts = DatalogAstUtils.removeFacts(program);
    }

    /**
     * Build a schema mapping using a list of Datalog programs, where each program corresponds to
     * a target document collection.
     *
     * @param programs a list of Datalog programs
     * @return the schema mapping
     */
    public static SchemaMapping fromDatalogPrograms(List<DatalogProgram> programs) {
        if (programs.size() < 1) {
            throw new IllegalArgumentException("Empty Datalog program list");
        }
        List<DatalogStatement> decls = new ArrayList<>(programs.get(0).declarations);
        for (int i = 1; i < programs.size(); ++i) {
            List<OutputDeclaration> outputDecls = DatalogAstUtils.findOutputDecls(programs.get(i));
            Set<String> outputNames = outputDecls.stream()
                    .map(outputDecl -> outputDecl.relation)
                    .collect(Collectors.toSet());
            List<RelationDeclaration> relDecls = DatalogAstUtils.findRelDeclsByNames(programs.get(i), outputNames);
            decls.addAll(relDecls);
            decls.addAll(outputDecls);
        }
        List<DatalogRule> rules = programs.stream()
                .flatMap(program -> program.rules.stream())
                .collect(Collectors.toList());
        List<DatalogFact> facts = programs.get(0).facts;
        return new SchemaMapping(new DatalogProgram(decls, rules, facts));
    }

    @Override
    public String toString() {
        return programWithoutFacts.toSouffle();
    }

}
