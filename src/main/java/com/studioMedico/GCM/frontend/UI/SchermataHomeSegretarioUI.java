package com.studioMedico.GCM.frontend.UI;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;

public class SchermataHomeSegretarioUI {

    public void mostraHomeSegretario(Stage homeStage) {
        try {
            String pathFXML = "/fxml/HomeSegretario.fxml";
            URL fxmlLocation = getClass().getResource(pathFXML);

            if (fxmlLocation == null) {
                System.err.println("ERRORE: FXML non trovato in: " + pathFXML);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // 1. Nessuna Modality. Questo garantisce che i pulsanti (minimizza/massimizza)
            // siano "sbloccati" e cliccabili dal sistema operativo.
            homeStage.setTitle("GCM - Home Segretario");

            Scene scene = new Scene(root);
            URL cssLocation = getClass().getResource("/css/HomeSegretario.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }

            homeStage.setScene(scene);

            // 2. NON calcoliamo le misure dello schermo. NON usiamo setWidth/setHeight.
            // Lasciamo la finestra "libera" per non far arrabbiare Ubuntu.

            // 3. Mostriamo la finestra. In questo momento appare, ei pulsanti si attivano.
            homeStage.show();

            // --- LA MAGIA PER TUTTI GLI OS (WINDOWS, MAC, LINUX) ---
            // 4. Creiamo un timer invisibile di 150 millisecondi.
            // Dà il tempo a Ubuntu di completare il disegno della barra del titolo.
            PauseTransition delay = new PauseTransition(Duration.millis(150));

            // 5. Allo scadere dei 150ms, massimizziamo in modo pulito e sicuro.
            delay.setOnFinished(event -> homeStage.setMaximized(true));

            // Facciamo partire il timer
            delay.play();

        } catch (Exception e) {
            System.err.println("ERRORE FATALE durante il caricamento di HomeSegretario.fxml:");
            e.printStackTrace();
        }
    }
}