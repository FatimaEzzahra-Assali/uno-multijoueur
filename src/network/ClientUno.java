package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

/**
 * La classe ClientChat crée un thread pour traiter les messages reçus du serveur (voir ThreadEcouteServeur).
 * La classe est centrée
 */
public class ClientUno {
    private static final String SERVEUR = "127.0.0.1";
    private static final int PORT = 4567;

    private AppClientUno app; // utile pour accéder à la méthode afficherConsole
    private ThreadClientUno threadClientUno; // gère la réception des messages du serveur
    private Socket socket; // La connexion
    private PrintWriter out; // Le flux vers le serveur (le flux d'entrée est unqiement utile dans le thread)
    private String pseudo; // Le pseudo de ce client

    public ClientUno() {
        // TODO A vous d'initialiser ce qu'il faut...
        try {
            //this.app = app;
            this.socket = new Socket(SERVEUR, PORT);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.threadClientUno = new ThreadClientUno(this);
            threadClientUno.start();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de creer le socket");
        }
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }


    public Socket getSocket() {
        return socket;
    }

    /**
     * Mise en forme de la phrase du protocole pour déclarer la connexion. Cette méthode est appelée
     * par le bouton "Connexion"
     */
    public void envoyerConnexion() {
        this.out.println("@CONNEXION " + this.pseudo);
    }

    public void jouerCarte(String couleur, String valeur) {
        out.println("@JOUERCARTE " + couleur + " " + valeur);
    }

    public void piocher() {
        out.println("@PIOCHER");
    }

    public void direUno() {
        out.println("@UNO");
    }

    public void rejoindre() {
        out.println("@JOINDRE " + this.pseudo);
    }

    public void quitter() {
        this.out.println("@QUITTER");
    }

    /**
     * La méthode afficherMessage est appelée par le ThreadEcouteServeur lorsqu'un message a été reçu du serveur.
     * Il faut savoir quel est le type du message pour éventuellement l'afficher d'une certaine manière dans
     * l'interface (dans cette application, tout s'affiche dans la console mais on pourrait imaginer une
     * fenêtre pour les messages publics, une autre pour les messages privés et encore une autre pour les
     * erreurs, etc.)
     * @param message Le message reçu du serveur
     */

    /*
    public void afficherMessage(String message) {
        String[] mots = message.split(" ");
        switch (mots[0]) {
            case "@PUBLIC_FROM" -> afficherMessagePublic(mots);
            case "@MP_FROM" -> afficherMessagePrive(mots);
            case "@ERROR" -> afficherErreur(mots);
        }
    }


    private void afficherMessagePrive(String[] mots) {
        String str = "**"+mots[1]+"** : "+getContenu(Arrays.copyOfRange(mots, 2, mots.length));
        app.afficherConsole(str);
    }

    private void afficherMessagePublic(String[] mots) {
        String str = mots[1]+" : "+getContenu(Arrays.copyOfRange(mots, 2, mots.length));
        app.afficherConsole(str);
    }

    private void afficherErreur(String[] mots) {
        String str = "!!!ERREUR!!! : "+getContenu(Arrays.copyOfRange(mots, 1, mots.length));
        app.afficherConsole(str);
    }

    private String getContenu(String[] mots) {
        String str = "";

        for (int i=0; i<mots.length-1; i++) {
            str += mots[i]+" ";
        }
        str += mots[mots.length-1];

        return str;
    }

   */

    /**
     * Mise en forme de la phrase du protocole pour déclarer la déconnexion. Cette méthode est appelée
     * par le bouton de fermeture de la fenêtre
     */
    public void envoyerDeconnexion() {
        this.out.println("@DECONNEXION ");
    }

    /**
     * Mise en forme de la phrase du protocole pour déclarer l'envoi d'un message public.
     * Cette méthode est appelée par le bouton "Public"
     * @param message C'est le contenu du la zone de message
     */
    public void envoyerMessagePublic(String message) {
        this.out.println("@TO_ALL " + message);
    }

    /**
     * Mise en forme de la phrase du protocole pour déclarer l'envoi d'un message public.
     * Cette méthode est appelée par le bouton "Public". Si le pseudo est vide, alors on affiche
     * un message d'erreur dans la zone console
     * @param message C'est le contenu du la zone de message
     * @param pseudoDest C'est le pseudo du destinaire trouvé dans la zone pseudo
     */
    public void envoyerMessagePrive(String pseudoDest, String message) {
        this.out.println("@MP_TO " + pseudoDest + " " + message);
    }
}
