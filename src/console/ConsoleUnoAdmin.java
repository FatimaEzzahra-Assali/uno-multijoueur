package console;

import jdbc.DaoScore;
import jdbc.DaoJoueur;
import jdbc.DaoPartie;
//import java.sql.*;
import java.util.Scanner;

public class ConsoleUnoAdmin {
    public static void main(String[] args) {
        System.out.println("Connexion réussie !");
        System.out.println("Base de données utilisée : uno_db");
        System.out.println("Bienvenue dans l'administration UNO !");

        Scanner scanner = new Scanner(System.in);
        String choix;

        do {
            System.out.println("\n       MENU");
            System.out.println("---------------------");
            System.out.println("1. Lister tous les joueurs");
            System.out.println("2. Lister toutes les parties");
            System.out.println("3. Lister tous les scores");
            System.out.println("4. Afficher le classement (score total par joueur)");
            System.out.println("5. Afficher le TOP 5 joueurs");
            System.out.println("0. Quitter");
            System.out.print("Votre choix: ");

            choix = scanner.nextLine();

            switch (choix) {
                case "1" -> DaoJoueur.afficherTousLesJoueurs();
                case "2" -> DaoPartie.afficherToutesLesParties();
                case "3" -> DaoScore.afficherTousLesScores();
                case "4" -> DaoScore.afficherClassementGlobal();
                case "5" -> DaoScore.afficherTop5();
                case "0" -> System.out.println("Au revoir !");
                default -> System.out.println("Choix invalide");
            }

        } while (!choix.equals("0"));
    }
}
