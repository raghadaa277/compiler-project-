package codegen.ir;

public enum Opcode {
    LABEL,
    ASSIGN,
    ADD, SUB, MULT, DIV,
    CALL, PARAM, RETURN,
    FUNC_DEF, FUNC_END,
    CLASS_DEF, CLASS_END,
    IF_GOTO, GOTO,
    IMPORT, GLOBAL,
    ATTRIB, ASSIGN_ATTR,
    LIST_NEW, DICT_NEW,
    KWARG, OBJECT_CREATE,
    BINARY_OP,
    NOP
}
