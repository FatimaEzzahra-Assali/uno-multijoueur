package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import network.ClientUno;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ControleurMessages extends ControleurCommun {

    public ControleurMessages(Stage stage) {
        super(stage);
    }

    @FXML private TextArea publicMessagesArea;
    @FXML private TextField messageContenu;
    @FXML private Button MessagePublic;
    @FXML private ListView<String> listeUtilisateurs;
    @FXML
    private Button messagePrive;
    @FXML private ListView<String> listePrives;
    @FXML private TextArea zoneMessagesPrives;
    @FXML private TextField messagePriveContenu;

    private final Map<String, StringBuilder> conversationsPrivees = new HashMap<>();
    private String utilisateurActuel = null;

    @FXML
    public void initialize() {
        ClientUno.getInstance().getThreadClientUno().setOnMessageCallback(message -> {
            if (message.startsWith("@PUBLIC_FROM")) {
                String[] parts = message.split(" ", 3);
                if (parts.length >= 3) {
                    String pseudo = parts[1];
                    String contenu = parts[2];
                    Platform.runLater(() -> publicMessagesArea.appendText(pseudo + " : " + contenu + "\n"));
                }
            }
            else if (message.startsWith("@MP_FROM")) {
                String[] parts = message.split(" ", 3);
                if (parts.length >= 3) {
                    String exp = parts[1];
                    String contenu = parts[2];

                    conversationsPrivees.putIfAbsent(exp, new StringBuilder());
                    conversationsPrivees.get(exp).append(exp).append(" : ").append(contenu).append("\n");

                    if (exp.equals(utilisateurActuel)) {
                        Platform.runLater(() -> zoneMessagesPrives.setText(conversationsPrivees.get(exp).toString()));
                    }
                }
            }
            else if (message.startsWith("@USERS ")) {
                String[] pseudos = message.substring(7).split(";");
                String monPseudo = ClientUno.getInstance().getPseudo();

                Platform.runLater(() -> {
                    // Exclure soi-même
                    var autresUtilisateurs = Arrays.stream(pseudos)
                            .filter(p -> !p.equalsIgnoreCase(monPseudo))
                            .toList();

                    listeUtilisateurs.getItems().setAll(autresUtilisateurs);
                    listePrives.getItems().setAll(autresUtilisateurs);
                });
            }
        });

        ClientUno.getInstance().demanderListeUtilisateurs();
    }

    @FXML
    public void envoyerMessagePublic() {
        String contenu = messageContenu.getText().trim();
        if (!contenu.isEmpty()) {
            ClientUno.getInstance().envoyerMessagePublic(contenu);
            messageContenu.clear();
        }
    }

    @FXML
    public void envoyerMessagePrive() {
        String destinataire = listePrives.getSelectionModel().getSelectedItem();
        String contenu = messagePriveContenu.getText().trim();

        if (destinataire != null && !contenu.isEmpty()) {
            ClientUno.getInstance().envoyerMessagePrive(destinataire, contenu);

            conversationsPrivees.putIfAbsent(destinataire, new StringBuilder());
            conversationsPrivees.get(destinataire).append("Moi : ").append(contenu).append("\n");

            if (destinataire.equals(utilisateurActuel)) {
                zoneMessagesPrives.setText(conversationsPrivees.get(destinataire).toString());
            }

            messagePriveContenu.clear();
        }
    }

    @FXML
    public void changerConversation() {
        String selected = listePrives.getSelectionModel().getSelectedItem();
        if (selected != null) {
            utilisateurActuel = selected;
            zoneMessagesPrives.setText(conversationsPrivees.getOrDefault(selected, new StringBuilder()).toString());
        }
    }
}