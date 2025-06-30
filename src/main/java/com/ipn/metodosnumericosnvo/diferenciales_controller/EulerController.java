package com.ipn.metodosnumericosnvo.diferenciales_controller;

import com.ipn.metodosnumericosnvo.metodo_diferenciales.MetodoEuler;
import com.ipn.metodosnumericosnvo.metodo_diferenciales.MetodoEuler.Step;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EulerController {

    @FXML private TextField funcionField;
    @FXML private TextField t0Field;
    @FXML private TextField y0Field;
    @FXML private TextField tfField;
    @FXML private TextField nField;
    @FXML private Button calcularBtn;
    @FXML private TableView<Step> tabla;
    @FXML private TableColumn<Step, Integer> colPaso;
    @FXML private TableColumn<Step, Double> colT;
    @FXML private TableColumn<Step, Double> colY;

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(new PropertyValueFactory<>("paso"));
        colT.setCellValueFactory(new PropertyValueFactory<>("t"));
        colY.setCellValueFactory(new PropertyValueFactory<>("y"));
    }

    @FXML
    private void onCalcular() {
        try {
            String funcion = funcionField.getText();
            double t0 = Double.parseDouble(t0Field.getText());
            double y0 = Double.parseDouble(y0Field.getText());
            double tf = Double.parseDouble(tfField.getText());
            int n = Integer.parseInt(nField.getText());

            List<Step> pasos = new ArrayList<>();
            MetodoEuler metodo = new MetodoEuler();
            metodo.resolver(funcion, t0, y0, tf, n, pasos);

            ObservableList<Step> data = FXCollections.observableArrayList(pasos);
            tabla.setItems(data);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error en los datos ingresados: " + e.getMessage());
            alert.showAndWait();
        }
    }

}
