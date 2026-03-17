module com.studioMedico.GCM {

    // Moduli base di JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.media;
    requires java.desktop;

    // Librerie esterne
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    // --- EXPORTS ---
    // Permettono a JavaFX di lanciare l'applicazione e vedere le classi
    exports com.studioMedico.GCM.backend.funzionamento;
    exports com.studioMedico.GCM.backend.funzionamento.oggettiModello;
    exports com.studioMedico.GCM.frontend.UI;
    exports com.studioMedico.GCM.frontend.controller;

    // --- OPENS ---
    // Indispensabile per FXML: permette a JavaFX di iniettare i campi @FXML
    opens com.studioMedico.GCM.frontend.controller to javafx.fxml;
    opens com.studioMedico.GCM.frontend.UI to javafx.fxml;
    opens com.studioMedico.GCM.backend.funzionamento to javafx.fxml;

    // Se usi classi modello (come Log) in tabelle o controlli JavaFX
    opens com.studioMedico.GCM.backend.funzionamento.oggettiModello to javafx.base;
}