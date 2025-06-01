package view;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ControleurCommun {
    protected Stage stage;

    @FXML private Button boutonInformations;
    @FXML private Button boutonAide;
    @FXML private Button boutonMessages;
    @FXML private Button boutonQuitter;
    @FXML private Button boutonJeu;

    public ControleurCommun(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {

    }

    @FXML
    protected void afficherInformations(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informations");
        alert.setHeaderText(null);
        alert.getDialogPane().setStyle("-fx-background-color: lightgrey;");

        String messageInfo =
                "Ce jeu a été développé par Buse COSAR, Tessnim KHELIFA et\n" +
                        "Fatima-Ezahra ASSALI.\n" +
                        "\nSous l'encadrement de Yann LANUEL et Yeliz MERSIN.";
        alert.setContentText(messageInfo);

        alert.showAndWait();
    }

    @FXML
    protected void afficherMessages(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Messages.fxml"));
        ControleurMessages ctrlMessages = new ControleurMessages(stage);
        loader.setController(ctrlMessages);

        Scene scene = new Scene(loader.load());
        Stage boite = new Stage();
        boite.setTitle("Messages");
        boite.setScene(scene);
        boite.showAndWait();
    }

    @FXML
    private void afficherJeu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Jeu.fxml"));
            ControleurJeu ctrl =  new ControleurJeu(stage);
            loader.setController(ctrl);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
