package dynamite.trans;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import dynamite.datalog.ast.DatalogFact;
import dynamite.reldb.RelFactGenVisitor;
import dynamite.reldb.RelInstanceParser;
import dynamite.reldb.ast.RIRow;
import dynamite.reldb.ast.RITable;
import dynamite.reldb.ast.RSTable;
import dynamite.util.FileUtils;

public class RelToInputFactsTransformer extends InputFactsTransformer {

    public static void parseToFactsCsv(RSTable tableSchema, Reader inputReader, String tmpPath) {
        FileUtils.createDirectory(Paths.get(tmpPath));
        dropLine(inputReader);
        RIRow row = RelInstanceParser.parseRow(tableSchema, inputReader);
        HashMap<String, Writer> writers = new HashMap<>();
        while (row != null) {
            // need the wrapper so the tablename from the schema is attached to the row
            RITable wrapper = new RITable(tableSchema, Collections.singletonList(row));
            List<DatalogFact> facts = wrapper.accept(new RelFactGenVisitor());
            emitFactsToFile(facts, writers, tmpPath);

            //iterate
            row = RelInstanceParser.parseRow(tableSchema, inputReader);
        }
        closeResources(writers);
    }

    private static void closeResources(HashMap<String, Writer> writers) {
        try {
            for (String relation : writers.keySet()) {
                writers.get(relation).close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not close all resources");
        }
    }

    private static void dropLine(Reader r) {
        try {
            int ch = r.read();
            while (ch != '\n') {
                ch = r.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IO exception when trying to drop top line");
        }
    }

}
