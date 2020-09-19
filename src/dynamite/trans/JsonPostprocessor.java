package dynamite.trans;

import java.util.Set;
import java.util.stream.Collectors;

import dynamite.datalog.ast.DatalogProgram;

public class JsonPostprocessor extends Postprocessor {

    public static void emitJson(DatalogProgram program, String outputPath) {
        constructMongoInstance(program, outputPath);
        System.out.println("Start to export");
        Set<String> outputDeclNames = computOutputDeclNames(program);
        long exportStartTime = System.currentTimeMillis();
        exportFinalJsonInstance(outputPath, outputDeclNames);
        System.out.println("=== Export Time: " + (System.currentTimeMillis() - exportStartTime) / 1000);
    }

    private static void exportFinalJsonInstance(String outputPath, Set<String> outputDeclNames) {
        Set<String> jsonDeclName = outputDeclNames.stream().filter(s -> !s.contains("?")).collect(Collectors.toSet());
        for (String outputName : jsonDeclName) {
            executeMongoexport(outputName, outputPath);
        }
    }

}
