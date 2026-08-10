package listener;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import semantic.SemanticError;

import java.util.ArrayList;
import java.util.List;

public class CustomErrorListener extends BaseErrorListener {
    private final List<SemanticError> syntaxErrors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        String detail = msg;
        if (offendingSymbol instanceof Token offendingToken) {
            String tokenText = offendingToken.getText().replace("\n", "\\n").replace("\r", "\\r");
            detail = msg + " (token: '" + tokenText + "')";
        }
        syntaxErrors.add(new SemanticError(line, "Syntax Error: " + detail));
    }

    public boolean hasErrors() {
        return !syntaxErrors.isEmpty();
    }

    public List<SemanticError> getSyntaxErrors() {
        return syntaxErrors;
    }
}