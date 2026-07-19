package symbolTable;

import java.util.HashMap;

public class SymbolEntry {
    private final String name;
    private final HashMap<String, Object> attributes = new HashMap<>();

    public SymbolEntry(String name) {
        this.name = name;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public String getName() {
        return name;
    }
}
