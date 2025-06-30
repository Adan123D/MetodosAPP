package com.ipn.metodosnumericosnvo.diferenciales_controller;

import com.ipn.metodosnumericosnvo.metodo_diferenciales.MetodoSEDO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class SEDOController {

    @FXML private TextField txtT0;
    @FXML private TextField txtU1;
    @FXML private TextField txtU2;
    @FXML private TextField txtTf;
    @FXML private TextField txtN;
    @FXML private TextField txtFuncion1;
    @FXML private TextField txtFuncion2;

    @FXML private TableView<MetodoSEDO.Iteracion> tabla;
    @FXML private TableColumn<MetodoSEDO.Iteracion, Integer> colPaso;
    @FXML private TableColumn<MetodoSEDO.Iteracion, Double> colT;
    @FXML private TableColumn<MetodoSEDO.Iteracion, Double> colU1;
    @FXML private TableColumn<MetodoSEDO.Iteracion, Double> colU2;

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPaso()).asObject());
        colT.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getT()).asObject());
        colU1.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getU1()).asObject());
        colU2.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getU2()).asObject());
    }

    @FXML
    private void onEjecutar() {
        try {
            double t0 = Double.parseDouble(txtT0.getText());
            double u1 = Double.parseDouble(txtU1.getText());
            double u2 = Double.parseDouble(txtU2.getText());
            double tf = Double.parseDouble(txtTf.getText());
            int n = Integer.parseInt(txtN.getText());
            String f1 = txtFuncion1.getText();
            String f2 = txtFuncion2.getText();

            List<MetodoSEDO.Iteracion> resultado = MetodoSEDO.resolver(t0, u1, u2, tf, n, f1, f2);
            tabla.setItems(FXCollections.observableArrayList(resultado));
        } catch (Exception e) {
            mostrarAlerta("Error", "Verifica tus entradas:\n" + e.getMessage());
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
