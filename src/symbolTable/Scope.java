package symbolTable;

import java.util.*;

public class Scope {
    public final Scope parent;
    public final List<Scope> children;
    public final Map<String, Symbol> symbols;
    public final ScopeType scopeType;
    public final int entryLine;

    public Scope(Scope parent, ScopeType scopeType, int entryLine) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.symbols = new LinkedHashMap<>();
        this.scopeType = scopeType;
        this.entryLine = entryLine;
        if (parent != null) {
            parent.children.add(this);
        }
    }

    public Symbol define(String name, SymbolKind kind, int line) {
        Symbol existing = symbols.get(name);
        if (existing != null) {
            return existing;
        }
        Symbol sym = new Symbol(name, kind, line);
        sym.ownerScope = this;
        symbols.put(name, sym);
        return sym;
    }

    public Symbol define(String name, SymbolKind kind, int line, DataType type) {
        Symbol sym = define(name, kind, line);
        sym.type = type;
        return sym;
    }

    public Symbol resolveLocal(String name) {
        return symbols.get(name);
    }

    public Symbol resolve(String name) {
        Symbol sym = symbols.get(name);
        if (sym != null) return sym;
        if (parent != null) return parent.resolve(name);
        return null;
    }

    public boolean existsInCurrentScope(String name) {
        return symbols.containsKey(name);
    }

    public Symbol markInitialized(String name) {
        Symbol sym = resolve(name);
        if (sym != null) {
            sym.initialized = true;
        }
        return sym;
    }

    public List<Symbol> getSymbolsInScope() {
        return new ArrayList<>(symbols.values());
    }

    public String getScopeLabel() {
        return scopeType + "_Line_" + entryLine;
    }

    @Override
    public String toString() {
        return "Scope[" + getScopeLabel() + "] symbols=" + symbols.keySet();
    }
}
