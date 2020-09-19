package dynamite;

import java.io.File;

import dynamite.core.IInstance;
import dynamite.datalog.DatalogOutput;
import dynamite.datalog.SouffleEvaluator;
import dynamite.datalog.ast.DatalogProgram;

public class ExperimentUtils {

    public static void assertNotNull(Object obj) {
        if (obj == null) throw new RuntimeException();
    }

    public static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new RuntimeException("AssertEquals Failed\nExpected:\n" + expected + "\nActual:\n" + actual);
        }
    }

    public static void assertConsistency(DatalogProgram program, IInstance srcExample, IInstance tgtExample) {
        DatalogOutput actual = SouffleEvaluator.evaluateOnInstance(program, srcExample);
        DatalogOutput expected = tgtExample.toDocumentInstance().toDatalogOutputWithoutUniverse();
        if (!expected.equals(actual)) {
            throw new RuntimeException("AssertEquals Failed\n"
                    + "Program:\n" + program.toSouffle()
                    + "Expected:\n" + expected
                    + "\nActual:\n" + actual);
        }
    }

    public static void assertFilesNotEmpty(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files == null || files.length < 1) {
            throw new RuntimeException("Could not find files in " + directoryPath);
        }
        for (File file : files) {
            if (file.length() <= 0) {
                throw new RuntimeException("Empty file: " + file.getPath());
            }
        }
    }

    public static String getFilePathByPrefix(File directory, String prefix) {
        File[] files = directory.listFiles((dir, name) -> name.startsWith(prefix));
        assert files.length == 1;
        return files[0].getPath();
    }

}
