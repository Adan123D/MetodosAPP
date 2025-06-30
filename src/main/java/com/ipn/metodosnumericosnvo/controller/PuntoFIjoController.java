package com.ipn.metodosnumericosnvo.controller;

import com.ipn.metodosnumericosnvo.utils.GeoGebraUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import com.ipn.metodosnumericosnvo.metodos_raices.PuntoFijo;

public class PuntoFIjoController {

    @FXML TextField gTxt, x0Txt, maxIterTxt, tolTxt;
    @FXML Label resultLbl;
    @FXML TableView<PuntoFijo.Iteration> tableView;
    @FXML TableColumn<PuntoFijo.Iteration, String> colIter, colXi, colGxi, colError;
    @FXML private Button animarBtn;
    @FXML private WebView geogebraView;
    @FXML private Label geogebraStatusLabel;

    private final PuntoFijo model = new PuntoFijo();
    private WebEngine webEngine;

    @FXML
    public void initialize() {
        colIter.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().i)));
        colXi.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%.6f", c.getValue().xi)));
        colGxi.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%.6f", c.getValue().gxi)));
        colError.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%.6f", c.getValue().error)));

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
                    if (gTxt.getText() != null && !gTxt.getText().isEmpty()) {
                        graficarFuncion(gTxt.getText());
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
     * Sets the function to be evaluated.
     * This method is called when a function is passed from the main menu.
     * 
     * @param funcion The function to be evaluated
     */
    public void setFuncion(String funcion) {
        if (funcion != null && !funcion.isEmpty() && gTxt != null) {
            gTxt.setText(funcion);
        }
    }

    @FXML
    private void onCalcular() {
        try {
            // Validar entradas
            String g = gTxt.getText().trim();
            if (g.isEmpty()) {
                throw new IllegalArgumentException("La función g(x) no puede estar vacía");
            }

            double x0;
            try {
                x0 = Double.parseDouble(x0Txt.getText().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El valor inicial x₀ debe ser un número válido");
            }

            int maxIt;
            try {
                maxIt = Integer.parseInt(maxIterTxt.getText().trim());
                if (maxIt <= 0) {
                    throw new IllegalArgumentException("El número máximo de iteraciones debe ser positivo");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El número máximo de iteraciones debe ser un entero válido");
            }

            double tol;
            try {
                tol = Double.parseDouble(tolTxt.getText().trim());
                if (tol <= 0) {
                    throw new IllegalArgumentException("La tolerancia debe ser un valor positivo");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("La tolerancia debe ser un número válido");
            }

            // Ejecutar el método
            var lista = model.resolver(g, x0, maxIt, tol);
            if (lista.isEmpty()) {
                throw new RuntimeException("No se realizaron iteraciones.");
            }

            // Mostrar resultados
            tableView.setItems(FXCollections.observableArrayList(lista));
            var last = lista.get(lista.size() - 1);
            resultLbl.setText(String.format("Última aproximación: %.10f  (error=%.10f)", last.gxi, last.error));

            // Graficar la función en GeoGebra
            graficarFuncion(g);

            // Marcar la raíz en la gráfica
            if (webEngine != null) {
                try {
                    String comando = String.format(
                        "ggbApplet.evalCommand('Raiz = (%.10f, 0)'); " +
                        "ggbApplet.evalCommand('SetColor(Raiz, 255, 0, 0)'); " +
                        "ggbApplet.evalCommand('SetPointSize(Raiz, 5)'); " +
                        "ggbApplet.evalCommand('SetPointStyle(Raiz, 0)');",
                        last.gxi
                    );
                    webEngine.executeScript(GeoGebraUtils.generarScriptGeoGebra(comando));
                } catch (Exception e) {
                    geogebraStatusLabel.setText("Error al marcar la raíz: " + e.getMessage());
                }
            }
        } catch (ArithmeticException e) {
            resultLbl.setText("Error de convergencia: " + e.getMessage());
            tableView.getItems().clear();
        } catch (IllegalArgumentException e) {
            resultLbl.setText("Error de entrada: " + e.getMessage());
            tableView.getItems().clear();
        } catch (Exception e) {
            resultLbl.setText("Error: " + e.getMessage());
            tableView.getItems().clear();
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
            if (gTxt.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("La función g(x) no puede estar vacía");
            }
            if (x0Txt.getText().trim().isEmpty() || maxIterTxt.getText().trim().isEmpty() || tolTxt.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Los valores de x0, iteraciones máximas y tolerancia son obligatorios");
            }

            // Parsear los valores
            String g = gTxt.getText().trim();
            double x0 = Double.parseDouble(x0Txt.getText().trim());
            int maxIt = Integer.parseInt(maxIterTxt.getText().trim());
            double tol = Double.parseDouble(tolTxt.getText().trim());

            // Validaciones adicionales
            if (tol <= 0) {
                throw new IllegalArgumentException("La tolerancia debe ser un valor positivo");
            }

            // Verificar que se hayan calculado las iteraciones
            if (tableView.getItems().isEmpty()) {
                // Si no hay iteraciones, calcularlas
                var lista = model.resolver(g, x0, maxIt, tol);
                if (lista.isEmpty()) {
                    throw new RuntimeException("No se realizaron iteraciones.");
                }

                // Mostrar resultados
                tableView.setItems(FXCollections.observableArrayList(lista));
                var last = lista.get(lista.size() - 1);
                resultLbl.setText(String.format("Última aproximación: %.10f  (error=%.10f)", last.gxi, last.error));

                // Actualizar la gráfica
                graficarFuncion(g);

                // Marcar la raíz en la gráfica
                if (webEngine != null) {
                    try {
                        String comando = String.format(
                            "ggbApplet.evalCommand('Raiz = (%.10f, 0)'); " +
                            "ggbApplet.evalCommand('SetColor(Raiz, 255, 0, 0)'); " +
                            "ggbApplet.evalCommand('SetPointSize(Raiz, 5)'); " +
                            "ggbApplet.evalCommand('SetPointStyle(Raiz, 0)');",
                            last.gxi
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
            alert.setContentText("La animación para el método de Punto Fijo no está implementada aún.");
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
