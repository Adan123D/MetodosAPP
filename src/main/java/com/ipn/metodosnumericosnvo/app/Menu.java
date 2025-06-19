package com.ipn.metodosnumericosnvo.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * Main application class for the Numerical Methods application.
 * This class is responsible for initializing and launching the JavaFX application.
 * It loads the main menu FXML file and sets up the primary stage.
 * */
public class Menu extends Application {

    /**
     * Starts the JavaFX application.
     * This method is called by the JavaFX runtime after the application has been initialized.
     * It loads the FXML file, creates the scene, and sets up the primary stage.
     *
     * @param stage The primary stage for this application
     * @throws IOException If the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(Menu.class.getResource("/com/ipn/metodosnumericosnvo/Menu.fxml"));

        // Create the scene with the loaded FXML content
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);

        // Configure and show the stage
        stage.setTitle("Métodos Numéricos");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main entry point for the application.
     * This method launches the JavaFX application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch();
    }
}
