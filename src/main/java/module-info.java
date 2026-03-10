module com.studioMedico.GCM {

    // Moduli base di JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;

    // Librerie esterne (assicurati che siano nel pom.xml)
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    // --- EXPORTS (Rendono i pacchetti visibili ad altri moduli) ---
    exports com.studioMedico.GCM.backend.funzionamento;
    exports com.studioMedico.GCM.backend.funzionamento.oggettiModello;

    // --- OPENS (Permettono a JavaFX/FXML di accedere alle classi via Reflection) ---
    // Questo serve se carichi i file FXML che puntano ai controller in questi pacchetti
    opens com.studioMedico.GCM.frontend.controller to javafx.fxml;
    opens com.studioMedico.GCM.frontend.UI to javafx.fxml;

    // Se hai il file Application (quello con start()) nel backend, apri anche quello
    opens com.studioMedico.GCM.backend.funzionamento to javafx.fxml;
}