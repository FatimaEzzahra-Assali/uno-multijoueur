package server.reseau;

import model.*;
import server.metier.ServeurUno;
import server.metier.ServeurUnoException;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ConnexionJoueurUno {

    private ThreadConnexionUno threadConnexion = null;
    private Socket socket;
    private String pseudo;
    private Joueur joueur; //un
    private ServeurUno serveur;
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
    public Joueur getJoueur() {return joueur;}
    public void setJoueur(Joueur joueur) {this.joueur = joueur;}
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


    // ********* LE PROTOCOLE *************

    //******* MESSAGES REÇUS PAR LE SERVEUR ********
	/*
	Message envoyé lorsque le joueur connecté donne son pseudo. Par exemple @CONNEXION Alice"
	*/
    private final static String regexCONNEXION = "^@CONNEXION \\p{Alnum}+$";
    private final static String regexDECONNEXION = "^@DECONNEXION$";			// évident
    private final static String regexDEMARRER = "^@DEMARRER_PARTIE$";			// Lorsque le joueur 1 décide de démarrer la partie

    /*
    Message envoyé par le joueur quand il pose une carte. Par exemple @CARTE_JOUEE 2 Jaune
    */
    //private final static String regexCARTE_JOUEE = "^@CARTE_JOUEE \\w+ \\w+$";
    private final static String regexCARTE_JOUEE = "^@CARTE_JOUEE \\p{Alpha}+ [^\\s]+$";
    private final static String regexFIN_TOUR = "^@FIN_TOUR$";					// évident
    private final static String regexPIOCHE = "^@PIOCHE$";						// évident

    /*
    Lorsque le joueur courant accepte d'encaisser un +2 (ou une pile de +2 si vous jouez comme ça)
    */
    private final static String regexENCAISSE = "^@ENCAISSE";
    private final static String regexUNO = "^@UNO$";

    /*
    Pour que le joueur puisse communiquer avec tous les autres joueurs
     */
    private final static String regexMP_TO = "^@MP_TO \\p{Alnum}+ .*$"; // \p{Alnum}+ représente le pseudo, et .* c'est le contenu du message
    private final static String regexTO_ALL = "^@TO_ALL .*$";


    //****** MESSAGES ENVOYES PAR LE SERVEUR *******
    /*
	Message que le serveur envoie chaque fois qu'une action modifie l'un main d'un joueur, n'importe lequel. Par
	exemple : @LISTE_JOUEURS (Alice;3) (Bob;7) (Chloé;1)
	Dans cet exemple, il y a 3 joueurs : Alice tient 3 cartes, Bob en tient 7 et Chloé en tient une seule
	Le message ne donne que le nombre de cartes tenues, pas leurs valeurs évidemment.
	En général, tous les joueurs recoivent le même message au même moment
	*/
    private final static String regexLISTE_JOUEURS = "^@LISTE_JOUEURS (\\[\\w+;\\d+] ?)+$";

    /*
    Lorsque la main d'un joueur a été modifiée car il a posé une carte, pioché une carte ou été puni, alors le serveur lui renvoie
    sa main complète, avec la valeur de chacune des cartes. Par exemple : @MAIN (2;Jaune) (+2;Vert)
    */
    private final static String regexMAIN = "^@MAIN (\\[\\w+;\\w+] ?)+$";

    /*
    C'est un message simple envoyé par le serveur pour donner une info. Par exemple quand un nouveau joueur se connecte, ou se déconnecte
    */
    private final static String regexINFO = "^@INFO .*$";

    /*
    Même chose mais c'est un message d'erreur. Souvent, une punition est reçue dans le même temps.
    */
    private final static String regexERREUR = "^@ERREUR .*$";

    /*
    Lorsqu'un joueur à posé une carte sur le tas, alors le serveur informe tous les joueurs de la valeur de cette carte. Cela permet,
    entre autres, de mettre à joueur le visuel du tas.
    */
    private final static String regexTAS = "^@TAS \\w+ \\w+$";


    private final static String regexVICTOIRE = "^@VICTOIRE$";		// évident
    private final static String regexFIN_MANCHE = "^@FIN_MANCHE$";	// évident

    // Les messages envoyés par le serveur. Le client recevra ce type de message, mais le serveur lui ne peut pas les recevoir (il les ignorera)
    private final static String regexMP_FROM = "^@MP_FROM \\p{Alnum}+ .*$";
    private final static String regexPUBLIC_FROM = "^@PUBLIC_FROM \\p{Alnum}+ .*$";
    private final static String regexERROR = "^@ERROR .*$";

    private final static String regexGET_USERS = "^@GET_USERS$"; //Pour l'interface
    // Liste des expressions régulières utilisées pour valider les messages reçus
    private final static String[] protocole = {regexCONNEXION, regexDECONNEXION, regexDEMARRER,
            regexCARTE_JOUEE, regexFIN_TOUR, regexPIOCHE, regexENCAISSE, regexUNO, regexMP_TO, regexTO_ALL, regexGET_USERS};
            // regexLISTE_JOUEURS,
            //regexMAIN, regexINFO, regexERREUR, regexTAS, regexVICTOIRE, regexFIN_MANCHE};


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
            case "@GET_USERS" -> envoyerListeUtilisateurs(serveur.getJoueursConnectes()); //Pour l'interface
            case "@CONNEXION" -> traiterConnexion(message);
            case "@DECONNEXION" -> traiterDeconnexion();
            case "@DEMARRER_PARTIE" -> traiterDemarrer(serveur);
            case "@MP_TO" -> traiterMP_TO(message);
            case "@TO_ALL" -> traiterTO_ALL(message);
            case "@CARTE_JOUEE" -> {
                if (mots.length == 3) {
                    //Tout ce qui fait partie de la logique du metier va dans ServeurUno.java
                    serveur.jouerCarte(this, mots[1], mots[2]);
                } else {
                    envoyer("@ERREUR Syntaxe : @CARTE couleur valeur");
                }
            }
            case "@FIN_TOUR" -> serveur.finirTour(this);
            case "@PIOCHE" -> traiterPioche();
            case "@UNO" -> traiterUno();
            //case "ENCAISSE" -> serveur.encaisse(this);
            case "@ENCAISSE" -> serveur.encaisse(this);
            default -> envoyerMessageErreur("Commande inconnue : " + commande); //message d'erreur
        }
    }

    private void traiterConnexion(String message) {
        String[] mots = message.split(" ");
        if(mots.length != 2) {
            this.envoyerMessageErreur("Pour vous connecter, utilisez la syntaxe : @CONNEXION pseudo");
            return;
        }
        try{
            this.serveur.getConnexionJoueur(mots[1]);
            this.envoyerMessageErreur("Ce pseudo existe déja.");

        }catch(ServeurUnoException e){
            this.setPseudo(mots[1]);
            this.setJoueur(new Joueur(mots[1]));

            //Enregistrement en base de données
            jdbc.metier.JoueurBDD joueurBDD = jdbc.DaoJoueur.getOrCreateJoueur(mots[1]);
            envoyerMessageInfo(this.getPseudo() + " s'est connecté au serveur.");
            System.out.println("Joueur enregistré dans la base : " + joueurBDD);
            //pour mon interface
            this.envoyerListeUtilisateurs(serveur.getJoueursConnectes());

            //pour mon interface
            for (ConnexionJoueurUno autre : serveur.getJoueursConnectes()) {
                if (!autre.equals(this)) {
                    autre.envoyerListeUtilisateurs(serveur.getJoueursConnectes());
                }
            }
        }
    }

    public void traiterDeconnexion(){
        this.serveur.messagePublic(this, "Je me suis déconnecté du serveur.");
        this.serveur.remove(this);
        fermerConnexion();

    }

    public void traiterDemarrer(ServeurUno serveur){
            if (serveur.isPartieEnCours()) return; //si une partie est dejà en cours
            if (serveur.getJoueursConnectes().size() < 2) {
                envoyerMessageErreur("Il faut au moins 2 joueurs !");
                return;
            }
            if(!this.equals(serveur.getJoueursConnectes().get(0))) {
                envoyerMessageErreur("Pour lancer une partie, vous devez vous connecter en premier.");
                return;
            }
            serveur.envoyerATous("@DEMARRER " + this.getPseudo() + " à lancé une partie.");
           // envoyerListeJoueurs(serveur.getJoueursConnectes());
            serveur.lancerPartie();
            serveur.envoyerTas(serveur.getPartie().getTas().sommet());
    }

    private void traiterTO_ALL(String message) {
        if (this.getPseudo() == null || this.getPseudo().isBlank()) {
            this.envoyerMessageErreur("Veuillez vous connecter avant d'envoyer un message");
            return;
        }
        this.serveur.messagePublic(this, message.split(" ", 2)[1].toString());
    }

    private void traiterMP_TO(String message) {
        if (this.getPseudo() == null || this.getPseudo().isBlank()) {
            this.envoyerMessageErreur("Veuillez vous connecter avant d'envoyer un message");
            return;
        }
        String[] mots = message.split(" ", 3);
        try {
            this.serveur.messagePrive(this, mots[1].toString(), mots[2].toString());
        } catch (ServeurUnoException e) {
            this.envoyerMessageErreur("Erreur d'envoi de message prive (est-ce que le destinataire est connecte ?)");
            return;
        }
    }
    /*
    public void traiterPioche() {
        Joueur joueur = getJoueur();
        try {
            Carte cartePiochee = joueur.piocher(serveur.getPartie());
            envoyerMessagePioche(cartePiochee);
        } catch (UNOException e) {
            envoyerMessageErreur(e.getMessage());
        }
        envoyerMessageMain();

    }
     */
    public void traiterPioche() {
        Joueur joueur = getJoueur();

        if (!serveur.isPartieEnCours()) {
            envoyerMessageErreur("La partie n’a pas encore commencé.");
            return;
        }

        if (!joueur.equals(serveur.getPartie().getJoueurCourant())) {
            envoyerMessageErreur("Ce n’est pas votre tour.");
            return;
        }

        try {
            // Le joueur pioche une carte
            Carte cartePiochee = joueur.piocher(serveur.getPartie());

            // Message de pioche
            envoyerMessagePioche(cartePiochee);

            // Main mise à jour
            envoyerMessageMain();

            // ➤ Tour terminé, qu'importe la carte
            serveur.finirTour(this);

        } catch (UNOException e) {
            envoyerMessageErreur(e.getMessage());
        }
    }

    public void traiterUno() {
        try {
            getJoueur().direUno();
            envoyerMessageUno();

        } catch (UNOException e) {
            envoyerMessageErreur(e.getMessage());
        }
    }

    public void envoyerMessageMain() {
        if (joueur == null) {
            envoyerMessageErreur("Vous n'êtes pas connecté au jeu.");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("@MAIN ");
        for (Carte carte : joueur.getMain()) {
            stringBuilder.append(" ").append(carte.toCode());
        }
        envoyer(stringBuilder.toString());
    }

    /*
    * Gestion du protocole pour le tas
    */
    public void envoyerTas(Carte carte) {
        String valeur = (carte instanceof CarteSimple simple) ? String.valueOf(simple.getValeur()) :
                (carte instanceof CartePlus2) ? "+2" : "?";
        envoyer("@TAS " + carte.getCouleur() + " " + getClass().getSimpleName() + " [ " + valeur + " " + carte.getCouleur() + " ]");
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


    //*************** ENCODAGE DES MESSAGES (BAS NIVEAU) ****************

    public void envoyer(String message) {
        threadConnexion.envoyerMessage(message);
    }

    public boolean verifieProtocole(String message) {
        for (String phraseDuProtocole : protocole) {
            if (verifiePhraseDuProtocole(message, phraseDuProtocole))
                return true;
        }
        return false;
   }
    public boolean verifiePhraseDuProtocole(String message, String phrase) {
        Pattern pattern = Pattern.compile(phrase, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(message).matches();
    }
    public void envoyerMessageErreur(String message) {
        envoyer("@ERREUR " + message);
    }

    public void envoyerMessagePublic(ConnexionJoueurUno emetteur, String message) {
        envoyer("@PUBLIC_FROM " + emetteur.getPseudo() + " " + message);
    }

    public void envoyerMessagePrive(ConnexionJoueurUno emetteur, String message) {envoyer("@MP_FROM " + emetteur.getPseudo() + " " + message);}
    public void envoyerCarteJouee(Carte carte) {
        envoyer("@CARTEJOUEE " + carte.toCode());
    }
    public void envoyerMessageInfo(String texte) {
        envoyer("@INFO " + texte);
    }
    public void envoyerMessagePioche(Carte carte) {envoyer("@PIOCHE Tu as pioché la carte : " + carte.toString());}
    public void envoyerMessageFinTour(){envoyer("@FIN_TOUR "+ this.getPseudo() + " a fini son tour.");}
    public void envoyerMessageUno(){envoyer("@UNO "+ this.getPseudo() + " a dit UNO !");}

    public void envoyerMessageVictoire(ServeurUno serveur, Joueur joueurVictoire, int score) {
        for (ConnexionJoueurUno c : serveur.getJoueursConnectes()) {
            c.envoyer("@VICTOIRE " + joueurVictoire.getNom() + " à gagné la partie et à obtenu un score de : " + score + " points !");
        }
    }
    //public void envoyerMessageTas(Carte carte) {
    //    envoyer("@TAS " + carte.toCode());
    //}

    public void envoyerListeJoueurs(List<ConnexionJoueurUno> joueursConnectes) {
        StringBuilder sb = new StringBuilder("@LISTE_JOUEURS");
        for (ConnexionJoueurUno c : joueursConnectes) {
            Joueur j = c.getJoueur();
            sb.append(" [").append(j.getNom()).append(";").append(j.getMain().size()).append("]");
        }
        serveur.envoyerATous(sb.toString());
    }

    public void envoyerListeUtilisateurs(List<ConnexionJoueurUno> connexions) {
        StringBuilder sb = new StringBuilder("@USERS ");
        for (ConnexionJoueurUno c : connexions) {
            String pseudo = c.getPseudo();
            if (pseudo != null) {
                sb.append(pseudo).append(";");
            }
        }
        envoyer(sb.toString());
    }

    public void envoyerFinManche(Joueur gagnant){
        for(ConnexionJoueurUno c : serveur.getJoueursConnectes()){
            c.envoyer("@FIN_MANCHE Le gagnant de cette manche est : " + gagnant.getNom() + " avec " + gagnant.getScore() + " points.");
        }
    }


}