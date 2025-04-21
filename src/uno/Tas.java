package uno;

import java.util.Stack;

public class Tas {
    private Stack<Carte> pile = new Stack<>();

    public void poserCarte(Carte carte) {
        pile.push(carte);
    }

    public Carte sommet() {
        return pile.peek();
    }

    public int taille() {
        return pile.size();
    }
}
