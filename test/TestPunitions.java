import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPunitions {
    private Partie partie;
    private Joueur alice, bob, charles;

    @BeforeEach
        //Pour	chacun	des	tests	de	cette	partie,	il	faut	réinitialiser	la	partie	pour	se	retrouver	dans
        //les	conditions	des	tests	précédent
    void setup(){
        alice = new Joueur("Alice");
        alice.ajouterCarte(new CarteSimple(Couleur.VERT, 2));   // à jouer
        alice.ajouterCarte(new CarteSimple(Couleur.JAUNE, 6));
        alice.ajouterCarte(new CarteSimple(Couleur.ROUGE, 1));

        bob = new Joueur("Bob");
        bob.ajouterCarte(new CarteSimple(Couleur.BLEU, 2));
        bob.ajouterCarte(new CarteSimple(Couleur.JAUNE, 4));
        bob.ajouterCarte(new CarteSimple(Couleur.ROUGE, 9));

        charles = new Joueur("Charles");
        charles.ajouterCarte(new CarteSimple(Couleur.BLEU, 9));
        charles.ajouterCarte(new CarteSimple(Couleur.BLEU, 7));
        charles.ajouterCarte(new CarteSimple(Couleur.BLEU, 0));

        // Carte au sommet du tas
        Tas tas = new Tas();
        tas.poserCarte(new CarteSimple(Couleur.VERT, 8)); // carte initiale

        Pioche pioche = new Pioche(List.of(
                new CarteSimple(Couleur.JAUNE, 6),
                new CarteSimple(Couleur.ROUGE, 4),
                new CarteSimple(Couleur.VERT, 2),
                new CarteSimple(Couleur.BLEU, 5),
                new CarteSimple(Couleur.VERT, 0)
        ));

        // Partie
        partie = new Partie(List.of(alice, bob, charles), pioche, tas);
    }

    @Test
    void testPunitionCoupIllegalAlice(){
        //verifie que Alice est le joueur courant
        assertEquals(alice, partie.getJoueurCourant());

        Carte sixJaune = new CarteSimple(Couleur.JAUNE,6) ;
        try{
            alice.poserCarte(sixJaune,partie);
        } catch (UNOException e) {
            partie.punir(alice);
        }


        //apres la punition d'alice verifier que bob est le joueur courant
        assertEquals(bob, partie.getJoueurCourant());

        //Vérifier que Alice possède 5 cartes dont le « 6 jaune » et le « 4 rouge » (les cartes	de	la	pioche)
        Carte quatreRouge = new CarteSimple(Couleur.ROUGE,4) ;

        assertEquals(5, alice.getNombreCartes());
        assertTrue(alice.getMain().contains(sixJaune));
        assertTrue(alice.getMain().contains(quatreRouge));

        //verifie que la prochaine carte de la pioche est le "2vert"
        Carte deuxVert = new CarteSimple(Couleur.VERT,2);
        assertEquals(deuxVert,partie.getPioche().piocher());
    }

    @Test
    public void testPunitionActionBobHorsTour(){
        //verifie que alice est le joueur courant
        assertEquals(alice, partie.getJoueurCourant());

        //partie.setAJoueCeTour(true);
        try{
            bob.piocher(partie);
        }catch(UNOException e){
            partie.punir(bob);
        }

        //verifie qu'alice est toujours le joueur courant
        assertEquals(alice, partie.getJoueurCourant());

        //Vérifier que Bob possède 5 cartes	dont le	« 6	jaune »	et le « 4 rouge » (les 2 cartes de la pioche)
        Carte sixJaune = new CarteSimple(Couleur.JAUNE,6) ;
        Carte quatreRouge = new CarteSimple(Couleur.ROUGE,4) ;

        assertEquals(5, bob.getNombreCartes());
        assertTrue(bob.getMain().contains(sixJaune));
        assertTrue(bob.getMain().contains(quatreRouge));

        //verifie que la prochaine carte de la pioche est le "2vert"
        Carte deuxVert = new CarteSimple(Couleur.VERT,2);
        assertEquals(deuxVert, partie.getPioche().piocher());
    }
}

