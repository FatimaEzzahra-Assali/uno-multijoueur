package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadClientUno extends Thread {
    private final ClientUno client;
    private final BufferedReader in;
    private boolean actif = true;

    public ThreadClientUno(ClientUno client) throws IOException {
        this.client = client;
        this.in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
    }

    @Override
    public void run() {
        System.out.println("ThreadClientUno démarré, en attente de messages...");
        try {
            String message;
            while (actif && (message = in.readLine()) != null) {
                traiterMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Erreur de lecture du serveur : " + e.getMessage());
        } finally {
            try {
                in.close();
                client.getSocket().close();
            } catch (IOException e) {
                System.err.println("Erreur à la fermeture du thread : " + e.getMessage());
            }
        }
    }

    private void traiterMessage(String message) {
        System.out.println("📨 Message du serveur : " + message);

        // Ici, tu pourrais ajouter un parseur plus intelligent plus tard
        if (message.startsWith("@INFO")) {
            System.out.println("[INFO] " + message.substring(6));
        } else if (message.startsWith("@ERREUR")) {
            System.err.println("[ERREUR] " + message.substring(8));
        } else if (message.startsWith("@MAIN")) {
            System.out.println("[MAIN] Main reçue : " + message);
        } else if (message.startsWith("@TAS")) {
            System.out.println("[TAS] Carte au sommet : " + message);
        } else {
            System.out.println("[AUTRE] " + message);
        }
    }

    public void fin() {
        this.actif = false;
        interrupt();
    }
}