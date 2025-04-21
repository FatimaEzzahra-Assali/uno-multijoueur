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

    public void piocher(Partie partie) throws UNOException {
        Pioche pioche = partie.getPioche();

        if(partie.getAJoueCeTour()){
            throw new UNOException("lLe joueur a déja joué son tour, impossible de piocher une carte.");
        }

        if (pioche.estVide()) {
            throw new UNOException("La pioche est vide.");
        }

        Carte cartePiochee = pioche.piocher();
        main.add(cartePiochee);

        partie.setAJoueCeTour(true);
    }


    public void poserCarte(Carte carte, Partie partie) throws UNOException {
        Carte sommet = partie.getTas().sommet();

        if (!main.contains(carte)) {
            throw new UNOException("Le joueur ne possède pas cette carte.");
        }

        if (!carte.estJouableSur(sommet)) {
            throw new UNOException("Carte non jouable sur le tas.");
        }

        main.remove(carte);
        partie.getTas().poserCarte(carte);

        partie.setAJoueCeTour(true);
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