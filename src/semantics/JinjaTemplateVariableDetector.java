package semantics;

import java.util.*;

public class JinjaTemplateVariableDetector {

    private final List<SemanticError> errors = new ArrayList<>();

    public List<SemanticError> getErrors() {
        return errors;
    }

    public void analyze(JinjaSymbolCollector collector, String htmlFilePath) {
        errors.clear();
        String baseName = deriveBaseName(htmlFilePath);
        String templateName = baseName + ".html";

        Set<String> passedVars = new HashSet<>(TemplateVariableChecker.getVarsForTemplate(templateName));
        Set<String> loopVarNames = collector.getLoopVars().keySet();

        for (var entry : collector.getReadVars().entrySet()) {
            String dottedName = entry.getKey();
            int line = entry.getValue();
            String baseVar = dottedName.contains(".")
                    ? dottedName.substring(0, dottedName.indexOf('.'))
                    : dottedName;

            if (loopVarNames.contains(baseVar)) continue;

            if (!passedVars.contains(baseVar)) {
                errors.add(new SemanticError(line,
                        "Flask Error: Variable '" + baseVar
                                + "' was not passed from the route via render_template()."));
            }
        }
    }

    public void printErrors() {
        if (errors.isEmpty()) return;
        System.out.println("\n========== FLASK/JINJA ERRORS (" + errors.size() + ") ==========");
        int i = 1;
        for (SemanticError e : errors) {
            System.out.println("  " + i + ". " + e);
            i++;
        }
        System.out.println("===============================================================");
    }

    private static String deriveBaseName(String filePath) {
        String name = filePath.replace("\\", "/");
        if (name.contains("/")) {
            name = name.substring(name.lastIndexOf('/') + 1);
        }
        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf('.'));
        }
        return name;
    }
}
