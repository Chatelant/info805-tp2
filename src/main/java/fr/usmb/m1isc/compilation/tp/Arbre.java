package fr.usmb.m1isc.compilation.tp;

public class Arbre {
    private String racine;
    private Arbre filsGauche;
    private Arbre filsDroit;

    public Arbre() {
        racine = "";
        filsGauche = null;
        filsDroit = null;
    }

    public Arbre(String racine) {
        this.racine = racine;
        filsGauche = null;
        filsDroit = null;
    }

    public Arbre(String racine, Arbre filsGauche){
        this.racine = racine;
        this.filsGauche = filsGauche;
    }

    public Arbre(String racine, Arbre filsGauche, Arbre filsDroit) {
        this.racine = racine;
        this.filsDroit = filsDroit;
        this.filsGauche = filsGauche;
    }

    public String toString() {
        if (filsDroit != null & filsGauche != null)
            return "( " + racine +" "+ filsGauche.toString() + " " + filsDroit.toString() + ")";
        if (filsDroit == null && filsGauche != null)
            return "( " + racine + " "  + filsGauche.toString() + " ())";
        if (filsDroit != null)
            return "( " + racine + " () " + filsDroit.toString() + ")";
        return racine;
    }
}
