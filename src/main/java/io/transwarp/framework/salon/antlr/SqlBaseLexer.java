// Generated from /home/stk/Projects/Proj1_2/src/main/antlr4/io/transwarp/framework/salon/sql/SqlBase.g4 by ANTLR 4.7
package io.transwarp.framework.salon.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SqlBaseLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, SELECT=3, FROM=4, JOIN=5, ON=6, WHERE=7, EQ=8, INTEGER_VALUE=9, 
		IDENTIFIER=10, WS=11;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "SELECT", "FROM", "JOIN", "ON", "WHERE", "EQ", "INTEGER_VALUE", 
		"IDENTIFIER", "DIGIT", "LETTER", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "'.'", "'SELECT'", "'FROM'", "'JOIN'", "'ON'", "'WHERE'", 
		"'='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "SELECT", "FROM", "JOIN", "ON", "WHERE", "EQ", "INTEGER_VALUE", 
		"IDENTIFIER", "WS"
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


	public SqlBaseLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SqlBase.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\rT\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\t\3\t\3\n\6\n?\n\n\r\n\16\n@\3\13\3\13\3\13\6\13F\n\13\r"+
		"\13\16\13G\3\f\3\f\3\r\3\r\3\16\6\16O\n\16\r\16\16\16P\3\16\3\16\2\2\17"+
		"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\2\31\2\33\r\3\2\5\3"+
		"\2\62;\4\2C\\c|\5\2\13\f\17\17\"\"\2V\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\33\3\2\2\2\3\35\3\2\2\2\5\37\3\2\2\2\7!\3\2"+
		"\2\2\t(\3\2\2\2\13-\3\2\2\2\r\62\3\2\2\2\17\65\3\2\2\2\21;\3\2\2\2\23"+
		">\3\2\2\2\25E\3\2\2\2\27I\3\2\2\2\31K\3\2\2\2\33N\3\2\2\2\35\36\7.\2\2"+
		"\36\4\3\2\2\2\37 \7\60\2\2 \6\3\2\2\2!\"\7U\2\2\"#\7G\2\2#$\7N\2\2$%\7"+
		"G\2\2%&\7E\2\2&\'\7V\2\2\'\b\3\2\2\2()\7H\2\2)*\7T\2\2*+\7Q\2\2+,\7O\2"+
		"\2,\n\3\2\2\2-.\7L\2\2./\7Q\2\2/\60\7K\2\2\60\61\7P\2\2\61\f\3\2\2\2\62"+
		"\63\7Q\2\2\63\64\7P\2\2\64\16\3\2\2\2\65\66\7Y\2\2\66\67\7J\2\2\678\7"+
		"G\2\289\7T\2\29:\7G\2\2:\20\3\2\2\2;<\7?\2\2<\22\3\2\2\2=?\5\27\f\2>="+
		"\3\2\2\2?@\3\2\2\2@>\3\2\2\2@A\3\2\2\2A\24\3\2\2\2BF\5\31\r\2CF\5\27\f"+
		"\2DF\7a\2\2EB\3\2\2\2EC\3\2\2\2ED\3\2\2\2FG\3\2\2\2GE\3\2\2\2GH\3\2\2"+
		"\2H\26\3\2\2\2IJ\t\2\2\2J\30\3\2\2\2KL\t\3\2\2L\32\3\2\2\2MO\t\4\2\2N"+
		"M\3\2\2\2OP\3\2\2\2PN\3\2\2\2PQ\3\2\2\2QR\3\2\2\2RS\b\16\2\2S\34\3\2\2"+
		"\2\7\2@EGP\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}