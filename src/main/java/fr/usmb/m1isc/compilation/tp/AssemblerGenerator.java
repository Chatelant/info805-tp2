package fr.usmb.m1isc.compilation.tp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AssemblerGenerator {
    private Arbre stack;
    private String data_segment;
    private String code_segment;

    public AssemblerGenerator(Arbre stack) throws IOException {
        this.stack = stack;
        generate();
    }

    public String cleanDataSegment(String data) {
        String[] lines = data.split("\n");
        String finalData = "";

        for (int i = 0; i < lines.length; i++) {
            if (!finalData.contains(lines[i])) finalData += (lines[i] + "\n");
        }

        return finalData;
    }

    public String getDataSegment(Arbre stack) {
        String tmp = "";
        if (stack == null)
            return "";
        if (stack.getRacine() == Operator.LET) {
            tmp = ("\t" + stack.getFilsGauche().toString() + " DD\n");
        }

        tmp += getDataSegment(stack.getFilsGauche()) + getDataSegment(stack.getFilsDroit());
        return cleanDataSegment(tmp);
    }

    public void printDataSegment(String data) {
        System.out.println("DATA SEGMENT");
        System.out.print(data);
        System.out.println("DATA ENDS");
    }

    public String getCodeSegment(Arbre data) {
        if (data == null)
            return "";

        // Création de la String résultat
        String tmp = "";

        // S'il s'agit d'une boucle while
        if (data.getRacine() == Operator.WHILE) {
            tmp += "debut_while_1:\n"; // TODO : voir si le 1 doit s'incrémenter à chaque while
            tmp += getCodeSegment(data.getFilsGauche()); // Condition du while
            tmp += "\tjz sortie_while_1\n";
            tmp += getCodeSegment(data.getFilsDroit()); // Contenu de la boucle
            tmp += "\tjmp debut_while_1\n";
            tmp += "sortie_while_1:\n";
        } else {
            tmp = getCodeSegment(data.getFilsGauche()) + getCodeSegment(data.getFilsDroit());
        }

        // S'il s'agit d'un input
        if (data.getRacine() == Operator.INPUT) tmp += "\tin eax\n";

        // S'il s'agit d'une affectation
        if (data.getRacine() == Operator.LET) {
            // Le contenu du fils droit est récupéré s'il s'agit d'une feuille
            if (data.getFilsDroit().getClass().getSimpleName().equals("Feuille")) {
                tmp += String.format("\tmov eax, %s\n", data.getFilsDroit());
            } else {
                // Sinon si le fils n'est pas une feuille, on s'assure que le résultat n'a pas été push pour
                // pouvoir l'utiliser dans l'affectation à venir
                String[] lines = tmp.split("\n");
                boolean isPush = lines[lines.length - 1].contains("push");

                // On recompose la string sans le "push"
                if (isPush) {
                    tmp = "";
                    for (int i = 0; i < lines.length - 1; i++) {
                        tmp += (lines[i] + "\n");
                    }
                }
            }
            // Le contenu du fils gauche est récupéré s'il s'agit d'une feuille
            if (data.getFilsGauche().getClass().getSimpleName().equals("Feuille")) {
                tmp += String.format("\tmov %s, eax\n", data.getFilsGauche());
            }
        }

        // S'il s'agit d'une des opérations suivantes : "+", "-", "/", "*"
        if (data.getRacine() == Operator.PLUS || data.getRacine() == Operator.MOINS ||
                data.getRacine() == Operator.MUL || data.getRacine() == Operator.DIV) {

            // Check la derniere commande
            String[] lines = tmp.split("\n");
            boolean isPush = lines[lines.length - 1].contains("push");

            // Le contenu du fils gauche est récupéré s'il s'agit d'une feuille ou s'il n'a pas déjà été push sur la pile
            if (data.getFilsGauche().getClass().getSimpleName().equals("Feuille") && !isPush) {
                tmp += String.format("\tmov eax, %s\n", data.getFilsGauche());
                tmp += "\tpush eax\n";
            }
            // Le contenu du fils droit est récupéré s'il s'agit d'une feuille
            if (data.getFilsDroit().getClass().getSimpleName().equals("Feuille")) {
                tmp += String.format("\tmov eax, %s\n", data.getFilsDroit());
            }

            // Pour chaque opération, on récupère sur la pile la valeur ebx pour effectuer le calcul.
            // Le calcul est stocké dans eax
            if (data.getRacine() == Operator.PLUS) {
                tmp += "\tpop ebx\n";
                tmp += "\tadd eax, ebx\n";
            } else if (data.getRacine() == Operator.MOINS) {
                tmp += "\tpop ebx\n";
                tmp += "\tsub eax, ebx\n";
            } else if (data.getRacine() == Operator.MUL) {
                tmp += "\tpop ebx\n";
                tmp += "\tmul eax, ebx\n";
            } else if (data.getRacine() == Operator.DIV) {
                tmp += "\tpop ebx\n";
                tmp += "\tdiv ebx, eax\n";
                tmp += "\tmov eax, ebx\n";
            }
            tmp += "\tpush eax\n";
        }

        if (data.getRacine() == Operator.MOD) {
            // Check la derniere commande
            String[] lines = tmp.split("\n");
            boolean isPush = lines[lines.length - 1].contains("push");

            // Le contenu du fils gauche est récupéré s'il s'agit d'une feuille ou s'il n'a pas déjà été push sur la pile
            if (data.getFilsDroit().getClass().getSimpleName().equals("Feuille") && !isPush) {
                tmp += String.format("\tmov eax, %s\n", data.getFilsDroit());
                tmp += "\tpush eax\n";
            }
            // Le contenu du fils droit est récupéré s'il s'agit d'une feuille
            if (data.getFilsGauche().getClass().getSimpleName().equals("Feuille")) {
                tmp += String.format("\tmov eax, %s\n", data.getFilsGauche());
            }

            tmp += "\tpop ebx\n";
            tmp += "\tmov ecx, eax\n";
            tmp += "\tdiv ecx, ebx\n";
            tmp += "\tmul ecx, ebx\n";
            tmp += "\tsub eax, ecx\n";
        }

        // S'il s'agit d'une comparaison : "<" "<="
        if (data.getRacine() == Operator.GT || data.getRacine() == Operator.GTE) {
            tmp += String.format("\tmov eax, %s\n", data.getFilsGauche());
            tmp += "\tpush eax\n";
            tmp += String.format("\tmov eax, %s\n", data.getFilsDroit());
            tmp += "\tpop ebx\n";
            tmp += "\tsub eax, ebx\n";

            if (data.getRacine() == Operator.GT) {
                tmp += "\tjle faux_gt_1\n"; // TODO : voir si le 1 doit s'incrémenter
                tmp += "\tmov eax, 1\n";
                tmp += "\tjmp sortie_gt_1\n";
                tmp += "faux_gt_1:\n";
                tmp += "\tmov eax, 0\n";
                tmp += "sortie_gt_1:\n";
            } else {
                tmp += "\tjle faux_gte_1\n"; // TODO : voir si le 1 doit s'incrémenter
                tmp += "\tmov eax, 1\n";
                tmp += "\tjmp sortie_gte_1\n";
                tmp += "faux_gte_1:\n";
                tmp += "\tmov eax, 0\n";
                tmp += "sortie_gte_1:\n";
            }
        }

        // S'il s'agit d'un output
        if (data.getRacine() == Operator.OUTPUT) {
            tmp += String.format("\tmov eax, %s\n", data.getFilsGauche());
            tmp += "\tout eax\n";
        }

        return tmp;
    }

    public void printCodeSegment(String data) {
        System.out.println("CODE SEGMENT");
        System.out.print(data);
        System.out.println("CODE ENDS");
    }

    public void generate() throws IOException {
        String dataSegment = getDataSegment(stack);
        this.setData_segment(dataSegment);
        printDataSegment(dataSegment);
        String codeSegment = getCodeSegment(stack);
        this.setCode_segment(codeSegment);
        printCodeSegment(codeSegment);
        this.saveAsFile();
    }

    public String writeCodeSegment() {
        return "CODE SEGMENT\n" + code_segment + "CODE ENDS\n";
    }

    public String writeDataSegment() {
        return "DATA SEGMENT\n" + data_segment + "DATA ENDS\n";
    }

    public void saveAsFile() throws IOException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("test.asm"));
            String tmp = writeDataSegment() + writeCodeSegment();
            writer.write(tmp);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getData_segment() {
        return data_segment;
    }

    public void setData_segment(String data_segment) {
        this.data_segment = data_segment;
    }

    public String getCode_segment() {
        return code_segment;
    }

    public void setCode_segment(String code_segment) {
        this.code_segment = code_segment;
    }
}
