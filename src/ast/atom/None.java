package ast.atom;

public class None extends Atom {
    public None(int line_number) {
        super("None", line_number);
        super.setValue("None");

    }
}