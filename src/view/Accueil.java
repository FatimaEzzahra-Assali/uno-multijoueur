package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Accueil extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Chargement du fichier FXML
        Parent root = FXMLLoader.load(getClass().getResource("Accueil.fxml"));

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Fenêtre Accueil");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}