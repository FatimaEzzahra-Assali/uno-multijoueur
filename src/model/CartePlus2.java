package model;

public class CartePlus2 extends Carte {

    public CartePlus2(Couleur couleur) {
        super(couleur);
    }

    @Override
    public boolean estJouableSur(Carte carte) {
        // Jouable si même couleur ou carte est une autre +2
        return this.couleur == carte.getCouleur() || carte instanceof CartePlus2;
    }

    @Override
    public void appliquerEffet(Partie partie) {
        // Cumul de l'effet +2
        partie.ajouterNbCartesAPiocher(2);
        partie.setActionPlus2Actif(true);
    }

    @Override
    public String toString() {
        return "CartePlus2 [" + couleur + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartePlus2 that = (CartePlus2) o;
        return this.couleur == that.couleur;
    }

}