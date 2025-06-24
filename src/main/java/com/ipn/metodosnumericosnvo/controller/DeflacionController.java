package com.ipn.metodosnumericosnvo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.math3.complex.Complex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ipn.metodosnumericosnvo.metodos_raices.Deflacion;

public class DeflacionController {

    @FXML private TextField polyField;
    @FXML private TableView<RaizTable> tablaRaices;
    @FXML private TableColumn<RaizTable, String> colReal, colImag, colCompleja;

    private final Deflacion modelo = new Deflacion();

    @FXML
    public void initialize() {
        colReal.setCellValueFactory(new PropertyValueFactory<>("real"));
        colImag.setCellValueFactory(new PropertyValueFactory<>("imag"));
        colCompleja.setCellValueFactory(new PropertyValueFactory<>("compleja"));
    }

    @FXML
    public void onCalcular() {
        try {
            String input = polyField.getText().replaceAll("\\s+", "");
            double[] coeffs = Arrays.stream(input.split(","))
                                    .mapToDouble(Double::parseDouble)
                                    .toArray();
            List<Complex> roots = modelo.findRoots(coeffs);
            List<RaizTable> rows = new ArrayList<>();
            for (Complex c : roots) {
                String a = String.format("%.6f", c.getReal());
                String b = String.format("%.6f", c.getImaginary());
                String str;
                if (Math.abs(c.getImaginary()) < 1e-10) {
                    str = a; // solo parte real
                } else if (c.getImaginary() >= 0) {
                    str = a + " + " + b + "i";
                } else {
                    str = a + " - " + b.substring(1) + "i";
                }
                rows.add(new RaizTable(a, b, str));
            }
            tablaRaices.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Verifica el formato: coef1,coef2,...,coefN\nError: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static class RaizTable {
        private final String real;
        private final String imag;
        private final String compleja;

        public RaizTable(String real, String imag, String compleja) {
            this.real = real;
            this.imag = imag;
            this.compleja = compleja;
        }

        public String getReal() { return real; }
        public String getImag() { return imag; }
        public String getCompleja() { return compleja; }
    }

    /**
     * Sets the initial function for the controller.
     * This method is called from the main menu when a function is already entered.
     *
     * @param function The function to set
     */
    public void setFuncion(String function) {
        if (polyField != null) {
            polyField.setText(function);
        }
    }
}
