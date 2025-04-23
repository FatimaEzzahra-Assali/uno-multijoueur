import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TestDuUno {

    private Joueur alice, bob, charles;
    private Partie partie;

    @BeforeEach
    public void setup() {
        // Cartes de départ pour les joueurs
        alice = new Joueur("Alice");
        alice.ajouterCarte(new CarteSimple(Couleur.VERT, 2));
        alice.ajouterCarte(new CarteSimple(Couleur.JAUNE, 6));

        bob = new Joueur("Bob");
        bob.ajouterCarte(new CarteSimple(Couleur.BLEU, 2));
        bob.ajouterCarte(new CarteSimple(Couleur.JAUNE, 4));

        charles = new Joueur("Charles");
        charles.ajouterCarte(new CarteSimple(Couleur.BLEU, 9));
        charles.ajouterCarte(new CarteSimple(Couleur.BLEU, 7));

        // Carte au sommet du tas
        Tas tas = new Tas();
        tas.poserCarte(new CarteSimple(Couleur.VERT, 8)); // carte initiale

        // Pioche (non utilisée ici mais nécessaire)
        Pioche pioche = new Pioche(List.of(
                new CarteSimple(Couleur.JAUNE, 6),
                new CarteSimple(Couleur.VERT, 2),
                new CarteSimple(Couleur.BLEU, 5),
                new CarteSimple(Couleur.VERT, 0),
                new CarteSimple(Couleur.BLEU, 3)
        ));

        // Partie
        partie = new Partie(List.of(alice, bob, charles), pioche, tas);
    }

    @Test
    public void testDireUnoBonMoment(){
        //Vérifier qu’Alice a bien 2 cartes
        assertEquals(2,alice.getNombreCartes());
        //Alice pose le « 2 Vert »
        Carte deuxVert = (CarteSimple) alice.getMain().get(0);
        alice.poserCarte(deuxVert, partie);
        //Alice dit « Uno ! »
        alice.direUno();
        //Alice finit son tour
        partie.finirTour();
        //Vérifier qu’Alice n’a plus qu’une seule carte
        assertEquals(1,alice.getNombreCartes());
        //Vérifier que la carte au sommet du tas est le « 2 Vert »
        assertEquals(partie.getTas().sommet(),deuxVert);
        //Vérifier que le joueur courant est Bob
        assertEquals(bob,partie.getJoueurCourant());
    }

    @Test
    /*
    * On decide de changer la regle : si le joueur oubli de dire uno, la carte qu'il a posé sur le tas reste sur le tas
    * */
    public void testOublierDireUno(){
    //Alice pose le « 2 Vert » Alice finit son tour
        Carte deuxVert = new CarteSimple(Couleur.VERT,2);

        alice.poserCarte((CarteSimple) alice.getMain().get(0), partie);
        //verifier qu'Alice n'a qu'une seule carte
        assertEquals(1,alice.getNombreCartes());

    //Dans le catch approprié : Punir Alice --> Alice sera punit dans la fonction finirTour()
        assertThrows(UNOException.class, () -> partie.finirTour());
    //Vérifier qu’Alice a maintenant 3 cartes
        assertEquals(3,alice.getNombreCartes());
    //Vérifier que la carte au sommet du tas est le « 2 Vert » Vérifier que le joueur courant est Bob
        assertEquals(partie.getTas().sommet(),deuxVert);
        assertEquals(bob,partie.getJoueurCourant());
    }

   @Test
   public void testDireUnoMauvaisMoment() {
        //Vérifier que Alice est le joueur courant
       assertEquals(alice, partie.getJoueurCourant());
       // Bob dit « Uno ! »
       assertThrows(UNOException.class, () -> bob.direUno());
       //Dans le catch approprié :
       //Punir Bob
       partie.punir(bob);
       //Vérifier que Bob a maintenant 4 cartes
       assertEquals(4, bob.getNombreCartes());
       //Vérifier qu’Alice est toujours le joueur courant Vérifier que la carte au sommet du tas est le « 8 Vert »
       assertEquals(partie.getTas().sommet(), new CarteSimple(Couleur.VERT, 8));
       assertEquals(alice, partie.getJoueurCourant());
   }


}
