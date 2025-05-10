package model;

public class CartePasseTonTour extends Carte {
    public CartePasseTonTour(Couleur couleur) {
        super(couleur);
    }

    @Override
    public boolean estJouableSur(Carte carteSommetTas) {
        return this.couleur == carteSommetTas.getCouleur()
                || carteSommetTas instanceof CartePasseTonTour;
    }

    @Override
    public void appliquerEffet(Partie partie) {
        // Effet de la carte passe ton tour : le joueur suivant passe son tour
        partie.passerAuJoueurSuivant();
    }

    @Override
    public String toString() {
        return "PasseTonTour [" + couleur + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartePasseTonTour that = (CartePasseTonTour) o;
        return this.couleur == that.couleur;
    }

    @Override
    public String toCode() {
        return "PTT" + ";" + couleur;
    }

}
