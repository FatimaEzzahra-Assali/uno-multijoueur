package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientUno {
    private static final String SERVEUR = "127.0.0.1";
    private static final int PORT = 4567;

    private Socket socket; // Connexion au serveur
    private PrintWriter out; // Pour envoyer des messages
    private ThreadClientUno threadClientUno; // Réception asynchrone
    private String pseudo; // Pseudo du joueur

    // Singleton
    private static ClientUno instance;

    public ClientUno() {
        try {
            System.out.println("Tentative de connexion au serveur...");
            this.socket = new Socket(SERVEUR, PORT);
            System.out.println("Socket créé.");
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            System.out.println("Flux de sortie prêt.");
        } catch (IOException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            throw new RuntimeException("Impossible de créer le socket", e);
        }
    }

    // Méthodes de communication avec le serveur
    public void envoyerConnexion() {
        out.println("@CONNEXION " + this.pseudo);
    }

    public void envoyerDeconnexion() {
        out.println("@DECONNEXION");
    }

    public void jouerCarte(String couleur, String valeur) {
        if (valeur.equals("12")) valeur = "+2";
        out.println("@CARTE_JOUEE " + couleur + " " + valeur);
    }

    /*
    public void piocher() {
        out.println("@PIOCHER");
    }
     */

    public void piocher() {
        out.println("@PIOCHE");
    }

    public void direUno() {
        out.println("@UNO");
    }

    public void rejoindre() {
        out.println("@JOINDRE " + this.pseudo);
    }

    public void quitter() {
        out.println("@QUITTER");
    }

    public void envoyerMessagePublic(String message) {
        out.println("@TO_ALL " + message);
    }

    public void envoyerMessagePrive(String pseudoDest, String message) {
        out.println("@MP_TO " + pseudoDest + " " + message);
    }

    // Getters/Setters
    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setThreadClientUno(ThreadClientUno threadClientUno) {
        this.threadClientUno = threadClientUno;
    }

    public ThreadClientUno getThreadClientUno() {
        return threadClientUno;
    }

    // Singleton methods
    public static ClientUno getInstance() {
        return instance;
    }

    public static void setInstance(ClientUno client) {
        instance = client;
    }

    //Pour l'interface
    public void demanderListeUtilisateurs() {
        out.println("@GET_USERS");
    }

    public void demarrerPartie() {
        out.println("@DEMARRER_PARTIE");
    }

    public void envoyer(String message) {
        if (out != null) {
            out.println(message);
        } else {
            System.err.println("Flux de sortie non initialisé !");
        }
    }
}