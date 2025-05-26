package network;

import java.util.Scanner;


public class AppClientUno {

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
                    System.out.println(" Utilise : carte <couleur> <valeur>");
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

        System.out.println(" Au revoir !");
    }
}