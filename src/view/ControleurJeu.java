package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import network.ClientUno;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class ControleurJeu extends ControleurCommun {

    @FXML private VBox joueurHaut;
    @FXML private VBox joueurGauche;
    @FXML private VBox joueurDroite;
    @FXML private HBox mainJoueur;

    @FXML private Label labelHaut;
    @FXML private Label labelGauche;
    @FXML private Label labelDroite;
    @FXML private Label labelBas;

    @FXML private ImageView cartePioche;
    @FXML private ImageView carteTas;
    @FXML private Button boutonDemarrer;
    @FXML private Button boutonPoseCarte;
    @FXML private Button boutonFinTour;
    @FXML private Button boutonPioche;
    @FXML private Button boutonUno;

    private boolean aDitUno = false;
    private boolean monTour = false;
    private String valeurCarteSelectionnee = null;
    private String couleurCarteSelectionnee = null;
    private String tasCouleur = null;
    private String tasValeur = null;
    private int tailleMainPrecedente = -1;
    private boolean premierTas = true;


    private String pseudo;
    private List<String> joueurs = new ArrayList<>();

    public ControleurJeu(javafx.stage.Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        this.pseudo = ClientUno.getInstance().getPseudo();

        boutonDemarrer.setVisible(false);
        boutonPoseCarte.setDisable(true);
        boutonPioche.setDisable(true);
        boutonPoseCarte.setDisable(true);
        // Afficher l’image de dos
        String imagePathDos = "/images/cartes/carte_dos.png";
        try {
            Image imageDos = new Image(getClass().getResource(imagePathDos).toExternalForm());
            cartePioche.setImage(imageDos);
        } catch (Exception e) {
            System.err.println("Image de dos non trouvée : " + imagePathDos);
        }

        ClientUno.getInstance().getThreadClientUno().setOnMessageCallback(message -> {
            if (message.startsWith("@USERS")) {
                Platform.runLater(() -> mettreAJourJoueurs(message));
            } else if (message.startsWith("@MAIN")) {
                Platform.runLater(() -> majMain(message));
            }else if (message.startsWith("@LISTE_JOUEURS")) {
                Platform.runLater(() -> majListeJoueurs(message));
            } else if (message.startsWith("@TAS")) {
                Platform.runLater(() -> majTas(message));
            }
            else if (message.startsWith("@INFO")) {
                Platform.runLater(() -> gererTour(message));
            }
            else if (message.startsWith("@VICTOIRE")) {
                Platform.runLater(() -> {
                    String gagnant = message.substring(10).trim();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Victoire !");
                    alert.setHeaderText(null);
                    alert.setContentText(gagnant + " a gagné la partie !");
                    alert.showAndWait();

                    boutonFinTour.setDisable(true);
                    boutonPoseCarte.setDisable(true);
                    boutonPioche.setDisable(true);
                    monTour = false;
                });
            }

        });

        ClientUno.getInstance().envoyer("@GET_USERS");
    }

    private void mettreAJourJoueurs(String message) {
        joueurs.clear();
        String[] tokens = message.substring(7).split(";");
        for (String j : tokens) {
            if (!j.isBlank()) joueurs.add(j);
        }

        // Détermine les positions
        int myIndex = joueurs.indexOf(pseudo);
        List<String> autres = new ArrayList<>(joueurs);
        autres.remove(pseudo);

        labelBas.setText(pseudo);
        labelHaut.setText(autres.size() > 0 ? autres.get(0) : "");
        labelGauche.setText(autres.size() > 1 ? autres.get(1) : "");
        labelDroite.setText(autres.size() > 2 ? autres.get(2) : "");

        // Seul le joueur 0 (le premier connecté) voit le bouton
        if (!joueurs.isEmpty() && joueurs.get(0).equals(pseudo)) {
            boutonDemarrer.setVisible(true);
        } else {
            boutonDemarrer.setVisible(false);
        }
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }


    private void majTas(String message) {
        try {
            String[] parties = message.split("\\[|\\]");
            if (parties.length < 2) return;

            String contenu = parties[1].trim();
            String[] valeurCouleur = contenu.split(" ");

            System.out.println(">>> Débogage valeurCouleur[]");
            for (int i = 0; i < valeurCouleur.length; i++) {
                System.out.println("valeurCouleur[" + i + "] = '" + valeurCouleur[i] + "'");
            }

            if (valeurCouleur.length == 1) {
                tasValeur = "PTT";  // valeur par défaut pour les cartes "PasseTonTour"
                tasCouleur = capitalizeFirstLetter(valeurCouleur[0].trim().toLowerCase());

                // afficher l'image correspondante
                String imagePath = "/images/cartes/carte_" + tasValeur + "_" + tasCouleur + ".png";
                System.out.println(imagePath);
                URL resource = getClass().getResource(imagePath);
                if (resource == null) {
                    System.err.println("⚠️ Image non trouvée : " + imagePath);
                    return;
                }
                carteTas.setImage(new Image(resource.toExternalForm()));
                return;
            }

            // Cas normal (valeur + couleur)
            if (premierTas) {
                tasCouleur = capitalizeFirstLetter(valeurCouleur[0].trim().toLowerCase());
                tasValeur = valeurCouleur[1].trim();
                premierTas = false;
            } else {
                tasValeur = valeurCouleur[0].trim();
                tasCouleur = capitalizeFirstLetter(valeurCouleur[1].trim().toLowerCase());
            }

            if (tasValeur.equals("+2")) tasValeur = "12";

            String imagePath = "/images/cartes/carte_" + tasValeur + "_" + tasCouleur + ".png";
            System.out.println(imagePath);
            URL resource = getClass().getResource(imagePath);
            if (resource == null) {
                System.err.println("⚠️ Image non trouvée : " + imagePath);
                return;
            }

            carteTas.setImage(new Image(resource.toExternalForm()));
        } catch (Exception e) {
            System.err.println("Erreur majTas : " + e.getMessage());
        }
    }

    private void majMain(String message) {
        out.println(">> MAJ MAIN appelée avec : " + message);
        int tailleAvant = mainJoueur.getChildren().size();
        mainJoueur.getChildren().clear();
        String[] cartes = message.substring(6).trim().split(" ");
        for (String c : cartes) {
            if (c.isBlank()) continue;

            String[] parts = c.replace("(", "").replace(")", "").split(";");
            if (parts.length == 2) {
                String rawValeur = parts[0];
                String valeur = switch (rawValeur) {
                    //case "PTT" -> "Passe";
                    case "+2" -> "12"; // affichage uniforme (optionnel)
                    default -> rawValeur;
                };

                String couleur = parts[1];
                String couleurFormatee = capitalizeFirstLetter(couleur);
                String imagePath = "/images/cartes/carte_" + valeur + "_" + couleurFormatee + ".png";

                try {
                    Image image = new Image(imagePath);
                    ImageView carteImage = new ImageView(image);
                    carteImage.setFitHeight(100);
                    carteImage.setFitWidth(65);

                    carteImage.setOnMouseClicked(e -> {
                        if (!monTour) return;

                        valeurCarteSelectionnee = valeur;
                        couleurCarteSelectionnee = couleur;
                        out.println("Carte sélectionnée : " + valeur + " " + couleur);

                        // Effacer l’effet sur toutes les cartes
                        for (javafx.scene.Node node : mainJoueur.getChildren()) {
                            node.setStyle("");
                        }
                        // Ajouter un effet visuel sur la carte sélectionnée
                        carteImage.setStyle("-fx-effect: dropshadow(gaussian, yellow, 10, 0.5, 0, 0);");
                    });
                    mainJoueur.getChildren().add(carteImage);
                    int tailleApres = mainJoueur.getChildren().size();


                } catch (Exception e) {
                    System.err.println("Image non trouvée : " + imagePath);
                }
            }
        }
        // --------> déplace tout ce bloc ici, après la boucle <--------
        int tailleApres = mainJoueur.getChildren().size();
        if (tailleApres == 1 && monTour) {
            boutonUno.setVisible(true);
        } else {
            boutonUno.setVisible(false);
            aDitUno = false; // reset
        }

        if (tailleMainPrecedente != -1 && tailleApres == tailleMainPrecedente + 2 && !monTour) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alerte +2");
            alert.setHeaderText(null);
            alert.setContentText("Vous avez pioché 2 cartes à cause d'un +2 !");
            alert.showAndWait();
        }

        tailleMainPrecedente = tailleApres;
        verifierSiCarteJouable();
    }

    private HBox genererCartesCachees(int nombre) {
        HBox hbox = new HBox();
        hbox.setSpacing(-40); // chevauchement : -40 = superposé
        for (int i = 0; i < nombre; i++) {
            ImageView dosCarte = new ImageView(new Image(getClass().getResource("/images/cartes/carte_dos.png").toExternalForm()));
            dosCarte.setFitHeight(100);
            dosCarte.setFitWidth(65);
            hbox.getChildren().add(dosCarte);
        }
        return hbox;
    }
    private void majListeJoueurs(String message) {
        joueurHaut.getChildren().clear();
        joueurGauche.getChildren().clear();
        joueurDroite.getChildren().clear();

        String[] lignes = message.substring("@LISTE_JOUEURS".length()).trim().split(" ");
        List<String> joueursSansMoi = new ArrayList<>();

        for (String ligne : lignes) {
            String[] split = ligne.replace("[", "").replace("]", "").split(";");
            if (split.length != 2) continue;

            String nom = split[0];
            int nbCartes = Integer.parseInt(split[1]);

            if (nom.equals(pseudo)) continue; // ne pas afficher ses propres cartes ici

            joueursSansMoi.add(nom);
            VBox cible;
            if (joueursSansMoi.size() == 1) {
                cible = joueurHaut;
            } else if (joueursSansMoi.size() == 2) {
                cible = joueurGauche;
            } else {
                cible = joueurDroite;
            }

// Ajoute le label (nom du joueur) dynamiquement
            Label label = new Label(nom);
            cible.getChildren().add(label);

// Ajoute les cartes dos
            HBox cartesCachees = genererCartesCachees(nbCartes);
            cible.getChildren().add(cartesCachees);
        }
    }

    @FXML
    public void demarrerPartie() {
        ClientUno.getInstance().envoyer("@DEMARRER_PARTIE");
        boutonDemarrer.setVisible(false); // on le cache après clic
    }

    private void gererTour(String message) {
        if (message.contains(pseudo)) {
            monTour = true;
            boutonPoseCarte.setDisable(false);
            boutonFinTour.setDisable(true); // on n'autorise pas de suite la fin du tour
            System.out.println(">> C’est mon tour !");
        } else {
            monTour = false;
            boutonPoseCarte.setDisable(true);
            boutonFinTour.setDisable(true);
            valeurCarteSelectionnee = null;
            couleurCarteSelectionnee = null;

            // Nettoyer visuellement la sélection éventuelle
            for (javafx.scene.Node node : mainJoueur.getChildren()) {
                node.setStyle("");
            }
        }
    }

    @FXML
    public void poserCarte() {
        if (!monTour) {
            System.out.println("Ce n’est pas ton tour.");
            return;
        }

        if (valeurCarteSelectionnee == null || couleurCarteSelectionnee == null) {
            System.out.println("Aucune carte sélectionnée.");
            return;
        }

        ClientUno.getInstance().jouerCarte(couleurCarteSelectionnee, valeurCarteSelectionnee);
        boutonPioche.setDisable(true);
        valeurCarteSelectionnee = null;
        couleurCarteSelectionnee = null;
        boutonPoseCarte.setDisable(true);
        boutonFinTour.setDisable(false);

        for (javafx.scene.Node node : mainJoueur.getChildren()) {
            node.setStyle("");
        }
    }

    @FXML
    public void finDuTour() {
        if (!monTour) {
            System.out.println("Ce n'est pas ton tour.");
            return;
        }

        ClientUno.getInstance().envoyer("@FIN_TOUR");

        monTour = false;
        boutonFinTour.setDisable(true);
        boutonPoseCarte.setDisable(true);
    }

    private void verifierSiCarteJouable() {
        if (!monTour || tasValeur == null || tasCouleur == null) {
            boutonPioche.setDisable(true);
            return;
        }

        boolean peutJouer = false;

        for (javafx.scene.Node node : mainJoueur.getChildren()) {
            if (node instanceof ImageView imageView) {
                String idImage = imageView.getImage().getUrl();
                if (idImage.contains("/carte_")) {
                    String nomCarte = idImage.substring(idImage.lastIndexOf("/") + 1)
                            .replace("carte_", "")
                            .replace(".png", "");

                    String[] parts = nomCarte.split("_"); // [Bleu, 2]
                    if (parts.length == 2) {
                        String couleur = parts[0];
                        String valeur = parts[1];

                        //if (valeur.equals("Passe")) valeur = "PTT";
                        if (valeur.equals("12")) valeur = "+2";

                        String tasValeurComparee = tasValeur.equals("12") ? "+2" : tasValeur;

                        if (couleur.equalsIgnoreCase(tasCouleur) || valeur.equalsIgnoreCase(tasValeurComparee)) {
                            peutJouer = true;
                            break;
                        }
                    }
                }
            }
        }

        boutonPioche.setDisable(peutJouer); // activer que si AUCUNE carte jouable
    }

    @FXML
    public void piocher() {
        if (!monTour) {
            System.out.println("Ce n'est pas ton tour.");
            return;
        }

        ClientUno.getInstance().piocher(); // envoie au serveur

        // On empêche tout en local immédiatement
        monTour = false;
        boutonPioche.setDisable(true);
        boutonPoseCarte.setDisable(true);
        boutonFinTour.setDisable(true);
    }

    @FXML
    public void direUno() {
        if (!monTour || mainJoueur.getChildren().size() != 1) return;

        ClientUno.getInstance().envoyer("@UNO");
        aDitUno = true;
        boutonUno.setVisible(false);
    }

}