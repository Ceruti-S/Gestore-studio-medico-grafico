module com.studioMedico.GCM
{

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    // package che contiene i controller JavaFX
    opens com.studioMedico.GCM.frontend to javafx.fxml;

    // package con il main
    exports com.studioMedico.GCM.backend.funzionamento;
    exports com.studioMedico.GCM.backend.funzionamento.oggettiModello;
    opens com.studioMedico.GCM.frontend.controller to javafx.fxml;
    opens com.studioMedico.GCM.frontend.UI to javafx.fxml;

}
