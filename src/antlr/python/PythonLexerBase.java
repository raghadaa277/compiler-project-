package antlr.python;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import java.util.ArrayDeque;
import java.util.Deque;

abstract public class PythonLexerBase extends Lexer {
    // A queue where extra tokens are pushed on (see the NEWLINE lexer rule).
    private java.util.LinkedList<Token> tokens = new java.util.LinkedList<>();
    // The stack that keeps track of the indentation level.
    private Deque<Integer> indents = new ArrayDeque<>();
    // The amount of opened braces, brackets and parenthesis.
    protected int opened = 0;
    // The most recently produced token.
    private Token lastToken = null;
    // Track the start position of the current NEWLINE rule match
    private boolean isDebug = false;

    protected PythonLexerBase(CharStream input) {
        super(input);
    }

    @Override
    public void emit(Token t) {
        super.setToken(t);
        tokens.offer(t);
    }

    @Override
    public Token nextToken() {
        // Check if the end-of-file is ahead and there are still some DEDENTS expected.
        if (_input.LA(1) == EOF && !this.indents.isEmpty()) {
            // Remove any trailing EOF tokens from our buffer.
            for (int i = tokens.size() - 1; i >= 0; i--) {
                if (tokens.get(i).getType() == EOF) {
                    tokens.remove(i);
                }
            }

            // First emit an extra line break that serves as the end of the statement.
            this.emit(createSyntheticToken(PythonLexer.NEWLINE, "\n"));

            // Now emit as much DEDENT tokens as needed.
            while (!indents.isEmpty()) {
                this.emit(createSyntheticToken(PythonLexer.DEDENT, ""));
                indents.pop();
            }

            // Put the EOF back on the token stream.
            this.emit(createSyntheticToken(PythonLexer.EOF, "<EOF>"));
        }

        Token next = super.nextToken();

        if (next.getChannel() == Token.DEFAULT_CHANNEL) {
            // Keep track of the last token on the default channel.
            this.lastToken = next;
        }

        return tokens.isEmpty() ? next : tokens.poll();
    }

    private Token createSyntheticToken(int type, String text) {
        if (isDebug) {
            System.out.println("=== DEBUG createSyntheticToken ===");
            System.out.println("type=" + type + " text='" + escapeString(text) + "'");
        }

        // Create synthetic token (not from input)
        CommonToken token = new CommonToken(type, text);

        // Set positions to -1 to indicate synthetic
        token.setStartIndex(-1);
        token.setStopIndex(-1);

        // Use line/char position from last token (or default)
        if (lastToken != null) {
            token.setLine(lastToken.getLine());
            token.setCharPositionInLine(lastToken.getCharPositionInLine() + 1);
        } else {
            token.setLine(1);
            token.setCharPositionInLine(0);
        }

        return token;
    }

    private Token createDEDENT() {
        // Create proper DEDENT token
        int pos = getCharIndex();
        CommonToken token = new CommonToken(PythonLexer.DEDENT, "");
        token.setStartIndex(pos);
        token.setStopIndex(pos);
        token.setLine(getLine());
        token.setCharPositionInLine(getCharPositionInLine());
        return token;
    }

    /**
     * Creates a token at a specific position in the input
     */
    private CommonToken createTokenAtPosition() {

        return (CommonToken) createDEDENT();
    }

    /**
     * Creates a token with explicit start and stop positions
     */
    private CommonToken createTokenWithPositions(int type, String text, int start, int stop) {
        // Ensure non-negative positions
        if (start < 0) start = 0;
        if (stop < 0) stop = 0;
        if (isDebug) {
            System.out.println("=== DEBUG createTokenWithPositions ===");
            System.out.println("type=" + type + " (" + PythonLexer.VOCABULARY.getSymbolicName(type) + ")");
            System.out.println("text='" + escapeString(text) + "', length=" + text.length());
            System.out.println("start=" + start + ", stop=" + stop);
        }

        return new CommonToken(this._tokenFactorySourcePair, type, DEFAULT_TOKEN_CHANNEL, start, stop);
    }

    // Calculates the indentation of the provided spaces, taking the
    // following rules into account:
    //
    // "Tabs are replaced (from left to right) by one to eight spaces
    //  such that the total number of characters up to and including
    //  the replacement is a multiple of eight [...]"
    //
    //  -- https://docs.python.org/3.1/reference/lexical_analysis.html#indentation
    static int getIndentationCount(String spaces) {
        int count = 0;
        for (char ch : spaces.toCharArray()) {
            if (ch == '\t') {
                count += 8 - (count % 8);
            } else {// A normal space char.
                count++;
            }
        }

        return count;
    }

    boolean atStartOfInput() {
        return super.getCharPositionInLine() == 0 && super.getLine() == 1;
    }

    void openBrace() {
        this.opened++;
    }

    void closeBrace() {
        this.opened--;
    }

    void onNewLine() {
        // Save the start position of this rule match
        int ruleStart = _tokenStartCharIndex;
        String original = getText();
        if (isDebug) {
            System.out.println("=== DEBUG onNewLine ===");
            System.out.println("ruleStart=" + ruleStart);
            System.out.println("getText() = '" + escapeString(original) + "'");
            System.out.println("Length = " + original.length());
            for (int i = 0; i < original.length(); i++) {
                char c = original.charAt(i);
                System.out.printf("  [%d] = 0x%02x %s\n", i, (int) c,
                        c == '\r' ? "\\r" : c == '\n' ? "\\n" : c == ' ' ? "[space]" : "'" + c + "'");
            }
        }

        String newLine = original.replaceAll("[^\r\n\f]+", "");
        String spaces = original.replaceAll("[\r\n\f]+", "");
        if (isDebug) {
            System.out.println("newLine='" + escapeString(newLine) + "' length=" + newLine.length());
            System.out.println("spaces='" + spaces + "' length=" + spaces.length());
        }

        // Strip newlines inside open clauses except if we are near EOF. We keep NEWLINEs near EOF to
        // satisfy the final newline needed by the single_put rule used by the REPL.
        int next = _input.LA(1);
        int nextnext = _input.LA(2);
        if (opened > 0 || (nextnext != -1 && (next == '\r' || next == '\n' || next == '\f' || next == '#'))) {
            // If we're inside a list or on a blank line, ignore all indents,
            // dedents and line breaks.
            skip();
        } else {
            // Create NEWLINE token (first part of the matched text)

            int newLineEnd = ruleStart + newLine.length() - 1;
            emit(createTokenWithPositions(PythonLexer.NEWLINE, newLine, ruleStart, newLineEnd));

            int indent = getIndentationCount(spaces);
            int previous = indents.isEmpty() ? 0 : indents.peek();

            if (indent == previous) {
                // skip indents of the same size as the present indent-size
                skip();
            } else if (indent > previous) {
                indents.push(indent);
                // Create INDENT token (second part of the matched text)
                int spacesStart = ruleStart + newLine.length();
                int spacesEnd = ruleStart + original.length() - 1;
                emit(createTokenWithPositions(PythonLexer.INDENT, spaces, spacesStart, spacesEnd));
            } else {
                // Possibly emit more than 1 DEDENT token.
                while (!indents.isEmpty() && indents.peek() > indent) {
                    // DEDENT tokens have no text, use current position
                    emit(createTokenAtPosition());
                    indents.pop();
                }
            }
        }
    }

    String escapeString(String s) {
        return s.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    @Override
    public void reset() {
        tokens = new java.util.LinkedList<>();
        indents = new ArrayDeque<>();
        opened = 0;
        lastToken = null;
        super.reset();
    }
}
