package dynamite.datalog.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.parser.DatalogParser.ProgramContext;

public class AntlrDatalogParser {

    public static DatalogProgram parse(String input) {
        CharStream cs = CharStreams.fromString(input);
        DatalogLexer lexer = new DatalogLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DatalogParser parser = new DatalogParser(tokens);

        ProgramContext context = parser.program();
        AntlrToAstVisitor visitor = new AntlrToAstVisitor();
        DatalogProgram datalogAST = visitor.visitProgram(context);
        return datalogAST;
    }

}
