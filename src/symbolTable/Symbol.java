package symbolTable;

public class Symbol {
    public final String name;
    public SymbolKind kind;
    public DataType type;
    public int declaredLine;
    public boolean initialized;
    public boolean mutable;
    public Scope ownerScope;

    public Symbol(String name) {
        this.name = name;
        this.kind = SymbolKind.VARIABLE;
        this.type = DataType.UNKNOWN;
        this.declaredLine = -1;
        this.initialized = false;
        this.mutable = true;
    }

    public Symbol(String name, SymbolKind kind, int declaredLine) {
        this.name = name;
        this.kind = kind;
        this.type = DataType.UNKNOWN;
        this.declaredLine = declaredLine;
        this.initialized = (kind == SymbolKind.FUNCTION || kind == SymbolKind.PARAMETER
                || kind == SymbolKind.BUILTIN || kind == SymbolKind.IMPORT
                || kind == SymbolKind.LOOP_VAR);
        this.mutable = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" [").append(kind);
        if (type != DataType.UNKNOWN) sb.append(", ").append(type);
        sb.append("]");
        if (declaredLine >= 0) sb.append(" decl:L").append(declaredLine);
        if (!initialized) sb.append(" uninit");
        return sb.toString();
    }
}
