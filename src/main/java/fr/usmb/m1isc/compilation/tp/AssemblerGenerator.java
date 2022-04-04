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
            if (data.getFilsDroit().getClass().getSimpleName().equals("Feuille")) {
                tmp += String.format("    mov eax, %s\n", data.getFilsDroit());
            } else {
                String[] lines = tmp.split("\n");
                boolean isPush = lines[lines.length - 1].contains("push");

                if (isPush) {
                    tmp = "";
                    for (int i = 0; i < lines.length - 1; i++) {
                        tmp += (lines[i] + "\n");
                    }
                }
            }
            if (data.getFilsGauche().getClass().getSimpleName().equals("Feuille")) {
                tmp += String.format("    mov %s, eax\n", data.getFilsGauche());
            }
        }

        if (data.getRacine() == Operator.PLUS || data.getRacine() == Operator.MOINS ||
            data.getRacine() == Operator.MUL || data.getRacine() == Operator.DIV) {

            // Check la derniere commande
            String[] lines = tmp.split("\n");
            boolean isPush = lines[lines.length - 1].contains("push");

            if (data.getFilsGauche().getClass().getSimpleName().equals("Feuille") && !isPush) {
                tmp += String.format("    mov eax, %s\n", data.getFilsGauche());
                tmp += "    push eax\n";
            }
            if (data.getFilsDroit().getClass().getSimpleName().equals("Feuille")) {
                tmp += String.format("    mov eax, %s\n", data.getFilsDroit());
            }


            if (data.getRacine() == Operator.PLUS) {
                tmp += "    pop ebx\n";
                tmp += "    add eax, ebx\n";
            } else if (data.getRacine() == Operator.MOINS) {
                tmp += "    pop ebx\n";
                tmp += "    sub eax, ebx\n";
            } else if (data.getRacine() == Operator.MUL) {
                tmp += "    pop ebx\n";
                tmp += "    mul eax, ebx\n";
            } else if (data.getRacine() == Operator.DIV) {
                tmp += "    pop ebx\n";
                tmp += "    div ebx, eax\n";
                tmp += "    mov eax, ebx\n";
            }
            tmp += "    push eax\n";
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
