package model;

import java.util.*;

public class Pioche {
    private Deque<Carte> cartes;

    public Pioche(List<Carte> cartesInitiales) {
        this.cartes = new ArrayDeque<>(cartesInitiales);
    }

    // Génération automatique des cartes disponibles
    public Pioche() {
        List<Carte> toutesLesCartes = new ArrayList<>();
        Couleur[] couleurs = Couleur.values();

        for (Couleur c : couleurs) {

            // 1 carte 0 par couleur
            toutesLesCartes.add(new CarteSimple(c, 0));

            // 2 cartes pour chaque valeur 1 à 9
            for (int i = 1; i <= 9; i++) {
                toutesLesCartes.add(new CarteSimple(c, i));
                toutesLesCartes.add(new CarteSimple(c, i));
            }

            // 2 cartes +2 et passe ton tour par couleur
            toutesLesCartes.add(new CartePlus2(c));
            toutesLesCartes.add(new CartePlus2(c));
            toutesLesCartes.add(new CartePasseTonTour(c));
            toutesLesCartes.add(new CartePasseTonTour(c));
        }

        Collections.shuffle(toutesLesCartes); // Mélange la liste de façon aléatoire
                                              // Collections est une classe utilitaire en Java
                                              // https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html

        this.cartes = new ArrayDeque<>(toutesLesCartes);
    }

    public Carte piocher() {
        return cartes.poll(); // Retire et retourne la première carte
    }

    public Carte voirProchaine() {
        return cartes.peek(); // Regarde la prochaine carte sans la retirer
    }

    public void remettreCarte(Carte carte) {
        cartes.add(carte);
    }

    public boolean estVide() {
        return cartes.isEmpty();
    }

    public int taille() {
        return cartes.size();
    }

    public void melanger() {
        List<Carte> liste = new ArrayList<>(cartes);
        Collections.shuffle(liste); //On utilise la methode shuffle de la classe Collections
        cartes = new ArrayDeque<>(liste);
    }


}