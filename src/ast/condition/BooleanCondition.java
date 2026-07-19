package ast.condition;

import ast.atom.Bool;

public class BooleanCondition extends Condition {
    private Bool boolValue;
    public BooleanCondition(int line_number) {
        super("BooleanCondition", line_number);
    }

    public void setBoolValue(Bool boolValue) {
        this.boolValue = boolValue;
    }

    @Override
    public String toString() {
        return boolValue.toString();
    }
}
