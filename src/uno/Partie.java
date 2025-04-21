package uno;

import java.util.List;

public class Partie {
    private List<Joueur> joueurs;
    private int joueurCourantIndex;
    private Pioche pioche;
    private Tas tas;

    public Partie(List<Joueur> joueurs, Pioche pioche, Tas tas) {
        this.joueurs = joueurs;
        this.pioche = pioche;
        this.tas = tas;
        this.joueurCourantIndex = 0; // commence par le premier joueur
    }

    public Joueur getJoueurCourant() {
        return joueurs.get(joueurCourantIndex);
    }

    public void passerAuJoueurSuivant() {
        joueurCourantIndex = (joueurCourantIndex + 1) % joueurs.size();
    }

    public void finirTour() {
        getJoueurCourant().resetUno();
        passerAuJoueurSuivant();
    }

    public Pioche getPioche() {
        return pioche;
    }

    public Tas getTas() {
        return tas;
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }
}