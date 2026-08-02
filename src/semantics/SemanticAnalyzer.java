package semantics;

import ast.Program;

import java.util.*;

public class SemanticAnalyzer {

    private final List<ErrorDetector> detectors = new ArrayList<>();
    private final List<SemanticError> allErrors = new ArrayList<>();

    public SemanticAnalyzer() {
        detectors.add(new UndefinedSymbolDetector());
        detectors.add(new ReturnOutsideFunctionDetector());
        detectors.add(new TypeMismatchDetector());
        detectors.add(new ArgumentCountDetector());
        detectors.add(new UnreachableCodeDetector());
        detectors.add(new InfiniteRecursionDetector());
    }

    public boolean analyze(Program program) {
        return analyze(program, null);
    }

    public boolean analyze(Program program, String filePath) {
        allErrors.clear();

        TemplateNotFoundDetector tnd = new TemplateNotFoundDetector();
        if (filePath != null) tnd.setFilePath(filePath);

        List<ErrorDetector> allDetectors = new ArrayList<>(detectors);
        allDetectors.add(tnd);

        for (ErrorDetector detector : allDetectors) {
            detector.reset();
            detector.detect(program);
            allErrors.addAll(detector.getErrors());
        }
        printErrors();
        return allErrors.isEmpty();
    }

    public void printErrors() {
        if (allErrors.isEmpty()) {
            System.out.println("\n[Semantic Analyzer] No semantic errors found. \u2713");
            return;
        }
        allErrors.sort((a, b) -> {
            if (a.line < 0 && b.line < 0) return 0;
            if (a.line < 0) return 1;
            if (b.line < 0) return -1;
            return Integer.compare(a.line, b.line);
        });
        System.out.println("\n========== SEMANTIC ANALYSIS ERRORS (" + allErrors.size() + ") ==========");
        int i = 1;
        for (SemanticError e : allErrors) {
            System.out.println("  " + i + ". " + e);
            i++;
        }
        System.out.println("===========================================================");
    }

    public List<SemanticError> getErrors() {
        return allErrors;
    }
}
