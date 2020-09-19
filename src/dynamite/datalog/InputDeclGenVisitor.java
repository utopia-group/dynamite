package dynamite.datalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.IProgramVisitor;
import dynamite.datalog.ast.InputDeclaration;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;

/**
 * A visitor to generate input declarations for a Datalog program.
 * <br>
 * This visitor does not modify the AST nodes.
 */
public final class InputDeclGenVisitor
        implements IProgramVisitor<DatalogProgram> {

    String tmpPath;

    public InputDeclGenVisitor(String tmpPath) {
        this.tmpPath = tmpPath;
    }

    @Override
    public DatalogProgram visit(DatalogProgram program) {
        return new DatalogProgram(inputRelationDeclarations(program), program.rules, new ArrayList<>());
    }

    private List<DatalogStatement> inputRelationDeclarations(DatalogProgram program) {
        List<DatalogStatement> res = new ArrayList<>(program.declarations);
        String relDeclFilepathFormat = tmpPath + "%s.facts";
        Set<String> outputDeclNames = program.declarations.stream()
                .filter(s -> s instanceof OutputDeclaration)
                .map(s -> ((OutputDeclaration) s).relation)
                .collect(Collectors.toSet());
        for (DatalogStatement relationDeclaration : program.declarations) {
            if (relationDeclaration instanceof RelationDeclaration) {
                String relDeclName = ((RelationDeclaration) relationDeclaration).relation;
                if (!outputDeclNames.contains(relDeclName)) { // not output decl, so input decl
                    String inputRelDeclFilepath = String.format(relDeclFilepathFormat, relDeclName);
                    Map<String, String> inputDeclParams = new TreeMap<>();
                    inputDeclParams.put("IO", "file");
                    inputDeclParams.put("filename", "\"" + inputRelDeclFilepath + "\"");
                    DatalogStatement inputDecl = new InputDeclaration(relDeclName, inputDeclParams);
                    res.add(inputDecl);
                }
            }
        }
        return res;
    }

}
