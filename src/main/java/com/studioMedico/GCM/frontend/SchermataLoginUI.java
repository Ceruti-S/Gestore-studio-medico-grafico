package com.studioMedico.GCM.frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SchermataLoginUI
{

    public char mostraLogin(Stage owner)
    {

        try
        {

            //carico il file FXML dalla cartella resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            //ottengo il controller associato al file FXML
            LoginController controller = loader.getController();

            Stage loginStage = new Stage();
            loginStage.setTitle("GCM - Login");
            loginStage.initModality(Modality.APPLICATION_MODAL); // Blocca le altre finestre
            loginStage.setScene(new Scene(root));

            //applico il CSS
            root.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            //mostra la finestra e aspetta finché non viene chiusa
            loginStage.showAndWait();

            return controller.getLoginResult();

        }
        catch (Exception e)
        {

            e.printStackTrace();
            return 'F';

        }

    }

}