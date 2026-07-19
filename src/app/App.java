package app;

import antlr.css.CssLexer;
import antlr.css.CssParser;
import antlr.html.HtmlLexer;
import antlr.html.HtmlParser;
import antlr.python.PythonLexer;
import antlr.python.PythonParser;
import ast.ASTNode;
import ast.HtmlContent;
import ast.Program;
import listener.CustomErrorListener;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import symbolTable.SymbolTableManager;
import visitor.css.StyleSheetVisitor;
import visitor.html.HtmlContentVisitor;
import visitor.python.ProgramVisitor;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class App {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: java app.App <directory_path_or_file>");
            return;
        }

        Path startPath = Paths.get(args[0]);

        System.out.println("PATH = " + startPath.toAbsolutePath());
        System.out.println("EXISTS = " + Files.exists(startPath));

        if (!Files.exists(startPath)) {
            System.err.println("Path does not exist!");
            return;
        }

        try {

            if (Files.isDirectory(startPath)) {

                try (Stream<Path> paths = Files.walk(startPath)) {

                    paths.filter(Files::isRegularFile)
                            .forEach(path -> {

                                String fileName = path.toString();

                                System.out.println("\n--- Processing: " + fileName + " ---");

                                processFile(fileName);
                            });
                }

            }

            else {

                System.out.println("\n--- Processing: " + startPath + " ---");

                processFile(startPath.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFile(String fileName) {

        try {

            // ================= PYTHON =================
            if (fileName.endsWith(".py")) {

                SymbolTableManager.INSTANCE.reset();

                PythonLexer lexer =
                        new PythonLexer(CharStreams.fromFileName(fileName));

                CommonTokenStream tokens =
                        new CommonTokenStream(lexer);

                PythonParser parser =
                        new PythonParser(tokens);

                parser.removeErrorListeners();
                parser.addErrorListener(new CustomErrorListener());

                ParseTree tree = parser.prog();

                showParseTree(parser.getRuleNames(), tree);

                ProgramVisitor visitor = new ProgramVisitor();

                Program program = visitor.visit(tree);

                System.out.println(program);

                System.out.println("\n");

                SymbolTableManager.INSTANCE.printFullTable();
            }

            // ================= HTML / J2 =================
            else if (fileName.endsWith(".html")
                    || fileName.endsWith(".j2")) {

                HtmlLexer lexer =
                        new HtmlLexer(CharStreams.fromFileName(fileName));

                CommonTokenStream tokens =
                        new CommonTokenStream(lexer);

                HtmlParser parser =
                        new HtmlParser(tokens);

                parser.removeErrorListeners();
                parser.addErrorListener(new CustomErrorListener());

                ParseTree tree = parser.html_content();

                showParseTree(parser.getRuleNames(), tree);

                HtmlContentVisitor visitor =
                        new HtmlContentVisitor();

                HtmlContent htmlContent = visitor.visit(tree);

                System.out.println(htmlContent);
            }

            // ================= CSS =================
            else if (fileName.endsWith(".css")) {

                CssLexer lexer =
                        new CssLexer(CharStreams.fromFileName(fileName));

                CommonTokenStream tokens =
                        new CommonTokenStream(lexer);

                CssParser parser =
                        new CssParser(tokens);

                parser.removeErrorListeners();
                parser.addErrorListener(new CustomErrorListener());

                ParseTree tree = parser.style_sheet();

                showParseTree(parser.getRuleNames(), tree);

                StyleSheetVisitor visitor =
                        new StyleSheetVisitor();

                ASTNode styleSheet = visitor.visit(tree);

                System.out.println(styleSheet);
            }

        } catch (Exception e) {

            System.err.println(
                    "Error parsing " + fileName + ": " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private static void showParseTree(
            String[] ruleNames,
            ParseTree parseTree
    ) {

        TreeViewer viewer = new TreeViewer(
                java.util.Arrays.asList(ruleNames),
                parseTree
        );

        viewer.setScale(1.5);

        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.add(viewer, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(mainPanel);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        JPanel controlPanel = new JPanel();

        JButton zoomInButton = new JButton("Zoom In");

        JButton zoomOutButton = new JButton("Zoom Out");

        JButton resetButton = new JButton("Reset Zoom");

        zoomInButton.addActionListener(e -> {

            viewer.setScale(viewer.getScale() * 1.2);

            viewer.repaint();
        });

        zoomOutButton.addActionListener(e -> {

            viewer.setScale(viewer.getScale() / 1.2);

            viewer.repaint();
        });

        resetButton.addActionListener(e -> {

            viewer.setScale(1.0);

            viewer.repaint();
        });

        controlPanel.add(zoomInButton);

        controlPanel.add(zoomOutButton);

        controlPanel.add(resetButton);

        JFrame frame = new JFrame("Parse Tree Viewer");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(scrollPane, BorderLayout.CENTER);

        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setSize(1000, 640);

        frame.setVisible(true);
    }

    private static void debugTokenStream(
            CommonTokenStream tokens,
            Lexer lexer
    ) {

        tokens.fill();

        List<Token> allTokens = tokens.getTokens();

        System.out.println("\n--- LEXER TOKEN DEBUG OUTPUT ---");

        for (Token t : allTokens) {

            if (t.getChannel() == Token.DEFAULT_CHANNEL) {

                String tokenName =
                        PythonLexer.VOCABULARY.getSymbolicName(t.getType());

                String tokenText =
                        t.getText()
                                .replace("\n", "\\n")
                                .replace("\r", "\\r");

                if (tokenName == null) {
                    tokenName = "VirtualType(" + t.getType() + ")";
                }

                System.out.printf(
                        "Line %d | %-20s | Text: '%s'\n",
                        t.getLine(),
                        tokenName,
                        tokenText
                );
            }
        }

        System.out.println("--------------------------------\n");
    }
}
