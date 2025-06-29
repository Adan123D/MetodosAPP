package com.ipn.metodosnumericosnvo.controller;

import com.ipn.metodosnumericosnvo.animation.SecanteAnimacionFX;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    private final Secante modelo = new Secante();

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(new PropertyValueFactory<>("paso"));
        colX0.setCellValueFactory(new PropertyValueFactory<>("x0"));
        colX1.setCellValueFactory(new PropertyValueFactory<>("x1"));
        colX2.setCellValueFactory(new PropertyValueFactory<>("x2"));
        colFx2.setCellValueFactory(new PropertyValueFactory<>("fx2"));
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
            resultadoLabel.setText(String.format("La raíz es: %.8f", raiz));
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
                modelo.resolver(funcion, x0, x1, tol, maxIt, pasos);
                tablaPasos.setItems(FXCollections.observableArrayList(pasos));
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
