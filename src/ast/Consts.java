package ast;

public class Consts {
    public static String STRING_INDENT = "\n│   ";

    public static String printIndent(int level) {
        return "\n" +
                "|   ".repeat(Math.max(0, level));
    }
}
