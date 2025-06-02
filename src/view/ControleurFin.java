package view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ControleurFin extends ControleurCommun {

    @FXML
    private Label labelGagnant;

    public ControleurFin(Stage stage) {
        super(stage);
    }

    public void setGagnant(String nomGagnant) {
        labelGagnant.setText(nomGagnant + " a gagné la manche !");
    }

}
