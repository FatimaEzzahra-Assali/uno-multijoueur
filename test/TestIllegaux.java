import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestIllegaux {
    private Joueur alice, bob, charles;
    private Partie partie;

    @BeforeEach
    public void setup() {
        // Cartes de départ pour les joueurs
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
    public void testCarteIllegale(){
        // Alice joue le 6 jaune
        CarteSimple sixJaune = (CarteSimple) alice.getMain().get(1);
        assertThrows(UNOException.class, () -> alice.poserCarte(sixJaune, partie));


        //Vérifier dans le catch approprié que Alice possède toujours 3 cartes dont le « 6 Jaune »
        assertEquals(3, alice.getNombreCartes());
        assertTrue(alice.getMain().stream().anyMatch(c -> c instanceof CarteSimple && ((CarteSimple) c).getValeur() == 6 && c.getCouleur() == Couleur.JAUNE));
    }

    @Test
    public void testDeuxCartesPoseesDeSuite() {
        //Alice pose le « 2 Vert » et finit son tour
        alice.poserCarte((CarteSimple) alice.getMain().get(0), partie);
        partie.finirTour();

        //Bob pose le « 2 Bleu » et finit son tour
        bob.poserCarte((CarteSimple) bob.getMain().get(0), partie);
        partie.finirTour();

        //Charles pose le « 6(9???) Bleu » (RAS, c’est correct mais Charles ne finit pas le tour)
        CarteSimple neufBleu = (CarteSimple) charles.getMain().get(0);
        charles.poserCarte((CarteSimple) neufBleu, partie);

        //Vérifier que Charles possède 2 cartes
        assertEquals(2, charles.getNombreCartes());

        //Charles pose le « 7(9????) Bleu » (Carte légale mais il a déjà posé...)
        assertThrows(UNOException.class, () -> charles.poserCarte((CarteSimple) neufBleu, partie));

        //Vérifier dans le catch approprié que Charles possède toujours 2 cartes dont le « 2 Bleu »
        assertEquals(2, charles.getNombreCartes());
        assertTrue(charles.getMain().stream().anyMatch(carte -> carte instanceof CarteSimple && ((CarteSimple) carte).getValeur() == 7 && carte.getCouleur() == Couleur.BLEU));
    }

    @Test
    public void testFinirTourSansRienFaire(){
        //Alice finit son tour
        assertThrows(UNOException.class, () -> {
            partie.finirTour();
        });
        //Vérifier dans le catch approprié que Alice possède toujours 3 cartes
        assertEquals(3, alice.getNombreCartes());
    }

    @Test
    public void testJouerPuisPiocher(){
        //Alice joue le « 2 Vert » (RAS, le coup est légal)
        CarteSimple deuxVert = (CarteSimple) alice.getMain().get(0);
        alice.poserCarte(deuxVert, partie);

        //Alice pioche
        assertThrows(UNOException.class, () -> alice.piocher(partie));

        //Vérifier dans le catch approprié que Alice possède toujours 2 cartes
        assertEquals(2, alice.getNombreCartes());

        //Vérifier que la carte de la pioche est toujours le « 6 jaune »
        Carte sixJaune = new CarteSimple(Couleur.JAUNE, 6);
        Carte cartePioche = partie.getPioche().piocher();
        assertEquals(cartePioche, sixJaune);

    }
}
