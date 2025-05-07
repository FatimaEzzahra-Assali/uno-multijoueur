package server.reseau;

import model.*;
import server.metier.ServeurUno;
import server.metier.ServeurUnoException;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.regex.Pattern;

public class ConnexionJoueurUno {

    private Socket socket;
    private String pseudo;
    private Joueur joueur;
    private ServeurUno serveur;
    private ThreadConnexionUno threadConnexion = null;
    private boolean valide = false;

    /**
     * A ce stade, on a pas encore échangé avec l'utilisateur. Donc on a
     * pas encore son pseudo
     */
    public ConnexionJoueurUno(Socket socket, ServeurUno serveur) {
        this.socket = socket;
        this.serveur = serveur;

        try {
            this.threadConnexion = new ThreadConnexionUno(this, socket);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'initialisation de la connexion joueur.");
        }
    }

    // ************* méthodes standard d'une classe Java **********************
    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }
    public Joueur getJoueur() {
        return this.joueur;
    }
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public ServeurUno getServeur() {
        return serveur;
    }
    public void setServeur(ServeurUno serveur) {
        this.serveur = serveur;
    }
    public ThreadConnexionUno getThreadConnexion() {
        return threadConnexion;
    }
    public void setThreadConnexion(ThreadConnexionUno threadConnexion) {
        this.threadConnexion = threadConnexion;
    }
    public String getPseudo() {
        return pseudo;
    }
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
    public boolean estValide() {
        return valide;
    }
    public void setEstValide(boolean valide) {
        this.valide = valide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnexionJoueurUno that = (ConnexionJoueurUno) o;
        return Objects.equals(pseudo, that.pseudo);
    }

    @Override
    public String toString() {
        return "ConnexionUtilisateur{" +
                "pseudo='" + pseudo + '\'' +
                '}';
    }
    // ******************** fin des méthodes standards ********************************


    // ********* gestion du protocole ***********
    // ********* Le protocole *************

    public void envoyer(String message) {
        threadConnexion.envoyerMessage(message);
    }

    // Les messages reçus cotés serveur
    private final static String regexCONNEXION = "^@CONNEXION \\p{Alnum}+$"; //connexion avec un pseudo (alphanumérique)
    private final static String regexDECONNEXION = "^@DECONNEXION$";
    private final static String regexCARTE = "^@CARTE \\p{Alpha}+ \\d+$";   // Message pour jouer une carte, avec une couleur (Alphanum) et une valeur (chiffre)
    private final static String regexPIOCHER = "^@PIOCHER$";
    private final static String regexUNO = "^@UNO$";
    private final static String regexLANCER = "^@LANCER$";  // Message pour demander à lancer la partie
    private final static String regexMAIN = "^@MAIN$";  // Le client demande ses cartes
    private final static String regexTAS = "^@TAS$";
    // Liste des expressions régulières utilisées pour valider les messages reçus
    private final static String[] protocole = {
            regexCONNEXION,
            regexDECONNEXION,
            regexCARTE,
            regexPIOCHER,
            regexUNO,
            regexLANCER,
            regexMAIN,
            regexTAS
    };

    public void traiterMessage(String message) {
        if (message == null) {
            valide = false;
            try {
                serveur.remove(this);
            } catch (ServeurUnoException e) {
                //on ignore
            }
            return;
        }

        System.out.println(message);

        if (!verifieProtocole(message)) {
            this.envoyerMessageErreur("ce que vous dites n'a aucun sens. Votre message est ignoré");
            return;
        }
        String[] mots = message.split(" ");
        String commande = mots[0];

        switch (commande) {
            case "@CONNEXION" -> {
                if (mots.length == 2) {
                    serveur.ajouterJoueur(this, mots[1]);
                } else {
                    envoyer("@ERREUR Syntaxe : @CONNEXION pseudo");
                }
            }
            case "@CARTE" -> {
                if (mots.length == 3) {
                    serveur.traiterCarte(this, mots[1], mots[2]);
                } else {
                    envoyer("@ERREUR Syntaxe : @CARTE couleur valeur");
                }
            }
            case "@PIOCHER" -> serveur.traiterPioche(this);
            case "@UNO" -> serveur.traiterUno(this);
            case "@LANCER" -> serveur.lancerPartie();
            case "@DECONNEXION" -> traiterDeconnexion();
            case "@MAIN" -> envoyerMain();
            case "@TAS" -> envoyerTas();
            default -> envoyerMessageErreur("Commande inconnue : " + commande); //message d'erreur
        }
    }

    public void traiterDeconnexion() {
        if (joueur != null) {
            serveur.envoyerATous("@INFO " + joueur.getNom() + " a quitté la partie."); //on envoie un message public pour annoncer le depart
        }
        serveur.remove(this);
        fermerConnexion();
    }

    public void envoyerMain() {
        if (joueur == null) {
            envoyer("@ERREUR Vous n'êtes pas connecté au jeu.");
            return;
        }

        StringBuilder builder = new StringBuilder("@MAIN ");
        builder.append(joueur.getNom()).append(" : ");
        if (joueur.getMain().isEmpty()) {
            builder.append("Aucune carte dans la main.");
            envoyer(builder.toString().trim());
            return;
        }
        for (Carte carte : joueur.getMain()) {
            builder.append(carte.getCouleur()).append(" "); //On affiche les cartes comme "ROUGE 2 - JAUNE 5"

            if (carte instanceof CarteSimple simple) {
                builder.append(simple.getValeur());
            } else if (carte instanceof CartePlus2) {
                builder.append("PLUS2");
            } else if (carte instanceof CartePasseTonTour) {
                builder.append("PASSE");
            }

            builder.append(" - ");
        }

        envoyer(builder.toString().trim());
    }

    /*Gestion du protocole pour le tas
    * */
    public void envoyerTas(){
        if (joueur == null) {
            envoyer("@ERREUR Vous n'êtes pas connecté au jeu.");
            return;
        }

        Carte carte = serveur.getPartie().getTas().sommet();

        if (carte == null) {
            envoyer("@TAS Aucune carte dans le tas.");
            return;
        }
        String message = "@TAS " + carte.getCouleur() + " " + carte.toString();
        envoyer(message);
    }

    public void fermerConnexion() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture de la connexion joueur : " + e.getMessage());
        }
        threadConnexion.fin();
    }

    public void envoyerMessageErreur(String message) {
        this.threadConnexion.envoyerMessage("@ERROR " + message);
    }

    /**
     * Le tableau de String 'protocole' définit précédemment contient tous les message susceptible d'être reçu d'un
     * utilisateur distant. On vérifie que le message correspond bienà l'un d'entre eux
     * @param message Le message dont on veut vérifier la conformité
     * @return true si ce message est conforme, false sinon
     */
    public boolean verifieProtocole(String message) {
        for (String phraseDuProtocole : protocole) {
            if (verifiePhraseDuProtocole(message, phraseDuProtocole))
                return true;
        }
        return false;
    }
    /**
     * Chaque phrase du protocol est définie par une expression régulière. Attention, si le principe est évidemment
     * le même qu'en shell, la syntaxe Java est parfois un peu différente... En plus, ici, on autorise les mots
     * clés en minuscule (@TO_ALL ou @TO_all ou @to_all, etc.)
     * @param message Le message à vérifier
     * @param phrase La phrase du protocole à laquelle on compare le message
     * @return true si ça matche, false sinon
     */
    public boolean verifiePhraseDuProtocole(String message, String phrase) {
        Pattern pattern = Pattern.compile(phrase, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(message).matches();
    }

}