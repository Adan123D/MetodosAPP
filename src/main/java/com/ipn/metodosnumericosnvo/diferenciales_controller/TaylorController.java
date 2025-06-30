package com.ipn.metodosnumericosnvo.diferenciales_controller;

import com.ipn.metodosnumericosnvo.metodo_diferenciales.MetodoTaylor;
import com.ipn.metodosnumericosnvo.metodo_diferenciales.MetodoTaylor.Iteracion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.application.Platform;
import java.util.List;
import java.text.DecimalFormat;

public class TaylorController {

    @FXML private TextField txtFuncion;
    @FXML private TextField txtT0;
    @FXML private TextField txtY0;
    @FXML private TextField txtTf;
    @FXML private TextField txtN;

    @FXML private TableView<Iteracion> tablaIteraciones;
    @FXML private TableColumn<Iteracion, Integer> colPaso;
    @FXML private TableColumn<Iteracion, Double> colT;
    @FXML private TableColumn<Iteracion, Double> colY;

    @FXML private Button btnEjecutar;
    @FXML private Button btnMostrarExpansion;

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPaso()).asObject());
        colT.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getT()).asObject());
        colY.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getY()).asObject());

        // Verificar que los componentes existen antes de configurarlos
        // (Previene NullPointerException si algún control no se inicializó correctamente)
        if (txtFuncion != null) {
            txtFuncion.setPromptText("Ejemplo: t^(-2)*(Sin(2*t)-2*t*y)");
            txtFuncion.setText("t^(-2)*(Sin(2*t)-2*t*y)"); // Cargar ejemplo por defecto
        }

        if (txtT0 != null) {
            txtT0.setPromptText("1");
            txtT0.setText("1"); // Cargar ejemplo por defecto
        }

        if (txtY0 != null) {
            txtY0.setPromptText("2");
            txtY0.setText("2"); // Cargar ejemplo por defecto
        }

        if (txtTf != null) {
            txtTf.setPromptText("2");
            txtTf.setText("2"); // Cargar ejemplo por defecto
        }

        if (txtN != null) {
            txtN.setPromptText("4"); // para h=0.25
            txtN.setText("4"); // 4 pasos para h=0.25
        }

        // Mostrar mensaje de bienvenida con instrucciones
        Platform.runLater(() -> {
            mostrarInformacion("Método de Taylor - Ejemplo cargado", 
                "Se ha cargado el ejemplo: t^-2*(sin(2*t)-2*t*y)\n" +
                "Intervalo [1, 2] con h=0.25, y(1)=2\n\n" +
                "Puede modificar los valores o pulsar 'Ejecutar' para resolver.");
        });
    }

    @FXML
    private void onEjecutar() {
        try {
            // Verificar que todos los campos tengan datos
            if (txtFuncion.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "Debe ingresar una función");
                return;
            }

            if (txtT0.getText().trim().isEmpty() || 
                txtY0.getText().trim().isEmpty() || 
                txtTf.getText().trim().isEmpty() || 
                txtN.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "Todos los campos son obligatorios");
                return;
            }

            // Intentar convertir los valores numéricos
            double t0, y0, tf;
            int n;

            try {
                t0 = Double.parseDouble(txtT0.getText().trim());
                y0 = Double.parseDouble(txtY0.getText().trim());
                tf = Double.parseDouble(txtTf.getText().trim());
                n = Integer.parseInt(txtN.getText().trim());
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Los valores numéricos no tienen el formato correcto");
                return;
            }

            // Validar rango de valores
            if (n <= 0) {
                mostrarAlerta("Error", "El número de pasos debe ser positivo");
                return;
            }

            if (t0 >= tf) {
                mostrarAlerta("Error", "El tiempo final debe ser mayor que el tiempo inicial");
                return;
            }

            // Obtener la función y procesarla
            String funcion = txtFuncion.getText().trim();

            // Mostrar indicación de procesamiento
            tablaIteraciones.setPlaceholder(new Label("Calculando..."));

            // Ejecutar el método de Taylor
            List<Iteracion> pasos = MetodoTaylor.resolver(t0, y0, tf, n, funcion);

            // Mostrar los resultados
            ObservableList<Iteracion> datos = FXCollections.observableArrayList(pasos);
            tablaIteraciones.setItems(datos);

            // Mostrar información de paso
            double h = (tf - t0) / n;

            // Formatear la salida para mostrar la solución final
            DecimalFormat df = new DecimalFormat("#.######");
            Iteracion ultimoPaso = pasos.get(pasos.size() - 1);

            mostrarInformacion("Cálculo completado", 
                               String.format("Solución para la ecuación dy/dt = %s\n" +
                                            "Intervalo [%.2f, %.2f] con h = %.4f\n" +
                                            "Condición inicial y(%.2f) = %.4f\n\n" +
                                            "Resultado: y(%.4f) = %s", 
                                            funcion, t0, tf, h, t0, y0,
                                            ultimoPaso.getT(), df.format(ultimoPaso.getY())));

        } catch (RuntimeException e) {
            mostrarAlerta("Error de cálculo", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Error inesperado", "Ocurrió un error durante el cálculo:\n" + e.getMessage());
            e.printStackTrace();
        }
    }


    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void mostrarInformacion(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    /**
     * Método para precargar un ejemplo específico
     * Útil para demostraciones o pruebas
     */
    public void precargarEjemplo() {
        // Ejemplo: t^-2*(sin(2*t)-2*t*y) con intervalo [1,2], h=0.25, y(1)=2
        txtFuncion.setText("t^(-2)*(Sin(2*t)-2*t*y)");
        txtT0.setText("1");
        txtY0.setText("2");
        txtTf.setText("2");
        txtN.setText("4"); // 4 pasos para h=0.25
    }

    @FXML
    private void onMostrarExpansion() {
        if (txtFuncion.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar una función para ver su expansión");
            return;
        }

        try {
            String funcion = txtFuncion.getText().trim();
            String expansion = MetodoTaylor.obtenerExpansionTaylor(funcion);

            // Crear diálogo con un TextArea para mostrar la expansión
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Expansión de Taylor");
            alert.setHeaderText("Serie de Taylor para dy/dt = " + funcion);

            // Crear un TextArea para el contenido
            TextArea textArea = new TextArea(expansion);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            // Asegurarse de que el TextArea sea ajustable
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            // Crear un GridPane para contener el TextArea
            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 0);
            expContent.setPadding(new Insets(10, 10, 10, 10));

            // Establecer el contenido del diálogo
            alert.getDialogPane().setContent(expContent);
            alert.getDialogPane().setMinHeight(300);
            alert.getDialogPane().setMinWidth(500);

            alert.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar la expansión: " + e.getMessage());
        }
    }
}
