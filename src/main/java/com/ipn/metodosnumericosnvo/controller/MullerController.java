package com.ipn.metodosnumericosnvo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import java.util.List;
import com.ipn.metodosnumericosnvo.metodos_raices.Muller;
import org.apache.commons.math3.complex.Complex;

public class MullerController {

    @FXML private TextField fxField, x1Field, x2Field, x3Field, tolField, maxItField;
    @FXML private TableView<Muller.Step> tablaPasos;
    @FXML private TableColumn<Muller.Step, Integer> colPaso;
    @FXML private TableColumn<Muller.Step, Double> colX1, colX2, colX3, colX4, colFx1, colFx2, colFx3, colFx4, colA, colB, colC;
    @FXML private Label resultadoLabel;

    private final Muller modelo = new Muller();

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(new PropertyValueFactory<>("paso"));
        colX1.setCellValueFactory(new PropertyValueFactory<>("x1"));
        colX2.setCellValueFactory(new PropertyValueFactory<>("x2"));
        colX3.setCellValueFactory(new PropertyValueFactory<>("x3"));
        colX4.setCellValueFactory(new PropertyValueFactory<>("x4"));
        colFx1.setCellValueFactory(new PropertyValueFactory<>("fx1"));
        colFx2.setCellValueFactory(new PropertyValueFactory<>("fx2"));
        colFx3.setCellValueFactory(new PropertyValueFactory<>("fx3"));
        colFx4.setCellValueFactory(new PropertyValueFactory<>("fx4"));
        colA.setCellValueFactory(new PropertyValueFactory<>("a"));
        colB.setCellValueFactory(new PropertyValueFactory<>("b"));
        colC.setCellValueFactory(new PropertyValueFactory<>("c"));

        // Valores predeterminados
        tolField.setText("0.0001");
        maxItField.setText("100");
    }

    /**
     * Sets the function to be evaluated.
     * This method is called when a function is passed from the main menu.
     * 
     * @param funcion The function to be evaluated
     */
    public void setFuncion(String funcion) {
        if (funcion != null && !funcion.isEmpty() && fxField != null) {
            fxField.setText(funcion);
        }
    }

    @FXML
    public void onCalcular() {
        try {
            String fx = fxField.getText();
            String x1 = x1Field.getText();
            String x2 = x2Field.getText();
            String x3 = x3Field.getText();
            double tol = Double.parseDouble(tolField.getText());
            int maxIt = Integer.parseInt(maxItField.getText());

            List<Muller.Step> pasos = new ArrayList<>();
            Complex raiz = modelo.resolver(fx, x1, x2, x3, tol, maxIt, pasos);

            tablaPasos.setItems(FXCollections.observableArrayList(pasos));

            // Mostrar la raíz en formato complejo
            if (Math.abs(raiz.getImaginary()) < 1e-10) {
                // Si la parte imaginaria es prácticamente cero, mostrar solo la parte real
                resultadoLabel.setText(String.format("Raíz encontrada: %.8f", raiz.getReal()));
            } else {
                // Mostrar en formato a+bi o a-bi
                String signo = raiz.getImaginary() < 0 ? "" : "+";
                resultadoLabel.setText(String.format("Raíz encontrada: %.8f%s%.8fi", 
                                                   raiz.getReal(), signo, raiz.getImaginary()));
            }
        } catch (NumberFormatException e) {
            resultadoLabel.setText("Verifica los números y la función.");
        } catch (IllegalArgumentException e) {
            resultadoLabel.setText(e.getMessage());
        } catch (Exception e) {
            resultadoLabel.setText("Error: " + e.getMessage());
        }
    }
}
