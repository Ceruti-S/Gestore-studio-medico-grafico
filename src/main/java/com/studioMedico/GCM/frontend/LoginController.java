package com.studioMedico.GCM.frontend;

import com.studioMedico.GCM.backend.funzionamento.ControlloLogin;
import com.studioMedico.GCM.backend.funzionamento.ValidatoreDati;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox chkRecovery;
    @FXML private Label lblStatus;

    private char loginResult = 'C';

    @FXML
    private void gestisciLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
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