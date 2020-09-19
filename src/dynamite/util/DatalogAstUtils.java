package dynamite.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.RelationPredicate;

public class DatalogAstUtils {

    /**
     * delimiter used in Datalog outputs
     */
    public static final String DATALOG_DELIMITER = "\\t";

    /**
     * Find all output declarations in the Datalog program.
     *
     * @param program the specified Datalog program
     * @return a list of output declarations
     */
    public static List<OutputDeclaration> findOutputDecls(DatalogProgram program) {
        return program.declarations.stream()
                .filter(OutputDeclaration.class::isInstance)
                .map(OutputDeclaration.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Find all relation declarations in the Datalog program given a set of relation names.
     *
     * @param program   the specified Datalog program
     * @param declNames the specified relation names
     * @return a list of relation declarations
     */
    public static List<RelationDeclaration> findRelDeclsByNames(DatalogProgram program, Set<String> declNames) {
        return program.declarations.stream()
                .filter(RelationDeclaration.class::isInstance)
                .map(RelationDeclaration.class::cast)
                .filter(relDecl -> declNames.contains(relDecl.relation))
                .collect(Collectors.toList());
    }

    /**
     * Build a new Datalog program that is the same as the specified one except that
     * the program has no facts.
     *
     * @param program the specified Datalog program
     * @return a Datalog program without facts
     */
    public static DatalogProgram removeFacts(DatalogProgram program) {
        return new DatalogProgram(program.declarations, program.rules, Collections.emptyList());
    }

    /**
     * Build a parameter map for {@link OutputDeclaration} that contains the limiter parameter.
     *
     * @return the parameter tree map
     */
    public static Map<String, String> buildDelimiterParamMap() {
        Map<String, String> parameterMap = new TreeMap<>();
        parameterMap.put("delimiter", "\"" + DATALOG_DELIMITER + "\"");
        return parameterMap;
    }

    /**
     * Count the number of body predicates in a Datalog program.
     *
     * @param program the specified Datalog program
     * @return the number of body predicates
     */
    public static int countBodyPredicates(DatalogProgram program) {
        return program.rules.stream()
                .map(rule -> rule.bodies.size())
                .reduce(0, Integer::sum);
    }

    /**
     * Build a map from head relation names to their corresponding rules for a given Datalog program.
     *
     * @param program the given Datalog program
     * @return the map from names of head relations to their corresponding rules
     */
    public static Map<String, DatalogRule> buildHeadNameToRuleMap(DatalogProgram program) {
        Map<String, DatalogRule> map = new HashMap<>();
        for (DatalogRule rule : program.rules) {
            for (RelationPredicate head : rule.heads) {
                if (map.containsKey(head.relation)) {
                    throw new IllegalStateException("Duplicated name of head relations: " + head.relation);
                }
                map.put(head.relation, rule);
            }
        }
        return map;
    }

}
