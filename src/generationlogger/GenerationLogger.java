package generationlogger;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Records every step of the compilation pipeline.
 * Each method appends a timestamped entry to the log.
 * The log is later serialized to generation_log.txt.
 */
public class GenerationLogger {

    private final List<String> log;
    private PrintStream out;

    public GenerationLogger() {
        this.log = new ArrayList<>();
        this.out = System.out;
    }

    public void setOutput(PrintStream out) {
        this.out = out;
    }

    public void log(String message) {
        log.add(message);
        if (out != null) {
            out.println(message);
        }
    }

    // ---- Pipeline step logging ----

    public void parsingPython() {
        log("Python Parser started");
    }

    public void generatingPythonAst() {
        log("Python AST generated");
    }

    public void semanticAnalysis() {
        log("Semantic Analysis completed");
    }

    public void generatingContext() {
        log("Context generated");
    }

    public void parsingJinja() {
        log("Jinja Parser completed");
    }

    public void generatingJinjaAst() {
        log("Jinja AST generated");
    }

    public void renderingTemplates() {
        log("Template rendering started");
    }

    public void resolvingVariables() {
        log("Variables resolved");
    }

    public void expandingLoops() {
        log("For loop expanded");
    }

    public void evaluatingConditions() {
        log("If statement evaluated");
    }

    public void generatingHtml(String fileName) {
        log("Generated " + fileName);
    }

    public void writingHtml() {
        log("Writing HTML pages");
    }

    public void copyingAssets() {
        log("Copying Assets...");
    }

    public void generationCompleted() {
        log("Compilation finished successfully");
    }

    // ---- Validation logging ----

    public void validationStarted() {
        log("Validation started");
    }

    public void validatingNoJinjaSyntax() {
        log("Checking for remaining Jinja syntax...");
    }

    public void validatingHtmlPages() {
        log("Validating generated HTML pages...");
    }

    public void validationPassed() {
        log("Validation passed - all pages are pure HTML");
    }

    public void validationFailed(String detail) {
        log("Validation FAILED: " + detail);
    }

    // ---- Utility ----

    public String getLogText() {
        StringBuilder sb = new StringBuilder();
        for (String line : log) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public List<String> getLogEntries() {
        return new ArrayList<>(log);
    }
}
