package semantics;

import ast.HtmlContent;
import ast.htmlContentItem.HtmlContentItem;
import ast.htmlElement.TagElement;
import ast.htmlElement.StyleSheet;
import ast.jinja.jinjaStatment.*;
import ast.tagContent.TagElementItem;

import java.util.*;

public class HtmlSemanticAnalyzer {

    private static final Set<String> PHRASING_ONLY_PARENTS = Set.of("p");
    private static final Set<String> BLOCK_ELEMENTS = Set.of(
            "div", "p", "h1", "h2", "h3", "h4", "h5", "h6",
            "ul", "ol", "li", "table", "form", "section", "article",
            "nav", "aside", "header", "footer", "main",
            "blockquote", "pre", "hr", "address", "fieldset"
    );

    private final List<SemanticError> errors = new ArrayList<>();
    private final Set<String> seenIds = new HashSet<>();
    private final Deque<String> tagStack = new ArrayDeque<>();

    public boolean analyze(HtmlContent htmlContent) {
        errors.clear();
        seenIds.clear();
        tagStack.clear();
        if (htmlContent != null && htmlContent.items != null) {
            for (HtmlContentItem item : htmlContent.items) {
                analyzeItem(item);
            }
        }
        // Check for unclosed tags
        while (!tagStack.isEmpty()) {
            String unclosed = tagStack.pop();
            errors.add(new SemanticError(-1,
                    "HTML Error: Tag <" + unclosed + "> was never closed."));
        }
        printErrors();
        return errors.isEmpty();
    }

    private void analyzeItem(HtmlContentItem item) {
        if (item == null) return;

        if (item instanceof TagElement tag) {
            analyzeTagElement(tag);
        } else if (item instanceof StyleSheet) {
            // CSS inside HTML <style> is not analyzed here
        } else if (item instanceof JinjaBlockStatement jbs) {
            if (jbs.htmlContent != null && jbs.htmlContent.items != null) {
                for (HtmlContentItem child : jbs.htmlContent.items) {
                    analyzeItem(child);
                }
            }
        } else if (item instanceof JinjaIfStatement jis) {
            if (jis.htmlContent != null && jis.htmlContent.items != null) {
                for (HtmlContentItem child : jis.htmlContent.items) {
                    analyzeItem(child);
                }
            }
        } else if (item instanceof JinjaForStatement jfs) {
            if (jfs.htmlContent != null && jfs.htmlContent.items != null) {
                for (HtmlContentItem child : jfs.htmlContent.items) {
                    analyzeItem(child);
                }
            }
        }
    }

    private void analyzeTagElement(TagElement tag) {
        if (tag.tags == null) return;

        String tagName = tag.tagName != null ? tag.tagName : "";

        // Collect attribute names
        Set<String> attrNames = new HashSet<>();
        for (TagElementItem attr : tag.tags) {
            if (attr == null) continue;
            if (attr.attributeName != null && !attr.attributeName.isEmpty()) {
                attrNames.add(attr.attributeName);
                // Check for ID duplicates
                if ("id".equals(attr.attributeName) && attr.attributeValue != null && !attr.attributeValue.isEmpty()) {
                    if (seenIds.contains(attr.attributeValue)) {
                        errors.add(new SemanticError(tag.line_number,
                                "HTML Error: Duplicate ID '" + attr.attributeValue + "' found."));
                    } else {
                        seenIds.add(attr.attributeValue);
                    }
                }
            }
        }

        // Track tag nesting
        if (tag.isClosingTag) {
            if (tagStack.isEmpty()) {
                errors.add(new SemanticError(tag.line_number,
                        "HTML Error: Unexpected closing tag </" + tagName + "> (no matching open tag)."));
            } else if (!tagStack.peek().equals(tagName)) {
                String expected = tagStack.peek();
                errors.add(new SemanticError(tag.line_number,
                        "HTML Error: Mismatched closing tag </" + tagName + "> — expected </" + expected + ">."));
                tagStack.pop();
            } else {
                tagStack.pop();
            }
        } else if (!tagName.isEmpty() && !isVoidElement(tagName)) {
            // Check nesting validity
            if (!tagStack.isEmpty() && PHRASING_ONLY_PARENTS.contains(tagStack.peek())
                    && BLOCK_ELEMENTS.contains(tagName)) {
                errors.add(new SemanticError(tag.line_number,
                        "HTML Error: Tag <" + tagName + "> cannot be nested inside <"
                                + tagStack.peek() + ">."));
            }
            tagStack.push(tagName);
        }

        // Check required attributes (only for opening tags)
        if (!tag.isClosingTag && !tagName.isEmpty()) {
            if ("img".equals(tagName) && !attrNames.contains("src")) {
                errors.add(new SemanticError(tag.line_number,
                        "HTML Error: <img> tag is missing required 'src' attribute."));
            }
            if ("a".equals(tagName) && !attrNames.contains("href")) {
                errors.add(new SemanticError(tag.line_number,
                        "HTML Error: <a> tag is missing required 'href' attribute."));
            }
        }
    }

    private boolean isVoidElement(String tagName) {
        return "br".equals(tagName) || "hr".equals(tagName) || "img".equals(tagName)
                || "input".equals(tagName) || "meta".equals(tagName) || "link".equals(tagName)
                || "area".equals(tagName) || "base".equals(tagName) || "col".equals(tagName)
                || "embed".equals(tagName) || "source".equals(tagName) || "track".equals(tagName)
                || "wbr".equals(tagName);
    }

    public void printErrors() {
        if (errors.isEmpty()) {
            System.out.println("\n[HTML Semantic Analyzer] No semantic errors found. \u2713");
            return;
        }
        System.out.println("\n========== HTML SEMANTIC ANALYSIS ERRORS (" + errors.size() + ") ==========");
        int i = 1;
        for (SemanticError e : errors) {
            System.out.println("  " + i + ". " + e);
            i++;
        }
        System.out.println("===============================================================");
    }

    public List<SemanticError> getErrors() {
        return errors;
    }
}
