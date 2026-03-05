package com.studioMedico.GCM.frontend;

import com.studioMedico.GCM.backend.funzionamento.ControlloLogin;
import com.studioMedico.GCM.backend.funzionamento.ValidatoreDati;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    public TextField txtPasswordInChiaro;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox chkRecovery;
    @FXML private Label lblStatus;

    private char loginResult = 'C';

    @FXML
    private void gestisciLogin() {
        String username = txtUsername.getText().trim();

        // Recupera la password dal campo attualmente attivo
        String password = txtPassword.isVisible() ? txtPassword.getText() : txtPasswordInChiaro.getText();

        boolean recoveryMode = chkRecovery.isSelected();

        // 1. Validazione
        if (!ValidatoreDati.isUsernameValido(username)) {
            mostraAlert("Errore Validazione", "Username non valido. Formato: M1_nome.");
            return;
        }

        // 2. Chiamata Backend
        try {
            String esito = ControlloLogin.controlloCredenziali(username, password, recoveryMode);
            processaEsito(esito);
        } catch (Exception ex) {
            mostraAlert("Errore Critico", "Errore di sistema: " + ex.getMessage());
        }

    }

    @FXML
    private void cambiaVisibilitaPassword()
    {

        if (txtPassword.isVisible())
        {
            // Passiamo a visualizzare la password in chiaro
            txtPasswordInChiaro.setText(txtPassword.getText());

            txtPasswordInChiaro.setVisible(true);
            txtPasswordInChiaro.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);

            // Portiamo il focus e il cursore alla fine
            txtPasswordInChiaro.requestFocus();
            txtPasswordInChiaro.end();
        }
        else
        {
            // Torniamo ai pallini
            txtPassword.setText(txtPasswordInChiaro.getText());

            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordInChiaro.setVisible(false);
            txtPasswordInChiaro.setManaged(false);

            // Portiamo il focus e il cursore alla fine
            txtPassword.requestFocus();
            txtPassword.end();
        }

    }

    private void processaEsito(String esito) {
        switch (esito) {
            case "login_success", "recovery_login_success" -> {
                loginResult = 'S';
                chiudiFinestra();
            }
            case "recovery_access_denied" -> {
                lblStatus.setText("Accesso Recovery Negato");
                loginResult = 'F';
            }
            case "login_fail" -> {
                lblStatus.setText("Credenziali Errate");
                loginResult = 'F';
            }
            default -> {
                lblStatus.setText("Errore imprevisto");
                loginResult = 'C';
            }
        }
    }

    public char getLoginResult() { return loginResult; }

    private void chiudiFinestra() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }

    private void mostraAlert(String titolo, String contenuto) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
}