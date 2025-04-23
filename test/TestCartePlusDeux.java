import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.*;

import javax.swing.plaf.basic.BasicListUI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCartePlusDeux {


    private Joueur alice, bob, charles;
    private Partie partie;

    @BeforeEach
    public void setup() {
        // Cartes de départ pour les joueurs
        alice = new Joueur("Alice");
        alice.ajouterCarte(new CartePlus2(Couleur.VERT));
        alice.ajouterCarte(new CarteSimple(Couleur.BLEU, 9));
        alice.ajouterCarte(new CarteSimple(Couleur.JAUNE, 4));

        bob = new Joueur("Bob");
        bob.ajouterCarte(new CarteSimple(Couleur.JAUNE, 6));
        bob.ajouterCarte(new CarteSimple(Couleur.VERT, 6));
        bob.ajouterCarte(new CarteSimple(Couleur.BLEU, 7));

        charles = new Joueur("Charles");
        charles.ajouterCarte(new CarteSimple(Couleur.BLEU, 1));
        charles.ajouterCarte(new CartePlus2(Couleur.VERT));
        charles.ajouterCarte(new CarteSimple(Couleur.VERT, 1));

        // Carte au sommet du tas
        Tas tas = new Tas();
        tas.poserCarte(new CarteSimple(Couleur.VERT, 9)); // carte initiale

        // Pioche (non utilisée ici mais nécessaire)
        Pioche pioche = new Pioche(List.of(
                new CarteSimple(Couleur.BLEU, 0),
                new CarteSimple(Couleur.VERT, 8),
                new CarteSimple(Couleur.VERT, 2),
                new CarteSimple(Couleur.ROUGE, 4),
                new CarteSimple(Couleur.VERT, 2)
        ));

        // Partie
        partie = new Partie(List.of(alice, bob, charles), pioche, tas);
    }

    @Test
    public void testCoupLegalCartePlus2(){
        //verifie qu'alice le joueur Courant
        assertEquals(alice,partie.getJoueurCourant());
        //alise pose +2 vert et finit son tour
        alice.poserCarte(alice.getMain().get(0), partie);
        partie.finirTour();

        //verifier que bob est le joueur Courant
        assertEquals(bob,partie.getJoueurCourant());
        //verifie que bob possede 3cartes
        assertEquals(3, bob.getNombreCartes());
        //bob doit encaisser l'attaque et finir automatiquement son tour
        partie.appliquerEffetsDebutTour();
        //verifie que bob à 5cartes
        assertEquals(5, bob.getNombreCartes());
        partie.finirTour();
        //verifier que charles est le joueur courant
        //car apres l'application de l'effets debut Tour le joueur pioche et passe son tour
        assertEquals(charles,partie.getJoueurCourant());
        //charles pose le 1 vert
        charles.poserCarte(charles.getMain().get(2), partie);
        //charles finit son tour
        partie.finirTour();
        //verifier que charles a deux cartes
        assertEquals(2, charles.getNombreCartes());

    }


    @Test
    public void testCoupLegalAvecCumulPlus2(){
        //verifie qu'alice le joueur Courant
        assertEquals(alice,partie.getJoueurCourant());
        //alice pioche une carte
        alice.piocher(partie);
        //alice finit son tour
        partie.finirTour();

        //verifie que bob est le joueur Courant
        assertEquals(bob,partie.getJoueurCourant());
        //assertEquals("Bob", partie.getJoueurCourant().getNom());
        //bob pioche une carte
        bob.piocher(partie);
        //bob finit son tour
        partie.finirTour();

        //verifier que charles est le joueur Courant
        assertEquals(charles,partie.getJoueurCourant());
        //charles pose la carte +2 vert
        charles.poserCarte(charles.getMain().get(1),partie);
        //charles finit son tour
        partie.finirTour();

        //verifier que alice est le joueur Courant
        assertEquals(alice,partie.getJoueurCourant());
        //alice pose le 2 vert
        alice.poserCarte(alice.getMain().get(0),partie);
        //alice finit son tour
        partie.finirTour();

        //verifie que bob est le joueur courant
        assertEquals(bob,partie.getJoueurCourant());
        //verifie que bob a 4cartes
        assertEquals(4, bob.getNombreCartes());

        partie.appliquerEffetsDebutTour();
        partie.finirTour();
        //verifier que bob a 7cartes
        //on ne peut pas tester s'il possede 8 carte car la pioche n'a que 5 cartes ,2 deja pioché par alice et bob
        //donc il ne este que 3 carte dans la pioche -> on teste s'il a 7 carte à la place
        assertEquals(7, bob.getNombreCartes());

        //verifier que charles est le joueur courant
        assertEquals(charles,partie.getJoueurCourant());

    }
}
