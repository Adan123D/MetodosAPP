package com.ipn.metodosnumericosnvo.controller;

import com.ipn.metodosnumericosnvo.metodos_raices.Biseccion_Aitken;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import com.ipn.metodosnumericosnvo.metodos_raices.Biseccion_Aitken;

public class Biseccion_AitkenController {

    @FXML private TextField fxField, aField, bField, tolField, maxItField;
    @FXML private TableView<Biseccion_Aitken.Step> table;
    @FXML private TableColumn<Biseccion_Aitken.Step, Integer> colPaso;
    @FXML private TableColumn<Biseccion_Aitken.Step, Double> colA, colB, colC, colFc, colAitken;
    @FXML private Label resultLabel;

    private final Biseccion_Aitken model = new Biseccion_Aitken();

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(new PropertyValueFactory<>("paso"));
        colA.setCellValueFactory(new PropertyValueFactory<>("a"));
        colB.setCellValueFactory(new PropertyValueFactory<>("b"));
        colC.setCellValueFactory(new PropertyValueFactory<>("c"));
        colFc.setCellValueFactory(new PropertyValueFactory<>("fc"));
        colAitken.setCellValueFactory(new PropertyValueFactory<>("aitken"));

        // Valores predeterminados
        tolField.setText("0.0001");
        maxItField.setText("100");
    }

    /**
     * Establece la función a evaluar en el campo de texto.
     * @param funcion La función a evaluar
     */
    public void setFuncion(String funcion) {
        if (fxField != null) {
            fxField.setText(funcion);
        }
    }

    @FXML
    public void onCalcular() {
        try {
            String fx = fxField.getText();
            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());
            double tol = Double.parseDouble(tolField.getText());
            int maxIt = Integer.parseInt(maxItField.getText());

            var steps = new ArrayList<Biseccion_Aitken.Step>();
            double root = model.resolver(fx, a, b, tol, maxIt, steps);

            table.setItems(FXCollections.observableArrayList(steps));
            resultLabel.setText(String.format("Raíz acelerada: %.10f", root));
        } catch (Exception e) {
            resultLabel.setText("Error: " + e.getMessage());
        }
    }
}
