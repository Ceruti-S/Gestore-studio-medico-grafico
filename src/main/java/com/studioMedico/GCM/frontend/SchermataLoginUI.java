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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();

            Stage loginStage = new Stage();
            loginStage.setTitle("GCM - Login");
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.setScene(new Scene(root));
            root.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            loginStage.setOnCloseRequest(event -> {
            });

            loginStage.showAndWait();
            return controller.getLoginResult();

        }
        catch (Exception e)
        {

            e.printStackTrace();
            return 'C'; //In caso di errore esco

        }

    }

}