package com.ipn.metodosnumericosnvo.integracion_controller;

import com.ipn.metodosnumericosnvo.metodos_integracion.Romberg;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;

public class RombergController {
    @FXML private TextField funcTxt, lowerTxt, upperTxt, iterTxt, errorTxt;
    @FXML private Label resultLbl;
    @FXML private TableView<ObservableList<String>> tableRomberg;

    private final Romberg model = new Romberg();

    /**
     * Sets the function text in the function text field.
     * This method is used to pass a function from the main menu to this controller.
     * 
     * @param funcion The function text to set
     */
    public void setFuncion(String funcion) {
        if (funcTxt != null) {
            funcTxt.setText(funcion);
        }
    }

    @FXML
    private void onCalcular() {
        try {
            String fx = funcTxt.getText().trim();
            double a = Double.parseDouble(lowerTxt.getText().trim());
            double b = Double.parseDouble(upperTxt.getText().trim());
            int n = Integer.parseInt(iterTxt.getText().trim());
            double tol = Double.parseDouble(errorTxt.getText().trim());

            double[][] R = new double[n][n];
            double result = model.calcularRomberg(fx, a, b, n, tol, R);
            resultLbl.setText(String.format("Resultado: %.10f", result));
            mostrarTabla(R, n);
        } catch (NumberFormatException e) {
            resultLbl.setText("Error: valores numéricos inválidos.");
        } catch (Exception e) {
            resultLbl.setText("Error: " + e.getMessage());
        }
    }

    private void mostrarTabla(double[][] R, int n) {
        tableRomberg.getColumns().clear();
        tableRomberg.getItems().clear();

        TableColumn<ObservableList<String>, String> col0 = new TableColumn<>("i");
        final int idx0 = 0;
        col0.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(idx0)));
        tableRomberg.getColumns().add(col0);

        for (int j = 0; j < n; j++) {
            TableColumn<ObservableList<String>, String> col = new TableColumn<>("R[" + j + "]");
            final int colIdx = j + 1;
            col.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(colIdx)));
            tableRomberg.getColumns().add(col);
        }

        for (int i = 0; i < n; i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(String.valueOf(i));
            for (int j = 0; j < n; j++) {
                row.add(String.format("%.10f", R[i][j]));
            }
            tableRomberg.getItems().add(row);
        }
    }
}