package testjdbc;

import jdbc.DaoJoueur;
import jdbc.DaoPartie;
import jdbc.DaoScore;
import jdbc.metier.JoueurBDD;
import jdbc.metier.PartieBDD;

public class TestScoreDao {
    public static void main(String[] args) {
        JoueurBDD joueur = DaoJoueur.getOrCreateJoueur("Bob");
        PartieBDD partie = DaoPartie.creerNouvellePartie();

        DaoScore.enregistrerScore(joueur.getId(), partie.getId(), 42);
    }
}
