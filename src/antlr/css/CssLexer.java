// Generated from D:/Telegram Desktop/psychic-broccoli-main/grammars/CssLexer.g4 by ANTLR 4.13.2
package antlr.css;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CssLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CSS_LBRACE=1, CSS_RBRACE=2, CSS_LPAREN=3, CSS_RPAREN=4, CSS_COLON=5, CSS_SEMI=6, 
		CSS_COMMA=7, CSS_DOT=8, CSS_GT=9, CSS_HASH=10, CSS_HEX_COLOR=11, CSS_NUMBER=12, 
		CSS_UNIT=13, CSS_STRING=14, CSS_ID=15, CSS_Space=16, CSS_Comment=17, CSS_TILDE=18;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"CSS_LBRACE", "CSS_RBRACE", "CSS_LPAREN", "CSS_RPAREN", "CSS_COLON", 
			"CSS_SEMI", "CSS_COMMA", "CSS_DOT", "CSS_GT", "CSS_HASH", "CSS_HEX_COLOR", 
			"CSS_NUMBER", "CSS_UNIT", "CSS_STRING", "CSS_ID", "CSS_Space", "CSS_Comment", 
			"CSS_TILDE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "'('", "')'", "':'", "';'", "','", "'.'", "'>'", 
			"'#'", null, null, null, null, null, null, null, "'~'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "CSS_LBRACE", "CSS_RBRACE", "CSS_LPAREN", "CSS_RPAREN", "CSS_COLON", 
			"CSS_SEMI", "CSS_COMMA", "CSS_DOT", "CSS_GT", "CSS_HASH", "CSS_HEX_COLOR", 
			"CSS_NUMBER", "CSS_UNIT", "CSS_STRING", "CSS_ID", "CSS_Space", "CSS_Comment", 
			"CSS_TILDE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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


	public CssLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CssLexer.g4"; }

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
		"\u0004\u0000\u0012\u0093\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0004\n>\b\n\u000b\n\f\n?\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0005\u000bE\b\u000b\n\u000b\f\u000bH\t"+
		"\u000b\u0003\u000bJ\b\u000b\u0001\u000b\u0001\u000b\u0004\u000bN\b\u000b"+
		"\u000b\u000b\f\u000bO\u0003\u000bR\b\u000b\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0003\fb\b\f\u0001\r\u0001\r\u0005\rf\b\r\n\r\f\ri\t\r\u0001"+
		"\r\u0001\r\u0001\r\u0005\rn\b\r\n\r\f\rq\t\r\u0001\r\u0003\rt\b\r\u0001"+
		"\u000e\u0001\u000e\u0005\u000ex\b\u000e\n\u000e\f\u000e{\t\u000e\u0001"+
		"\u000f\u0004\u000f~\b\u000f\u000b\u000f\f\u000f\u007f\u0001\u000f\u0001"+
		"\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u0088"+
		"\b\u0010\n\u0010\f\u0010\u008b\t\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0089\u0000\u0012"+
		"\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r"+
		"\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e"+
		"\u001d\u000f\u001f\u0010!\u0011#\u0012\u0001\u0000\b\u0003\u000009AFa"+
		"f\u0001\u000019\u0001\u000009\u0001\u0000\"\"\u0001\u0000\'\'\u0002\u0000"+
		"AZaz\u0004\u0000--09AZaz\u0003\u0000\t\n\r\r  \u00a3\u0000\u0001\u0001"+
		"\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001"+
		"\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000"+
		"\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000"+
		"\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000"+
		"\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000"+
		"\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000"+
		"\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000"+
		"\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000"+
		"\u0000#\u0001\u0000\u0000\u0000\u0001%\u0001\u0000\u0000\u0000\u0003\'"+
		"\u0001\u0000\u0000\u0000\u0005)\u0001\u0000\u0000\u0000\u0007+\u0001\u0000"+
		"\u0000\u0000\t-\u0001\u0000\u0000\u0000\u000b/\u0001\u0000\u0000\u0000"+
		"\r1\u0001\u0000\u0000\u0000\u000f3\u0001\u0000\u0000\u0000\u00115\u0001"+
		"\u0000\u0000\u0000\u00137\u0001\u0000\u0000\u0000\u00159\u0001\u0000\u0000"+
		"\u0000\u0017I\u0001\u0000\u0000\u0000\u0019a\u0001\u0000\u0000\u0000\u001b"+
		"s\u0001\u0000\u0000\u0000\u001du\u0001\u0000\u0000\u0000\u001f}\u0001"+
		"\u0000\u0000\u0000!\u0083\u0001\u0000\u0000\u0000#\u0091\u0001\u0000\u0000"+
		"\u0000%&\u0005{\u0000\u0000&\u0002\u0001\u0000\u0000\u0000\'(\u0005}\u0000"+
		"\u0000(\u0004\u0001\u0000\u0000\u0000)*\u0005(\u0000\u0000*\u0006\u0001"+
		"\u0000\u0000\u0000+,\u0005)\u0000\u0000,\b\u0001\u0000\u0000\u0000-.\u0005"+
		":\u0000\u0000.\n\u0001\u0000\u0000\u0000/0\u0005;\u0000\u00000\f\u0001"+
		"\u0000\u0000\u000012\u0005,\u0000\u00002\u000e\u0001\u0000\u0000\u0000"+
		"34\u0005.\u0000\u00004\u0010\u0001\u0000\u0000\u000056\u0005>\u0000\u0000"+
		"6\u0012\u0001\u0000\u0000\u000078\u0005#\u0000\u00008\u0014\u0001\u0000"+
		"\u0000\u00009=\u0005#\u0000\u0000:;\u0007\u0000\u0000\u0000;<\u0007\u0000"+
		"\u0000\u0000<>\u0007\u0000\u0000\u0000=:\u0001\u0000\u0000\u0000>?\u0001"+
		"\u0000\u0000\u0000?=\u0001\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000"+
		"@\u0016\u0001\u0000\u0000\u0000AJ\u00050\u0000\u0000BF\u0007\u0001\u0000"+
		"\u0000CE\u0007\u0002\u0000\u0000DC\u0001\u0000\u0000\u0000EH\u0001\u0000"+
		"\u0000\u0000FD\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GJ\u0001"+
		"\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000IA\u0001\u0000\u0000\u0000"+
		"IB\u0001\u0000\u0000\u0000JQ\u0001\u0000\u0000\u0000KM\u0005.\u0000\u0000"+
		"LN\u0007\u0002\u0000\u0000ML\u0001\u0000\u0000\u0000NO\u0001\u0000\u0000"+
		"\u0000OM\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000PR\u0001\u0000"+
		"\u0000\u0000QK\u0001\u0000\u0000\u0000QR\u0001\u0000\u0000\u0000R\u0018"+
		"\u0001\u0000\u0000\u0000ST\u0005p\u0000\u0000Tb\u0005x\u0000\u0000UV\u0005"+
		"f\u0000\u0000Vb\u0005r\u0000\u0000Wb\u0005%\u0000\u0000XY\u0005e\u0000"+
		"\u0000Yb\u0005m\u0000\u0000Z[\u0005v\u0000\u0000[b\u0005h\u0000\u0000"+
		"\\]\u0005v\u0000\u0000]b\u0005w\u0000\u0000^_\u0005r\u0000\u0000_`\u0005"+
		"e\u0000\u0000`b\u0005m\u0000\u0000aS\u0001\u0000\u0000\u0000aU\u0001\u0000"+
		"\u0000\u0000aW\u0001\u0000\u0000\u0000aX\u0001\u0000\u0000\u0000aZ\u0001"+
		"\u0000\u0000\u0000a\\\u0001\u0000\u0000\u0000a^\u0001\u0000\u0000\u0000"+
		"b\u001a\u0001\u0000\u0000\u0000cg\u0005\"\u0000\u0000df\b\u0003\u0000"+
		"\u0000ed\u0001\u0000\u0000\u0000fi\u0001\u0000\u0000\u0000ge\u0001\u0000"+
		"\u0000\u0000gh\u0001\u0000\u0000\u0000hj\u0001\u0000\u0000\u0000ig\u0001"+
		"\u0000\u0000\u0000jt\u0005\"\u0000\u0000ko\u0005\'\u0000\u0000ln\b\u0004"+
		"\u0000\u0000ml\u0001\u0000\u0000\u0000nq\u0001\u0000\u0000\u0000om\u0001"+
		"\u0000\u0000\u0000op\u0001\u0000\u0000\u0000pr\u0001\u0000\u0000\u0000"+
		"qo\u0001\u0000\u0000\u0000rt\u0005\'\u0000\u0000sc\u0001\u0000\u0000\u0000"+
		"sk\u0001\u0000\u0000\u0000t\u001c\u0001\u0000\u0000\u0000uy\u0007\u0005"+
		"\u0000\u0000vx\u0007\u0006\u0000\u0000wv\u0001\u0000\u0000\u0000x{\u0001"+
		"\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000"+
		"z\u001e\u0001\u0000\u0000\u0000{y\u0001\u0000\u0000\u0000|~\u0007\u0007"+
		"\u0000\u0000}|\u0001\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000"+
		"\u007f}\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080"+
		"\u0081\u0001\u0000\u0000\u0000\u0081\u0082\u0006\u000f\u0000\u0000\u0082"+
		" \u0001\u0000\u0000\u0000\u0083\u0084\u0005/\u0000\u0000\u0084\u0085\u0005"+
		"*\u0000\u0000\u0085\u0089\u0001\u0000\u0000\u0000\u0086\u0088\t\u0000"+
		"\u0000\u0000\u0087\u0086\u0001\u0000\u0000\u0000\u0088\u008b\u0001\u0000"+
		"\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000\u0089\u0087\u0001\u0000"+
		"\u0000\u0000\u008a\u008c\u0001\u0000\u0000\u0000\u008b\u0089\u0001\u0000"+
		"\u0000\u0000\u008c\u008d\u0005*\u0000\u0000\u008d\u008e\u0005/\u0000\u0000"+
		"\u008e\u008f\u0001\u0000\u0000\u0000\u008f\u0090\u0006\u0010\u0000\u0000"+
		"\u0090\"\u0001\u0000\u0000\u0000\u0091\u0092\u0005~\u0000\u0000\u0092"+
		"$\u0001\u0000\u0000\u0000\r\u0000?FIOQagosy\u007f\u0089\u0001\u0006\u0000"+
		"\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}