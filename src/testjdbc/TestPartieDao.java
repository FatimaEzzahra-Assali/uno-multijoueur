package testjdbc;

import jdbc.DaoPartie;
import jdbc.metier.PartieBDD;

public class TestPartieDao {
    public static void main(String[] args) {
        PartieBDD partie = DaoPartie.creerNouvellePartie();
        System.out.println("Nouvelle partie créée : " + partie);
    }
}
