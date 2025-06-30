package com.ipn.metodosnumericosnvo.controller;

import com.ipn.metodosnumericosnvo.metodos_raices.Biseccion_Aitken;
import com.ipn.metodosnumericosnvo.utils.GeoGebraUtils;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.ArrayList;

public class Biseccion_AitkenController {

    @FXML private TextField fxField, aField, bField, tolField, maxItField;
    @FXML private TableView<Biseccion_Aitken.Step> table;
    @FXML private TableColumn<Biseccion_Aitken.Step, Integer> colPaso;
    @FXML private TableColumn<Biseccion_Aitken.Step, Double> colA, colB, colC, colFc, colAitken;
    @FXML private Label resultLabel;
    @FXML private Button animarBtn;
    @FXML private WebView geogebraView;
    @FXML private Label geogebraStatusLabel;

    private final Biseccion_Aitken model = new Biseccion_Aitken();
    private WebEngine webEngine;

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(new PropertyValueFactory<>("paso"));
        colA.setCellValueFactory(new PropertyValueFactory<>("a"));
        colB.setCellValueFactory(new PropertyValueFactory<>("b"));
        colC.setCellValueFactory(new PropertyValueFactory<>("c"));
        colFc.setCellValueFactory(new PropertyValueFactory<>("fc"));
        colAitken.setCellValueFactory(new PropertyValueFactory<>("aitken"));

        // Valores predeterminados
        tolField.setText("0.0001");
        maxItField.setText("100");

        // Inicializar GeoGebra
        initializeGeoGebra();
    }

    /**
     * Inicializa el componente GeoGebra para visualizar la función.
     */
    private void initializeGeoGebra() {
        if (geogebraView == null) {
            return;
        }

        webEngine = geogebraView.getEngine();

        try {
            // Cargar GeoGebra desde el archivo HTML local
            URL geogebraHtmlUrl = getClass().getResource("/com/ipn/metodosnumericosnvo/html/geogebra-offline.html");

            if (geogebraHtmlUrl != null) {
                webEngine.load(geogebraHtmlUrl.toExternalForm());
                geogebraStatusLabel.setText("Cargando GeoGebra...");
            } else {
                // Si no se encuentra el archivo local, intentar cargar desde Internet
                webEngine.load("https://www.geogebra.org/classic/graphing");
                geogebraStatusLabel.setText("Cargando GeoGebra desde Internet...");
            }

            // Configurar el listener para cuando la página se cargue completamente
            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    geogebraStatusLabel.setText("GeoGebra cargado correctamente");
                    setupJSBridge();

                    // Si ya hay una función ingresada, graficarla
                    if (fxField.getText() != null && !fxField.getText().isEmpty()) {
                        graficarFuncion(fxField.getText());
                    }
                } else if (newState == Worker.State.FAILED) {
                    geogebraStatusLabel.setText("Error al cargar GeoGebra");
                }
            });
        } catch (Exception e) {
            geogebraStatusLabel.setText("Error al inicializar GeoGebra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura el puente JavaScript para interactuar con GeoGebra.
     */
    private void setupJSBridge() {
        try {
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("javaApp", this);

            // Configurar GeoGebra con opciones básicas
            webEngine.executeScript(
                "if (typeof ggbApplet !== 'undefined') {" +
                "  ggbApplet.setPerspective('G');" +  // Establece la perspectiva de graficación
                "  ggbApplet.setAxesVisible(true, true);" +
                "  ggbApplet.setGridVisible(true);" +
                "}"
            );

            // Deshabilitar interacción para simplificar la visualización
            webEngine.executeScript(GeoGebraUtils.toggleInteractiveFeatures(false));

        } catch (Exception e) {
            geogebraStatusLabel.setText("Error al configurar GeoGebra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método para ser llamado desde JavaScript.
     * Permite recibir mensajes desde la aplicación GeoGebra.
     */
    public void recibirMensajeDeJS(String mensaje) {
        geogebraStatusLabel.setText(mensaje);
    }

    /**
     * Grafica la función en GeoGebra.
     * 
     * @param funcion La función a graficar
     */
    private void graficarFuncion(String funcion) {
        if (webEngine == null || funcion == null || funcion.trim().isEmpty()) {
            return;
        }

        try {
            // Escapar comillas para prevenir errores en JavaScript
            funcion = funcion.replace("\"", "\\\"").replace("'", "\\'");

            // Procesar la función para asegurarse de que tenga formato correcto
            funcion = GeoGebraUtils.convertirFuncionParaGeoGebra(funcion);

            // Crear una nueva función en GeoGebra
            String comando = String.format("ggbApplet.evalCommand('f(x) = %s'); " +
                                        "ggbApplet.setAxesVisible(true, true); " +
                                        "ggbApplet.setGridVisible(true)", funcion);

            String script = GeoGebraUtils.generarScriptGeoGebra(comando);

            Object result = webEngine.executeScript(script);

            if (result instanceof Boolean && (Boolean)result) {
                geogebraStatusLabel.setText("Función graficada: " + funcion);
            } else {
                geogebraStatusLabel.setText("No se pudo graficar la función. Verifica la sintaxis.");
            }
        } catch (Exception e) {
            geogebraStatusLabel.setText("Error al graficar la función: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Establece la función a evaluar en el campo de texto.
     * @param funcion La función a evaluar
     */
    public void setFuncion(String funcion) {
        if (fxField != null) {
            fxField.setText(funcion);
        }
    }

    @FXML
    public void onCalcular() {
        try {
            String fx = fxField.getText();
            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());
            double tol = Double.parseDouble(tolField.getText());
            int maxIt = Integer.parseInt(maxItField.getText());

            var steps = new ArrayList<Biseccion_Aitken.Step>();
            double root = model.resolver(fx, a, b, tol, maxIt, steps);

            table.setItems(FXCollections.observableArrayList(steps));
            resultLabel.setText(String.format("%.10f", root));

            // Graficar la función en GeoGebra
            graficarFuncion(fx);

            // Marcar la raíz en la gráfica
            if (webEngine != null) {
                try {
                    String comando = String.format(
                        "ggbApplet.evalCommand('Raiz = (%.10f, 0)'); " +
                        "ggbApplet.evalCommand('SetColor(Raiz, 255, 0, 0)'); " +
                        "ggbApplet.evalCommand('SetPointSize(Raiz, 5)'); " +
                        "ggbApplet.evalCommand('SetPointStyle(Raiz, 0)');",
                        root
                    );
                    webEngine.executeScript(GeoGebraUtils.generarScriptGeoGebra(comando));
                } catch (Exception e) {
                    geogebraStatusLabel.setText("Error al marcar la raíz: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            resultLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Método que se ejecuta al hacer clic en el botón "Animar".
     * Muestra un mensaje indicando que la animación no está implementada aún.
     */
    @FXML
    private void onAnimar() {
        try {
            // Validar que los campos no estén vacíos
            if (fxField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("La función no puede estar vacía");
            }
            if (aField.getText().trim().isEmpty() || bField.getText().trim().isEmpty() || 
                tolField.getText().trim().isEmpty() || maxItField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Todos los campos son obligatorios");
            }

            // Parsear los valores
            String fx = fxField.getText();
            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());
            double tol = Double.parseDouble(tolField.getText());
            int maxIt = Integer.parseInt(maxItField.getText());

            // Validaciones adicionales
            if (tol <= 0) {
                throw new IllegalArgumentException("La tolerancia debe ser un valor positivo");
            }
            if (maxIt <= 0) {
                throw new IllegalArgumentException("Las iteraciones deben ser mayor que 0");
            }

            // Verificar que se hayan calculado los pasos
            if (table.getItems().isEmpty()) {
                // Si no hay pasos, calcularlos
                var steps = new ArrayList<Biseccion_Aitken.Step>();
                double root = model.resolver(fx, a, b, tol, maxIt, steps);

                // Mostrar los pasos en la tabla
                table.setItems(FXCollections.observableArrayList(steps));

                // Mostrar el resultado
                resultLabel.setText(String.format("%.10f", root));

                // Graficar la función en GeoGebra
                graficarFuncion(fx);

                // Marcar la raíz en la gráfica
                if (webEngine != null) {
                    try {
                        String comando = String.format(
                            "ggbApplet.evalCommand('Raiz = (%.10f, 0)'); " +
                            "ggbApplet.evalCommand('SetColor(Raiz, 255, 0, 0)'); " +
                            "ggbApplet.evalCommand('SetPointSize(Raiz, 5)'); " +
                            "ggbApplet.evalCommand('SetPointStyle(Raiz, 0)');",
                            root
                        );
                        webEngine.executeScript(GeoGebraUtils.generarScriptGeoGebra(comando));
                    } catch (Exception ex) {
                        geogebraStatusLabel.setText("Error al marcar la raíz: " + ex.getMessage());
                    }
                }
            }

            // Mostrar un mensaje indicando que la animación no está implementada aún
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Animación");
            alert.setHeaderText("Animación no disponible");
            alert.setContentText("La animación para el método de Bisección-Aitken no está implementada aún.");
            alert.showAndWait();

        } catch (Exception e) {
            // Mostrar un mensaje de error si algo falla
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al procesar la solicitud");
            alert.setContentText("Asegúrese de ingresar valores válidos y que la función sea correcta.\n" + e.getMessage());
            alert.showAndWait();
        }
    }
}
