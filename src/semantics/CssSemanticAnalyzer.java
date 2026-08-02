package semantics;

import ast.css.*;
import ast.cssTerm.*;
import ast.htmlElement.StyleSheet;

import java.util.*;

public class CssSemanticAnalyzer {

    private final List<SemanticError> errors = new ArrayList<>();
    private final Set<String> seenSelectors = new HashSet<>();

    private static final Set<String> KNOWN_PROPERTIES = new HashSet<>(Arrays.asList(
            "color", "background", "background-color", "background-image", "background-size",
            "background-position", "background-repeat", "background-attachment",
            "font-size", "font-family", "font-weight", "font-style", "font", "font-display",
            "margin", "margin-top", "margin-right", "margin-bottom", "margin-left",
            "padding", "padding-top", "padding-right", "padding-bottom", "padding-left",
            "border", "border-color", "border-width", "border-style", "border-radius",
            "border-top", "border-right", "border-bottom", "border-left",
            "border-collapse", "border-spacing",
            "width", "height", "min-width", "min-height", "max-width", "max-height",
            "display", "position", "top", "right", "bottom", "left",
            "float", "clear", "overflow", "overflow-x", "overflow-y", "visibility", "opacity",
            "text-align", "text-decoration", "text-transform", "text-shadow", "line-height",
            "letter-spacing", "word-spacing", "white-space", "word-wrap",
            "list-style", "list-style-type", "list-style-position", "list-style-image",
            "box-shadow", "box-sizing", "transition", "transform", "transform-origin",
            "flex", "flex-direction", "flex-wrap", "flex-grow", "flex-shrink", "flex-basis",
            "justify-content", "align-items", "align-content", "align-self", "order",
            "grid", "grid-template", "grid-template-columns", "grid-template-rows",
            "grid-template-areas", "grid-column", "grid-row", "grid-gap", "gap",
            "column-gap", "row-gap",
            "z-index", "cursor", "outline", "outline-color", "outline-style", "outline-width",
            "vertical-align", "content", "src", "font-face",
            "animation", "animation-name", "animation-duration", "animation-delay",
            "animation-iteration-count", "animation-direction", "animation-timing-function",
            "object-fit", "object-position",
            "filter", "backdrop-filter",
            "clip", "clip-path",
            "resize", "user-select", "pointer-events",
            "table-layout", "empty-cells", "caption-side"
    ));

    // Categories: LENGTH, COLOR, NUMBER, KEYWORD_ONLY
    private static final Set<String> LENGTH_PROPS = new HashSet<>(Arrays.asList(
            "width", "height", "min-width", "min-height", "max-width", "max-height",
            "margin", "margin-top", "margin-right", "margin-bottom", "margin-left",
            "padding", "padding-top", "padding-right", "padding-bottom", "padding-left",
            "font-size", "line-height", "letter-spacing", "word-spacing",
            "border-width", "border-radius", "border-top", "border-right", "border-bottom", "border-left",
            "top", "right", "bottom", "left",
            "outline-width", "outline-offset",
            "flex-basis", "gap", "column-gap", "row-gap", "grid-gap",
            "grid-template-columns", "grid-template-rows",
            "background-size", "background-position",
            "transform-origin", "object-position",
            "box-shadow", "text-shadow",
            "border-spacing", "vertical-align", "indent"
    ));

    private static final Set<String> COLOR_PROPS = new HashSet<>(Arrays.asList(
            "color", "background-color", "border-color",
            "border-top-color", "border-right-color", "border-bottom-color", "border-left-color",
            "outline-color", "text-decoration-color",
            "border-color"
    ));

    private static final Set<String> NUMERIC_PROPS = new HashSet<>(Arrays.asList(
            "opacity", "z-index", "flex-grow", "flex-shrink", "order",
            "animation-iteration-count", "column-count"
    ));

    private static final Set<String> GLOBAL_KEYWORDS = new HashSet<>(Arrays.asList(
            "auto", "inherit", "initial", "unset", "none", "normal", "0"
    ));

    private static final Set<String> LENGTH_KEYWORDS = new HashSet<>(Arrays.asList(
            "auto", "inherit", "initial", "unset", "none", "normal", "0",
            "thin", "medium", "thick",
            "max-content", "min-content", "fit-content", "cover", "contain"
    ));

    private static final Set<String> NAMED_COLORS = new HashSet<>(Arrays.asList(
            "red", "blue", "green", "yellow", "black", "white", "gray", "grey",
            "orange", "purple", "pink", "brown", "cyan", "magenta", "lime",
            "navy", "teal", "aqua", "fuchsia", "maroon", "olive", "silver",
            "transparent", "currentcolor", "currentColor"
    ));

    private static final Set<String> DISPLAY_VALUES = new HashSet<>(Arrays.asList(
            "block", "inline", "inline-block", "flex", "inline-flex", "grid",
            "inline-grid", "table", "inline-table", "none", "contents",
            "list-item", "flow-root", "inherit", "initial", "unset"
    ));

    private static final Set<String> POSITION_VALUES = new HashSet<>(Arrays.asList(
            "static", "relative", "absolute", "fixed", "sticky", "inherit", "initial", "unset"
    ));

    private static final Set<String> FLOAT_VALUES = new HashSet<>(Arrays.asList(
            "left", "right", "none", "inherit", "initial", "unset"
    ));

    private static final Set<String> OVERFLOW_VALUES = new HashSet<>(Arrays.asList(
            "visible", "hidden", "scroll", "auto", "clip", "inherit", "initial", "unset"
    ));

    private static final Set<String> TEXT_ALIGN_VALUES = new HashSet<>(Arrays.asList(
            "left", "right", "center", "justify", "start", "end", "inherit", "initial", "unset"
    ));

    private static final Set<String> FONT_WEIGHT_VALUES = new HashSet<>(Arrays.asList(
            "normal", "bold", "bolder", "lighter",
            "100", "200", "300", "400", "500", "600", "700", "800", "900",
            "inherit", "initial", "unset"
    ));

    private static final Set<String> BORDER_STYLE_VALUES = new HashSet<>(Arrays.asList(
            "none", "hidden", "solid", "dashed", "dotted", "double",
            "groove", "ridge", "inset", "outset",
            "inherit", "initial", "unset"
    ));

    private static final Set<String> VISIBILITY_VALUES = new HashSet<>(Arrays.asList(
            "visible", "hidden", "collapse", "inherit", "initial", "unset"
    ));

    private static final Set<String> OBJECT_FIT_VALUES = new HashSet<>(Arrays.asList(
            "fill", "contain", "cover", "none", "scale-down", "inherit", "initial", "unset"
    ));

    private static final Set<String> CURSOR_VALUES = new HashSet<>(Arrays.asList(
            "auto", "default", "pointer", "crosshair", "text", "wait", "help",
            "move", "not-allowed", "grab", "grabbing", "zoom-in", "zoom-out",
            "inherit", "initial", "unset"
    ));

    private static final Set<String> TEXT_DECORATION_VALUES = new HashSet<>(Arrays.asList(
            "none", "underline", "overline", "line-through", "blink",
            "inherit", "initial", "unset"
    ));

    private static final Set<String> WHITESPACE_VALUES = new HashSet<>(Arrays.asList(
            "normal", "nowrap", "pre", "pre-wrap", "pre-line", "break-spaces",
            "inherit", "initial", "unset"
    ));

    public boolean analyze(StyleSheet styleSheet) {
        errors.clear();
        seenSelectors.clear();
        if (styleSheet != null && styleSheet.ruleSets != null) {
            for (RuleSet rule : styleSheet.ruleSets) {
                analyzeRuleSet(rule);
            }
        }
        printErrors();
        return errors.isEmpty();
    }

    private void analyzeRuleSet(RuleSet rule) {
        if (rule == null) return;

        // Check for duplicate selectors
        if (rule.selectorDeclaration != null && rule.selectorDeclaration.selectorLists != null) {
            String selectorStr = rule.selectorDeclaration.toSelectorString();
            if (seenSelectors.contains(selectorStr)) {
                errors.add(new SemanticError(rule.line_number,
                        "CSS Error: Duplicate selector '" + selectorStr + "'."));
            } else {
                seenSelectors.add(selectorStr);
            }
        }

        // Check for empty rule set
        if (rule.declarationList == null || rule.declarationList.declarations == null
                || rule.declarationList.declarations.isEmpty()) {
            errors.add(new SemanticError(rule.line_number,
                    "CSS Error: Empty rule set (no declarations)."));
            return;
        }

        // Check each declaration
        for (CssDeclaration decl : rule.declarationList.declarations) {
            if (decl == null) continue;
            if (decl.id != null && !decl.id.isEmpty()) {
                String propLower = decl.id.toLowerCase();
                if (!KNOWN_PROPERTIES.contains(propLower)) {
                    errors.add(new SemanticError(decl.line_number,
                            "CSS Error: Unknown property '" + decl.id + "'."));
                } else {
                    validateDeclarationValue(decl, propLower);
                }
            }
        }
    }

    private void validateDeclarationValue(CssDeclaration decl, String propLower) {
        if (decl.cssTermList == null || decl.cssTermList.isEmpty()) {
            errors.add(new SemanticError(decl.line_number,
                    "CSS Error: Property '" + decl.id + "' has no value."));
            return;
        }

        for (CssTerm term : decl.cssTermList) {
            if (term == null) continue;

            // Only validate identifier terms (bare words like "hello")
            if (term instanceof IdentifierTerm it) {
                String val = it.value;
                if (val == null || val.isEmpty()) continue;

                // Global keywords are always valid
                if (GLOBAL_KEYWORDS.contains(val)) continue;

                // Check based on property category
                if (LENGTH_PROPS.contains(propLower)) {
                    if (!LENGTH_KEYWORDS.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for '" + decl.id + "'. Expected a length (e.g., 10px, 2em) or a valid keyword."));
                    }
                } else if (COLOR_PROPS.contains(propLower)) {
                    if (!NAMED_COLORS.contains(val.toLowerCase())) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid color value for '" + decl.id + "'."));
                    }
                } else if (NUMERIC_PROPS.contains(propLower)) {
                    errors.add(new SemanticError(decl.line_number,
                            "CSS Error: '" + val + "' is not a valid numeric value for '" + decl.id + "'. Expected a number."));
                } else if ("display".equals(propLower)) {
                    if (!DISPLAY_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for 'display'."));
                    }
                } else if ("position".equals(propLower)) {
                    if (!POSITION_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for 'position'."));
                    }
                } else if ("float".equals(propLower) || "clear".equals(propLower)) {
                    if (!FLOAT_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for '" + decl.id + "'."));
                    }
                } else if ("overflow".equals(propLower) || "overflow-x".equals(propLower) || "overflow-y".equals(propLower)) {
                    if (!OVERFLOW_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for '" + decl.id + "'."));
                    }
                } else if ("text-align".equals(propLower)) {
                    if (!TEXT_ALIGN_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for 'text-align'."));
                    }
                } else if ("font-weight".equals(propLower)) {
                    if (!FONT_WEIGHT_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for 'font-weight'."));
                    }
                } else if ("border-style".equals(propLower)
                        || "border-top-style".equals(propLower)
                        || "border-right-style".equals(propLower)
                        || "border-bottom-style".equals(propLower)
                        || "border-left-style".equals(propLower)
                        || "outline-style".equals(propLower)) {
                    if (!BORDER_STYLE_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for '" + decl.id + "'."));
                    }
                } else if ("visibility".equals(propLower)) {
                    if (!VISIBILITY_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for 'visibility'."));
                    }
                } else if ("object-fit".equals(propLower)) {
                    if (!OBJECT_FIT_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for 'object-fit'."));
                    }
                } else if ("cursor".equals(propLower)) {
                    if (!CURSOR_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for 'cursor'."));
                    }
                } else if ("text-decoration".equals(propLower)) {
                    if (!TEXT_DECORATION_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for 'text-decoration'."));
                    }
                } else if ("white-space".equals(propLower)) {
                    if (!WHITESPACE_VALUES.contains(val)) {
                        errors.add(new SemanticError(decl.line_number,
                                "CSS Error: '" + val + "' is not a valid value for 'white-space'."));
                    }
                }
                // For other properties: font-family, content, etc. — identifier may be valid
            }
        }
    }

    public void printErrors() {
        if (errors.isEmpty()) {
            System.out.println("\n[CSS Semantic Analyzer] No semantic errors found. \u2713");
            return;
        }
        System.out.println("\n========== CSS SEMANTIC ANALYSIS ERRORS (" + errors.size() + ") ==========");
        int i = 1;
        for (SemanticError e : errors) {
            System.out.println("  " + i + ". " + e);
            i++;
        }
        System.out.println("==============================================================");
    }

    public List<SemanticError> getErrors() {
        return errors;
    }
}
