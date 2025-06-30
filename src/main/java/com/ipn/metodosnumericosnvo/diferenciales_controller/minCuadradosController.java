package com.ipn.metodosnumericosnvo.diferenciales_controller;

import com.ipn.metodosnumericosnvo.metodo_diferenciales.minCuadrados;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.ipn.metodosnumericosnvo.eliminacion_controller.EliminacionGaussController;

import java.io.IOException;

public class minCuadradosController {

    @FXML private TextField gradoField;
    @FXML private TextField xField;
    @FXML private TextField yField;
    @FXML private TableView<minCuadrados.Coeficientes> tablaCoeficientes;
    @FXML private TableColumn<minCuadrados.Coeficientes, String> colCoeficientes;
    @FXML private Button calcularBtn;
    @FXML private Label resultLabel;

    @FXML
    public void initialize() {
        colCoeficientes.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                "a" + data.getValue().grado + ": " + data.getValue().coef[0]
        ));
    }

    // Variable para almacenar la matriz del sistema
    private double[][] matrizSistema;

    @FXML
    private void onCalcular() {
        // Validar que el campo de grado no esté vacío
        if (gradoField.getText() == null || gradoField.getText().trim().isEmpty()) {
            mostrarError("El campo 'Grado del polinomio' no puede estar vacío.");
            return;
        }

        // Validar que el campo X no esté vacío
        if (xField.getText() == null || xField.getText().trim().isEmpty()) {
            mostrarError("El campo 'X' no puede estar vacío.");
            return;
        }

        // Validar que el campo Y no esté vacío
        if (yField.getText() == null || yField.getText().trim().isEmpty()) {
            mostrarError("El campo 'Y' no puede estar vacío.");
            return;
        }

        try {
            int grado = Integer.parseInt(gradoField.getText().trim());

            String[] xText = xField.getText().split(",");
            String[] yText = yField.getText().split(",");

            // Validar que los arreglos X e Y tengan la misma longitud
            if (xText.length != yText.length) {
                mostrarError("Los campos X e Y deben tener la misma cantidad de valores.");
                return;
            }

            // Validar que haya suficientes puntos para el grado del polinomio
            if (xText.length <= grado) {
                mostrarError("Se necesitan al menos " + (grado + 1) + " puntos para un polinomio de grado " + grado + ".");
                return;
            }

            double[] X = new double[xText.length];
            double[] Y = new double[yText.length];

            try {
                for (int i = 0; i < xText.length; i++) {
                    X[i] = Double.parseDouble(xText[i].trim());
                    Y[i] = Double.parseDouble(yText[i].trim());
                }
            } catch (NumberFormatException e) {
                mostrarError("Los valores de X e Y deben ser números válidos.");
                return;
            }

            minCuadrados.Coeficientes coeficientes = minCuadrados.obtenerCoeficientes(grado, X, Y);

            // Guardar la matriz del sistema para usarla en el método de eliminación gaussiana
            matrizSistema = minCuadrados.getMatrizSistema();

            ObservableList<minCuadrados.Coeficientes> datos = FXCollections.observableArrayList(coeficientes);
            tablaCoeficientes.setItems(datos);

            // Mostrar resultados
            resultLabel.setText("Coeficientes: " + coeficientes.coef[0]); // Solo mostrando el primer coeficiente por ejemplo
        } catch (NumberFormatException e) {
            mostrarError("El grado del polinomio debe ser un número entero válido.");
        } catch (Exception e) {
            mostrarError("Error al calcular: " + e.getMessage());
        }
    }

    @FXML
    private void onResolverSistema() {
        if (matrizSistema == null) {
            mostrarError("Primero debe calcular los coeficientes para generar el sistema de ecuaciones.");
            return;
        }

        try {
            // Cargar el archivo FXML de Eliminación Gaussiana
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/EliminacionGauss.fxml"));

            if (loader.getLocation() == null) {
                mostrarError("No se pudo encontrar el archivo EliminacionGauss.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador
            EliminacionGaussController controller = loader.getController();

            // Crear y mostrar la nueva ventana
            Stage eliminacionGaussStage = new Stage();
            eliminacionGaussStage.setTitle("Eliminación Gaussiana - Sistema de Mínimos Cuadrados");
            eliminacionGaussStage.setScene(new Scene(root, 800, 600));
            eliminacionGaussStage.initModality(Modality.NONE);
            eliminacionGaussStage.show();

            // Configurar la matriz en el controlador
            // Nota: Esto depende de cómo esté implementado el controlador de Eliminación Gaussiana
            // Puede ser necesario ajustar esta parte según la implementación específica
            controller.setMatriz(matrizSistema);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir la ventana de Eliminación Gaussiana: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
