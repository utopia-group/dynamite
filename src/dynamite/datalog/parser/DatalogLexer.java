// Generated from scripts/Datalog.g4 by ANTLR 4.7.1
package dynamite.datalog.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DatalogLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, INPUT=2, OUTPUT=3, RELATION=4, TYPE=5, CAT=6, COMMA=7, PERIOD=8, 
		Q_MARK=9, LEFT_PAREN=10, RIGHT_PAREN=11, COLON=12, COLON_DASH=13, MULTIPLY=14, 
		ADD=15, EQ=16, NE=17, LT=18, LE=19, GT=20, GE=21, INT=22, STRING=23, ID=24;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"WS", "INPUT", "OUTPUT", "RELATION", "TYPE", "CAT", "COMMA", "PERIOD", 
		"Q_MARK", "LEFT_PAREN", "RIGHT_PAREN", "COLON", "COLON_DASH", "MULTIPLY", 
		"ADD", "EQ", "NE", "LT", "LE", "GT", "GE", "INT", "STRING", "ID"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'.input'", "'.output'", "'.decl'", "'.type'", "'cat'", "','", 
		"'.'", "'?'", "'('", "')'", "':'", "':-'", "'*'", "'+'", "'='", "'!='", 
		"'<'", "'<='", "'>'", "'>='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "WS", "INPUT", "OUTPUT", "RELATION", "TYPE", "CAT", "COMMA", "PERIOD", 
		"Q_MARK", "LEFT_PAREN", "RIGHT_PAREN", "COLON", "COLON_DASH", "MULTIPLY", 
		"ADD", "EQ", "NE", "LT", "LE", "GT", "GE", "INT", "STRING", "ID"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public DatalogLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Datalog.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\32\u0091\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\3\2\6\2\65\n\2\r\2\16\2\66\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r"+
		"\3\r\3\16\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\23"+
		"\3\23\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\26\3\27\6\27}\n\27\r\27\16"+
		"\27~\3\30\3\30\7\30\u0083\n\30\f\30\16\30\u0086\13\30\3\30\3\30\3\31\3"+
		"\31\3\31\7\31\u008d\n\31\f\31\16\31\u0090\13\31\2\2\32\3\3\5\4\7\5\t\6"+
		"\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24"+
		"\'\25)\26+\27-\30/\31\61\32\3\2\6\5\2\13\f\17\17\"\"\5\2\f\f\17\17$$\5"+
		"\2C\\aac|\6\2\62;C\\aac|\2\u0095\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2"+
		"\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2"+
		"\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2"+
		"\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2"+
		"\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\3\64\3\2\2\2\5:\3\2\2\2"+
		"\7A\3\2\2\2\tI\3\2\2\2\13O\3\2\2\2\rU\3\2\2\2\17Y\3\2\2\2\21[\3\2\2\2"+
		"\23]\3\2\2\2\25_\3\2\2\2\27a\3\2\2\2\31c\3\2\2\2\33e\3\2\2\2\35h\3\2\2"+
		"\2\37j\3\2\2\2!l\3\2\2\2#n\3\2\2\2%q\3\2\2\2\'s\3\2\2\2)v\3\2\2\2+x\3"+
		"\2\2\2-|\3\2\2\2/\u0080\3\2\2\2\61\u0089\3\2\2\2\63\65\t\2\2\2\64\63\3"+
		"\2\2\2\65\66\3\2\2\2\66\64\3\2\2\2\66\67\3\2\2\2\678\3\2\2\289\b\2\2\2"+
		"9\4\3\2\2\2:;\7\60\2\2;<\7k\2\2<=\7p\2\2=>\7r\2\2>?\7w\2\2?@\7v\2\2@\6"+
		"\3\2\2\2AB\7\60\2\2BC\7q\2\2CD\7w\2\2DE\7v\2\2EF\7r\2\2FG\7w\2\2GH\7v"+
		"\2\2H\b\3\2\2\2IJ\7\60\2\2JK\7f\2\2KL\7g\2\2LM\7e\2\2MN\7n\2\2N\n\3\2"+
		"\2\2OP\7\60\2\2PQ\7v\2\2QR\7{\2\2RS\7r\2\2ST\7g\2\2T\f\3\2\2\2UV\7e\2"+
		"\2VW\7c\2\2WX\7v\2\2X\16\3\2\2\2YZ\7.\2\2Z\20\3\2\2\2[\\\7\60\2\2\\\22"+
		"\3\2\2\2]^\7A\2\2^\24\3\2\2\2_`\7*\2\2`\26\3\2\2\2ab\7+\2\2b\30\3\2\2"+
		"\2cd\7<\2\2d\32\3\2\2\2ef\7<\2\2fg\7/\2\2g\34\3\2\2\2hi\7,\2\2i\36\3\2"+
		"\2\2jk\7-\2\2k \3\2\2\2lm\7?\2\2m\"\3\2\2\2no\7#\2\2op\7?\2\2p$\3\2\2"+
		"\2qr\7>\2\2r&\3\2\2\2st\7>\2\2tu\7?\2\2u(\3\2\2\2vw\7@\2\2w*\3\2\2\2x"+
		"y\7@\2\2yz\7?\2\2z,\3\2\2\2{}\4\62;\2|{\3\2\2\2}~\3\2\2\2~|\3\2\2\2~\177"+
		"\3\2\2\2\177.\3\2\2\2\u0080\u0084\7$\2\2\u0081\u0083\n\3\2\2\u0082\u0081"+
		"\3\2\2\2\u0083\u0086\3\2\2\2\u0084\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085"+
		"\u0087\3\2\2\2\u0086\u0084\3\2\2\2\u0087\u0088\7$\2\2\u0088\60\3\2\2\2"+
		"\u0089\u008e\t\4\2\2\u008a\u008d\t\5\2\2\u008b\u008d\5\23\n\2\u008c\u008a"+
		"\3\2\2\2\u008c\u008b\3\2\2\2\u008d\u0090\3\2\2\2\u008e\u008c\3\2\2\2\u008e"+
		"\u008f\3\2\2\2\u008f\62\3\2\2\2\u0090\u008e\3\2\2\2\b\2\66~\u0084\u008c"+
		"\u008e\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}