package ast.keyValue;

import ast.atom.Atom;

public class AtomKeyValue extends KeyValue{
    private Atom value;
    public AtomKeyValue(int line_number) {
        super("AtomKeyValue", line_number);
    }

    public void setValue(Atom value) {
        this.value = value;
    }


    @Override
    public String symbolTablePrint() {
        return super.symbolTablePrint() + value.symbolTablePrint();
    }

    @Override
    public String toString() {
        return super.toString() + value.toString();
    }
}
