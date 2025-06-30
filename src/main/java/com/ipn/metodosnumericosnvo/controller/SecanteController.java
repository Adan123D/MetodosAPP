package com.ipn.metodosnumericosnvo.controller;

import com.ipn.metodosnumericosnvo.animation.SecanteAnimacionFX;
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
import java.util.List;

import com.ipn.metodosnumericosnvo.metodos_raices.Secante;

public class SecanteController {

    @FXML private TextField fxField, x0Field, x1Field, tolField, maxItField;
    @FXML private TableView<Secante.Step> tablaPasos;
    @FXML private TableColumn<Secante.Step, Integer> colPaso;
    @FXML private TableColumn<Secante.Step, Double> colX0, colX1, colX2, colFx2;
    @FXML private Label resultadoLabel;
    @FXML private Button animarBtn;
    @FXML private WebView geogebraView;
    @FXML private Label geogebraStatusLabel;

    private final Secante modelo = new Secante();
    private WebEngine webEngine;

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(new PropertyValueFactory<>("paso"));
        colX0.setCellValueFactory(new PropertyValueFactory<>("x0"));
        colX1.setCellValueFactory(new PropertyValueFactory<>("x1"));
        colX2.setCellValueFactory(new PropertyValueFactory<>("x2"));
        colFx2.setCellValueFactory(new PropertyValueFactory<>("fx2"));

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

    @FXML
    public void onCalcular() {
        try {
            String func = fxField.getText();
            double x0 = Double.parseDouble(x0Field.getText());
            double x1 = Double.parseDouble(x1Field.getText());
            double tol = Double.parseDouble(tolField.getText());
            int maxIt = Integer.parseInt(maxItField.getText());

            List<Secante.Step> pasos = new ArrayList<>();
            double raiz = modelo.resolver(func, x0, x1, tol, maxIt, pasos);

            tablaPasos.setItems(FXCollections.observableArrayList(pasos));
            resultadoLabel.setText(String.format("%.10f", raiz));

            // Graficar la función en GeoGebra
            graficarFuncion(func);

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
        } catch (NumberFormatException e) {
            resultadoLabel.setText("Verifica los números y la función.");
        } catch (IllegalArgumentException e) {
            resultadoLabel.setText(e.getMessage());
        } catch (Exception e) {
            resultadoLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Sets the function text in the function field.
     * This method is called from the main menu when a function is already entered.
     * 
     * @param funcion The function text to set
     */
    public void setFuncion(String funcion) {
        if (fxField != null) {
            fxField.setText(funcion);
        }
    }

    /**
     * Método que se ejecuta al hacer clic en el botón "Animar".
     * Crea y muestra una animación del método de la secante.
     */
    @FXML
    private void onAnimar() {
        try {
            // Validar que los campos no estén vacíos
            if (fxField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("La función no puede estar vacía");
            }
            if (x0Field.getText().trim().isEmpty() || x1Field.getText().trim().isEmpty() || tolField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Los valores de x0, x1 y tolerancia son obligatorios");
            }

            // Parsear los valores
            String funcion = fxField.getText();
            double x0 = Double.parseDouble(x0Field.getText());
            double x1 = Double.parseDouble(x1Field.getText());
            double tol = Double.parseDouble(tolField.getText());
            int maxIt = Integer.parseInt(maxItField.getText());

            // Validaciones adicionales
            if (tol <= 0) {
                throw new IllegalArgumentException("La tolerancia debe ser un valor positivo");
            }

            // Verificar que se hayan calculado los pasos
            if (tablaPasos.getItems().isEmpty()) {
                // Si no hay pasos, calcularlos
                List<Secante.Step> pasos = new ArrayList<>();
                double raiz = modelo.resolver(funcion, x0, x1, tol, maxIt, pasos);
                tablaPasos.setItems(FXCollections.observableArrayList(pasos));

                // Mostrar la raíz encontrada
                resultadoLabel.setText(String.format("%.10f", raiz));

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

            // Obtener los pasos de la tabla
            List<Secante.Step> pasos = tablaPasos.getItems();

            // Crear y mostrar la animación
            SecanteAnimacionFX animacion = new SecanteAnimacionFX(funcion, x0, x1, tol, pasos);
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
