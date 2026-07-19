package ast.compundStmt;

import java.util.List;

public class GlobalStatement extends CompoundStatement{
    private List<String> globals;

    public GlobalStatement(int line_number) {
        super("GlobalStatement", line_number);
    }

    public void setGlobals(List<String> globals) {
        this.globals = globals;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString()).append("( ");
        for(String global : globals){
            stringBuilder.append(global)
                    .append(globals.indexOf(global) == globals.size() - 1 ? "" : ",");
        }
        stringBuilder.append(" ) ");
        return stringBuilder.toString();
    }
}
