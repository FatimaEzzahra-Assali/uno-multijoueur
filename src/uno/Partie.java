package uno;

import java.util.List;

public class Partie {
    private List<Joueur> joueurs;
    private int joueurCourantIndex;
    private boolean aJoueCeTour;
    private Pioche pioche;
    private Tas tas;

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
        getJoueurCourant().resetUno();
        passerAuJoueurSuivant();
        aJoueCeTour = false; // Réinitialiser pour le prochain joueur
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