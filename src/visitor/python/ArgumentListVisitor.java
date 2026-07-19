package visitor.python;

import antlr.python.PythonParser;
import antlr.python.PythonParserBaseVisitor;
import ast.argsList.ArgumentsList;
import ast.argsList.AtomArguments;
import ast.argsList.ComplexArguments;
import ast.argument.Argument;
import ast.atom.Atom;

import java.util.ArrayList;
import java.util.List;

public class ArgumentListVisitor extends PythonParserBaseVisitor<ArgumentsList> {
    @Override
    public ArgumentsList visitAtomArgs(PythonParser.AtomArgsContext ctx) {
        AtomArguments arguments = new AtomArguments(ctx.getStart().getLine());
        AtomVisitor atomVisitor = new AtomVisitor();
        List<Atom> atomList = new ArrayList<>();
        for (int i = 0; i < ctx.atom().size(); i++) {
            Atom atom = atomVisitor.visit(ctx.atom(i));
            atomList.add(atom);
        }
        arguments.setArgs(atomList);
        return arguments;
    }

    @Override
    public ArgumentsList visitComplexArgs(PythonParser.ComplexArgsContext ctx) {
        ComplexArguments complexArguments = new ComplexArguments(ctx.getStart().getLine());
        List<Argument> arguments = new ArrayList<>();
        for(int i = 0; i < ctx.argument().size(); i++){
            Argument argument = new ArgumentVisitor().visit(ctx.argument(i));
            arguments.add(argument);
        }
        complexArguments.setArguments(arguments);
        return complexArguments;
    }

}
