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

    public boolean delete(String name) {
        if (table.containsKey(name)) {
            table.remove(name);
            return true;
        }
        return false;
    }

    public SymbolEntry update(String name, String key, Object value) {
        SymbolEntry entry = table.get(name);
        if (entry != null) {
            entry.setAttribute(key, value);
        }
        return entry;
    }


    public SymbolEntry lookup(String name) {
        return table.get(name);
    }

    public SymbolEntry insert(String name) {


        SymbolEntry entry = new SymbolEntry(name);
        table.put(name, entry);
        return entry;
    }


    public void setAttribute(String name, String key, Object value) {
        SymbolEntry entry = lookup(name);
        if (entry == null) {
            entry = new SymbolEntry(name);
            table.put(name, entry);
        }
        entry.setAttribute(key, value);
    }

    // get_attribute: retrieve attribute of entry
    public Object getAttribute(String name, String key) {
        SymbolEntry entry = lookup(name);
        if (entry == null) {
            System.out.println("Error: symbol '" + name + "' not defined!");
            return null;
        }
        return entry.getAttribute(key);
    }

    // symbolTable/SymbolTable.java
    public void printCurrentTable(String indent) {
        if (table.isEmpty()) {
            System.out.println(indent + "(Empty Scope)");
            return;
        }
        int nameWidth = 20;
        for (String key : table.keySet()) {
            if (key.length() + 2 > nameWidth) {
                nameWidth = key.length() + 2;
            }
        }
        System.out.println(indent + "-".repeat(90));
        System.out.println(indent + String.format(" %-" + nameWidth + "s | %-15s | %s", "Symbol Name", "Type", "Attributes"));
        System.out.println(indent + "-".repeat(90));

        for (Map.Entry<String, SymbolEntry> entry : table.entrySet()) {
            SymbolEntry symbolEntry = entry.getValue();
            Object type = symbolEntry.getAttribute("Type");
            Object value = symbolEntry.getAttribute("Value");

            String valueStr = (value != null ? value.toString() : "null");
            String[] valueLines = valueStr.split("\n");

            System.out.println(indent + String.format(" %-" + nameWidth + "s | %-15s | %s",
                    entry.getKey(),
                    (type != null ? type : "null"),
                    valueLines[0].trim()));

            for (int i = 1; i < valueLines.length; i++) {
                System.out.println(indent + String.format(" %-" + nameWidth + "s | %-15s | %s",
                        "", "", valueLines[i].trim()));
            }
        }
        System.out.println();
    }

}
