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

    public ConnexionJoueurUno getConnexionJoueur(String pseudo) throws ServeurUnoException {
        for (ConnexionJoueurUno c : joueursConnectes) {
            if (pseudo.equalsIgnoreCase(c.getPseudo())) {
                return c;
            }
        }
        throw new ServeurUnoException("l'utilisateur "+ pseudo +" n'existe pas.");
    }

    /*
     * Un joueur va pouvoir lancer une partie a condition qu'il y a au moins deux joueurs connectés au serveur
     * et qu'il est le premier à s'être connecté au serveur.
     */
    public void lancerPartie() {
        // Pour chaque connexion, on récupère le joueur associé

        List<Joueur> joueurs = new ArrayList<>();
        for (ConnexionJoueurUno connexion : joueursConnectes) {
            if (connexion.getJoueur() != null) {//debug
                joueurs.add(connexion.getJoueur());
            }
        }

        // On crée la partie avec pioche + tas aléatoires
        partie = new Partie(joueurs, new Pioche(), new Tas());

        partie.initialiserPartie();
        setPartieEnCours(true);
        for (ConnexionJoueurUno connexion : joueursConnectes) {
            connexion.envoyerMessageMain();
        }
        for (ConnexionJoueurUno connexion : joueursConnectes) {
            connexion.envoyerListeJoueurs(joueursConnectes);
        }
        tourSuivant();
    }
    /*
    public void jouerCarte(ConnexionJoueurUno connexion, String couleur, String valeur) {
        if (!isPartieEnCours()) {
            connexion.envoyerMessageErreur("La partie n’a pas encore commencé.");
            return;
        }
        Joueur joueur = connexion.getJoueur();
        try {
            Couleur coul = Couleur.valueOf(couleur);
            int val = Integer.parseInt(valeur);
            Carte carteAJouer = joueur.trouverCarteDansMain(coul, val); //on cherche la carte dans la main du joueur

            joueur.poserCarte(carteAJouer, partie);
            envoyerTas(carteAJouer);
            connexion.envoyerCarteJouee(carteAJouer);
            //on envoie la main du joueur à chaque fois qu'il joue une carte
            connexion.envoyerMessageMain();
            connexion.envoyerListeJoueurs(getJoueursConnectes());
            //envoyerATous("@INFO " + joueur.getNom() + " a joué " + couleur + " " + valeur);

            //si le joueur n'a plus de carte après avoir posé sa dernière carte, il a gagné
            //alors, FIN DE PARTIE
            if (joueur.getMain().isEmpty()) {
                int scoreGagnant = calculerScoreGagnant(joueur); //on aura besion du score pour la gestion de la base de données
                connexion.envoyerMessageVictoire(this, joueur, scoreGagnant);
                // A VOIR finirPartie(); //on fini la partie (voir la fonction plus bas)
                return;
            }
            //Ces 2 là, je les ai enlevés, c'est au joueur de faire la demande de passer au tour suivant (et c'est là qu'on gère le uno?)
            //partie.finirTour();
            //tourSuivant();
        } catch (UNOException e) {
            connexion.envoyerMessageErreur(e.getMessage());
        } catch (Exception e) {
            connexion.envoyerMessageErreur("Carte invalide");
        }

    }
     */
    public void jouerCarte(ConnexionJoueurUno connexion, String couleur, String valeur) {
        if (!isPartieEnCours()) {
            connexion.envoyerMessageErreur("La partie n’a pas encore commencé.");
            return;
        }

        Joueur joueur = connexion.getJoueur();

        //Version plus robuste que l'ancienne, elle permet de mieux gerer les cartes spéciales comme +2 ou PTT
        try {
            Couleur coul = Couleur.valueOf(couleur.toUpperCase());
            Carte carteAJouer;

            // Création de la carte à jouer
            if (valeur.equals("+2")) {
                carteAJouer = new CartePlus2(coul);
            } else if (valeur.equalsIgnoreCase("PTT")) {
                carteAJouer = new CartePasseTonTour(coul);
            } else {
                int val = Integer.parseInt(valeur);
                carteAJouer = new CarteSimple(coul, val);
            }

            // Recherche dans la main du joueur
            Carte carteDansMain = null;

            if (carteAJouer instanceof CarteSimple simple) {
                carteDansMain = joueur.trouverCarteDansMain(coul, simple.getValeur());
            } else {
                for (Carte c : joueur.getMain()) {
                    if (c.equals(carteAJouer)) {
                        carteDansMain = c;
                        break;
                    }
                }
            }

            if (carteDansMain == null) {
                connexion.envoyerMessageErreur("Tu n'as pas cette carte en main !");
                return;
            }

            // Poser la carte
            joueur.poserCarte(carteDansMain, partie);

            // Si c’est une carte +2, activer l’effet sans empilement
            if (carteDansMain instanceof CartePlus2) {
                partie.setActionPlus2Actif(true);
            }

            // Mise à jour pour tous
            envoyerTas(carteDansMain);
            connexion.envoyerCarteJouee(carteDansMain);
            connexion.envoyerMessageMain();
            connexion.envoyerListeJoueurs(getJoueursConnectes());

        } catch (UNOException e) {
            connexion.envoyerMessageErreur(e.getMessage());
        } catch (Exception e) {
            connexion.envoyerMessageErreur("Carte invalide");
        }
    }

    public void encaisse(ConnexionJoueurUno connexion) {
        Joueur joueur = connexion.getJoueur();

        if (!partieEnCours) {
            connexion.envoyerMessageErreur("La partie n’a pas encore commencé.");
            return;
        }

        if (!joueur.equals(partie.getJoueurCourant())) {
            connexion.envoyerMessageErreur("Ce n’est pas votre tour.");
            return;
        }

        if (!partie.isActionPlus2Actif()) {
            connexion.envoyerMessageErreur("Il n’y a aucun +2 à encaisser.");
            return;
        }

        int nb = partie.getNbCartesAPiocher();
        Pioche pioche = partie.getPioche();

        for (int i = 0; i < nb; i++) {
            if (!pioche.estVide()) {
                joueur.ajouterCarte(pioche.piocher());
            }
        }

        connexion.envoyerMessageMain(); // on renvoie la main mise à jour
        partie.resetActionPlus2();  // annul du cumul des cartes à encaisser
        partie.setAJoueCeTour(true);

        try {
            partie.finirTour();
            tourSuivant();
        } catch (UNOException e) {
            connexion.envoyerMessageErreur(e.getMessage());
        }
    }


    public void finirTour(ConnexionJoueurUno connexion) {
        Joueur joueur = connexion.getJoueur();

        if (!partieEnCours) {
            connexion.envoyerMessageErreur("La partie n’a pas commencé.");
            return;
        }

        if (!joueur.equals(partie.getJoueurCourant())) {
            connexion.envoyerMessageErreur("Ce n’est pas ton tour.");
            return;
        }
        try {
            partie.finirTour();
            connexion.envoyerMessageFinTour();
        } catch (UNOException e) {
            connexion.envoyerMessageErreur(e.getMessage());
        }
        // Si victoire
        if (joueur.getMain().isEmpty()) {
            int scoreGagnant = calculerScoreGagnant(joueur);
            connexion.envoyerMessageVictoire(this, joueur, scoreGagnant);
            System.out.println("JE FAIS AFFICHER FIN MANCHE");
            for (ConnexionJoueurUno c : joueursConnectes) {
                c.envoyerFinManche(this, joueur);
            }
            finirManche(joueur);
            return;
        }
    tourSuivant();
    }

    private void tourSuivant() {
        Joueur joueur = partie.getJoueurCourant();
        ConnexionJoueurUno connexion = getConnexionJoueur(joueur.getNom());

        // === PIOCHER AUTOMATIQUEMENT si +2 actif ===
        if (partie.isActionPlus2Actif()) {
            encaisse(connexion);
            return; // empêche double appel après encaisse()
        }

        envoyerATous("@INFO C’est au tour de " + joueur.getNom() + ".");
        connexion.envoyerMessageMain(); // On envoie la main a chaque fois qu'on passe au tour suivant
        partie.appliquerEffetsDebutTour(); //on passe au tour suivant donc on applique les effets en debut de tour
    }

    //appelé lorsuq'un joueur a 0 carte dans sa main
    /*public void finirManche() {
        setPartieEnCours(false);

        // 1. Enregistrer la partie
        jdbc.metier.PartieBDD partieBDD = jdbc.DaoPartie.creerNouvellePartie();

        // 2. Enregistrer les scores
        for (ConnexionJoueurUno connexion : joueursConnectes) {
            Joueur j = connexion.getJoueur();
            int score = 0;
            for (Carte c : j.getMain()) {
                if (c instanceof CarteSimple simple) {
                    score += simple.getValeur();
                } else if (c instanceof CartePlus2 || c instanceof CartePasseTonTour) {
                    score += 10;
                }
            }

            // Enregistrement score en BDD
            jdbc.DaoScore.enregistrerScore(j.getNom(), partieBDD.getId(), score);
        }

        // 3. Envoyer fin de manche à tous
        for (ConnexionJoueurUno joueur : joueursConnectes) {
            joueur.envoyerFinManche();
        }

        setPartie(null);
        joueursConnectes.clear();
    }*/
    public void finirManche(Joueur gagnant) {

        // 1. Enregistrer la partie dans la BDD
        jdbc.metier.PartieBDD partieBDD = jdbc.DaoPartie.creerNouvellePartie();


        if (gagnant == null) {
            System.err.println("Erreur : aucun joueur n’a fini la manche.");
            return;
        }

        // 3. Calculer le score à attribuer au gagnant
        int scoreTotal = 0;
        for (ConnexionJoueurUno connexion : joueursConnectes) {
            Joueur joueur = connexion.getJoueur();
            if (joueur != gagnant) {
                for (Carte carte : joueur.getMain()) {
                    if (carte instanceof CarteSimple simple) {
                        scoreTotal += simple.getValeur();
                    } else if (carte instanceof CartePlus2 || carte instanceof CartePasseTonTour) {
                        scoreTotal += 10;
                    }
                    // Tu peux ajouter d’autres cartes spéciales ici si besoin
                }
            }
        }

        // 4. Mettre à jour le score du gagnant en mémoire et en BDD
        gagnant.ajouterScore(scoreTotal);

        //On enregistre dans la bdd les scores de chaque joueur
        assert partieBDD != null;
        for (ConnexionJoueurUno connexion : joueursConnectes) {
            Joueur joueur = connexion.getJoueur();
            int scoreManche = (joueur == gagnant) ? scoreTotal : 0; // score uniquement de cette manche
            jdbc.DaoScore.enregistrerScore(joueur.getNom(), partieBDD.getId(), scoreManche);
        }

        System.err.println(">> Envoi du message @FIN_MANCHE à tous les joueurs.");
        // 5. Informer tous les joueurs de la fin de la manche
        for (ConnexionJoueurUno connexion : joueursConnectes) {
            connexion.envoyerFinManche(this, gagnant);
        }

        for (ConnexionJoueurUno c : joueursConnectes) {
            c.getJoueur().getMain().clear();     // Vide la main du joueur
            c.getJoueur().resetUno();            // Réinitialise Uno
            c.getJoueur().setPartie(null);       // Supprime la référence à l’ancienne partie
        }

        // 6. Réinitialiser la partie (mais pas les joueurs connectés)
        for (ConnexionJoueurUno c : joueursConnectes) {
            c.getJoueur().getMain().clear();
            c.getJoueur().resetUno();
            c.getJoueur().setPartie(null);
        }

        setPartieEnCours(false); // <-- C’est important ici
        setPartie(null);
    }

    /* public void finirManche() {
        setPartieEnCours(false);
        setPartie(null);
        for (ConnexionJoueurUno joueur : joueursConnectes) {
            joueur.envoyerFinManche();
        }
        joueursConnectes.clear();
    }*/

    public void envoyerATous(String message) {
        for (ConnexionJoueurUno joueur : joueursConnectes) {
            joueur.envoyer(message);
        }
    }

    /*public void envoyerTas(Carte carte){
        //on envoie le message a tout le monde
        for (ConnexionJoueurUno joueur : joueursConnectes) {
            joueur.envoyerTas();
        }
    }
    public void envoyerTas(Carte carte){
        String valeur = "";

        if (carte instanceof CarteSimple simple) {
            valeur = String.valueOf(simple.getValeur());
        } else if (carte instanceof CartePlus2) {
            valeur = "+2";
        }
        String couleur = carte.getCouleur().name();

        String message = "@TAS " + couleur + " " + carte.getClass().getSimpleName() + " [ " + valeur + " " + couleur + " ]";

        for (ConnexionJoueurUno joueur : joueursConnectes) {
            joueur.envoyer(message);
        }
    }
*/

    public void envoyerTas(Carte carte){
        if (carte != null) {
            for (ConnexionJoueurUno joueur : joueursConnectes) {
                joueur.envoyerTas(carte);
            }
        }
    }

    public void messagePublic(ConnexionJoueurUno emetteur, String message) {
        // On remplace les mots tabous du message
        for (String mot : motsCensures) {
            message = message.replace(mot, censure);
        }
        // Et on parcours la liste des utilisateurs pour leur envoyer le message à chacun (sauf à soi-même)
        for (ConnexionJoueurUno c : joueursConnectes) {
            /*if (c.equals(emetteur))
                continue;
            */
            c.envoyerMessagePublic(emetteur, message);
        }
    }

    public void messagePrive(ConnexionJoueurUno emetteur, String pseudoDestination, String message){
        ConnexionJoueurUno dest = getConnexionJoueur(pseudoDestination);

        dest.envoyerMessagePrive(emetteur, message);
    }

    public ArrayList<String> motsCensures = new ArrayList<>();
    public String censure = "@#$?!";

    private void initCensure() {
        motsCensures.add("BAYROU");
        motsCensures.add("MACRON");
        motsCensures.add("LE PEN");
        motsCensures.add("MELENCHON");
        motsCensures.add("RETAILLEAU");
        motsCensures.add("FAURE");
        motsCensures.add("TONDELLIER");
        motsCensures.add("ROUSSEL");
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

    public void envoyerListeUtilisateursConnectes(ConnexionJoueurUno demandeur) {
        StringBuilder sb = new StringBuilder("@USERS");
        for (ConnexionJoueurUno c : joueursConnectes) {
            sb.append(" ").append(c.getPseudo());
        }
        demandeur.envoyer(sb.toString());
    }

}
