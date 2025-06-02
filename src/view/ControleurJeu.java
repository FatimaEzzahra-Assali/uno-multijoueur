package view;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
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
    @FXML private Label labelTour;
    @FXML private Label labelInfos;

    @FXML private ImageView cartePioche;
    @FXML private ImageView carteTas;
    @FXML private Button boutonDemarrer;
    @FXML private Button boutonPoseCarte;
    @FXML private Button boutonFinTour;
    @FXML private Button boutonPioche;
    @FXML private Button boutonUno;

    private boolean cartePoseeCeTour = false;
    private boolean aDitUno = false;
    private boolean monTour = false;
    private String valeurCarteSelectionnee = null;
    private String couleurCarteSelectionnee = null;
    private String tasCouleur = null;
    private String tasValeur = null;
    private int tailleMainPrecedente = -1;
    private boolean premierTas = true;
    private boolean animationAPiocher = false;
    private List<String> cartesInitiales = new ArrayList<>();
    private boolean distributionEnCours = false;


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
            /*
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
            */
            else if (message.startsWith("@ERREUR")) {
                Platform.runLater(() -> {
                    afficherInfo(message.replace("@ERREUR", "").trim(), true);
                    // Si erreur de carte non jouable, on garde les boutons actifs
                    if (message.contains("Carte non jouable") && monTour && !cartePoseeCeTour) {
                        boutonPoseCarte.setDisable(false);
                        verifierSiCarteJouable(); // réévalue si pioche doit être réactivée
                    }
                });
            }
            else if (message.startsWith("@CARTEJOUEE")) {
                Platform.runLater(() -> {
                    cartePoseeCeTour = true;
                    boutonFinTour.setDisable(false);
                    boutonPioche.setDisable(true);
                    boutonPoseCarte.setDisable(true);
                });
            }
            else if (message.startsWith("@FIN_MANCHE")) {
                Platform.runLater(() -> afficherSceneFin(message));
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

        labelBas.setText("Moi (" + pseudo + ")");
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
                tasCouleur = valeurCouleur[0].trim().toLowerCase();
                tasCouleur = capitalizeFirstLetter(tasCouleur);

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
                tasCouleur = capitalizeFirstLetter(valeurCouleur[1].trim().toLowerCase());
                tasValeur = valeurCouleur[0].trim();
                premierTas = false;
            } else {
                tasValeur = valeurCouleur[0].trim();
                tasCouleur = capitalizeFirstLetter(valeurCouleur[1].trim().toLowerCase());
            }

            if (tasValeur.equals("+2")) tasValeur = "12";
            if (tasValeur.equals("?")) tasValeur = "PTT";

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
                    if (!distributionEnCours) {
                        mainJoueur.getChildren().add(carteImage);
                    }
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
            afficherInfo("Vous avez pioché 2 cartes à cause d'un +2 !", false);

            Platform.runLater(() -> {
                animerPiocheVersMain();
                PauseTransition pause = new PauseTransition(Duration.millis(300));
                pause.setOnFinished(e -> animerPiocheVersMain());
                pause.play();
            });
        }

        if (tailleMainPrecedente == -1 && tailleApres == 7) {
            // Première distribution
            cartesInitiales.clear();
            cartesInitiales.addAll(List.of(cartes));
            mainJoueur.getChildren().clear(); // vide la main visuelle
            distributionEnCours = true;
            Platform.runLater(() -> distribuerCartesProgressivement(0));
            return;
        }
        tailleMainPrecedente = tailleApres;
        if (animationAPiocher) {
            animerPiocheVersMain();
            animationAPiocher = false;
        }
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
            // Animation de distribution vers la zone du joueur (7 cartes)
            if (distributionEnCours) {
                for (int i = 0; i < 7; i++) {
                    int delayMs = i * 400;
                    PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
                    VBox finalCible = cible;
                    pause.setOnFinished(e -> animerDistributionVersZone(finalCible));
                    pause.play();
                }
            }
        }
    }

    @FXML
    public void demarrerPartie() {
        ClientUno.getInstance().envoyer("@DEMARRER_PARTIE");
        boutonDemarrer.setVisible(false); // on le cache après clic
    }

    private void gererTour(String message) {
        String joueurActuel = message.substring(6).trim(); // Supprime "@INFO "
        cartePoseeCeTour = false;
        if (joueurActuel.contains(pseudo)) {
            monTour = true;
            cartePoseeCeTour = false;
            boutonPoseCarte.setDisable(false);
            boutonFinTour.setDisable(true);
            verifierSiCarteJouable(); // <--- AJOUT IMPORTANT
            labelTour.setText("C’est ton tour !");
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

            labelTour.setText(joueurActuel);
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

        valeurCarteSelectionnee = null;
        couleurCarteSelectionnee = null;
        boutonPoseCarte.setDisable(true);


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
        if (!monTour || tasValeur == null || tasCouleur == null || cartePoseeCeTour) {
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

                    String[] parts = nomCarte.split("_");
                    if (parts.length == 2) {
                        String couleur = parts[0];
                        String valeur = parts[1];

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

        boutonPioche.setDisable(false);
    }

    @FXML
    public void piocher() {
        if (!monTour) {
            System.out.println("Ce n'est pas ton tour.");
            return;
        }

        animationAPiocher = true; // ✅ Signaler qu’on doit animer
        ClientUno.getInstance().piocher();

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


    private void afficherInfo(String texte, boolean erreur) {
        labelInfos.setText(texte);
        //ensuite le message s'enleve
        new Thread(() -> {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ignored) {}
            Platform.runLater(() -> labelInfos.setText(""));
        }).start();
    }

    /*
    private void animerPiocheVersMain() {
        // Image temporaire depuis la pioche
        ImageView carteAnimee = new ImageView(new Image(getClass().getResource("/images/cartes/carte_dos.png").toExternalForm()));
        carteAnimee.setFitWidth(65);
        carteAnimee.setFitHeight(100);

        // Position initiale = position de la pioche
        carteAnimee.setLayoutX(cartePioche.localToScene(cartePioche.getBoundsInLocal()).getMinX());
        carteAnimee.setLayoutY(cartePioche.localToScene(cartePioche.getBoundsInLocal()).getMinY());

        // Ajouter à la scène racine
        ((Pane) mainJoueur.getScene().getRoot()).getChildren().add(carteAnimee);

        // Calcul de la destination (au centre de la main du joueur)
        double destinationX = mainJoueur.localToScene(mainJoueur.getBoundsInLocal()).getMinX() + mainJoueur.getWidth() / 2 - 32;
        double destinationY = mainJoueur.localToScene(mainJoueur.getBoundsInLocal()).getMinY();

        // Crée la transition
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(carteAnimee);
        transition.setDuration(Duration.millis(500));
        transition.setFromX(carteAnimee.getLayoutX());
        transition.setFromY(carteAnimee.getLayoutY());
        transition.setToX(destinationX - carteAnimee.getLayoutX());
        transition.setToY(destinationY - carteAnimee.getLayoutY());

        // Après l’animation, retirer la carte temporaire
        transition.setOnFinished(e -> ((Pane) mainJoueur.getScene().getRoot()).getChildren().remove(carteAnimee));
        transition.play();
    }
     */

    private void animerPiocheVersMain() {
        // Crée une carte dos temporaire
        ImageView carteAnimee = new ImageView(new Image(getClass().getResource("/images/cartes/carte_dos.png").toExternalForm()));
        carteAnimee.setFitWidth(65);
        carteAnimee.setFitHeight(100);

        // Récupère la racine
        AnchorPane root = (AnchorPane) mainJoueur.getScene().getRoot();
        root.getChildren().add(carteAnimee);

        // Position de départ (centre de cartePioche dans la scène)
        Bounds piocheBoundsScene = cartePioche.localToScene(cartePioche.getBoundsInLocal());
        double startX = piocheBoundsScene.getMinX() + piocheBoundsScene.getWidth() / 2;
        double startY = piocheBoundsScene.getMinY() + piocheBoundsScene.getHeight() / 2;

        // Position de destination (centre de mainJoueur dans la scène)
        Bounds mainBoundsScene = mainJoueur.localToScene(mainJoueur.getBoundsInLocal());
        double endX = mainBoundsScene.getMinX() + mainBoundsScene.getWidth() / 2;
        double endY = mainBoundsScene.getMinY() + mainBoundsScene.getHeight() / 2;

        // Convertir les deux points dans le repère local du root
        Point2D startInRoot = root.sceneToLocal(startX, startY);
        Point2D endInRoot = root.sceneToLocal(endX, endY);

        // Place la carte au point de départ
        carteAnimee.setLayoutX(startInRoot.getX() - carteAnimee.getFitWidth() / 2);
        carteAnimee.setLayoutY(startInRoot.getY() - carteAnimee.getFitHeight() / 2);

        // Crée la transition
        TranslateTransition transition = new TranslateTransition(Duration.millis(600), carteAnimee);
        transition.setToX(endInRoot.getX() - startInRoot.getX());
        transition.setToY(endInRoot.getY() - startInRoot.getY());

        // Supprime la carte après animation
        transition.setOnFinished(e -> root.getChildren().remove(carteAnimee));
        transition.play();
    }

    private void distribuerCartesInitiales(int nombre) {
        if (nombre <= 0) return;

        animerPiocheVersMain();

        PauseTransition pause = new PauseTransition(Duration.millis(250));
        pause.setOnFinished(e -> distribuerCartesInitiales(nombre - 1));
        pause.play();
    }

    private void distribuerCartesProgressivement(int index) {
        if (index >= cartesInitiales.size()) {
            distributionEnCours = false;
            return;
        }

        // Anime une pioche
        animerPiocheVersMain();

        // Affiche la carte correspondante après un petit délai (après l'animation)
        PauseTransition delay = new PauseTransition(Duration.millis(300));
        delay.setOnFinished(e -> {
            ajouterCarteAlaMain(cartesInitiales.get(index));
            distribuerCartesProgressivement(index + 1); // récursion
        });
        delay.play();
    }

    private void ajouterCarteAlaMain(String c) {
        String[] parts = c.replace("(", "").replace(")", "").split(";");
        if (parts.length != 2) return;

        String rawValeur = parts[0];
        String valeur = rawValeur.equals("+2") ? "12" : rawValeur;
        String couleur = capitalizeFirstLetter(parts[1]);

        String imagePath = "/images/cartes/carte_" + valeur + "_" + couleur + ".png";
        try {
            Image image = new Image(imagePath);
            ImageView carteImage = new ImageView(image);
            carteImage.setFitHeight(100);
            carteImage.setFitWidth(65);

            carteImage.setOnMouseClicked(e -> {
                if (!monTour) return;
                valeurCarteSelectionnee = valeur;
                couleurCarteSelectionnee = parts[1];
                for (javafx.scene.Node node : mainJoueur.getChildren()) node.setStyle("");
                carteImage.setStyle("-fx-effect: dropshadow(gaussian, yellow, 10, 0.5, 0, 0);");
            });

            mainJoueur.getChildren().add(carteImage);

        } catch (Exception e) {
            System.err.println("Erreur chargement image : " + imagePath);
        }
    }

    private void animerDistributionVersZone(VBox destinationZone) {
        ImageView carteAnimee = new ImageView(new Image(getClass().getResource("/images/cartes/carte_dos.png").toExternalForm()));
        carteAnimee.setFitWidth(65);
        carteAnimee.setFitHeight(100);

        AnchorPane root = (AnchorPane) mainJoueur.getScene().getRoot();
        root.getChildren().add(carteAnimee);

        Bounds piocheBoundsScene = cartePioche.localToScene(cartePioche.getBoundsInLocal());
        Bounds destBoundsScene = destinationZone.localToScene(destinationZone.getBoundsInLocal());

        Point2D start = root.sceneToLocal(
                piocheBoundsScene.getMinX() + piocheBoundsScene.getWidth() / 2,
                piocheBoundsScene.getMinY() + piocheBoundsScene.getHeight() / 2
        );

        Point2D end = root.sceneToLocal(
                destBoundsScene.getMinX() + destBoundsScene.getWidth() / 2,
                destBoundsScene.getMinY() + destBoundsScene.getHeight() / 2
        );

        carteAnimee.setLayoutX(start.getX() - carteAnimee.getFitWidth() / 2);
        carteAnimee.setLayoutY(start.getY() - carteAnimee.getFitHeight() / 2);

        TranslateTransition transition = new TranslateTransition(Duration.millis(500), carteAnimee);
        transition.setToX(end.getX() - start.getX());
        transition.setToY(end.getY() - start.getY());
        transition.setOnFinished(e -> root.getChildren().remove(carteAnimee));
        transition.play();
    }




}