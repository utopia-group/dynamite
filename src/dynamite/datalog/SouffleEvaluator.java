package dynamite.datalog;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dynamite.core.IInstance;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.util.DatalogAstUtils;
import dynamite.util.FileUtils;

/**
 * A wrapper for the Souffle datalog evaluator.
 */
public final class SouffleEvaluator implements IDatalogEvaluator {

    // root path of the temporary directory
    public static final String ROOT_PATH = "tmp";
    // file name of the Datalog program to be dumped
    public static final String PROGRAM_FILENAME = "program.dl";
    // file extension of Souffle output
    public static final String OUTPUT_EXTENSION = ".csv";
    // file name for storing the souffle stdout
    public static final String STDOUT_PATH = ROOT_PATH + File.separator + "out.txt";
    // file name for storing the souffle stderr
    public static final String STDERR_PATH = ROOT_PATH + File.separator + "err.txt";

    @Override
    public synchronized DatalogOutput evaluate(DatalogProgram program) {
        String tmpDirName = "Souffle_" + System.currentTimeMillis();
        return evaluateUsingFolder(program, tmpDirName);
    }

    public synchronized DatalogOutput evaluateUsingFolder(DatalogProgram program, String folderName) {
        Path path = Paths.get(ROOT_PATH, folderName);
        FileUtils.createDirectory(path);
        Path filePath = Paths.get(ROOT_PATH, folderName, PROGRAM_FILENAME);
        FileUtils.writeStringToFile(filePath.toString(), program.toSouffle());
        executeSouffle(filePath, path);
        List<String> relNames = extractOutputRelationNames(program);
        DatalogOutput output = parseDatalogOutput(path, relNames);
        FileUtils.deleteDirAndFiles(path);
        return output;
    }

    public synchronized void evaluateToFolder(DatalogProgram program, Path outDirPath) {
        String tmpDirName = "Souffle_" + System.currentTimeMillis();
        FileUtils.createDirectory(outDirPath);
        FileUtils.createDirectory(Paths.get(ROOT_PATH, tmpDirName));
        Path filePath = Paths.get(ROOT_PATH, tmpDirName, PROGRAM_FILENAME);
        FileUtils.writeStringToFile(filePath.toString(), program.toSouffle());
        executeSouffle(filePath, outDirPath);
    }

    // internal sequence number for temporary Souffle output
    private static int seqNumber = 1000000;

    /**
     * Evaluate a Datalog program on an input instance.
     *
     * @param program   the Datalog program
     * @param inputInst the input instance
     * @return the output of the Datalog program on the provided input
     */
    public static synchronized DatalogOutput evaluateOnInstance(DatalogProgram program, IInstance inputInst) {
        List<DatalogFact> facts = inputInst.toDatalogFacts();
        DatalogProgram progWithInput = new DatalogProgram(program.declarations, program.rules, facts);
        SouffleEvaluator evaluator = new SouffleEvaluator();
        DatalogOutput actualOutput = null;
        while (actualOutput == null) {
            try {
                ++seqNumber;
                actualOutput = evaluator.evaluateUsingFolder(progWithInput, String.valueOf(seqNumber));
            } catch (Exception e) {
                System.out.println("Recovered from Souffle internal error.");
                actualOutput = null;
            }
        }
        return actualOutput;
    }

    /**
     * Execute the Souffle evaluator assuming command `souffle` exists
     *
     * @param programPath path to the Souffle program
     * @param outDirPath  path to the output
     */
    private void executeSouffle(Path programPath, Path outDirPath) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("souffle", "-D" + outDirPath.toString(), programPath.toString());
        processBuilder.redirectOutput(Redirect.to(new File(STDOUT_PATH)));
        processBuilder.redirectError(Redirect.to(new File(STDERR_PATH)));
        try {
            Process process = processBuilder.start();
            int exitValue = process.waitFor();
            String outMsg = FileUtils.readFromFile(STDOUT_PATH);
            String errMsg = FileUtils.readFromFile(STDERR_PATH);
            String exceptionMsg = buildExceptionMessage(exitValue, outMsg, errMsg);
            if (exitValue != 0) throw new RuntimeException(exceptionMsg);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Cannot execute souffle on " + programPath.toAbsolutePath());
        }
    }

    private List<String> extractOutputRelationNames(DatalogProgram program) {
        return program.declarations.stream()
                .filter(OutputDeclaration.class::isInstance)
                .map(OutputDeclaration.class::cast)
                .map(outputDecl -> outputDecl.relation)
                .collect(Collectors.toList());
    }

    private DatalogOutput parseDatalogOutput(Path outputDir, List<String> relNames) {
        List<RelationOutput> relOutputs = relNames.stream()
                .map(relName -> parseRelationOutput(outputDir, relName))
                .collect(Collectors.toList());
        return new DatalogOutput(relOutputs);
    }

    private RelationOutput parseRelationOutput(Path outputDir, String relName) {
        String filePath = outputDir.toString() + File.separator + relName + OUTPUT_EXTENSION;
        List<String> lines = FileUtils.readLinesFromFile(filePath);
        List<List<String>> data = new ArrayList<>();
        for (String line : lines) {
            data.add(new ArrayList<>(Arrays.asList(line.split(DatalogAstUtils.DATALOG_DELIMITER))));
        }
        return new RelationOutput(relName, data);
    }

    private String buildExceptionMessage(int exitCode, String outMsg, String errMsg) {
        StringBuilder builder = new StringBuilder();
        builder.append("Souffle exit value: ").append(exitCode).append("\n");
        builder.append("Stdout:\n").append(outMsg).append("\n");
        builder.append("Stderr:\n").append(errMsg);
        return builder.toString();
    }

}
