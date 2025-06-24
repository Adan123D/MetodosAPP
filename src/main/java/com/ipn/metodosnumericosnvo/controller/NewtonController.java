package com.ipn.metodosnumericosnvo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.ipn.metodosnumericosnvo.metodos_raices.Newton;

import java.util.ArrayList;
import java.util.List;

public class NewtonController {

    @FXML private TextField fxField, gxField, x0Field, tolField, maxItField;
    @FXML private TableView<Newton.Step> tablaPasos;
    @FXML private TableColumn<Newton.Step, Integer> colPaso;
    @FXML private TableColumn<Newton.Step, Double> colX0, colFx0, colX1, colFx1;
    @FXML private Label resultadoLabel;

    private final Newton modelo = new Newton();

    /**
     * Sets the function to be evaluated.
     * This method is called when a function is passed from the main menu.
     * 
     * @param funcion The function to be evaluated
     */
    public void setFuncion(String funcion) {
        if (funcion != null && !funcion.isEmpty()) {
            fxField.setText(funcion);
        }
    }

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(new PropertyValueFactory<>("paso"));
        colX0.setCellValueFactory(new PropertyValueFactory<>("x0"));
        colFx0.setCellValueFactory(new PropertyValueFactory<>("fx0"));
        colX1.setCellValueFactory(new PropertyValueFactory<>("x1"));
        colFx1.setCellValueFactory(new PropertyValueFactory<>("fx1"));
    }

    @FXML
    public void onCalcular() {
        try {
            String exprF = fxField.getText();
            String exprG = gxField.getText();
            double x0 = Double.parseDouble(x0Field.getText());
            double tol = Double.parseDouble(tolField.getText());
            int maxIt = Integer.parseInt(maxItField.getText());

            List<Newton.Step> pasos = new ArrayList<>();
            double raiz;

            // Si el campo de la derivada está vacío, calcular automáticamente
            if (exprG == null || exprG.trim().isEmpty()) {
                raiz = modelo.resolverConDerivadaAutomatica(exprF, x0, tol, maxIt, pasos);
                // Mostrar la derivada calculada en el campo
                String derivada = modelo.getDerivadaCalculada();
                if (derivada != null && !derivada.isEmpty()) {
                    gxField.setText(derivada);
                }
            } else {
                // Si se proporcionó una derivada, usar el método original
                raiz = modelo.resolver(exprF, exprG, x0, tol, maxIt, pasos);
            }

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
}
