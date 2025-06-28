package com.ipn.metodosnumericosnvo.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la graficadora de funciones matemáticas usando GeoGebra API.
 * Permite visualizar gráficas de funciones ingresadas por el usuario.
 */
public class GraficadoraController implements Initializable {

    @FXML
    private WebView webView;

    @FXML
    private TextField funcionTextField;

    @FXML
    private Button graficarButton;

    @FXML
    private Label mensajeLabel;

    private WebEngine webEngine;

    /**
     * Inicializa el controlador de la graficadora.
     * Carga la aplicación web de GeoGebra y configura los eventos.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        webEngine = webView.getEngine();

        try {
            // Intentar cargar GeoGebra desde el archivo local para soporte offline
            URL geogebraHtmlUrl = getClass().getResource("/com/ipn/metodosnumericosnvo/html/geogebra-offline.html");

            if (geogebraHtmlUrl != null) {
                webEngine.load(geogebraHtmlUrl.toExternalForm());
                mensajeLabel.setText("Cargando GeoGebra localmente...");
            } else {
                // Si no se encuentra el archivo local, intentar cargar desde Internet
                webEngine.load("https://www.geogebra.org/classic/graphing");
                mensajeLabel.setText("Cargando GeoGebra desde Internet...");
            }

            // Esperar a que la página se cargue completamente
            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    mensajeLabel.setText("GeoGebra cargado correctamente. Ingresa una función para graficar.");
                    setupJSBridge();
                } else if (newState == Worker.State.FAILED) {
                    mensajeLabel.setText("Error al cargar GeoGebra. Verifica tu conexión a internet o intenta de nuevo más tarde.");

                    // Intentar cargar una versión básica de respaldo si falló la carga
                    fallbackToBasicMode();
                }
            });
        } catch (Exception e) {
            mensajeLabel.setText("Error al inicializar la graficadora: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método de respaldo para cuando no se puede cargar GeoGebra.
     * Carga una versión muy básica de graficadora.
     */
    private void fallbackToBasicMode() {
        try {
            String basicHtml = "<html><body style='background:#f5f5f5;'><div style='text-align:center;padding:20px;'>" +
                               "<h2 style='color:#cc0000;'>No se pudo cargar GeoGebra</h2>" +
                               "<p>No se ha podido cargar la graficadora. Verifica tu conexión a internet e intenta de nuevo.</p>" +
                               "</div></body></html>";

            webEngine.loadContent(basicHtml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Configura el puente JavaScript para interactuar con GeoGebra.
     */
    private void setupJSBridge() {
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("javaApp", this);
    }

    /**
     * Método llamado cuando se presiona el botón Graficar.
     * Envía la función ingresada a GeoGebra para su visualización.
     */
    @FXML
    private void onGraficarButtonClick(ActionEvent event) {
        graficarFuncion();
    }

    /**
     * Método llamado cuando se presiona Enter en el campo de texto de la función.
     */
    @FXML
    private void onFuncionEntered(ActionEvent event) {
        graficarFuncion();
    }

    /**
     * Grafica la función ingresada utilizando la API de GeoGebra.
     */
    private void graficarFuncion() {
        String funcion = funcionTextField.getText().trim();

        if (funcion.isEmpty()) {
            mensajeLabel.setText("Por favor, ingresa una función para graficar.");
            return;
        }

        try {
            // Escapar comillas para prevenir errores en JavaScript
            funcion = funcion.replace("\"", "\\\"").replace("'", "\\'");

            // Crear una nueva función en GeoGebra
            String comando = String.format("ggbApplet.evalCommand('f(x) = %s'); " +
                                        "ggbApplet.setAxesVisible(true, true); " +
                                        "ggbApplet.setGridVisible(true)", funcion);

            String script = com.ipn.metodosnumericosnvo.utils.GeoGebraUtils.generarScriptGeoGebra(comando);

            Object result = webEngine.executeScript(script);

            if (result instanceof Boolean && (Boolean)result) {
                mensajeLabel.setText("Función graficada: " + funcion);
            } else {
                mensajeLabel.setText("No se pudo graficar la función. Verifica la sintaxis.");
            }
        } catch (Exception e) {
            mensajeLabel.setText("Error al graficar la función: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método para ser llamado desde JavaScript.
     * Permite recibir mensajes desde la aplicación GeoGebra.
     */
    public void recibirMensajeDeJS(String mensaje) {
        mensajeLabel.setText(mensaje);
    }

    /**
     * Establece la función a graficar.
     * Este método es llamado desde el menú principal cuando se abre la graficadora
     * con una función ya ingresada.
     * 
     * @param funcion La función a graficar
     */
    public void setFuncion(String funcion) {
        if (funcion != null && !funcion.isEmpty()) {
            funcionTextField.setText(funcion);
            // Intentar graficar solo si la página de GeoGebra ya está cargada
            if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
                graficarFuncion();
            }
        }
    }

    /**
     * Limpia la gráfica actual.
     */
    @FXML
    private void onLimpiarButtonClick(ActionEvent event) {
        webEngine.executeScript(
            "if (typeof ggbApplet !== 'undefined') {" +
            "  ggbApplet.reset();" +
            "  ggbApplet.setAxesVisible(true, true);" +
            "  ggbApplet.setGridVisible(true);" +
            "}");
        funcionTextField.clear();
        mensajeLabel.setText("Gráfica limpiada. Ingresa una nueva función.");
    }

    /**
     * Muestra la ayuda de la graficadora.
     */
    @FXML
    private void onAyudaButtonClick(ActionEvent event) {
        try {
            // Crear un nuevo WebView para mostrar la ayuda
            WebView helpWebView = new WebView();
            WebEngine helpEngine = helpWebView.getEngine();

            // Cargar el archivo HTML de ayuda
            URL helpUrl = getClass().getResource("/com/ipn/metodosnumericosnvo/help/graficadora-ayuda.html");

            if (helpUrl != null) {
                helpEngine.load(helpUrl.toExternalForm());
            } else {
                // Si no se encuentra el archivo, mostrar un mensaje básico
                helpEngine.loadContent(
                    "<html><body style='font-family:Arial;padding:20px;'>" +
                    "<h1>Ayuda no disponible</h1>" +
                    "<p>El archivo de ayuda no se ha encontrado.</p>" +
                    "</body></html>"
                );
            }

            // Crear una nueva ventana para la ayuda
            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda - Graficadora de Funciones");
            helpStage.setScene(new Scene(helpWebView, 800, 600));
            helpStage.initModality(Modality.WINDOW_MODAL);
            helpStage.setResizable(true);
            helpStage.show();

        } catch (Exception e) {
            mensajeLabel.setText("Error al mostrar la ayuda: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
