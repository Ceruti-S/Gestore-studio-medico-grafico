package com.studioMedico.GCM.frontend;

import com.studioMedico.GCM.backend.funzionamento.ControlloLogin;
import com.studioMedico.GCM.backend.funzionamento.ValidatoreDati;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SchermataLogin extends JDialog {

    /*
     * 'S' → login riuscito
     * 'F' → login fallito
     * 'C' → chiusura con X
     */
    private char loginResult = 'F';

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkRecovery;

    private SchermataLogin(Frame parent) {
        super(parent, "Login", true);
        initGUI();
    }

    /**
     * Metodo pubblico da chiamare dal main.
     * Apre la schermata di login (modale) e ritorna l'esito.
     */
    public static char mostraLogin() {
        SchermataLogin dialog = new SchermataLogin(null);
        dialog.setVisible(true); // BLOCCANTE
        return dialog.loginResult;
    }

    /**
     * Inizializzazione GUI
     */
    private void initGUI() {

        setSize(360, 230);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Gestione chiusura con X
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                loginResult = 'C';
                dispose();
            }
        });

        // ------------------ PANNELLO CAMPI ------------------
        JPanel panelCampi = new JPanel(new GridLayout(3, 2, 5, 5));
        panelCampi.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelCampi.add(new JLabel("Nome utente:"));
        txtUsername = new JTextField();
        panelCampi.add(txtUsername);

        panelCampi.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panelCampi.add(txtPassword);

        chkRecovery = new JCheckBox("Accesso in Recovery Mode");
        panelCampi.add(new JLabel());
        panelCampi.add(chkRecovery);

        add(panelCampi, BorderLayout.CENTER);

        // ------------------ PULSANTE LOGIN ------------------
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(this::gestisciLogin);
        add(btnLogin, BorderLayout.SOUTH);
    }

    /**
     * Gestione click sul pulsante Login
     */
    private void gestisciLogin(ActionEvent e) {

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        boolean recoveryMode = chkRecovery.isSelected();

        // 1️⃣ Validazione username
        if (!ValidatoreDati.isUsernameValido(username)) {
            JOptionPane.showMessageDialog(this,
                    "Username non valido.\nFormato: M1_nome, I20_nome, ecc.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }


        // 3️⃣ Chiamata backend
        try {
            String esito = ControlloLogin.controlloCredenziali(
                    username,
                    password,
                    recoveryMode
            );

            switch (esito) {

                case "login_success":
                case "recovery_login_success":
                    loginResult = 'S';
                    dispose();
                    break;

                case "recovery_access_denied":
                    JOptionPane.showMessageDialog(this,
                            "Non hai i permessi per la Recovery Mode",
                            "Accesso negato",
                            JOptionPane.ERROR_MESSAGE);
                    loginResult = 'F';
                    break;

                case "login_fail":
                    JOptionPane.showMessageDialog(this,
                            "Credenziali errate",
                            "Login fallito",
                            JOptionPane.ERROR_MESSAGE);
                    loginResult = 'F';
                    break;

                default:
                    JOptionPane.showMessageDialog(this,
                            "Errore imprevisto durante il login",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    loginResult = 'F';
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore di sistema:\n" + ex.getMessage(),
                    "Errore critico",
                    JOptionPane.ERROR_MESSAGE);
            loginResult = 'F';
        }
    }
}
