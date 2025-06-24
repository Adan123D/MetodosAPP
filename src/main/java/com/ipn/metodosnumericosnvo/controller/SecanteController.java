package com.ipn.metodosnumericosnvo.controller;

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
}
