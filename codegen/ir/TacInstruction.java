package codegen.ir;

public class TacInstruction {
    public final Opcode opcode;
    public String result;
    public String arg1;
    public String arg2;
    public String label;
    public int line;

    public TacInstruction(Opcode opcode) {
        this.opcode = opcode;
    }

    public TacInstruction(Opcode opcode, int line) {
        this.opcode = opcode;
        this.line = line;
    }

    public static TacInstruction label(String name) {
        TacInstruction i = new TacInstruction(Opcode.LABEL);
        i.label = name;
        return i;
    }

    public static TacInstruction assign(String dst, String src) {
        TacInstruction i = new TacInstruction(Opcode.ASSIGN);
        i.result = dst;
        i.arg1 = src;
        return i;
    }

    public static TacInstruction binaryOp(String dst, String a, String b, String op) {
        TacInstruction i = new TacInstruction(Opcode.BINARY_OP);
        i.result = dst;
        i.arg1 = a;
        i.arg2 = b;
        i.label = op;
        return i;
    }

    public static TacInstruction call(String dst, String func, int numArgs) {
        TacInstruction i = new TacInstruction(Opcode.CALL);
        i.result = dst;
        i.arg1 = func;
        i.arg2 = String.valueOf(numArgs);
        return i;
    }

    public static TacInstruction param(String arg) {
        TacInstruction i = new TacInstruction(Opcode.PARAM);
        i.arg1 = arg;
        return i;
    }

    public static TacInstruction ret(String val) {
        TacInstruction i = new TacInstruction(Opcode.RETURN);
        i.arg1 = val;
        return i;
    }

    public static TacInstruction funcDef(String name, int numParams) {
        TacInstruction i = new TacInstruction(Opcode.FUNC_DEF);
        i.arg1 = name;
        i.arg2 = String.valueOf(numParams);
        return i;
    }

    public static TacInstruction funcEnd() {
        return new TacInstruction(Opcode.FUNC_END);
    }

    public static TacInstruction classDef(String name) {
        TacInstruction i = new TacInstruction(Opcode.CLASS_DEF);
        i.arg1 = name;
        return i;
    }

    public static TacInstruction classEnd() {
        return new TacInstruction(Opcode.CLASS_END);
    }

    public static TacInstruction ifGoto(String cond, String label) {
        TacInstruction i = new TacInstruction(Opcode.IF_GOTO);
        i.arg1 = cond;
        i.label = label;
        return i;
    }

    public static TacInstruction goto_(String label) {
        TacInstruction i = new TacInstruction(Opcode.GOTO);
        i.label = label;
        return i;
    }

    public static TacInstruction imprt(String module, String names) {
        TacInstruction i = new TacInstruction(Opcode.IMPORT);
        i.arg1 = module;
        i.arg2 = names;
        return i;
    }

    public static TacInstruction global(String name) {
        TacInstruction i = new TacInstruction(Opcode.GLOBAL);
        i.arg1 = name;
        return i;
    }

    public static TacInstruction attrib(String dst, String obj, String attr) {
        TacInstruction i = new TacInstruction(Opcode.ATTRIB);
        i.result = dst;
        i.arg1 = obj;
        i.arg2 = attr;
        return i;
    }

    public static TacInstruction assignAttr(String obj, String attr, String src) {
        TacInstruction i = new TacInstruction(Opcode.ASSIGN_ATTR);
        i.arg1 = obj;
        i.arg2 = attr;
        i.label = src;
        return i;
    }

    public static TacInstruction listNew(String dst, int n) {
        TacInstruction i = new TacInstruction(Opcode.LIST_NEW);
        i.result = dst;
        i.arg2 = String.valueOf(n);
        return i;
    }

    public static TacInstruction dictNew(String dst, int n) {
        TacInstruction i = new TacInstruction(Opcode.DICT_NEW);
        i.result = dst;
        i.arg2 = String.valueOf(n);
        return i;
    }

    public static TacInstruction kwarg(String name, String val) {
        TacInstruction i = new TacInstruction(Opcode.KWARG);
        i.arg1 = name;
        i.arg2 = val;
        return i;
    }

    public static TacInstruction objectCreate(String dst, String cls, int n) {
        TacInstruction i = new TacInstruction(Opcode.OBJECT_CREATE);
        i.result = dst;
        i.arg1 = cls;
        i.arg2 = String.valueOf(n);
        return i;
    }

    @Override
    public String toString() {
        switch (opcode) {
            case LABEL: return label + ":";
            case ASSIGN: return "  " + result + " = " + arg1;
            case BINARY_OP: return "  " + result + " = " + arg1 + " " + label + " " + arg2;
            case CALL: return "  " + (result != null ? result + " = " : "") + "CALL " + arg1 + "(" + arg2 + " args)";
            case PARAM: return "  PARAM " + arg1;
            case RETURN: return "  RETURN " + arg1;
            case FUNC_DEF: return "FUNC " + arg1 + "(" + arg2 + " params)";
            case FUNC_END: return "END FUNC";
            case CLASS_DEF: return "CLASS " + arg1;
            case CLASS_END: return "END CLASS";
            case IF_GOTO: return "  IF " + arg1 + " FALSE GOTO " + label;
            case GOTO: return "  GOTO " + label;
            case IMPORT: return "  IMPORT " + arg1 + " -> " + arg2;
            case GLOBAL: return "  GLOBAL " + arg1;
            case ATTRIB: return "  " + result + " = " + arg1 + "." + arg2;
            case ASSIGN_ATTR: return "  " + arg1 + "." + arg2 + " = " + label;
            case LIST_NEW: return "  " + result + " = [" + arg2 + " items]";
            case DICT_NEW: return "  " + result + " = {" + arg2 + " kvs}";
            case KWARG: return "  KWARG " + arg1 + " = " + arg2;
            case OBJECT_CREATE: return "  " + result + " = new " + arg1 + "(" + arg2 + " args)";
            default: return "  " + opcode;
        }
    }
}
