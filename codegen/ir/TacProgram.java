package codegen.ir;

import java.util.ArrayList;
import java.util.List;

public class TacProgram {
    public final List<TacInstruction> instructions = new ArrayList<>();
    private int tempCounter = 0;
    private int labelCounter = 0;

    public void add(TacInstruction inst) {
        instructions.add(inst);
    }

    public String newTemp() {
        return "_t" + (tempCounter++);
    }

    public String newLabel() {
        return "L" + (labelCounter++);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== THREE ADDRESS CODE (TAC) ===\n");
        int i = 0;
        for (TacInstruction inst : instructions) {
            sb.append(String.format("%3d: %s%n", i++, inst));
        }
        sb.append("=== END TAC ===\n");
        return sb.toString();
    }
}
