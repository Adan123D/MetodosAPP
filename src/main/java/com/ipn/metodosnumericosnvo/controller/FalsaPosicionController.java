package com.ipn.metodosnumericosnvo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;

import java.util.ArrayList;
import java.util.List;

import com.ipn.metodosnumericosnvo.metodos_raices.FalsaPosicion;

public class FalsaPosicionController {

    @FXML private TextField fxField, x0Field, x1Field, tolField;
    @FXML private TableView<FalsaPosicion.Step> tablaPasos;
    @FXML private TableColumn<FalsaPosicion.Step, Integer> colPaso;
    @FXML private TableColumn<FalsaPosicion.Step, Double> colX0, colX1, colX2, colFx2;
    @FXML private Label resultadoLabel;

    /**
     * Sets the function text in the function field.
     * @param function The function text to set
     */
    public void setFuncion(String function) {
        if (fxField != null) {
            fxField.setText(function);
        }
    }

    private final FalsaPosicion modelo = new FalsaPosicion();

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().paso).asObject());
        colX0.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().x0).asObject());
        colX1.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().x1).asObject());
        colX2.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().x2).asObject());
        colFx2.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().fx2).asObject());
    }

    @FXML
    public void onCalcular() {
        try {
            String expr = fxField.getText();
            double x0 = Double.parseDouble(x0Field.getText());
            double x1 = Double.parseDouble(x1Field.getText());
            double tol = Double.parseDouble(tolField.getText());

            List<FalsaPosicion.Step> pasos = new ArrayList<>();
            double raiz = modelo.resolver(expr, x0, x1, tol, pasos);

            tablaPasos.setItems(FXCollections.observableArrayList(pasos));
            resultadoLabel.setText(String.format("La raíz es: %.6f", raiz));
        } catch (NumberFormatException e) {
            resultadoLabel.setText("Verifica que los números estén bien escritos.");
        } catch (IllegalArgumentException e) {
            resultadoLabel.setText(e.getMessage());
        } catch (Exception e) {
            resultadoLabel.setText("Error: " + e.getMessage());
        }
    }
}
