package com.ipn.metodosnumericosnvo.integracion_controller;

import com.ipn.metodosnumericosnvo.metodos_integracion.CuadraturaAdaptativa;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CuadraturaAdaptativaController {

    @FXML private TextField txtFuncion;
    @FXML private TextField txtA;
    @FXML private TextField txtB;
    @FXML private TextField txtTol;
    @FXML private TextField txtMaxRec;
    @FXML private TableView<CuadraturaAdaptativa.Iteracion> tablaIteraciones;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Integer> colNivel;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Double> colA;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Double> colB;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Double> colS;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Double> colS2;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Double> colError;
    @FXML private Label lblResultado;

    @FXML
    public void initialize() {
        colNivel.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getNivel()).asObject());
        colA.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getA()).asObject());
        colB.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getB()).asObject());
        colS.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getS()).asObject());
        colS2.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getS2()).asObject());
        colError.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getError()).asObject());
    }

    @FXML
    private void onCalcular() {
        try {
            String funcion = txtFuncion.getText();
            if (funcion == null || funcion.trim().isEmpty()) {
                mostrarAlerta("Error", "Debe ingresar una función válida");
                return;
            }

            double a, b, tol;
            int maxRec;

            try {
                a = Double.parseDouble(txtA.getText());
                b = Double.parseDouble(txtB.getText());
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Los límites de integración deben ser valores numéricos válidos");
                return;
            }

            if (a >= b) {
                mostrarAlerta("Error", "El límite inferior (a) debe ser menor que el límite superior (b)");
                return;
            }

            try {
                tol = Double.parseDouble(txtTol.getText());
                if (tol <= 0) {
                    mostrarAlerta("Error", "La tolerancia debe ser un valor positivo");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "La tolerancia debe ser un valor numérico válido");
                return;
            }

            try {
                maxRec = Integer.parseInt(txtMaxRec.getText());
                if (maxRec <= 0) {
                    mostrarAlerta("Error", "El número máximo de recursiones debe ser un valor positivo");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "El número máximo de recursiones debe ser un valor entero válido");
                return;
            }

            // Verificar si la función tiene singularidades potenciales
            if ((funcion.contains("x^(-") || funcion.contains("1/x") || 
                 funcion.contains("1/(x") || funcion.contains("/x")) && 
                (a <= 0 && b >= 0)) {
                mostrarAdvertencia("Advertencia", "La función puede tener una singularidad en x=0 que está dentro del intervalo de integración. " +
                                  "Se intentará calcular la integral dividiendo el intervalo, pero el resultado puede no ser preciso.");
            }

            List<CuadraturaAdaptativa.Iteracion> pasos = new ArrayList<>();

            try {
                double resultado = CuadraturaAdaptativa.resolver(funcion, a, b, tol, maxRec, pasos);
                tablaIteraciones.setItems(FXCollections.observableArrayList(pasos));
                lblResultado.setText(String.format("Resultado: %.10f", resultado));
            } catch (Exception e) {
                Throwable cause = e;
                while (cause.getCause() != null) {
                    cause = cause.getCause();
                }
                mostrarAlerta("Error de cálculo", "Ocurrió un error durante el cálculo de la integral:\n" + cause.getMessage());
                // Limpiar la tabla de iteraciones si hubo un error
                tablaIteraciones.setItems(FXCollections.observableArrayList(pasos));
                lblResultado.setText("Error: No se pudo calcular el resultado");
            }
        } catch (Exception e) {
            mostrarAlerta("Error inesperado", "Ocurrió un error inesperado:\n" + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @FXML
    protected void onHomeButtonClick() {
        try {
            // Obtener la ventana actual
            Stage currentStage = (Stage) txtFuncion.getScene().getWindow();

            // Cargar el archivo FXML del menú principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ipn/metodosnumericosnvo/Menu.fxml"));
            Parent root = loader.load();

            // Crear y mostrar la nueva ventana
            Stage menuStage = new Stage();
            menuStage.setTitle("Métodos Numéricos");
            menuStage.setScene(new Scene(root, 1000, 660));
            menuStage.show();

            // Cerrar la ventana actual
            currentStage.close();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir el menú principal: " + e.getMessage());
        }
    }

    @FXML
    protected void onExitButtonClick() {
        Platform.exit();
    }
}
