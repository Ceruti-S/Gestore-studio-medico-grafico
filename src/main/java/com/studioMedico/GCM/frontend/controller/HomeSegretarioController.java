package com.studioMedico.GCM.frontend.controller;

import javafx.fxml.FXML;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeSegretarioController {

    @FXML
    private void effettuaLogout() {
        try {
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            String modulePath = System.getProperty("jdk.module.path");
            String mainModule = "com.studioMedico.GCM/com.studioMedico.GCM.backend.funzionamento.MainClass";

            List<String> command = new ArrayList<>();
            command.add(javaBin);
            if (modulePath != null) {
                command.add("--module-path");
                command.add(modulePath);
            }
            command.add("-m");
            command.add(mainModule);

            new ProcessBuilder(command).start();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}