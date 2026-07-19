package ast.argsList;

import ast.atom.Atom;

import java.util.List;

public class AtomArguments extends ArgumentsList {
    private List<Atom> args;

    public AtomArguments(int line_number) {
        super("AtomArguments", line_number);
    }

    public void setArgs(List<Atom> args) {
        this.args = args;
    }

    public List<Atom> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Atom atom : args) {
            stringBuilder.append(atom.toString())
                    .append((args.indexOf(atom) == args.size() - 1) ? "" : ", ");
        }
        return stringBuilder.toString();
    }
}
