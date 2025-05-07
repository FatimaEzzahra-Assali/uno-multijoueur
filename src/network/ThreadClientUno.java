package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadClientUno extends Thread{
    private ClientUno client;
    private BufferedReader in;

    ThreadClientUno(ClientUno client) {
        // TODO à vous de compléter ce qu'il faut
        try {
            this.client = client;
            this.in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de creer le flux d'entree du socket");
        }
        start();
    }


    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) { //on s'arrête à la fin du flux
                afficher(message); //on adapte l'affichage dans la console en fonction du type de message
            }
        } catch (IOException e) {
            System.err.println("Fin de lecture du flux d'entree du socket");
        }
    }

    private void afficher(String message) {
        String[] parts = message.split(" ", 2); // on découpe en 2 parties max
        String type = parts[0]; // le premier mot (exemple: @INFO, @VICTOIRE)
        String contenuMessage = parts.length > 1 ? parts[1] : ""; // le reste du message

        switch (type) {
            case "@VICTOIRE":
                System.out.println("VICTOIRE POUR " + contenuMessage + " !");
                break;
            case "@INFO":
                System.out.println("INFORMATION : " + contenuMessage);
                break;
            case "@ERREUR":
                System.out.println("ERREUR : " + contenuMessage);
                break;
            default:
                System.out.println(message);
        }
    }

}
