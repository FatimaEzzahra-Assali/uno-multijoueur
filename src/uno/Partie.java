package uno;

import java.util.List;

public class Partie {
    private List<Joueur> joueurs;
    private int joueurCourantIndex;
    private boolean aJoueCeTour;
    private Pioche pioche;
    private Tas tas;
    private int nbCartesAPiocher =0; //pour compter le nombre de cartes à piocher dues aux +2 enchainés
    private boolean actionPlus2Actif = false; //indique qu'un enchainement de +2 est en cours. le joueur doit jouer un autre +2 ou encaisser

    public Partie(List<Joueur> joueurs, Pioche pioche, Tas tas) {
        this.joueurs = joueurs;
        this.pioche = pioche;
        this.tas = tas;
        this.joueurCourantIndex = 0; // commence par le premier joueur
        this.aJoueCeTour = false;

        for (Joueur joueur : joueurs) {
            joueur.setPartie(this);
        }
    }

    public Joueur getJoueurCourant() {
        return joueurs.get(joueurCourantIndex);
    }

    public boolean getAJoueCeTour() {return aJoueCeTour;}
    public void setAJoueCeTour(boolean aJoueCeTour) {
        this.aJoueCeTour = aJoueCeTour;
    }


    public void passerAuJoueurSuivant() {
        joueurCourantIndex = (joueurCourantIndex + 1) % joueurs.size();
    }

    public void finirTour()throws UNOException{
        //Si le joueur tente de passer son tour sans dire Uno,
        if(getJoueurCourant().getNombreCartes() == 1 && !getJoueurCourant().aDitUno()){
            punir(this.getJoueurCourant());
            throw new UNOException("Le joueur courant n'a pas dit Uno !");
        }

        //Si le joueur fini son tour sans poser de carte, alors exception
        /*if(!aJoueCeTour){
            throw new UNOException("Le joueur ne peut pas finir son tour sans poser une carte.");
        }*/
        getJoueurCourant().resetUno();

        passerAuJoueurSuivant();
        aJoueCeTour = false; // Réinitialiser pour le prochain joueur

       // appliquerEffetsDebutTour(); // <--- Ajout important ici

    }


    public Pioche getPioche() {
        return pioche;
    }

    public Tas getTas() {
        return tas;
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    public int getNbCartesAPiocher(){
        return nbCartesAPiocher;
    }

    public void ajouterNbCartesAPiocher(int n){
        nbCartesAPiocher += n;
    }

    public boolean isActionPlus2Actif(){
        return actionPlus2Actif;
    }

    public void setActionPlus2Actif(boolean actif){
        actionPlus2Actif = actif;
    }

    public void resetActionPlus2() {
        nbCartesAPiocher = 0;
        actionPlus2Actif = false;
    }

    //le joueur pioche et passe son tour
    public void appliquerEffetsDebutTour() {
        // D'autres effets spéciaux à appliquer ici s
        Joueur joueur = getJoueurCourant();

        if (actionPlus2Actif) {
            // Si le joueur n’a pas de CartePlus2 en main, il doit encaisser
            boolean aCartePlus2 = joueur.possedeCartePlus2();

            if (!aCartePlus2) {
                for (int i = 0; i < nbCartesAPiocher; i++) {
                    if (!pioche.estVide()) {
                        joueur.ajouterCarte(pioche.piocher());
                    }

                }
                resetActionPlus2();
                //passerAuJoueurSuivant();
                aJoueCeTour = false;

            }
        }
    }

    //pour la punition le joueur pioche deux cartes puis passe son tour
    public void punir(Joueur joueur){
         for(int i=0; i<2;i++){
            if(!pioche.estVide()){
                joueur.ajouterCarte(pioche.piocher());
            }
        }
         //ici si le joueur puni est le joueur courant, on passe le tour sinon on ne fait pas appel à cette condition
        //comme dans le cas de la punition de bob lorsqu'il essaye de piocher hors son tour il pioche 2 (punition)mais on est toujours dans le tour d'alice
        if(joueur.equals(getJoueurCourant())){
            passerAuJoueurSuivant();
            aJoueCeTour = false;
        }
    }
}