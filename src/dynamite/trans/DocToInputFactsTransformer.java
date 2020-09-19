package dynamite.trans;

import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import dynamite.datalog.ast.DatalogFact;
import dynamite.docdb.FactGenVisitor;
import dynamite.docdb.InstCanonicalNameVisitor;
import dynamite.docdb.ast.DICollection;
import dynamite.docdb.ast.DIDocument;
import dynamite.docdb.ast.DocumentInstance;

public class DocToInputFactsTransformer extends InputFactsTransformer {

    public static void emitFactToFileFromDIDocument(String tableName, DIDocument doc, HashMap<String, Writer> writers, String tmpPath) {
        if (doc == null) return;
        DICollection collection = new DICollection(tableName, Collections.singletonList(doc));
        DocumentInstance singleton = new DocumentInstance(Collections.singletonList(collection));
        // set canonical names to all documents and attributes
        singleton.accept(new InstCanonicalNameVisitor());
        List<DatalogFact> facts = singleton.accept(new FactGenVisitor());
        emitFactsToFile(facts, writers, tmpPath);
    }

}
