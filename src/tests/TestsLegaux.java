package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uno.Carte;
import uno.CarteSimple;
import uno.Couleur;
import uno.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestsLegaux {
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

        // Pioche (non utilisée ici mais nécessaire)
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
    public void testAliceJoueCarteValideEtPasseTour() throws Exception {
        // Vérifier que le joueur courant est Alice
        assertEquals("Alice", partie.getJoueurCourant().getNom());

        // Vérifier que Alice a bien 3 cartes
        assertEquals(3, alice.getNombreCartes());

        // Alice joue le 2 Vert
        CarteSimple deuxVert = (CarteSimple) alice.getMain().get(0);
        alice.poserCarte(deuxVert, partie);

        // Vérifier qu'elle a maintenant 2 cartes
        assertEquals(2, alice.getNombreCartes());

        // Vérifier que ses cartes sont le 6 jaune et le 1 rouge
        assertTrue(alice.getMain().stream().anyMatch(c -> c instanceof CarteSimple && ((CarteSimple) c).getValeur() == 6 && c.getCouleur() == Couleur.JAUNE));
        assertTrue(alice.getMain().stream().anyMatch(c -> c instanceof CarteSimple && ((CarteSimple) c).getValeur() == 1 && c.getCouleur() == Couleur.ROUGE));

        // Vérifier que la carte au sommet du tas est le 2 Vert
        Carte sommet = partie.getTas().sommet();
        assertTrue(sommet instanceof CarteSimple);
        assertEquals(Couleur.VERT, sommet.getCouleur());
        assertEquals(2, ((CarteSimple) sommet).getValeur());

        // Vérifier que le nombre de cartes dans le tas est 2
        assertEquals(2, partie.getTas().taille());

        // Alice finit son tour
        partie.finirTour();

        // Vérifier que le joueur courant est maintenant Bob
        assertEquals("Bob", partie.getJoueurCourant().getNom());
    }
}