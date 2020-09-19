// Generated from scripts/Datalog.g4 by ANTLR 4.7.1
package dynamite.datalog.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DatalogParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, INPUT=2, OUTPUT=3, RELATION=4, TYPE=5, CAT=6, COMMA=7, PERIOD=8, 
		Q_MARK=9, LEFT_PAREN=10, RIGHT_PAREN=11, COLON=12, COLON_DASH=13, MULTIPLY=14, 
		ADD=15, EQ=16, NE=17, LT=18, LE=19, GT=20, GE=21, INT=22, STRING=23, ID=24;
	public static final int
		RULE_program = 0, RULE_decls = 1, RULE_decl = 2, RULE_inputDecl = 3, RULE_outputDecl = 4, 
		RULE_paramMap = 5, RULE_entry = 6, RULE_relationDecl = 7, RULE_attrs = 8, 
		RULE_attr = 9, RULE_typeDecl = 10, RULE_rules = 11, RULE_datalogRule = 12, 
		RULE_heads = 13, RULE_relPred = 14, RULE_expList = 15, RULE_exp = 16, 
		RULE_cat = 17, RULE_bodies = 18, RULE_datalogPred = 19, RULE_biPred = 20, 
		RULE_op = 21, RULE_facts = 22, RULE_fact = 23;
	public static final String[] ruleNames = {
		"program", "decls", "decl", "inputDecl", "outputDecl", "paramMap", "entry", 
		"relationDecl", "attrs", "attr", "typeDecl", "rules", "datalogRule", "heads", 
		"relPred", "expList", "exp", "cat", "bodies", "datalogPred", "biPred", 
		"op", "facts", "fact"
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

	@Override
	public String getGrammarFileName() { return "Datalog.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DatalogParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public DeclsContext decls() {
			return getRuleContext(DeclsContext.class,0);
		}
		public RulesContext rules() {
			return getRuleContext(RulesContext.class,0);
		}
		public FactsContext facts() {
			return getRuleContext(FactsContext.class,0);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			decls();
			setState(49);
			rules();
			setState(50);
			facts();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclsContext extends ParserRuleContext {
		public List<DeclContext> decl() {
			return getRuleContexts(DeclContext.class);
		}
		public DeclContext decl(int i) {
			return getRuleContext(DeclContext.class,i);
		}
		public DeclsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decls; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitDecls(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclsContext decls() throws RecognitionException {
		DeclsContext _localctx = new DeclsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_decls);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INPUT) | (1L << OUTPUT) | (1L << RELATION) | (1L << TYPE))) != 0)) {
				{
				{
				setState(52);
				decl();
				}
				}
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclContext extends ParserRuleContext {
		public InputDeclContext inputDecl() {
			return getRuleContext(InputDeclContext.class,0);
		}
		public OutputDeclContext outputDecl() {
			return getRuleContext(OutputDeclContext.class,0);
		}
		public RelationDeclContext relationDecl() {
			return getRuleContext(RelationDeclContext.class,0);
		}
		public TypeDeclContext typeDecl() {
			return getRuleContext(TypeDeclContext.class,0);
		}
		public DeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclContext decl() throws RecognitionException {
		DeclContext _localctx = new DeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_decl);
		try {
			setState(62);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INPUT:
				enterOuterAlt(_localctx, 1);
				{
				setState(58);
				inputDecl();
				}
				break;
			case OUTPUT:
				enterOuterAlt(_localctx, 2);
				{
				setState(59);
				outputDecl();
				}
				break;
			case RELATION:
				enterOuterAlt(_localctx, 3);
				{
				setState(60);
				relationDecl();
				}
				break;
			case TYPE:
				enterOuterAlt(_localctx, 4);
				{
				setState(61);
				typeDecl();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InputDeclContext extends ParserRuleContext {
		public TerminalNode INPUT() { return getToken(DatalogParser.INPUT, 0); }
		public TerminalNode ID() { return getToken(DatalogParser.ID, 0); }
		public ParamMapContext paramMap() {
			return getRuleContext(ParamMapContext.class,0);
		}
		public InputDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitInputDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InputDeclContext inputDecl() throws RecognitionException {
		InputDeclContext _localctx = new InputDeclContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_inputDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(INPUT);
			setState(65);
			match(ID);
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LEFT_PAREN) {
				{
				setState(66);
				paramMap();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OutputDeclContext extends ParserRuleContext {
		public TerminalNode OUTPUT() { return getToken(DatalogParser.OUTPUT, 0); }
		public TerminalNode ID() { return getToken(DatalogParser.ID, 0); }
		public ParamMapContext paramMap() {
			return getRuleContext(ParamMapContext.class,0);
		}
		public OutputDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitOutputDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputDeclContext outputDecl() throws RecognitionException {
		OutputDeclContext _localctx = new OutputDeclContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_outputDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(OUTPUT);
			setState(70);
			match(ID);
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LEFT_PAREN) {
				{
				setState(71);
				paramMap();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamMapContext extends ParserRuleContext {
		public TerminalNode LEFT_PAREN() { return getToken(DatalogParser.LEFT_PAREN, 0); }
		public List<EntryContext> entry() {
			return getRuleContexts(EntryContext.class);
		}
		public EntryContext entry(int i) {
			return getRuleContext(EntryContext.class,i);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(DatalogParser.RIGHT_PAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(DatalogParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DatalogParser.COMMA, i);
		}
		public ParamMapContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_paramMap; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitParamMap(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamMapContext paramMap() throws RecognitionException {
		ParamMapContext _localctx = new ParamMapContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_paramMap);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(LEFT_PAREN);
			setState(75);
			entry();
			setState(80);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(76);
				match(COMMA);
				setState(77);
				entry();
				}
				}
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(83);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EntryContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(DatalogParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(DatalogParser.ID, i);
		}
		public TerminalNode EQ() { return getToken(DatalogParser.EQ, 0); }
		public TerminalNode STRING() { return getToken(DatalogParser.STRING, 0); }
		public EntryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entry; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitEntry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntryContext entry() throws RecognitionException {
		EntryContext _localctx = new EntryContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_entry);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			match(ID);
			setState(86);
			match(EQ);
			setState(87);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==ID) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelationDeclContext extends ParserRuleContext {
		public TerminalNode RELATION() { return getToken(DatalogParser.RELATION, 0); }
		public TerminalNode ID() { return getToken(DatalogParser.ID, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(DatalogParser.LEFT_PAREN, 0); }
		public AttrsContext attrs() {
			return getRuleContext(AttrsContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(DatalogParser.RIGHT_PAREN, 0); }
		public RelationDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitRelationDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationDeclContext relationDecl() throws RecognitionException {
		RelationDeclContext _localctx = new RelationDeclContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_relationDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			match(RELATION);
			setState(90);
			match(ID);
			setState(91);
			match(LEFT_PAREN);
			setState(92);
			attrs();
			setState(93);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttrsContext extends ParserRuleContext {
		public List<AttrContext> attr() {
			return getRuleContexts(AttrContext.class);
		}
		public AttrContext attr(int i) {
			return getRuleContext(AttrContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DatalogParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DatalogParser.COMMA, i);
		}
		public AttrsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attrs; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitAttrs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttrsContext attrs() throws RecognitionException {
		AttrsContext _localctx = new AttrsContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_attrs);
		int _la;
		try {
			setState(104);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(95);
				attr();
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(96);
					match(COMMA);
					setState(97);
					attr();
					}
					}
					setState(102);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case RIGHT_PAREN:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttrContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(DatalogParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(DatalogParser.ID, i);
		}
		public TerminalNode COLON() { return getToken(DatalogParser.COLON, 0); }
		public AttrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitAttr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttrContext attr() throws RecognitionException {
		AttrContext _localctx = new AttrContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_attr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			match(ID);
			setState(107);
			match(COLON);
			setState(108);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeDeclContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(DatalogParser.TYPE, 0); }
		public TerminalNode ID() { return getToken(DatalogParser.ID, 0); }
		public TypeDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitTypeDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeDeclContext typeDecl() throws RecognitionException {
		TypeDeclContext _localctx = new TypeDeclContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_typeDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			match(TYPE);
			setState(111);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RulesContext extends ParserRuleContext {
		public List<DatalogRuleContext> datalogRule() {
			return getRuleContexts(DatalogRuleContext.class);
		}
		public DatalogRuleContext datalogRule(int i) {
			return getRuleContext(DatalogRuleContext.class,i);
		}
		public RulesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rules; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitRules(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RulesContext rules() throws RecognitionException {
		RulesContext _localctx = new RulesContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_rules);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(113);
					datalogRule();
					}
					} 
				}
				setState(118);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DatalogRuleContext extends ParserRuleContext {
		public HeadsContext heads() {
			return getRuleContext(HeadsContext.class,0);
		}
		public TerminalNode COLON_DASH() { return getToken(DatalogParser.COLON_DASH, 0); }
		public BodiesContext bodies() {
			return getRuleContext(BodiesContext.class,0);
		}
		public TerminalNode PERIOD() { return getToken(DatalogParser.PERIOD, 0); }
		public DatalogRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datalogRule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitDatalogRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DatalogRuleContext datalogRule() throws RecognitionException {
		DatalogRuleContext _localctx = new DatalogRuleContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_datalogRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			heads();
			setState(120);
			match(COLON_DASH);
			setState(121);
			bodies();
			setState(122);
			match(PERIOD);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HeadsContext extends ParserRuleContext {
		public List<RelPredContext> relPred() {
			return getRuleContexts(RelPredContext.class);
		}
		public RelPredContext relPred(int i) {
			return getRuleContext(RelPredContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DatalogParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DatalogParser.COMMA, i);
		}
		public HeadsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_heads; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitHeads(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HeadsContext heads() throws RecognitionException {
		HeadsContext _localctx = new HeadsContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_heads);
		int _la;
		try {
			setState(133);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(124);
				relPred();
				setState(129);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(125);
					match(COMMA);
					setState(126);
					relPred();
					}
					}
					setState(131);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case COLON_DASH:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelPredContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(DatalogParser.ID, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(DatalogParser.LEFT_PAREN, 0); }
		public ExpListContext expList() {
			return getRuleContext(ExpListContext.class,0);
		}
		public TerminalNode RIGHT_PAREN() { return getToken(DatalogParser.RIGHT_PAREN, 0); }
		public RelPredContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relPred; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitRelPred(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelPredContext relPred() throws RecognitionException {
		RelPredContext _localctx = new RelPredContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_relPred);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			match(ID);
			setState(136);
			match(LEFT_PAREN);
			setState(137);
			expList();
			setState(138);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpListContext extends ParserRuleContext {
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DatalogParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DatalogParser.COMMA, i);
		}
		public ExpListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitExpList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpListContext expList() throws RecognitionException {
		ExpListContext _localctx = new ExpListContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_expList);
		int _la;
		try {
			setState(149);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CAT:
			case INT:
			case STRING:
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(140);
				exp();
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(141);
					match(COMMA);
					setState(142);
					exp();
					}
					}
					setState(147);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case RIGHT_PAREN:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpContext extends ParserRuleContext {
		public CatContext cat() {
			return getRuleContext(CatContext.class,0);
		}
		public TerminalNode ID() { return getToken(DatalogParser.ID, 0); }
		public TerminalNode INT() { return getToken(DatalogParser.INT, 0); }
		public TerminalNode STRING() { return getToken(DatalogParser.STRING, 0); }
		public ExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exp; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpContext exp() throws RecognitionException {
		ExpContext _localctx = new ExpContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_exp);
		try {
			setState(155);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CAT:
				enterOuterAlt(_localctx, 1);
				{
				setState(151);
				cat();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(152);
				match(ID);
				}
				break;
			case INT:
				enterOuterAlt(_localctx, 3);
				{
				setState(153);
				match(INT);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 4);
				{
				setState(154);
				match(STRING);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CatContext extends ParserRuleContext {
		public TerminalNode CAT() { return getToken(DatalogParser.CAT, 0); }
		public TerminalNode LEFT_PAREN() { return getToken(DatalogParser.LEFT_PAREN, 0); }
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(DatalogParser.COMMA, 0); }
		public TerminalNode RIGHT_PAREN() { return getToken(DatalogParser.RIGHT_PAREN, 0); }
		public CatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cat; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitCat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CatContext cat() throws RecognitionException {
		CatContext _localctx = new CatContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_cat);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			match(CAT);
			setState(158);
			match(LEFT_PAREN);
			setState(159);
			exp();
			setState(160);
			match(COMMA);
			setState(161);
			exp();
			setState(162);
			match(RIGHT_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BodiesContext extends ParserRuleContext {
		public List<DatalogPredContext> datalogPred() {
			return getRuleContexts(DatalogPredContext.class);
		}
		public DatalogPredContext datalogPred(int i) {
			return getRuleContext(DatalogPredContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DatalogParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DatalogParser.COMMA, i);
		}
		public BodiesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bodies; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitBodies(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BodiesContext bodies() throws RecognitionException {
		BodiesContext _localctx = new BodiesContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_bodies);
		int _la;
		try {
			setState(173);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CAT:
			case INT:
			case STRING:
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(164);
				datalogPred();
				setState(169);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(165);
					match(COMMA);
					setState(166);
					datalogPred();
					}
					}
					setState(171);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case PERIOD:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DatalogPredContext extends ParserRuleContext {
		public BiPredContext biPred() {
			return getRuleContext(BiPredContext.class,0);
		}
		public RelPredContext relPred() {
			return getRuleContext(RelPredContext.class,0);
		}
		public DatalogPredContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datalogPred; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitDatalogPred(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DatalogPredContext datalogPred() throws RecognitionException {
		DatalogPredContext _localctx = new DatalogPredContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_datalogPred);
		try {
			setState(177);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(175);
				biPred();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(176);
				relPred();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BiPredContext extends ParserRuleContext {
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public OpContext op() {
			return getRuleContext(OpContext.class,0);
		}
		public BiPredContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_biPred; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitBiPred(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BiPredContext biPred() throws RecognitionException {
		BiPredContext _localctx = new BiPredContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_biPred);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(179);
			exp();
			setState(180);
			op();
			setState(181);
			exp();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OpContext extends ParserRuleContext {
		public TerminalNode EQ() { return getToken(DatalogParser.EQ, 0); }
		public TerminalNode NE() { return getToken(DatalogParser.NE, 0); }
		public TerminalNode LT() { return getToken(DatalogParser.LT, 0); }
		public TerminalNode LE() { return getToken(DatalogParser.LE, 0); }
		public TerminalNode GT() { return getToken(DatalogParser.GT, 0); }
		public TerminalNode GE() { return getToken(DatalogParser.GE, 0); }
		public OpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_op; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitOp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OpContext op() throws RecognitionException {
		OpContext _localctx = new OpContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << NE) | (1L << LT) | (1L << LE) | (1L << GT) | (1L << GE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FactsContext extends ParserRuleContext {
		public List<FactContext> fact() {
			return getRuleContexts(FactContext.class);
		}
		public FactContext fact(int i) {
			return getRuleContext(FactContext.class,i);
		}
		public FactsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_facts; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitFacts(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FactsContext facts() throws RecognitionException {
		FactsContext _localctx = new FactsContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_facts);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ID) {
				{
				{
				setState(185);
				fact();
				}
				}
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FactContext extends ParserRuleContext {
		public RelPredContext relPred() {
			return getRuleContext(RelPredContext.class,0);
		}
		public TerminalNode PERIOD() { return getToken(DatalogParser.PERIOD, 0); }
		public FactContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fact; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DatalogVisitor ) return ((DatalogVisitor<? extends T>)visitor).visitFact(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FactContext fact() throws RecognitionException {
		FactContext _localctx = new FactContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_fact);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			relPred();
			setState(192);
			match(PERIOD);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\32\u00c5\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\3\2\3\2\3\2\3\3\7\38\n\3\f\3\16\3;\13\3\3\4\3\4\3\4\3\4\5\4A\n\4"+
		"\3\5\3\5\3\5\5\5F\n\5\3\6\3\6\3\6\5\6K\n\6\3\7\3\7\3\7\3\7\7\7Q\n\7\f"+
		"\7\16\7T\13\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n"+
		"\3\n\7\ne\n\n\f\n\16\nh\13\n\3\n\5\nk\n\n\3\13\3\13\3\13\3\13\3\f\3\f"+
		"\3\f\3\r\7\ru\n\r\f\r\16\rx\13\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3"+
		"\17\7\17\u0082\n\17\f\17\16\17\u0085\13\17\3\17\5\17\u0088\n\17\3\20\3"+
		"\20\3\20\3\20\3\20\3\21\3\21\3\21\7\21\u0092\n\21\f\21\16\21\u0095\13"+
		"\21\3\21\5\21\u0098\n\21\3\22\3\22\3\22\3\22\5\22\u009e\n\22\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\7\24\u00aa\n\24\f\24\16\24\u00ad"+
		"\13\24\3\24\5\24\u00b0\n\24\3\25\3\25\5\25\u00b4\n\25\3\26\3\26\3\26\3"+
		"\26\3\27\3\27\3\30\7\30\u00bd\n\30\f\30\16\30\u00c0\13\30\3\31\3\31\3"+
		"\31\3\31\2\2\32\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\2\4"+
		"\3\2\31\32\3\2\22\27\2\u00c1\2\62\3\2\2\2\49\3\2\2\2\6@\3\2\2\2\bB\3\2"+
		"\2\2\nG\3\2\2\2\fL\3\2\2\2\16W\3\2\2\2\20[\3\2\2\2\22j\3\2\2\2\24l\3\2"+
		"\2\2\26p\3\2\2\2\30v\3\2\2\2\32y\3\2\2\2\34\u0087\3\2\2\2\36\u0089\3\2"+
		"\2\2 \u0097\3\2\2\2\"\u009d\3\2\2\2$\u009f\3\2\2\2&\u00af\3\2\2\2(\u00b3"+
		"\3\2\2\2*\u00b5\3\2\2\2,\u00b9\3\2\2\2.\u00be\3\2\2\2\60\u00c1\3\2\2\2"+
		"\62\63\5\4\3\2\63\64\5\30\r\2\64\65\5.\30\2\65\3\3\2\2\2\668\5\6\4\2\67"+
		"\66\3\2\2\28;\3\2\2\29\67\3\2\2\29:\3\2\2\2:\5\3\2\2\2;9\3\2\2\2<A\5\b"+
		"\5\2=A\5\n\6\2>A\5\20\t\2?A\5\26\f\2@<\3\2\2\2@=\3\2\2\2@>\3\2\2\2@?\3"+
		"\2\2\2A\7\3\2\2\2BC\7\4\2\2CE\7\32\2\2DF\5\f\7\2ED\3\2\2\2EF\3\2\2\2F"+
		"\t\3\2\2\2GH\7\5\2\2HJ\7\32\2\2IK\5\f\7\2JI\3\2\2\2JK\3\2\2\2K\13\3\2"+
		"\2\2LM\7\f\2\2MR\5\16\b\2NO\7\t\2\2OQ\5\16\b\2PN\3\2\2\2QT\3\2\2\2RP\3"+
		"\2\2\2RS\3\2\2\2SU\3\2\2\2TR\3\2\2\2UV\7\r\2\2V\r\3\2\2\2WX\7\32\2\2X"+
		"Y\7\22\2\2YZ\t\2\2\2Z\17\3\2\2\2[\\\7\6\2\2\\]\7\32\2\2]^\7\f\2\2^_\5"+
		"\22\n\2_`\7\r\2\2`\21\3\2\2\2af\5\24\13\2bc\7\t\2\2ce\5\24\13\2db\3\2"+
		"\2\2eh\3\2\2\2fd\3\2\2\2fg\3\2\2\2gk\3\2\2\2hf\3\2\2\2ik\3\2\2\2ja\3\2"+
		"\2\2ji\3\2\2\2k\23\3\2\2\2lm\7\32\2\2mn\7\16\2\2no\7\32\2\2o\25\3\2\2"+
		"\2pq\7\7\2\2qr\7\32\2\2r\27\3\2\2\2su\5\32\16\2ts\3\2\2\2ux\3\2\2\2vt"+
		"\3\2\2\2vw\3\2\2\2w\31\3\2\2\2xv\3\2\2\2yz\5\34\17\2z{\7\17\2\2{|\5&\24"+
		"\2|}\7\n\2\2}\33\3\2\2\2~\u0083\5\36\20\2\177\u0080\7\t\2\2\u0080\u0082"+
		"\5\36\20\2\u0081\177\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083"+
		"\u0084\3\2\2\2\u0084\u0088\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0088\3\2"+
		"\2\2\u0087~\3\2\2\2\u0087\u0086\3\2\2\2\u0088\35\3\2\2\2\u0089\u008a\7"+
		"\32\2\2\u008a\u008b\7\f\2\2\u008b\u008c\5 \21\2\u008c\u008d\7\r\2\2\u008d"+
		"\37\3\2\2\2\u008e\u0093\5\"\22\2\u008f\u0090\7\t\2\2\u0090\u0092\5\"\22"+
		"\2\u0091\u008f\3\2\2\2\u0092\u0095\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094"+
		"\3\2\2\2\u0094\u0098\3\2\2\2\u0095\u0093\3\2\2\2\u0096\u0098\3\2\2\2\u0097"+
		"\u008e\3\2\2\2\u0097\u0096\3\2\2\2\u0098!\3\2\2\2\u0099\u009e\5$\23\2"+
		"\u009a\u009e\7\32\2\2\u009b\u009e\7\30\2\2\u009c\u009e\7\31\2\2\u009d"+
		"\u0099\3\2\2\2\u009d\u009a\3\2\2\2\u009d\u009b\3\2\2\2\u009d\u009c\3\2"+
		"\2\2\u009e#\3\2\2\2\u009f\u00a0\7\b\2\2\u00a0\u00a1\7\f\2\2\u00a1\u00a2"+
		"\5\"\22\2\u00a2\u00a3\7\t\2\2\u00a3\u00a4\5\"\22\2\u00a4\u00a5\7\r\2\2"+
		"\u00a5%\3\2\2\2\u00a6\u00ab\5(\25\2\u00a7\u00a8\7\t\2\2\u00a8\u00aa\5"+
		"(\25\2\u00a9\u00a7\3\2\2\2\u00aa\u00ad\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab"+
		"\u00ac\3\2\2\2\u00ac\u00b0\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ae\u00b0\3\2"+
		"\2\2\u00af\u00a6\3\2\2\2\u00af\u00ae\3\2\2\2\u00b0\'\3\2\2\2\u00b1\u00b4"+
		"\5*\26\2\u00b2\u00b4\5\36\20\2\u00b3\u00b1\3\2\2\2\u00b3\u00b2\3\2\2\2"+
		"\u00b4)\3\2\2\2\u00b5\u00b6\5\"\22\2\u00b6\u00b7\5,\27\2\u00b7\u00b8\5"+
		"\"\22\2\u00b8+\3\2\2\2\u00b9\u00ba\t\3\2\2\u00ba-\3\2\2\2\u00bb\u00bd"+
		"\5\60\31\2\u00bc\u00bb\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be\u00bc\3\2\2\2"+
		"\u00be\u00bf\3\2\2\2\u00bf/\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c2\5"+
		"\36\20\2\u00c2\u00c3\7\n\2\2\u00c3\61\3\2\2\2\239@EJRfjv\u0083\u0087\u0093"+
		"\u0097\u009d\u00ab\u00af\u00b3\u00be";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}