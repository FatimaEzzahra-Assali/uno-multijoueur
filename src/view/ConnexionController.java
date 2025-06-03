package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import network.ClientUno;
import network.ThreadClientUno;

public class ConnexionController extends ControleurCommun{

    @FXML
    private TextField pseudoField;

    public ConnexionController(Stage stage) {
        super(stage);
    }

    @FXML
    public void handleConnexion() {
        String pseudo = pseudoField.getText().trim();

        if (pseudo.isEmpty()) {
            afficherAlerte("Erreur", "Veuillez entrer un pseudo.");
            return;
        }

        try {
            ClientUno client = ClientUno.getInstance();

            if (client == null) {
                client = new ClientUno();
                ClientUno.setInstance(client);
                client.setPseudo(pseudo);
                client.envoyerConnexion();

                ThreadClientUno thread = new ThreadClientUno(client);
                client.setThreadClientUno(thread);

                client.getThreadClientUno().setOnMessageCallback(message -> {
                    if (message.contains("s'est connecté au serveur")) {
                        Platform.runLater(this::afficherAccueil);
                    } else if (message.contains("pseudo existe")) {
                        Platform.runLater(() -> afficherAlerte("Erreur", "Ce pseudo est déjà utilisé."));
                    }
                });

                thread.start();

            } else {
                afficherAlerte("Déjà connecté", "Vous êtes déjà connecté.");
            }

        } catch (Exception e) {
            afficherAlerte("Erreur", "Connexion au serveur impossible.");
            e.printStackTrace();
        }
    }

    private void afficherAccueil(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Accueil.fxml"));
            AccueilController controller = new AccueilController(stage);
            loader.setController(controller);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherAlerte(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
