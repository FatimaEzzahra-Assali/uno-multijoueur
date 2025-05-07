package model;

public abstract class Carte {
    protected Couleur couleur;

    public Carte(Couleur couleur) {
        this.couleur = couleur;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public void setCouleur(Couleur couleur) {
        this.couleur = couleur;
    }

    /**
     * Vérifie si cette carte peut être jouée sur une autre carte.
     */
    public abstract boolean estJouableSur(Carte carteSommetTas);

    /**
     * Applique l'effet de la carte à la partie.
     * Par défaut, ne fait rien (utile pour CarteSimple).
     */
    public void appliquerEffet(Partie partie) {
        // Par défaut, aucun effet
    }

    public boolean estPlus2() {
        return this instanceof CartePlus2;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + couleur + "]";
    }

}
