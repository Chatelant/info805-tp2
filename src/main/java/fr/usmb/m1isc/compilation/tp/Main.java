package fr.usmb.m1isc.compilation.tp;

import java.io.FileReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws Exception {
        LexicalAnalyzer yy;
        Arbre a = new Arbre("+",new Arbre(),new Arbre("2"));
        System.out.println(a);
        if (args.length > 0)
            yy = new LexicalAnalyzer(new FileReader(args[0]));
        else
            yy = new LexicalAnalyzer(new InputStreamReader(System.in));
        @SuppressWarnings("deprecation")
        parser p = new parser(yy);
        p.parse();
    }

}
