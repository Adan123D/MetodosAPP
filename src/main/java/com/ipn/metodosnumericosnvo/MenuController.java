package com.ipn.metodosnumericosnvo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class MenuController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    /**
     * Generic method to handle menu item clicks.
     * This method can be used for all menu items.
     * 
     * @param event The action event from the menu item click
     */
    @FXML
    public void handleMenuItemClick(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String menuText = menuItem.getText();
        welcomeText.setText("Selected: " + menuText);

        // Here you can add specific logic for each menu item
        // based on the menuText value
    }

    /**
     * Handles the click event for the Gráfica button.
     * This method will be used to display graphs.
     */
    @FXML
    protected void onGraficaButtonClick() {
        welcomeText.setText("Función de Gráfica seleccionada");
        // Here you would add code to display a graph
    }

    /**
     * Handles the click event for the Home button.
     * This method will be used to return to the home screen.
     */
    @FXML
    protected void onHomeButtonClick() {
        welcomeText.setText("Página principal");
        // Here you would add code to return to the home screen
    }

    /**
     * Handles the click event for the Exit button.
     * This method will close the application.
     */
    @FXML
    protected void onExitButtonClick() {
        Platform.exit();
    }
}
