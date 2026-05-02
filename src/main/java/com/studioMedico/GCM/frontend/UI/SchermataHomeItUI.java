package com.studioMedico.GCM.frontend.UI;

import com.studioMedico.GCM.frontend.controller.HomeITcontroller;
import com.studioMedico.GCM.frontend.controller.HomeSegretarioController;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;

public class SchermataHomeItUI
{

    public void mostraHomeIT(Stage homeStage)
    {

        try
        {

            String pathFXML = "/fxml/HomeIT.fxml";
            URL fxmlLocation = getClass().getResource(pathFXML);

            if(fxmlLocation == null)
            {

                System.err.println("ERRORE: FXML non trovato in: " + pathFXML);
                return;

            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            HomeITcontroller controller = new HomeITcontroller();

            homeStage.setTitle("GCM - Home IT");

            Scene scene = new Scene(root);
            URL cssLocation = getClass().getResource("/css/ITSupport.css");
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

            System.err.println("ERRORE FATALE durante il caricamento di HomeITfxml:");
            e.printStackTrace();

        }
    }
}