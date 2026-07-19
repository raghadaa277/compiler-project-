package ast;

public abstract class ASTNode {
    public String node_name;
    public int line_number;

    public ASTNode(String node_name, int line_number) {
        this.node_name = node_name;
        this.line_number = line_number;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public void setLine_number(int line_number) {
        this.line_number = line_number;
    }

    public String symbolTablePrint(){
        return toString();
    }

    @Override
    public String toString() {
        return node_name + " [Line: " + this.line_number + "]";
    }
}
