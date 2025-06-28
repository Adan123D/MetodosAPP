package com.ipn.metodosnumericosnvo.controller;


// JavaFX imports
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Application controllers
import com.ipn.metodosnumericosnvo.integracion_controller.RombergController;
import com.ipn.metodosnumericosnvo.integracion_controller.Simpson1_3Controller;
import com.ipn.metodosnumericosnvo.integracion_controller.Simpson3_8Controller;
import com.ipn.metodosnumericosnvo.integracion_controller.TrapecioController;

import java.io.IOException;

// JLaTeXMath imports
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

// AWT imports
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Controller class for the main menu of the Numerical Methods application.
 * Handles user interactions with the menu items and the function visualizer.
 */
public class MenuController {
    // UI Components
    @FXML
    private Label welcomeText;

    @FXML
    private TextField functionTextField;

    @FXML
    private Pane functionDisplayPane;

    @FXML
    private Label functionErrorLabel;

    @FXML
    private javafx.scene.control.MenuButton symbolsMenuButton;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     * Sets the initial state of UI components.
     */
    @FXML
    public void initialize() {
        // Set initial state
        functionErrorLabel.setVisible(false);

        // Set a default welcome message
        welcomeText.setText("Bienvenido al Visualizador de Funciones");
    }

    //--------------------------------------------------
    // Navigation and Menu Event Handlers
    //--------------------------------------------------

    /**
     * Generic method to handle menu item clicks.
     * This method can be used for all menu items in the application.
     * 
     * @param event The action event from the menu item click
     */
    @FXML
    public void handleMenuItemClick(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String menuText = menuItem.getText();
        welcomeText.setText("Selected: " + menuText);

        // Handle specific menu items
        switch (menuText) {
            default:
                // For menu items that are not yet implemented
                break;
        }
    }

    /**
     * Handles the click event for the Método de Bisección menu item.
     * This method opens a new window for the bisection method.
     * If a function is entered in the main menu, it will be passed to the bisection window.
     */
    @FXML
    protected void onBiseccionMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de bisección
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Biseccion.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Biseccion.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador y configurar la función inicial si existe
            BiseccionController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de bisección
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage biseccionStage = new Stage();
            biseccionStage.setTitle("Método de Bisección");
            biseccionStage.setScene(new Scene(root, 800, 600));
            biseccionStage.initModality(Modality.NONE);
            biseccionStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Bisección: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Método de Falsa Posición menu item.
     * This method opens a new window for the false position method.
     * If a function is entered in the main menu, it will be passed to the false position window.
     */
    @FXML
    protected void onFalsaPosicionMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de falsa posición
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/FalsaPosicion.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo FalsaPosicion.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            FalsaPosicionController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de falsa posición
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage falsaPosicionStage = new Stage();
            falsaPosicionStage.setTitle("Método de Falsa Posición");
            falsaPosicionStage.setScene(new Scene(root, 800, 600));
            falsaPosicionStage.initModality(Modality.NONE);
            falsaPosicionStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Falsa Posición: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Método de Newton menu item.
     * This method opens a new window for the Newton-Raphson method.
     * If a function is entered in the main menu, it will be passed to the Newton window.
     */
    @FXML
    protected void onNewtonMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de Newton
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Newton.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Newton.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            NewtonController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de Newton
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage newtonStage = new Stage();
            newtonStage.setTitle("Método de Newton-Raphson");
            newtonStage.setScene(new Scene(root, 800, 600));
            newtonStage.initModality(Modality.NONE);
            newtonStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Newton: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Método de la Secante menu item.
     * This method opens a new window for the Secant method.
     * If a function is entered in the main menu, it will be passed to the Secant window.
     */
    @FXML
    protected void onSecanteMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de la Secante
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Secante.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Secante.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            SecanteController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de la Secante
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage secanteStage = new Stage();
            secanteStage.setTitle("Método de la Secante");
            secanteStage.setScene(new Scene(root, 800, 600));
            secanteStage.initModality(Modality.NONE);
            secanteStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de la Secante: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Deflación menu item.
     * This method opens a new window for the deflation method.
     * If a function is entered in the main menu, it will be passed to the deflation window.
     */
    @FXML
    protected void onDeflacionMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de deflación
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Deflacion.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Deflacion.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            DeflacionController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de deflación
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage deflacionStage = new Stage();
            deflacionStage.setTitle("Método de Deflación");
            deflacionStage.setScene(new Scene(root, 800, 600));
            deflacionStage.initModality(Modality.NONE);
            deflacionStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Deflación: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Método de Punto Fijo menu item.
     * This method opens a new window for the fixed point method.
     * If a function is entered in the main menu, it will be passed to the fixed point window.
     */
    @FXML
    protected void onPuntoFijoMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de punto fijo
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/PuntoFIjo.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo PuntoFIjo.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            PuntoFIjoController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de punto fijo
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage puntoFijoStage = new Stage();
            puntoFijoStage.setTitle("Método de Punto Fijo");
            puntoFijoStage.setScene(new Scene(root, 800, 600));
            puntoFijoStage.initModality(Modality.NONE);
            puntoFijoStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Punto Fijo: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Método de Muller menu item.
     * This method opens a new window for the Muller method.
     * If a function is entered in the main menu, it will be passed to the Muller window.
     */
    @FXML
    protected void onMullerMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de Muller
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Muller.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Muller.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            MullerController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de Muller
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage mullerStage = new Stage();
            mullerStage.setTitle("Método de Müller");
            mullerStage.setScene(new Scene(root, 950, 600));
            mullerStage.initModality(Modality.NONE);
            mullerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Müller: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Método de Steffensen menu item.
     * This method opens a new window for the Steffensen method.
     * If a function is entered in the main menu, it will be passed to the Steffensen window.
     */
    @FXML
    protected void onSteffensenMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de Steffensen
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Steffensen.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Steffensen.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            SteffensenController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de Steffensen
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage steffensenStage = new Stage();
            steffensenStage.setTitle("Método de Steffensen");
            steffensenStage.setScene(new Scene(root, 800, 600));
            steffensenStage.initModality(Modality.NONE);
            steffensenStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Steffensen: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Método de Secante-Aitken menu item.
     * This method opens a new window for the Secante-Aitken method.
     * If a function is entered in the main menu, it will be passed to the Secante-Aitken window.
     */
    @FXML
    protected void onSecanteAitkenMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de Secante-Aitken
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Secante_Aitken.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Secante_Aitken.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            Secante_AitkenController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de Secante-Aitken
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage secanteAitkenStage = new Stage();
            secanteAitkenStage.setTitle("Método de la Secante con Aceleración de Aitken");
            secanteAitkenStage.setScene(new Scene(root, 800, 600));
            secanteAitkenStage.initModality(Modality.NONE);
            secanteAitkenStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Secante-Aitken: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Método de Bisección-Aitken menu item.
     * This method opens a new window for the Bisección-Aitken method.
     * If a function is entered in the main menu, it will be passed to the Bisección-Aitken window.
     */
    @FXML
    protected void onBiseccionAitkenMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de Bisección-Aitken
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Biseccion_Aitken.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Biseccion_Aitken.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            Biseccion_AitkenController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de Bisección-Aitken
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage biseccionAitkenStage = new Stage();
            biseccionAitkenStage.setTitle("Método de Bisección con Aitken Δ²");
            biseccionAitkenStage.setScene(new Scene(root, 800, 600));
            biseccionAitkenStage.initModality(Modality.NONE);
            biseccionAitkenStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Bisección-Aitken: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Polinomio Interpolante de Lagrange menu item.
     * This method opens a new window for the Lagrange interpolation method.
     */
    @FXML
    protected void onLagrangeMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de Lagrange
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Lagrange.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Lagrange.fxml");
                return;
            }

            Parent root = loader.load();

            // Crear y mostrar la nueva ventana
            Stage lagrangeStage = new Stage();
            lagrangeStage.setTitle("Polinomio Interpolante de Lagrange");
            lagrangeStage.setScene(new Scene(root, 600, 400));
            lagrangeStage.initModality(Modality.NONE);
            lagrangeStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Lagrange: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Derivación Numérica menu item.
     * This method opens a new window for the numerical differentiation method.
     */
    @FXML
    protected void onNumericalDifferentiationMenuItemClick() {
        try {
            // Cargar el archivo FXML de Derivación Numérica
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/NumericalDifferentiation.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo NumericalDifferentiation.fxml");
                return;
            }

            Parent root = loader.load();

            // Crear y mostrar la nueva ventana
            Stage numericalDiffStage = new Stage();
            numericalDiffStage.setTitle("Derivación Numérica");
            numericalDiffStage.setScene(new Scene(root, 600, 400));
            numericalDiffStage.initModality(Modality.NONE);
            numericalDiffStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Derivación Numérica: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Extrapolación de Richardson menu item.
     * This method opens a new window for the Richardson extrapolation method.
     */
    @FXML
    protected void onRichardsonExtrapolationMenuItemClick() {
        try {
            // Cargar el archivo FXML de Extrapolación de Richardson
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/ExtrapolacionRichardson.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo ExtrapolacionRichardson.fxml");
                return;
            }

            Parent root = loader.load();

            // Crear y mostrar la nueva ventana
            Stage richardsonStage = new Stage();
            richardsonStage.setTitle("Extrapolación de Richardson");
            richardsonStage.setScene(new Scene(root, 600, 400));
            richardsonStage.initModality(Modality.NONE);
            richardsonStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Extrapolación de Richardson: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Derivación para puntos desigualmente espaciados menu item.
     * This method opens a new window for the numerical differentiation with unequally spaced points.
     */
    @FXML
    protected void onDerivacionPuntosDesigualesMenuItemClick() {
        try {
            // Cargar el archivo FXML de Derivación para puntos desigualmente espaciados
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/DerivacionPuntosDesiguales.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo DerivacionPuntosDesiguales.fxml");
                return;
            }

            Parent root = loader.load();

            // Crear y mostrar la nueva ventana
            Stage puntosDesigualesStage = new Stage();
            puntosDesigualesStage.setTitle("Derivación para Puntos Desigualmente Espaciados");
            puntosDesigualesStage.setScene(new Scene(root, 800, 600));
            puntosDesigualesStage.initModality(Modality.NONE);
            puntosDesigualesStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Derivación para Puntos Desigualmente Espaciados: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Simpson 1/3 menu item.
     * This method opens a new window for the Simpson 1/3 integration method.
     * If a function is entered in the main menu, it will be passed to the Simpson 1/3 window.
     */
    @FXML
    protected void onSimpson13MenuItemClick() {
        try {
            // Cargar el archivo FXML del método de Simpson 1/3
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Simpson1_3.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Simpson1_3.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            Simpson1_3Controller controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de Simpson 1/3
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage simpson13Stage = new Stage();
            simpson13Stage.setTitle("Método de Simpson 1/3");
            simpson13Stage.setScene(new Scene(root, 600, 400));
            simpson13Stage.initModality(Modality.NONE);
            simpson13Stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Simpson 1/3: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Simpson 3/8 menu item.
     * This method opens a new window for the Simpson 3/8 integration method.
     * If a function is entered in the main menu, it will be passed to the Simpson 3/8 window.
     */
    @FXML
    protected void onSimpson38MenuItemClick() {
        try {
            // Cargar el archivo FXML del método de Simpson 3/8
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Simpson3_8.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Simpson3_8.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            Simpson3_8Controller controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de Simpson 3/8
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage simpson38Stage = new Stage();
            simpson38Stage.setTitle("Método de Simpson 3/8");
            simpson38Stage.setScene(new Scene(root, 600, 400));
            simpson38Stage.initModality(Modality.NONE);
            simpson38Stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Simpson 3/8: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Trapecio menu item.
     * This method opens a new window for the Trapecio integration method.
     * If a function is entered in the main menu, it will be passed to the Trapecio window.
     */
    @FXML
    protected void onTrapecioMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de Trapecio
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Trapecio.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Trapecio.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            TrapecioController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de Trapecio
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage trapecioStage = new Stage();
            trapecioStage.setTitle("Método del Trapecio");
            trapecioStage.setScene(new Scene(root, 600, 400));
            trapecioStage.initModality(Modality.NONE);
            trapecioStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Trapecio: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Romberg menu item.
     * This method opens a new window for the Romberg integration method.
     * If a function is entered in the main menu, it will be passed to the Romberg window.
     */
    @FXML
    protected void onRombergMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de Romberg
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Romberg.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Romberg.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            RombergController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de Romberg
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage rombergStage = new Stage();
            rombergStage.setTitle("Método de Romberg");
            rombergStage.setScene(new Scene(root, 600, 400));
            rombergStage.initModality(Modality.NONE);
            rombergStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Romberg: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Gráfica button.
     * This method opens a new window for visualizing mathematical functions as charts.
     * If a function is entered in the main menu, it will be passed to the chart visualizer.
     */
    @FXML
    protected void onGraficaButtonClick() {
        try {
            // Cargar el archivo FXML del visualizador de gráficas
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Graficadora.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Graficadora.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador y configurar la función inicial si existe
            GraficadoraController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al visualizador
            String functionText = functionTextField.getText().trim();

            // Crear y mostrar la nueva ventana
            Stage chartStage = new Stage();
            chartStage.setTitle("Visualizador de Funciones");
            chartStage.setScene(new Scene(root, 1000, 700));
            chartStage.initModality(Modality.NONE);
            chartStage.show();

    } catch (IOException e) {
        e.printStackTrace();
        showError("Error al abrir el visualizador de gráficas: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        showError("Error inesperado al abrir el visualizador: " + e.getMessage());
    }
}

    /**
     * Handles the click event for the Home button.
     * This method will be used to return to the home screen of the application.
     */
    @FXML
    protected void onHomeButtonClick() {
        welcomeText.setText("Página principal");
        // Here you would add code to return to the home screen
    }

    /**
     * Handles the click event for the Exit button.
     * This method will close the application safely.
     */
    @FXML
    protected void onExitButtonClick() {
        Platform.exit();
    }

    //--------------------------------------------------
    // Function Visualizer Event Handlers
    //--------------------------------------------------

    /**
     * Handles the action when a function is entered in the text field.
     * This method is called when the user presses Enter in the function text field.
     * It triggers the function visualization process.
     */
    @FXML
    protected void onFunctionEntered() {
        visualizeFunction();
    }

    /**
     * Handles the click event for the Visualizar button.
     * This method is called when the user clicks the Visualizar button.
     * It triggers the function visualization process.
     */
    @FXML
    protected void onVisualizeFunctionClick() {
        visualizeFunction();
    }

    //--------------------------------------------------
    // Function Visualization Core Methods
    //--------------------------------------------------

    /**
     * Visualizes the function entered in the text field.
     * This method renders the function as a LaTeX formula using the JLaTeXMath library.
     * The process involves:
     * 1. Converting the function text to LaTeX format
     * 2. Creating a TeXFormula from the LaTeX string
     * 3. Rendering the formula to an image
     * 4. Displaying the image in the UI
     */
    private void visualizeFunction() {
        String functionText = functionTextField.getText().trim();

        // Validate input
        if (functionText.isEmpty()) {
            showError("Por favor ingrese una función.");
            return;
        }

        try {
            // Clear previous content and hide any error messages
            functionDisplayPane.getChildren().clear();
            functionErrorLabel.setVisible(false);

            // Step 1: Convert the function text to LaTeX format
            String latexFormula = convertToLatex(functionText);

            // Step 2: Create a TeXFormula from the LaTeX string
            TeXFormula formula = new TeXFormula(latexFormula);

            // Step 3: Create a TeXIcon from the formula (STYLE_DISPLAY for equation mode, size 20)
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);

            // Step 4: Create a BufferedImage to render the formula
            BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();

            // Set white background for the image
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());

            // Paint the LaTeX formula on the image
            icon.paintIcon(null, g2, 0, 0);
            g2.dispose();  // Release graphics resources

            // Step 5: Convert the BufferedImage to a JavaFX WritableImage
            WritableImage fxImage = SwingFXUtils.toFXImage(image, null);

            // Step 6: Create an ImageView to display the image in JavaFX
            ImageView imageView = new ImageView(fxImage);

            // Step 7: Center the image in the display pane
            imageView.setLayoutX((functionDisplayPane.getPrefWidth() - icon.getIconWidth()) / 2);
            imageView.setLayoutY((functionDisplayPane.getPrefHeight() - icon.getIconHeight()) / 2);

            // Step 8: Add the ImageView to the display pane
            functionDisplayPane.getChildren().add(imageView);

            // Update welcome text to show the visualized function
            welcomeText.setText("Función visualizada: " + functionText);
        } catch (Exception e) {
            // Handle any errors that occur during visualization
            showError("Error al visualizar la función: " + e.getMessage());
        }
    }

    /**
     * Converts a function string to LaTeX format.
     * This method applies a series of transformations to convert standard mathematical
     * notation into proper LaTeX syntax for rendering with JLaTeXMath.
     * 
     * The transformations include:
     * 1. Converting mathematical functions (sin, cos, etc.) to LaTeX commands
     * 2. Converting operators and symbols to their LaTeX equivalents
     * 3. Handling fractions (a/b → \frac{a}{b})
     * 4. Handling exponents (x^n → x^{n})
     * 
     * @param function The function string to convert (e.g., "sin(x^2) + 5/x")
     * @return The LaTeX representation of the function (e.g., "\sin(x^{2}) + \frac{5}{x}")
     */
    private String convertToLatex(String function) {
        // Initialize with the original function
        String latexFormula = function;

        //--------------------------------------------------
        // Step 1: Replace mathematical functions and symbols with LaTeX commands
        //--------------------------------------------------
        latexFormula = latexFormula
            // Trigonometric functions
            .replace("sin", "\\sin")
            .replace("cos", "\\cos")
            .replace("tan", "\\tan")
            .replace("cot", "\\cot")
            .replace("sec", "\\sec")
            .replace("csc", "\\csc")

            // Inverse trigonometric functions
            .replace("arcsin", "\\arcsin")
            .replace("arccos", "\\arccos")
            .replace("arctan", "\\arctan")

            // Hyperbolic functions
            .replace("sinh", "\\sinh")
            .replace("cosh", "\\cosh")
            .replace("tanh", "\\tanh")

            // Logarithmic functions
            .replace("log", "\\log")
            .replace("ln", "\\ln")

            // Square root, cube root and nth root
            .replace("sqrt", "\\sqrt")
            .replace("cbrt", "\\sqrt[3]")

            // Absolute value - convert |x| to \left|x\right|
            .replace("|", "\\left|")
            .replace("|", "\\right|")

            // Common operations and symbols
            .replace("*", " \\cdot ")  // Multiplication dot
            .replace("<=", " \\leq ")  // Less than or equal
            .replace(">=", " \\geq ")  // Greater than or equal
            .replace("!=", " \\neq ")  // Not equal
            .replace("==", " = ")      // Equal
            .replace("pi", "\\pi")     // Pi symbol
            .replace("e^", "\\mathrm{e}^")  // Exponential function
            .replace("inf", "\\infty"); // Infinity symbol

        //--------------------------------------------------
        // Step 1.5: Handle nth root (convert root(n,x) to \sqrt[n]{x})
        //--------------------------------------------------
        if (latexFormula.contains("root(")) {
            StringBuilder result = new StringBuilder();
            int i = 0;

            while (i < latexFormula.length()) {
                // Look for "root(" pattern
                if (i + 5 <= latexFormula.length() && latexFormula.substring(i, i + 5).equals("root(")) {
                    // Found the start of a root function
                    result.append("\\sqrt[");
                    i += 5; // Move past "root("

                    // Find the index of the comma
                    int commaIndex = -1;
                    int parenCount = 1; // We're already inside one parenthesis
                    int j = i;

                    while (j < latexFormula.length() && commaIndex == -1) {
                        char c = latexFormula.charAt(j);
                        if (c == '(') parenCount++;
                        else if (c == ')') parenCount--;
                        else if (c == ',' && parenCount == 1) commaIndex = j;
                        j++;
                    }

                    if (commaIndex != -1) {
                        // Extract the n value
                        String nValue = latexFormula.substring(i, commaIndex);
                        result.append(nValue).append("]{");

                        // Find the closing parenthesis
                        int closeParenIndex = -1;
                        parenCount = 1;
                        j = commaIndex + 1;

                        while (j < latexFormula.length() && closeParenIndex == -1) {
                            char c = latexFormula.charAt(j);
                            if (c == '(') parenCount++;
                            else if (c == ')') {
                                parenCount--;
                                if (parenCount == 0) closeParenIndex = j;
                            }
                            j++;
                        }

                        if (closeParenIndex != -1) {
                            // Extract the x value
                            String xValue = latexFormula.substring(commaIndex + 1, closeParenIndex);
                            result.append(xValue).append("}");
                            i = closeParenIndex + 1; // Move past the closing parenthesis
                        } else {
                            // No closing parenthesis found, just append the rest
                            result.append(latexFormula.substring(commaIndex + 1));
                            i = latexFormula.length();
                        }
                    } else {
                        // No comma found, just append the rest
                        result.append(latexFormula.substring(i));
                        i = latexFormula.length();
                    }
                } else {
                    // Not a root function, just append the character
                    result.append(latexFormula.charAt(i));
                    i++;
                }
            }

            latexFormula = result.toString();
        }

        //--------------------------------------------------
        // Step 2: Handle fractions (convert a/b to \frac{a}{b})
        //--------------------------------------------------
        if (latexFormula.contains("/")) {
            StringBuilder result = new StringBuilder();
            String[] parts = latexFormula.split("/");

            if (parts.length >= 2) {
                // Convert to LaTeX fraction notation
                // Example: "5/x" becomes "\frac{5}{x}"

                // First part is the numerator
                result.append("\\frac{").append(parts[0]).append("}{");

                // Last part is the denominator
                result.append(parts[parts.length - 1]).append("}");

                // Note: This is a simplified approach that works for basic fractions
                // More complex expressions with multiple division operators would need
                // a more sophisticated parser
            } else {
                // If splitting didn't work as expected, keep the original
                result.append(latexFormula);
            }

            latexFormula = result.toString();
        }

        //--------------------------------------------------
        // Step 3: Handle exponents (convert x^n to x^{n})
        //--------------------------------------------------
        if (latexFormula.contains("^")) {
            StringBuilder result = new StringBuilder();
            int i = 0;

            // Process the string character by character
            while (i < latexFormula.length()) {
                char c = latexFormula.charAt(i);

                // When we find a caret (^), we need special handling
                if (c == '^' && i + 1 < latexFormula.length()) {
                    result.append('^');

                    // Case 1: The exponent is already enclosed in braces
                    if (latexFormula.charAt(i + 1) == '{') {
                        // Add the opening brace
                        result.append(latexFormula.charAt(i + 1));
                        i += 2;

                        // Copy everything until the matching closing brace
                        // We need to keep track of nested braces
                        int braceCount = 1;
                        while (i < latexFormula.length() && braceCount > 0) {
                            char nextChar = latexFormula.charAt(i);
                            result.append(nextChar);

                            // Update brace counter
                            if (nextChar == '{') braceCount++;
                            if (nextChar == '}') braceCount--;

                            i++;
                        }
                    } 
                    // Case 2: The exponent is not in braces, so we need to add them
                    else {
                        result.append('{');

                        // Case 2a: If the exponent is a number, include all consecutive digits
                        // Example: x^123 becomes x^{123}
                        if (Character.isDigit(latexFormula.charAt(i + 1))) {
                            int j = i + 1;
                            while (j < latexFormula.length() && Character.isDigit(latexFormula.charAt(j))) {
                                result.append(latexFormula.charAt(j));
                                j++;
                            }
                            i = j;
                        } 
                        // Case 2b: If the exponent is a single character, just include it
                        // Example: x^y becomes x^{y}
                        else {
                            result.append(latexFormula.charAt(i + 1));
                            i += 2;
                        }

                        // Close the braces for the exponent
                        result.append('}');
                    }
                } 
                // For any character that's not part of an exponent, just copy it
                else {
                    result.append(c);
                    i++;
                }
            }

            latexFormula = result.toString();
        }

        return latexFormula;
    }


    //--------------------------------------------------
    // Symbol Menu Handlers
    //--------------------------------------------------

    /**
     * Handles the click event for items in the symbols dropdown menu.
     * Extracts the symbol or function from the menu item text and inserts it at the cursor position.
     * 
     * @param event The action event from the menu item click
     */
    @FXML
    protected void onSymbolMenuItemClick(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String menuText = menuItem.getText();

        // Extract the symbol or function from the menu text
        // The format is typically "Description (symbol)"
        if (menuText.contains("(") && menuText.contains(")")) {
            String symbol = menuText.substring(menuText.indexOf("(") + 1, menuText.indexOf(")"));

            // Handle different types of symbols and functions
            switch (symbol) {
                // Basic operators
                case "^":
                case "*":
                case "/":
                case "+":
                case "-":
                    insertTextAtCursor(symbol);
                    break;

                // Functions that need parentheses
                case "sin":
                case "cos":
                case "tan":
                case "cot":
                case "sec":
                case "csc":
                case "arcsin":
                case "arccos":
                case "arctan":
                case "sinh":
                case "cosh":
                case "tanh":
                case "ln":
                case "log":
                case "sqrt":
                case "cbrt":
                case "abs":
                    insertTextAtCursor(symbol + "(", ")");
                    break;

                // Special case for exponential
                case "e^":
                    insertTextAtCursor("e^(", ")");
                    break;

                // Special case for root
                case "root":
                    insertTextAtCursor("root(n,", ")");
                    break;

                // Constants
                case "π":
                    insertTextAtCursor("pi");
                    break;
                case "e":
                    insertTextAtCursor("e");
                    break;
                case "inf":
                    insertTextAtCursor("inf");
                    break;

                // Parentheses and brackets
                case "()":
                    insertTextAtCursor("(", ")");
                    break;
                case "[]":
                    insertTextAtCursor("[", "]");
                    break;
                case "{}":
                    insertTextAtCursor("{", "}");
                    break;

                // Special case for factorial
                case "!":
                    insertTextAtCursor("!");
                    break;

                // Special case for fraction
                case "a/b":
                    insertTextAtCursor("(", ")/()");
                    break;

                default:
                    // If we can't identify the symbol, just insert the text as is
                    insertTextAtCursor(symbol);
                    break;
            }
        } else {
            // If the menu text doesn't follow the expected format, just use it as is
            insertTextAtCursor(menuText);
        }
    }

    // Math Operation Button Handlers have been replaced by the Symbol Menu Handlers

    //--------------------------------------------------
    // Utility Methods
    //--------------------------------------------------

    /**
     * Inserts text at the current cursor position in the function text field.
     * If there is a selection, the text will replace the selection.
     * 
     * @param textToInsert The text to insert at the cursor position
     */
    private void insertTextAtCursor(String textToInsert) {
        insertTextAtCursor(textToInsert, "");
    }

    /**
     * Inserts text at the current cursor position in the function text field,
     * with optional suffix text. If there is a selection, the text will wrap around the selection.
     * 
     * @param prefixText The text to insert before the cursor or selection
     * @param suffixText The text to insert after the cursor or selection
     */
    private void insertTextAtCursor(String prefixText, String suffixText) {
        // Get the current text and selection
        String currentText = functionTextField.getText();
        int caretPosition = functionTextField.getCaretPosition();
        int selectionStart = functionTextField.getSelection().getStart();
        int selectionEnd = functionTextField.getSelection().getEnd();

        // Check if there is a selection
        if (selectionStart != selectionEnd) {
            // There is a selection, wrap it with prefix and suffix
            String selectedText = currentText.substring(selectionStart, selectionEnd);
            String newText = currentText.substring(0, selectionStart) + 
                             prefixText + selectedText + suffixText + 
                             currentText.substring(selectionEnd);

            // Update the text field
            functionTextField.setText(newText);

            // Set the caret position after the inserted text
            functionTextField.positionCaret(selectionStart + prefixText.length() + selectedText.length() + suffixText.length());
        } else {
            // No selection, just insert at caret position
            String newText = currentText.substring(0, caretPosition) + 
                             prefixText + suffixText + 
                             currentText.substring(caretPosition);

            // Update the text field
            functionTextField.setText(newText);

            // Set the caret position between prefix and suffix
            functionTextField.positionCaret(caretPosition + prefixText.length());
        }

        // Set focus back to the text field
        functionTextField.requestFocus();
    }

private void showError(String message) {
    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
}
