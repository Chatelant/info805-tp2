package fr.usmb.m1isc.compilation.tp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AssemblerGenerator {
    private Arbre stack;

    public AssemblerGenerator(Arbre stack) {
        this.stack = stack;
        generate();
    }

    public String getDataSegment(Arbre stack) {
        if (stack == null)
            return "";
        if (stack.getRacine() == Operator.LET)
            return "    " + stack.getFilsGauche().toString() + " DD\n" + getDataSegment(stack.getFilsGauche()) + getDataSegment(stack.getFilsDroit());

        return getDataSegment(stack.getFilsGauche()) + getDataSegment(stack.getFilsDroit());
    }

    public void printDataSegment(String data) {
        System.out.println("DATA SEGMENT");
        System.out.print(data);
        System.out.println("DATA ENDS");
    }

    public String getCodeSegment(Arbre data) {
        if (data == null)
            return "";

        String tmp = getCodeSegment(data.getFilsGauche()) + getCodeSegment(data.getFilsDroit());

        if (data.getRacine() == Operator.LET) {
            tmp += String.format("  mov eax, %s\n", data.getFilsDroit());
            tmp += String.format("  mov %s, eax\n", data.getFilsGauche());
            tmp += String.format("  mov eax, %s\n", data.getFilsGauche());
            tmp += "  push eax\n";
        } else if (data.getRacine() == Operator.PLUS) {
            tmp += String.format("  mov eax, %s\n", data.getFilsGauche());
            tmp += "  push eax\n";
            tmp += String.format("  mov eax, %s\n", data.getFilsDroit());
            tmp += "  pop ebx\n";
            tmp += "  add eax, ebx\n";
        } else if (data.getRacine() == Operator.MOINS) {
            tmp += String.format("  mov eax, %s\n", data.getFilsGauche());
            tmp += "  push eax\n";
            tmp += String.format("  mov eax, %s\n", data.getFilsDroit());
            tmp += "  pop ebx\n";
            tmp += "  sub eax, ebx\n";
        } else if (data.getRacine() == Operator.MUL) {
            tmp += String.format("  mov eax, %s\n", data.getFilsGauche());
            tmp += "  push eax\n";
            tmp += String.format("  mov eax, %s\n", data.getFilsDroit());
            tmp += "  pop ebx\n";
            tmp += "  mul eax, ebx\n";
        } else if (data.getRacine() == Operator.DIV) {
            tmp += String.format("  mov eax, %s\n", data.getFilsGauche());
            tmp += "  push eax\n";
            tmp += String.format("  mov eax, %s\n", data.getFilsDroit());
            tmp += "  pop ebx\n";
            tmp += "  div eax, ebx\n";
        }
        return tmp;
    }

    public void printCodeSegment(String data) {
        System.out.println("CODE SEGMENT");
        System.out.print(data);
        System.out.println("CODE ENDS");
    }

    public String generate() {
        String dataSegment = getDataSegment(stack);
        printDataSegment(dataSegment);
        String codeSegment = getCodeSegment(stack);
        printCodeSegment(codeSegment);
        return "Test";
    }
}
