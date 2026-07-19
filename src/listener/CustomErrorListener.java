package listener;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class CustomErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {

        // Check if the exception itself is null or vague
        if (e == null && msg.contains("extraneous input")) {
            System.err.println("!!! PARSER ERROR DETECTED (VAGUE/NULL) !!!");
            System.err.printf("Line %d:%d: Could not process token at this position. Offending token may be missing. Message: %s\n",
                    line, charPositionInLine, msg);
        } else {
            System.err.println("!!! PARSER ERROR DETECTED !!!");
            System.err.printf("Line %d:%d: %s\n", line, charPositionInLine, msg);
        }

        // Optional: Dump the token that caused the issue
        if (offendingSymbol instanceof Token offendingToken) {
            System.err.printf("Offending Token: Type=%s, Text='%s', Channel=%d\n",
                    recognizer.getVocabulary().getSymbolicName(offendingToken.getType()),
                    offendingToken.getText().replace("\n", "\\n").replace("\r", "\\r"),
                    offendingToken.getChannel());
        }

        // Throw an exception to halt processing immediately
        throw new RuntimeException("Syntax Error encountered: " + msg);
    }
}