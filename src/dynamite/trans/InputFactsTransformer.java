package dynamite.trans;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.StringLiteral;

public class InputFactsTransformer {

    public static final String DATALOG_INPUT_DELIM = "\t";
    //    String tmpPath = "tmp/Souffle_input/";

    public static void emitFactsToFile(List<DatalogFact> facts, HashMap<String, Writer> writers, String tmpPath) {
        for (DatalogFact fact : facts) {
            String csvLine = String.format("%s\n", fact.relationPred.arguments.stream()
                    .map(expr -> toSouffleInput(expr))
                    .map(s -> s.replace("\n", "\\n"))
                    .map(s -> s.replace("\t", "\\t"))
                    .collect(Collectors.joining(DATALOG_INPUT_DELIM)));
            String relationName = fact.relationPred.relation;
            try {
                // Do not replace this with a putIfAbsent call; for some reason that will cause too many files to be opened
                if (writers.get(relationName) == null) {
                    writers.put(relationName, new BufferedWriter(new FileWriter(tmpPath + relationName + ".facts", false)));
                }
                Writer writer = writers.get(relationName);
                writer.append(csvLine);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("File writing error");
            }
        }
    }

    private static String toSouffleInput(DatalogExpression expr) {
        // NOTE: remove quotation marks from String literals for Souffle input files
        return expr instanceof StringLiteral
                ? ((StringLiteral) expr).value
                : expr.toSouffle();
    }
}
