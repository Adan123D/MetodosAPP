package com.ipn.metodosnumericosnvo.controller;

import com.ipn.metodosnumericosnvo.animation.BiseccionAnimacionFX;
import com.ipn.metodosnumericosnvo.metodos_raices.Biseccion;
import com.ipn.metodosnumericosnvo.utils.GeoGebraUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.List;

public class BiseccionController {

    @FXML private TextField funcionField;
    @FXML private TextField x0Field;
    @FXML private TextField x1Field;
    @FXML private TextField tolField;
    @FXML private TableView<Biseccion.Iteracion> tablaIteraciones;
    @FXML private TableColumn<Biseccion.Iteracion, Integer> colIteracion;
    @FXML private TableColumn<Biseccion.Iteracion, Double> colX0, colX1, colX2;
    @FXML private TableColumn<Biseccion.Iteracion, Double> colFx0, colFx1, colFx2, colError;
    @FXML private Button calcularBtn;
    @FXML private Label raizLabel;
    @FXML private WebView geogebraView;
    @FXML private Label geogebraStatusLabel;

    private WebEngine webEngine;

    /**
     * Sets the function text in the function field.
     * @param function The function text to set
     */
    public void setFuncion(String function) {
        if (funcionField != null) {
            funcionField.setText(function);
        }
    }

    @FXML
    public void initialize() {
        colIteracion.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().iteracion).asObject());
        colX0.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().x0).asObject());
        colX1.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().x1).asObject());
        colX2.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().x2).asObject());
        colFx0.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().fx0).asObject());
        colFx1.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().fx1).asObject());
        colFx2.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().fx2).asObject());
        colError.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().error).asObject());

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
                    if (funcionField.getText() != null && !funcionField.getText().isEmpty()) {
                        graficarFuncion(funcionField.getText());
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

    @FXML
    private void onCalcular() {
        String funcion = funcionField.getText();
        double x0 = Double.parseDouble(x0Field.getText());
        double x1 = Double.parseDouble(x1Field.getText());
        double tol = Double.parseDouble(tolField.getText());

        // Obtener las iteraciones
        java.util.List<Biseccion.Iteracion> iteraciones = Biseccion.resolver(funcion, x0, x1, tol);
        ObservableList<Biseccion.Iteracion> datos = FXCollections.observableArrayList(iteraciones);
        tablaIteraciones.setItems(datos);

        // Mostrar la raíz encontrada
        double raiz = Biseccion.obtenerRaiz(iteraciones);
        raizLabel.setText(String.format("%.10f", raiz));

        // Graficar la función en GeoGebra
        graficarFuncion(funcion);

        // Marcar la raíz en la gráfica
        if (webEngine != null) {
            try {
                String comando = String.format(
                    "ggbApplet.evalCommand('Raiz = (%.10f, 0)'); " +
                    "ggbApplet.evalCommand('SetColor(Raiz, 255, 0, 0)'); " +
                    "ggbApplet.evalCommand('SetPointSize(Raiz, 5)'); " +
                    "ggbApplet.evalCommand('SetPointStyle(Raiz, 0)');",
                    raiz
                );
                webEngine.executeScript(GeoGebraUtils.generarScriptGeoGebra(comando));
            } catch (Exception e) {
                geogebraStatusLabel.setText("Error al marcar la raíz: " + e.getMessage());
            }
        }
    }

    /**
     * Método que se ejecuta al hacer clic en el botón "Animar".
     * Crea y muestra una animación del método de bisección.
     */
    @FXML
    private void onAnimar() {
        try {
            // Validar que los campos no estén vacíos
            if (funcionField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("La función no puede estar vacía");
            }
            if (x0Field.getText().trim().isEmpty() || x1Field.getText().trim().isEmpty() || tolField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Los valores de x0, x1 y tolerancia son obligatorios");
            }

            // Parsear los valores
            String funcion = funcionField.getText();
            double x0 = Double.parseDouble(x0Field.getText());
            double x1 = Double.parseDouble(x1Field.getText());
            double tol = Double.parseDouble(tolField.getText());

            // Validaciones adicionales
            if (tol <= 0) {
                throw new IllegalArgumentException("La tolerancia debe ser un valor positivo");
            }

            // Verificar que se hayan calculado las iteraciones
            if (tablaIteraciones.getItems().isEmpty()) {
                // Si no hay iteraciones, calcularlas
                java.util.List<Biseccion.Iteracion> iteraciones = Biseccion.resolver(funcion, x0, x1, tol);
                ObservableList<Biseccion.Iteracion> datos = FXCollections.observableArrayList(iteraciones);
                tablaIteraciones.setItems(datos);

                // Mostrar la raíz encontrada
                double raiz = Biseccion.obtenerRaiz(iteraciones);
                raizLabel.setText(String.format("%.10f", raiz));

                // Actualizar la gráfica
                graficarFuncion(funcion);

                // Marcar la raíz en la gráfica
                if (webEngine != null) {
                    try {
                        String comando = String.format(
                            "ggbApplet.evalCommand('Raiz = (%.10f, 0)'); " +
                            "ggbApplet.evalCommand('SetColor(Raiz, 255, 0, 0)'); " +
                            "ggbApplet.evalCommand('SetPointSize(Raiz, 5)'); " +
                            "ggbApplet.evalCommand('SetPointStyle(Raiz, 0)');",
                            raiz
                        );
                        webEngine.executeScript(GeoGebraUtils.generarScriptGeoGebra(comando));
                    } catch (Exception ex) {
                        geogebraStatusLabel.setText("Error al marcar la raíz: " + ex.getMessage());
                    }
                }
            }

            // Obtener las iteraciones de la tabla
            java.util.List<Biseccion.Iteracion> iteraciones = tablaIteraciones.getItems();

            // Crear y mostrar la animación
            BiseccionAnimacionFX animacion = new BiseccionAnimacionFX(funcion, x0, x1, tol, iteraciones);
            animacion.mostrarAnimacion();

        } catch (Exception e) {
            // Mostrar un mensaje de error si algo falla
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al crear la animación");
            alert.setContentText("Asegúrese de ingresar valores válidos y que la función sea correcta.\n" + e.getMessage());
            alert.showAndWait();
        }
    }
}
