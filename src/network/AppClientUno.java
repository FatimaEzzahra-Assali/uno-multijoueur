package network;

import java.util.Scanner;

/**
 * C'est l'application avec interface utilisateur. Toutefois, dans cette classe, on ne traite que des
 * communications de haut niveau. On utilise pas le protocole. C'est la classe ClientChat qui se chargera
 * de cà, grâce aux méthodes traiterXXX.
 * Cette classe est complète. En principe, vous n'avez pas à la modifier
 */
public class AppClientUno {

    /*
    public void afficherConsole(String message) {
        System.out.println(message);
    }
    public void afficherErreur(String message) {
        System.err.println(message);
    }
    public void afficherMessagePrive(String pseudoDest, String message) {
        afficherConsole("**"+pseudoDest+"** : "+message);
    }
    public void afficherMessagePublic(String message) {
        afficherConsole(message);
    }
    */

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ClientUno client = new ClientUno();

        System.out.print("Entre ton pseudo : ");
        String pseudo = sc.nextLine();
        client.setPseudo(pseudo);
        client.envoyerConnexion();

        while (true) {
            System.out.print("> ");
            String commande = sc.nextLine();

            if (commande.startsWith("carte ")) {
                String[] parts = commande.split(" ");
                if (parts.length == 3) {
                    client.jouerCarte(parts[1], parts[2]);
                } else {
                    System.out.println("⚠️ Utilise : carte <couleur> <valeur>");
                }
            } else if (commande.equals("piocher")) {
                client.piocher();
            } else if (commande.equals("uno")) {
                client.direUno();
            } else if (commande.equals("quitter")) {
                break;
            } else {
                //client.envoyerTexteLibre(commande);
            }
        }

        System.out.println("👋 Au revoir !");
    }
}
