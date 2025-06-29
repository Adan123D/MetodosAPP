package com.ipn.metodosnumericosnvo.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import java.util.List;
import com.ipn.metodosnumericosnvo.metodos_raices.PuntoFijo;

public class PuntoFIjoController {

    @FXML TextField gTxt, x0Txt, maxIterTxt, tolTxt;
    @FXML Label resultLbl;
    @FXML TableView<PuntoFijo.Iteration> tableView;
    @FXML TableColumn<PuntoFijo.Iteration, String> colIter, colXi, colGxi, colError;

    private final PuntoFijo model = new PuntoFijo();

    @FXML
    public void initialize() {
        colIter.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().i)));
        colXi.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%.6f", c.getValue().xi)));
        colGxi.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%.6f", c.getValue().gxi)));
        colError.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%.6f", c.getValue().error)));
    }

    /**
     * Sets the function to be evaluated.
     * This method is called when a function is passed from the main menu.
     * 
     * @param funcion The function to be evaluated
     */
    public void setFuncion(String funcion) {
        if (funcion != null && !funcion.isEmpty() && gTxt != null) {
            gTxt.setText(funcion);
        }
    }

    @FXML
    private void onCalcular() {
        try {
            // Validar entradas
            String g = gTxt.getText().trim();
            if (g.isEmpty()) {
                throw new IllegalArgumentException("La función g(x) no puede estar vacía");
            }

            double x0;
            try {
                x0 = Double.parseDouble(x0Txt.getText().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El valor inicial x₀ debe ser un número válido");
            }

            int maxIt;
            try {
                maxIt = Integer.parseInt(maxIterTxt.getText().trim());
                if (maxIt <= 0) {
                    throw new IllegalArgumentException("El número máximo de iteraciones debe ser positivo");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El número máximo de iteraciones debe ser un entero válido");
            }

            double tol;
            try {
                tol = Double.parseDouble(tolTxt.getText().trim());
                if (tol <= 0) {
                    throw new IllegalArgumentException("La tolerancia debe ser un valor positivo");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("La tolerancia debe ser un número válido");
            }

            // Ejecutar el método
            var lista = model.resolver(g, x0, maxIt, tol);
            if (lista.isEmpty()) {
                throw new RuntimeException("No se realizaron iteraciones.");
            }

            // Mostrar resultados
            tableView.setItems(FXCollections.observableArrayList(lista));
            var last = lista.get(lista.size() - 1);
            resultLbl.setText(String.format("Última aproximación: %.6f  (error=%.6f)", last.gxi, last.error));
        } catch (ArithmeticException e) {
            resultLbl.setText("Error de convergencia: " + e.getMessage());
            tableView.getItems().clear();
        } catch (IllegalArgumentException e) {
            resultLbl.setText("Error de entrada: " + e.getMessage());
            tableView.getItems().clear();
        } catch (Exception e) {
            resultLbl.setText("Error: " + e.getMessage());
            tableView.getItems().clear();
        }
    }
}
