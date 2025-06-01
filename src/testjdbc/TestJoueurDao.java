package testjdbc;

import jdbc.DaoJoueur;
import jdbc.metier.JoueurBDD;

public class TestJoueurDao {
    public static void main(String[] args) {
        JoueurBDD joueur = DaoJoueur.getOrCreateJoueur("Alice");
        System.out.println("Joueur : " + joueur);
    }
}
