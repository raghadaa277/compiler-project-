package symbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {

    private HashMap<String, SymbolEntry> table = new HashMap<>();
    private SymbolTable parent;
    private List<SymbolTable> children = new ArrayList<>();
    private String scopeName;

    public String getScopeName() {
        return scopeName;
    }


    public SymbolTable(SymbolTable parent, String scopeName) {
        this.parent = parent;
        this.scopeName = scopeName;
        this.table = new HashMap<>();
    }

    public void addChild(SymbolTable child) {
        children.add(child);
    }
    public SymbolTable getParent() { return parent; }
    public List<SymbolTable> getChildren() { return children; }

    public SymbolTable() {
        allocate();
    }

    // allocate: create empty table
    public void allocate() {
        table = new HashMap<>();
    }

    public void free() {
        table.clear();
    }


    public SymbolEntry lookup(String name) {
        return table.get(name);
    }

    public SymbolEntry insert(String name) {
        if (table.containsKey(name)) {
            return table.get(name);
        }

        SymbolEntry entry = new SymbolEntry(name);
        table.put(name, entry);
        return entry;
    }

    public boolean containsLocally(String name) {
        return table.containsKey(name);
    }

    public void setAttribute(String name, String key, Object value) {
        SymbolEntry entry = lookup(name);
        if (entry == null) {
            entry = new SymbolEntry(name);
            table.put(name, entry);
        }
        entry.setAttribute(key, value);
    }


    public Object getAttribute(String name, String key) {
        SymbolEntry entry = lookup(name);
        if (entry == null) {
            System.out.println("Error: symbol '" + name + "' not defined!");
            return null;
        }
        return entry.getAttribute(key);
    }


    public void printCurrentTable(String indent) {
        if (table.isEmpty()) {
            System.out.println(indent + "(No Symbols Declared)");
            return;
        }
        System.out.println(indent + "----------------------------------------------------");
        System.out.println(indent + String.format("%-20s | %-15s | %s", "Symbol Name", "Type", "Attributes"));
        System.out.println(indent + "----------------------------------------------------");

        for (Map.Entry<String, SymbolEntry> entry : table.entrySet()) {
            SymbolEntry symbolEntry = entry.getValue();
            Object type = symbolEntry.getAttribute("Type");
            Object value = symbolEntry.getAttribute("Value");

            System.out.println(indent + String.format("%-20s | %-15s | %s",
                    entry.getKey(),
                    (type != null ? type : "null"),
                    (value != null ? value : "null")));
        }
        System.out.println();
    }

}
