package server.reseau;

import java.io.*;
import java.net.Socket;

/**
 * ThreadConnexionUno est un thread qui gère la communication réseau
 * pour un joueur (lecture des messages depuis le socket).
 * Il délègue le traitement à ConnexionJoueurUno.
 */
public class ThreadConnexionUno extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ConnexionJoueurUno connexion;
    private boolean fin = false;

    public ThreadConnexionUno(ConnexionJoueurUno connexion, Socket socket) throws IOException {
        this.connexion = connexion;
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        System.out.println("Thread connexion cree");
        start();
    }

    @Override
    public void run() {
        try {
            while (!fin) {
                String message = in.readLine();
                if (message == null) break; //deconnection du client
                connexion.traiterMessage(message);
            }
        } catch (IOException e) {
            System.err.println("[ThreadConnexionUno] Erreur de lecture: " + e.getMessage());
        } finally { //??? rajouté
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("[ThreadConnexionUno] Erreur à la fermeture de la socket: " + e.getMessage());
            }
        }
    }

    public void envoyerMessage(String message) {
        out.println(message);
    }

    public void fin() {
        this.fin = true;
    }
}