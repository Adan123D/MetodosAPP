package com.ipn.metodosnumericosnvo.diferenciales_controller;

import com.ipn.metodosnumericosnvo.metodo_diferenciales.MetodoRungeKutta3;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class RungeKutta3Controller {

    @FXML private TextField txtFuncion;
    @FXML private TextField txtT0;
    @FXML private TextField txtY0;
    @FXML private TextField txtTf;
    @FXML private TextField txtN;

    @FXML private TableView<MetodoRungeKutta3.Iteracion> tablaIteraciones;
    @FXML private TableColumn<MetodoRungeKutta3.Iteracion, Integer> colPaso;
    @FXML private TableColumn<MetodoRungeKutta3.Iteracion, Double> colT;
    @FXML private TableColumn<MetodoRungeKutta3.Iteracion, Double> colY;

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPaso()).asObject());
        colT.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getT()).asObject());
        colY.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getY()).asObject());
    }

    @FXML
    private void onEjecutar() {
        try {
            String funcion = txtFuncion.getText();
            double t0 = Double.parseDouble(txtT0.getText());
            double y0 = Double.parseDouble(txtY0.getText());
            double tf = Double.parseDouble(txtTf.getText());
            int n = Integer.parseInt(txtN.getText());

            List<MetodoRungeKutta3.Iteracion> pasos = MetodoRungeKutta3.resolver(t0, y0, tf, n, funcion);
            ObservableList<MetodoRungeKutta3.Iteracion> datos = FXCollections.observableArrayList(pasos);
            tablaIteraciones.setItems(datos);
        } catch (Exception e) {
            mostrarAlerta("Error", "Verifica los datos ingresados:\n" + e.getMessage());
        }
    }


    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
