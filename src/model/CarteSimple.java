package model;

public class CarteSimple extends Carte {
    private int valeur;

    public CarteSimple(Couleur couleur, int valeur) {
        super(couleur);
        this.valeur = valeur;
    }

    public int getValeur() {
        return valeur;
    }

    @Override
    public boolean estJouableSur(Carte carte) {
        if (carte instanceof CarteSimple) {
            CarteSimple autre = (CarteSimple) carte;
            return this.couleur == autre.couleur || this.valeur == autre.valeur;
        } else {
            return this.couleur == carte.getCouleur();
        }
    }

    @Override
    public String toString() {
        return "CarteSimple [" + couleur + " " + valeur + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarteSimple that = (CarteSimple) o;
        return this.couleur == that.couleur && this.valeur == that.valeur;
    }

    @Override
    public String toCode() {
        return valeur + ";" + couleur;
    }

}
