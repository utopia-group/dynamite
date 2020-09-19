package dynamite.datalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.IProgramVisitor;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.TypedAttribute;
import dynamite.util.DatalogAstUtils;

/**
 * A visitor to build the Datalog program that adds a declaration for the universe
 * of all output relations. It also adds a rule to compute the universe,
 * which is the join of all output relations.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class UniverseGenVisitor implements
        IProgramVisitor<DatalogProgram> {

    private static final String UNIVERSE_NAME = "__Universe";
    private static final String DELIMITER = "?";
    // relation name -> relation declarations
    private Map<String, RelationDeclaration> nameToRelDeclMap;
    // ordered output relation names
    private List<String> orderedOutputRelNames;

    @Override
    public DatalogProgram visit(DatalogProgram program) {
        // build the maps
        orderedOutputRelNames = sortOutputRelationNames(program);
        nameToRelDeclMap = buildNameToRelDeclMap(program);
        // build the new program
        List<DatalogStatement> decls = new ArrayList<>(program.declarations);
        decls.add(genUniverseRelDecl());
        decls.add(new OutputDeclaration(UNIVERSE_NAME, DatalogAstUtils.buildDelimiterParamMap()));
        List<DatalogRule> rules = new ArrayList<>(program.rules);
        rules.add(genUniverseRule());
        return new DatalogProgram(decls, rules, program.facts);
    }

    private static List<String> sortOutputRelationNames(DatalogProgram program) {
        return program.declarations.stream()
                .filter(OutputDeclaration.class::isInstance)
                .map(OutputDeclaration.class::cast)
                .map(outputDecl -> outputDecl.relation)
                .sorted()
                .collect(Collectors.toList());
    }

    private static Map<String, RelationDeclaration> buildNameToRelDeclMap(DatalogProgram program) {
        return program.declarations.stream()
                .filter(RelationDeclaration.class::isInstance)
                .map(RelationDeclaration.class::cast)
                .collect(Collectors.toMap(relDecl -> relDecl.relation, relDecl -> relDecl));
    }

    private RelationDeclaration genUniverseRelDecl() {
        List<TypedAttribute> attributes = new ArrayList<>();
        for (String relName : orderedOutputRelNames) {
            RelationDeclaration relDecl = nameToRelDeclMap.get(relName);
            for (TypedAttribute typedAttr : relDecl.attributes) {
                attributes.add(new TypedAttribute(getDeclAttrName(relName, typedAttr.attrName), typedAttr.typeName));
            }
        }
        return new RelationDeclaration(UNIVERSE_NAME, attributes);
    }

    private static String getDeclAttrName(String relName, String attrName) {
        return "_" + relName + DELIMITER + attrName;
    }

    private DatalogRule genUniverseRule() {
        // build head
        List<DatalogExpression> headArgs = new ArrayList<>();
        for (String relName : orderedOutputRelNames) {
            RelationDeclaration relDecl = nameToRelDeclMap.get(relName);
            for (TypedAttribute typedAttr : relDecl.attributes) {
                headArgs.add(buildIdent(relName, typedAttr));
            }
        }
        RelationPredicate head = new RelationPredicate(UNIVERSE_NAME, headArgs);
        // build bodies
        List<DatalogPredicate> bodies = new ArrayList<>();
        for (String relName : orderedOutputRelNames) {
            List<DatalogExpression> bodyArgs = new ArrayList<>();
            RelationDeclaration relDecl = nameToRelDeclMap.get(relName);
            for (TypedAttribute typedAttr : relDecl.attributes) {
                bodyArgs.add(buildIdent(relName, typedAttr));
            }
            bodies.add(new RelationPredicate(relName, bodyArgs));
        }
        return new DatalogRule(Collections.singletonList(head), bodies);
    }

    private static Identifier buildIdent(String relName, TypedAttribute typedAttr) {
        if (typedAttr.attrName.equals("__id")) {
            return new Identifier("v_" + relName);
        } else {
            return new Identifier("v_" + relName + DELIMITER + typedAttr.attrName);
        }
    }

}
