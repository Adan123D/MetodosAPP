package com.ipn.metodosnumericosnvo.eliminacion_controller;

import com.ipn.metodosnumericosnvo.eliminacion.EliminacionGauss;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

public class EliminacionGaussController {

    @FXML private TextField txtIncognitas;
    @FXML private TableView<double[]> tablaMatriz;
    @FXML private TableView<double[]> tablaTriangular;
    @FXML private TableView<Double> tablaResultados;
    @FXML private RadioButton radioGauss, radioPivoteo, radioEscalado;

    private int n;

    @FXML
    public void generarMatriz() {
        tablaMatriz.getColumns().clear();
        n = Integer.parseInt(txtIncognitas.getText());
        tablaMatriz.getItems().clear();

        for (int j = 0; j <= n; j++) {
            final int col = j;
            TableColumn<double[], Double> column = new TableColumn<>("x" + j);
            column.setCellValueFactory(param -> new javafx.beans.property.SimpleDoubleProperty(param.getValue()[col]).asObject());
            column.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            column.setOnEditCommit(event -> {
                double[] row = event.getRowValue();
                row[col] = event.getNewValue();
            });
            tablaMatriz.getColumns().add(column);
        }

        for (int i = 0; i < n; i++) {
            double[] row = new double[n + 1];
            tablaMatriz.getItems().add(row);
        }
    }

    @FXML
    public void calcular() {
        double[][] matriz = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            matriz[i] = tablaMatriz.getItems().get(i);
        }

        EliminacionGauss solver = new EliminacionGauss();
        double[][] triangular;
        double[] resultados;

        if (radioPivoteo.isSelected()) {
            triangular = solver.eliminacionGaussPivoteo(matriz);
        } else if (radioEscalado.isSelected()) {
            triangular = solver.eliminacionGaussEscalada(matriz);
        } else {
            triangular = solver.eliminacionGaussSimple(matriz);
        }

        resultados = solver.sustitucionRegresiva(triangular);

        mostrarTabla(tablaTriangular, triangular);
        mostrarResultados(resultados);
    }

    private void mostrarTabla(TableView<double[]> tabla, double[][] datos) {
        tabla.getColumns().clear();
        int cols = datos[0].length;
        for (int j = 0; j < cols; j++) {
            final int col = j;
            TableColumn<double[], Double> column = new TableColumn<>("Col " + j);
            column.setCellValueFactory(param -> new javafx.beans.property.SimpleDoubleProperty(param.getValue()[col]).asObject());
            tabla.getColumns().add(column);
        }
        tabla.setItems(FXCollections.observableArrayList(datos));
    }

    private void mostrarResultados(double[] resultados) {
        tablaResultados.getColumns().clear();
        TableColumn<Double, Double> col = new TableColumn<>("Resultado");
        col.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue()).asObject());
        tablaResultados.getColumns().add(col);
        tablaResultados.setItems(FXCollections.observableArrayList(
                java.util.Arrays.stream(resultados).boxed().toList()));
    }
}