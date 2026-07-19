package ast.complexExp;

import ast.atom.Atom;

import java.util.List;

public class ListLiteral extends ComplexExpression {
    private List<Atom> listItems;

    public ListLiteral(int line_number) {
        super("ListLiteral", line_number);
    }

    public void setListItems(List<Atom> listItems) {
        this.listItems = listItems;
    }

    @Override
    public String symbolTablePrint() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" [ ");
        if (listItems != null) {
            for (Atom listItem : listItems) {
                stringBuilder.append(listItem.toString())
                        .append((listItems.indexOf(listItem) == listItems.size() - 1)
                                ? "" : ", ");
            }
        }
        stringBuilder.append(" ] ");

        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString()).append(" ( [ ");
        if (listItems != null) {
            for (Atom listItem : listItems) {
                stringBuilder.append(listItem.toString())
                        .append((listItems.indexOf(listItem) == listItems.size() - 1)
                                ? "" : ", ");
            }
        }
        stringBuilder.append(" ] ) ");

        return stringBuilder.toString();
    }
}
