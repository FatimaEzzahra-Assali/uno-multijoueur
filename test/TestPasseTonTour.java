import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestPasseTonTour {

    private Joueur alice, bob, charles;
    private Partie partie;

    @BeforeEach
    public void setup() {
        // Cartes de départ pour les joueurs
        alice = new Joueur("Alice");
        alice.ajouterCarte(new CartePasseTonTour(Couleur.ROUGE));
        alice.ajouterCarte(new CarteSimple(Couleur.BLEU, 9));
        alice.ajouterCarte(new CarteSimple(Couleur.JAUNE, 4));

        bob = new Joueur("Bob");
        bob.ajouterCarte(new CarteSimple(Couleur.JAUNE, 6));
        bob.ajouterCarte(new CarteSimple(Couleur.VERT, 6));
        bob.ajouterCarte(new CarteSimple(Couleur.BLEU, 7));


        charles = new Joueur("Charles");
        charles.ajouterCarte(new CarteSimple(Couleur.BLEU, 1));
        charles.ajouterCarte(new CartePasseTonTour(Couleur.VERT));
        charles.ajouterCarte(new CarteSimple(Couleur.ROUGE, 1));


        // Carte au sommet du tas
        Tas tas = new Tas();
        tas.poserCarte(new CarteSimple(Couleur.ROUGE, 9)); // carte initiale

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
    public void testLegalPasseTonTour(){
        //Vérifier que Alice est bien le joueur courant
        assertEquals(alice, partie.getJoueurCourant());
        //Alice pose le « Passe ton tout rouge »
        CartePasseTonTour passeTonTourRouge = (CartePasseTonTour) alice.getMain().get(0);
        alice.poserCarte(passeTonTourRouge, partie);
        //Alice finit son tour
        partie.finirTour();
        //Vérifier que Charles est le joueur courant
        assertEquals(charles, partie.getJoueurCourant());
        //Vérifier que la carte du tas est bien le « Passe ton tour rouge »
        assertEquals(passeTonTourRouge, partie.getTas().sommet());


        //Charles pose le « Passe ton tour vert »
        CartePasseTonTour passeTonTourVert = (CartePasseTonTour) charles.getMain().get(1);
        charles.poserCarte(passeTonTourVert, partie);
        //Charles finit son tour
        partie.finirTour();
        //Vérifier que Bob est le joueur courant
        assertEquals(bob, partie.getJoueurCourant());
        //Vérifier que la carte du tas est bien le « Passe ton tour vert »
        assertEquals(passeTonTourVert, partie.getTas().sommet());

        //Bob pose le « 6 Vert »
        CarteSimple sixVert = (CarteSimple) bob.getMain().get(1);
        bob.poserCarte(sixVert, partie);
        //Bob finit son tour
        partie.finirTour();
        //Vérifier que Charles est le joueur courant Vérifier que la carte du tas est bien le « 6 Vert »
        assertEquals(charles, partie.getJoueurCourant());
        assertEquals(sixVert, partie.getTas().sommet());
    }


    @Test
    public void testIllegalPasseTonTour(){
        //Vérifier que Alice est bien le joueur courant
        assertEquals(alice, partie.getJoueurCourant());
        //Alice pose le « Passe ton tout rouge »
        CartePasseTonTour passeTonTourRouge = (CartePasseTonTour) alice.getMain().get(0);
        alice.poserCarte(passeTonTourRouge, partie);
        //Alice finit son tour
        partie.finirTour();

        //Vérifier que Charles est le joueur courant
        assertEquals(charles, partie.getJoueurCourant());
        // Vérifier que Charles possède bien 3 cartes
        assertEquals(3, charles.getNombreCartes());
        // Charles pose le « 1 Bleu »
        CarteSimple unBleu = (CarteSimple) charles.getMain().get(0);
        assertThrows(UNOException.class, () -> charles.poserCarte(unBleu, partie) );
        //Charles finit son tour
        assertThrows(UNOException.class, () -> partie.finirTour());
        //Vérifier dans l’exception appropriée que Charles a toujours 3 cartes
        assertEquals(3,charles.getNombreCartes());
    }
}
