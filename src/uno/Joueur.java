package uno;

import java.util.ArrayList;
import java.util.List;

public class Joueur  {
    private String nom;
    private List<Carte> main;
    private boolean aDitUno;
    private Partie partie;

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
    //verifier que ce n'est pas le tour de bob pour le test punition sinon le test ne detectera pas l'exception
        if (!partie.getJoueurCourant().equals(this)) {
            this.getPartie().punir(this);
            throw new UNOException("Ce n'est pas ton tour !");
        }

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
            //Si le joueur pose une carte illegale, on la punit
            this.getPartie().punir(this);
            throw new UNOException("Carte non jouable sur le tas.");
        }

        if (carte.estPlus2()) {
            partie.setActionPlus2Actif(true);
            partie.ajouterNbCartesAPiocher(2);
            //partie.passerAuJoueurSuivant();
        }
        main.remove(carte);
        partie.getTas().poserCarte(carte);
        //On appliquer l'effet de la carte
        carte.appliquerEffet(partie);

        partie.setAJoueCeTour(true);


    }

    public int getNombreCartes() {
        return main.size();
    }

    public void direUno() throws UNOException {

        //Si le joueur courant n'est pas this
        if(!(this.equals(this.getPartie().getJoueurCourant()))){
            this.getPartie().punir(this);
            throw new UNOException("Le joueur dit Uno, mais il n'est pas son tour !");
        }
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

    public Partie getPartie() {
        return partie;
    }

    public void setPartie(Partie partie) {
        this.partie = partie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Joueur joueur = (Joueur) o;
        return nom.equals(joueur.nom);
    }

    @Override
    public String toString() {
        return nom + " (" + main.size() + " cartes)";
    }

    public boolean possedeCartePlus2() {
        for (Carte c : main) {
            if (c instanceof CartePlus2) {
                return true;
            }
        }
        return false;
    }
}