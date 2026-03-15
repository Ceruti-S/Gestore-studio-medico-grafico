package com.studioMedico.GCM.frontend.UI;

import com.studioMedico.GCM.frontend.controller.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

public class SchermataHomeSegretarioUI {

    public char mostraHomeSegretario(Stage owner) {
        try {
            // 1. Definiamo il percorso relativo alla cartella resources
            // Se il file è in src/main/resources/fxml/HomeSegretario.fxml
            String pathFXML = "/fxml/HomeSegretario.fxml";
            URL fxmlLocation = getClass().getResource(pathFXML);

            if (fxmlLocation == null) {
                // Se non lo trova, stampiamo un errore chiaro per il debug
                System.err.println("ERRORE: FXML non trovato in: " + pathFXML);
                System.err.println("Verifica che il file sia in: src/main/resources/fxml/");
                return 'C';
            }

            // 2. Carichiamo l'FXML
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // 3. Recuperiamo il controller
            LoginController controller = loader.getController();

            // 4. Creiamo la finestra
            Stage homeStage = new Stage();
            homeStage.setTitle("Home Segretario");
            if (owner != null) homeStage.initOwner(owner);
            homeStage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(root);

            // 5. Carichiamo il CSS (Assumendo che sia in resources/css/HomeSegretario.css)
            URL cssLocation = getClass().getResource("/css/HomeSegretario.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }

            homeStage.setScene(scene);

            // 6. Mostriamo la finestra
            homeStage.showAndWait();

            // Ritorna il risultato (o 'S' di default se il controller è nullo)
            return (controller != null) ? controller.getLoginResult() : 'S';

        } catch (Exception e) {
            System.err.println("ERRORE FATALE durante il caricamento di HomeSegretario.fxml:");
            e.printStackTrace();
            return 'C';
        }
    }
}