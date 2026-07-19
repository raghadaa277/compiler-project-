package semantics;

public class SemanticError {
    public final int line;
    public final String message;

    public SemanticError(int line, String message) {
        this.line = line;
        this.message = message;
    }

    @Override
    public String toString() {
        if (line < 0) return message;
        return "Line " + line + ": " + message;
    }
}
