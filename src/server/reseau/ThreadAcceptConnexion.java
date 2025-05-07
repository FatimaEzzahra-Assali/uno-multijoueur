package server.reseau;

import server.metier.ServeurUno;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ThreadAcceptConnexion extends Thread {
    private ServeurUno serveur;
    private ServerSocket serverSocket;

    public ThreadAcceptConnexion(ServeurUno serveur) {
        this.serveur = serveur;
        try {
            this.serverSocket = new ServerSocket(serveur.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        start();
    }

    @Override
    public void run() {
        try {
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connexion !");
                new ConnexionJoueurUno(socket, serveur);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
