package uno;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Pioche {
    private Deque<Carte> cartes;

    public Pioche(List<Carte> cartesInitiales) {
        this.cartes = new ArrayDeque<>(cartesInitiales);
    }

    public Carte piocher() {
        return cartes.poll(); // Retire et retourne la première carte
    }

    public Carte voirProchaine() {
        return cartes.peek(); // Regarde la prochaine carte sans la retirer
    }

    public boolean estVide() {
        return cartes.isEmpty();
    }

    public int taille() {
        return cartes.size();
    }


}