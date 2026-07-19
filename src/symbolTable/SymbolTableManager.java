package symbolTable;

import java.util.Stack;

public enum SymbolTableManager {
    INSTANCE;

    private SymbolTable root = new SymbolTable(null, "Global");
    private SymbolTable currentScope = root;

    public void enterScope(String name) {
        SymbolTable newScope = new SymbolTable(currentScope, name);
        if (currentScope != null) {
            currentScope.addChild(newScope);
        }
        currentScope = newScope;
    }

    public void exitScope() {
        if (currentScope != null && currentScope.getParent() != null) {
            currentScope = currentScope.getParent();
        } else {
            System.out.println("Warning: Attempted to exit Global scope.");
        }
    }


    public void registerGlobalReference(String name) {
        SymbolEntry globalEntry = root.lookup(name);
        if (globalEntry != null && currentScope != root) {
            SymbolEntry localRef = currentScope.insert(name + " (Global)");
            localRef.setAttribute("Type", "GlobalReference");
            localRef.setAttribute("Value", "From parent scope: " + globalEntry.getAttribute("Value"));
        }
    }

    public SymbolEntry lookupLocalOrGlobal(String name) {

        SymbolEntry local = currentScope.lookup(name);
        if (local != null) return local;


        SymbolTable temp = currentScope;
        while (temp != null) {
            SymbolEntry entry = temp.lookup(name);
            if (entry != null) {
                registerGlobalReference(name);
                return entry;
            }
            temp = temp.getParent();
        }
        return null;
    }
    public void reset() {
        root = new SymbolTable(null, "Global");
        currentScope = root;
    }

    public SymbolEntry insert(String name) {
        return currentScope.insert(name);
    }

    public SymbolEntry lookup(String name) {
        SymbolTable temp = currentScope;
        while (temp != null) {
            SymbolEntry entry = temp.lookup(name);
            if (entry != null) return entry;
            temp = temp.getParent();
        }
        return null;
    }

    public void printFullTable() {
        System.out.println("===== FINAL SYMBOL TABLE (HIERARCHICAL TREE) =====");
        printScope(root, 0);
    }

    private void printScope(SymbolTable table, int level) {
        String indent = "  ".repeat(level);

        String parentName = (table.getParent() != null) ? table.getParent().getScopeName() : "None (Root)";
        System.out.println(indent + "Scope: [" + table.getScopeName() + "] | Parent: [" + parentName + "]");


        table.printCurrentTable(indent);

        for (SymbolTable child : table.getChildren()) {
            printScope(child, level + 1);
        }
    }

    public boolean isDefinedLocally(String name) {
        if (currentScope == null) return false;
        return currentScope.containsLocally(name);
    }

    public SymbolEntry lookupLocally(String name) {
        if (currentScope == null) return null;
        return currentScope.lookup(name);
    }
}