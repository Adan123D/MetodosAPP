package com.ipn.metodosnumericosnvo.controller;

import com.ipn.metodosnumericosnvo.utils.GeoGebraUtils;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import com.ipn.metodosnumericosnvo.metodos_raices.Secante_Aitken;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Secante_AitkenController {
    @FXML private TextField txtFunc;
    @FXML private TextField txtX0;
    @FXML private TextField txtX1;
    @FXML private TextField txtTolerancia;
    @FXML private TextField txtMaxIter;
    @FXML private Label lblResultado;
    @FXML private Label lblError;
    @FXML private Button animarBtn;
    @FXML private WebView geogebraView;
    @FXML private Label geogebraStatusLabel;

    // TableView y columnas para mostrar los pasos del método
    @FXML private TableView<Secante_Aitken.Step> tablaPasos;
    @FXML private TableColumn<Secante_Aitken.Step, Integer> colIteracion;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colXPrevPrev;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colFxPrevPrev;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colXPrev;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colFxPrev;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colXActual;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colFxActual;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colXAitken;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colFxAitken;

    private final Secante_Aitken modelo = new Secante_Aitken();
    private WebEngine webEngine;

    @FXML
    public void initialize() {
        // Inicializar las columnas de la tabla
        colIteracion.setCellValueFactory(new PropertyValueFactory<>("iteracion"));
        colXPrevPrev.setCellValueFactory(new PropertyValueFactory<>("xPrevPrev"));
        colFxPrevPrev.setCellValueFactory(new PropertyValueFactory<>("fxPrevPrev"));
        colXPrev.setCellValueFactory(new PropertyValueFactory<>("xPrev"));
        colFxPrev.setCellValueFactory(new PropertyValueFactory<>("fxPrev"));
        colXActual.setCellValueFactory(new PropertyValueFactory<>("xActual"));
        colFxActual.setCellValueFactory(new PropertyValueFactory<>("fxActual"));
        colXAitken.setCellValueFactory(new PropertyValueFactory<>("xAitken"));
        colFxAitken.setCellValueFactory(new PropertyValueFactory<>("fxAitken"));

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
                    if (txtFunc.getText() != null && !txtFunc.getText().isEmpty()) {
                        graficarFuncion(txtFunc.getText());
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

    // Método para establecer la función desde el menú principal
    public void setFuncion(String funcion) {
        if (txtFunc != null) {
            txtFunc.setText(funcion);
        }
    }

    @FXML
    private void calcularRaiz() {
        lblResultado.setText("");
        lblError.setText("");
        try {
            String funcionTxt = txtFunc.getText();
            if (funcionTxt == null || funcionTxt.isEmpty())
                throw new IllegalArgumentException("Introduce la función f(x).");

            double x0 = Double.parseDouble(txtX0.getText());
            double x1 = Double.parseDouble(txtX1.getText());
            double tolerancia = Double.parseDouble(txtTolerancia.getText());
            int maxIter = Integer.parseInt(txtMaxIter.getText());
            if (tolerancia <= 0) throw new IllegalArgumentException("La tolerancia debe ser positiva.");
            if (maxIter <= 0) throw new IllegalArgumentException("Las iteraciones deben ser mayor que 0.");

            Secante_Aitken.Funcion fx = val -> {
                Argument x = new Argument("x = " + val);
                Expression expr = new Expression(funcionTxt, x);
                double result = expr.calculate();
                if (Double.isNaN(result)) throw new Exception("La evaluación de la función no es válida para x=" + val);
                return result;
            };

            // Lista para almacenar los pasos
            List<Secante_Aitken.Step> pasos = new ArrayList<>();

            // Calcular la raíz y recopilar los pasos
            double raiz = modelo.calcularRaiz(fx, x0, x1, tolerancia, maxIter, pasos);

            // Mostrar los pasos en la tabla
            tablaPasos.setItems(FXCollections.observableArrayList(pasos));

            // Mostrar el resultado
            lblResultado.setText(String.format("%.10f", raiz));

            // Graficar la función en GeoGebra
            graficarFuncion(funcionTxt);

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
            lblError.setText("Entradas numéricas no válidas.");
        } catch (IllegalArgumentException e) {
            lblError.setText(e.getMessage());
        } catch (ArithmeticException e) {
            lblError.setText("Error numérico: " + e.getMessage());
        } catch (Exception e) {
            lblError.setText("Error: " + e.getMessage());
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
            if (txtFunc.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("La función no puede estar vacía");
            }
            if (txtX0.getText().trim().isEmpty() || txtX1.getText().trim().isEmpty() || 
                txtTolerancia.getText().trim().isEmpty() || txtMaxIter.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Todos los campos son obligatorios");
            }

            // Parsear los valores
            String funcionTxt = txtFunc.getText();
            double x0 = Double.parseDouble(txtX0.getText());
            double x1 = Double.parseDouble(txtX1.getText());
            double tolerancia = Double.parseDouble(txtTolerancia.getText());
            int maxIter = Integer.parseInt(txtMaxIter.getText());

            // Validaciones adicionales
            if (tolerancia <= 0) {
                throw new IllegalArgumentException("La tolerancia debe ser un valor positivo");
            }
            if (maxIter <= 0) {
                throw new IllegalArgumentException("Las iteraciones deben ser mayor que 0");
            }

            // Verificar que se hayan calculado los pasos
            if (tablaPasos.getItems().isEmpty()) {
                // Si no hay pasos, calcularlos
                Secante_Aitken.Funcion fx = val -> {
                    Argument x = new Argument("x = " + val);
                    Expression expr = new Expression(funcionTxt, x);
                    double result = expr.calculate();
                    if (Double.isNaN(result)) throw new Exception("La evaluación de la función no es válida para x=" + val);
                    return result;
                };

                // Lista para almacenar los pasos
                List<Secante_Aitken.Step> pasos = new ArrayList<>();

                // Calcular la raíz y recopilar los pasos
                double raiz = modelo.calcularRaiz(fx, x0, x1, tolerancia, maxIter, pasos);

                // Mostrar los pasos en la tabla
                tablaPasos.setItems(FXCollections.observableArrayList(pasos));

                // Mostrar el resultado
                lblResultado.setText(String.format("%.10f", raiz));

                // Graficar la función en GeoGebra
                graficarFuncion(funcionTxt);

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

            // Mostrar un mensaje indicando que la animación no está implementada aún
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Animación");
            alert.setHeaderText("Animación no disponible");
            alert.setContentText("La animación para el método de Secante-Aitken no está implementada aún.");
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
