package com.ipn.metodosnumericosnvo.controller;

import com.ipn.metodosnumericosnvo.animation.BiseccionAnimacionFX;
import com.ipn.metodosnumericosnvo.metodos_raices.Biseccion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
