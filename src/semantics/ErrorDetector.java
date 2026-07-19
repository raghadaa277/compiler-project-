package semantics;

import ast.Program;
import java.util.List;

public interface ErrorDetector {
    void detect(Program program);
    List<SemanticError> getErrors();
    default void reset() {}
}
