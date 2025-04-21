package uno;

import java.util.ArrayList;
import java.util.List;

public class Joueur {
    private String nom;
    private List<Carte> main;
    private boolean aDitUno;

    public Joueur(String nom) {
        this.nom = nom;
        this.main = new ArrayList<>();
        this.aDitUno = false;
    }

    public String getNom() {
        return nom;
    }

    public List<Carte> getMain() {
        return main;
    }

    public void ajouterCarte(Carte carte) {
        main.add(carte);
    }

    public void piocher(Pioche pioche) {
        if (!pioche.estVide()) {
            main.add(pioche.piocher());
        }
    }

    public void poserCarte(Carte carte, Partie partie) throws Exception {
        Carte sommet = partie.getTas().sommet();

        if (!main.contains(carte)) {
            throw new Exception("Le joueur ne possède pas cette carte.");
        }

        if (!carte.estJouableSur(sommet)) {
            throw new Exception("Carte non jouable sur le tas.");
        }

        main.remove(carte);
        partie.getTas().poserCarte(carte);
    }

    public int getNombreCartes() {
        return main.size();
    }

    public void direUno() {
        if (main.size() == 1) {
            aDitUno = true;
        }
    }

    public boolean aDitUno() {
        return aDitUno;
    }

    public void resetUno() {
        aDitUno = false;
    }

    @Override
    public String toString() {
        return nom + " (" + main.size() + " cartes)";
    }
}