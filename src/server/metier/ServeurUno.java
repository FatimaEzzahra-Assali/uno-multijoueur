package server.metier;

import model.*;
import server.reseau.ConnexionJoueurUno;
import server.reseau.ThreadAcceptConnexion;

import java.util.ArrayList;
import java.util.List;

public class ServeurUno {

    private ArrayList<ConnexionJoueurUno> joueursConnectes = new ArrayList<>(); //liste des joueurs connectés
    private Partie partie;  //gestion de la partie
    private boolean partieEnCours = false; //on rajoute cette variable pour savoir si la partie est en cours ou non
    private int port;       //port sur lequel le serveur est en cours de lancement

    public ServeurUno(int port) {
        this.partieEnCours = false;
        this.port = port;
        // On lance le thread qui s'occupe de gérer les connexions qui arrivent sur le serveur
        new ThreadAcceptConnexion(this);
    }

    //******************* les méthodes standard ************************

    public boolean isPartieEnCours() {
        return partieEnCours;
    }

    public void setPartieEnCours(boolean partieEnCours) {
        this.partieEnCours = partieEnCours;
    }

    public Partie getPartie() {
        return partie;
    }

    public void setPartie(Partie partie) {
        this.partie = partie;
    }

    public ArrayList<ConnexionJoueurUno> getJoueursConnectes() {
        return joueursConnectes;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean add(ConnexionJoueurUno joueur) throws ServeurUnoException {
        if (joueur == null)
            throw new ServeurUnoException("La connexion utilisateur vaut null");

        return joueursConnectes.add(joueur);
    }

    public boolean remove(ConnexionJoueurUno utilisateur) throws ServeurUnoException {
        if (utilisateur == null)
            throw new ServeurUnoException("La connexion utilisateur vaut null");

        return joueursConnectes.remove(utilisateur);
    }

    //******************** fin des méthodes standard ************************

    /*Gestion du protocole @CONNEXION
    * Cette fonction permet à un joueur de se connecter au serveur et de lui donner un pseudo
    * @param connexion : la connexion du joueur qui vient de se connecter.
    * @param pseudo : le pseudo du joueur qui vient de se connecter.
    * */
    public void ajouterJoueur(ConnexionJoueurUno connexion, String pseudo) {

        for (ConnexionJoueurUno c : joueursConnectes) {
            if (pseudo.equalsIgnoreCase(c.getPseudo())) {
                connexion.envoyer("@ERREUR Ce pseudo est déjà utilisé.");
                return;
            }
        }

        if (partieEnCours) {
            connexion.envoyer("@ERREUR La partie a déjà commencé !");
            return;
        }

        Joueur joueur = new Joueur(pseudo);
        connexion.setJoueur(joueur);
        connexion.setPseudo(pseudo);

        joueursConnectes.add(connexion);

        // Informer tous les joueurs
        envoyerATous("@REJOINDRE " + pseudo);
    }

    /*
     * Un joueur va pouvoir lancer une partie a condition qu'il y a au moins deux joueurs connectés au serveur
     * @param connexion : la connexion du joueur qui vient de se connecter.
     * @param pseudo : le pseudo du joueur qui vient de se connecter.
     * */
    public void lancerPartie() {
        if (partieEnCours) return;
        if (joueursConnectes.size() < 2) {
            envoyerATous("@ERREUR Il faut au moins 2 joueurs !");
            return;
        }

        // POur chaque connexion, on récupère le joueur associé
        List<Joueur> joueurs = new ArrayList<>();
        for (ConnexionJoueurUno connexion : joueursConnectes) {
            joueurs.add(connexion.getJoueur());
        }

        // On crée la partie avec pioche + tas aléatoires
        partie = new Partie(joueurs, new Pioche(), new Tas());

        partie.initialiserPartie();
        partieEnCours = true;
        envoyerATous("@COMMENCER");
        envoyerATous("@INFO Il y a " + joueursConnectes.size() + " joueurs connectés.");
        tourSuivant();
    }

    /* Gestion du protocole @CARTE
    * @param connexion : la connexion du joueur qui vient de se connecter.
    * @param couleur : la couleur de la carte.
    * @param valeur : la valeur de la carte.
    * la valeur 10 : carte cartePlus2
    * la valeur 11 : carte passeTonTour
    * */
    public void traiterCarte(ConnexionJoueurUno connexion, String couleur, String valeur) {
        //???
        if (!partieEnCours) {
            connexion.envoyer("@ERREUR La partie n’a pas encore commencé.");
            return;
        }

        Joueur joueur = connexion.getJoueur();
        try {
            Couleur coul = Couleur.valueOf(couleur);
            int val = Integer.parseInt(valeur);
            Carte carteAJouer = joueur.trouverCarteDansMain(coul, val); //on cherche la carte dans la main du joueur

            joueur.poserCarte(carteAJouer, partie);
            envoyerATous("@INFO " + joueur.getNom() + " a joué " + couleur + " " + valeur);

            //si le joueur n'a plus de carte après avoir posé sa dernière carte, il a gagné
            //alors, FIN DE PARTIE
            if (joueur.getMain().isEmpty()) {
                int scoreGagnant = calculerScoreGagnant(joueur); //on aura besion du score pour la gestion de la base de données
                envoyerATous("@INFO " + joueur.getNom() + "à gagné et a obtenu un score de " + scoreGagnant + " !");
                // A VOIR finirPartie(); //on fini la partie (voir la fonction plus bas)
                return;
            }
            partie.finirTour();
            tourSuivant();
        } catch (UNOException e) {
            connexion.envoyer("@ERREUR " + e.getMessage());
        } catch (Exception e) {
            connexion.envoyer("@ERREUR Carte invalide");
        }

    }

    /*Gestion du protocole @PIOCHER
    * @param connexion : la connexion du joueur qui vient de se connecter.
    * */
    public void traiterPioche(ConnexionJoueurUno connexion) {
        Joueur joueur = connexion.getJoueur();
        try {
            Carte cartePiochee = joueur.piocher(partie);
            connexion.envoyer("@INFO Tu as pioché la carte : " + cartePiochee.toString());
            //partie.finirTour();
            tourSuivant();
        } catch (UNOException e) {
            connexion.envoyer("@ERREUR " + e.getMessage());
        }

    }

    /*Gestion du protocole @UNO
    * @param connexion : la connexion du joueur qui vient de se connecter
    * */
    public void traiterUno(ConnexionJoueurUno connexion) {
        Joueur joueur = connexion.getJoueur();
        try {
            joueur.direUno();
            envoyerATous("@INFO " + joueur.getNom() + " a dit UNO !");

        } catch (UNOException e) {
            connexion.envoyer("@ERREUR " + e.getMessage());
        }
    }


    /* Gestion du protocole @FIN
    * @param connexion : la connexion du joueur qui vient de se connecter.
    */
    private void tourSuivant() {
        Joueur joueur = partie.getJoueurCourant();
        envoyerATous("@INFO C’est au tour de " + joueur.getNom() + ".");

        //on passe au tour suivant donc on applique les effets en debut de tour
        partie.appliquerEffetsDebutTour();
    }

    //appelé lorsuq'un joueur a 0 carte dans sa main
    public void finirPartie() {
        setPartieEnCours(false);
        setPartie(null);
        joueursConnectes.clear();
        envoyerATous("@FIN Fin du jeu ");

    }

    public void envoyerATous(String message) {
        for (ConnexionJoueurUno joueur : joueursConnectes) {
            joueur.envoyer(message);
        }
    }

    public int calculerScoreGagnant(Joueur joueur) {
        int score = 0;
        for (ConnexionJoueurUno c : joueursConnectes) {
            Joueur j = c.getJoueur();
            //il faut bien évidement sauter la main du joueur qui a gagné
            if (!j.equals(joueur)) {
                for (Carte carte : j.getMain()) {
                    if (carte instanceof CarteSimple simple) {
                        score += simple.getValeur();
                    } else if (carte instanceof CartePlus2 || carte instanceof CartePasseTonTour) {
                        score += 10;
                    }
                    // RAJOUTER si on rajoute d'autres cartes
                }
            }
        }

        return score;
    }

}
