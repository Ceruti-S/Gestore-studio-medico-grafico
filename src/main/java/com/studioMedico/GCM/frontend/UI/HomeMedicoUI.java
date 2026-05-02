package com.studioMedico.GCM.frontend.UI;

import com.studioMedico.GCM.frontend.controller.HomeAmministratoreController;
import com.studioMedico.GCM.frontend.controller.HomeMedicoController;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;

public class HomeMedicoUI
{

    public void mostraHome(Stage homeStage)
    {

        try
        {

            String pathFXML = "/fxml/HomeMedico.fxml";
            URL fxmlLocation = getClass().getResource(pathFXML);

            if(fxmlLocation == null)
            {

                System.err.println("ERRORE: FXML non trovato in: " + pathFXML);
                return;

            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            HomeMedicoController controller = new HomeMedicoController();

            homeStage.setTitle("GCM - Home Medico");

            Scene scene = new Scene(root);
            URL cssLocation = getClass().getResource("/css/Medico.css");
            if(cssLocation != null)
            {

                scene.getStylesheets().add(cssLocation.toExternalForm());

            }

            homeStage.setScene(scene);

            homeStage.show();

            PauseTransition delay = new PauseTransition(Duration.millis(150));

            delay.setOnFinished(event -> homeStage.setMaximized(true));

            delay.play();

        }
        catch(Exception e)
        {

            System.err.println("ERRORE FATALE durante il caricamento di HomeMedicofxml:");
            e.printStackTrace();

        }

    }

}
