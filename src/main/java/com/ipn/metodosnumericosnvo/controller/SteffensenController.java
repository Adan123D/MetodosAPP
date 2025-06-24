package com.ipn.metodosnumericosnvo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import java.util.List;

import com.ipn.metodosnumericosnvo.metodos_raices.Steffensen;

public class SteffensenController {

    @FXML private TextField fxField, x0Field, tolField, maxItField;
    @FXML private TableView<Steffensen.Step> table;
    @FXML private TableColumn<Steffensen.Step, Integer> colPaso;
    @FXML private TableColumn<Steffensen.Step, Double> colXn, colFxn, colFxnPlus, colXn1, colFxn1;
    @FXML private Label resultLabel;

    private final Steffensen model = new Steffensen();

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(new PropertyValueFactory<>("paso"));
        colXn.setCellValueFactory(new PropertyValueFactory<>("xn"));
        colFxn.setCellValueFactory(new PropertyValueFactory<>("fxn"));
        colFxnPlus.setCellValueFactory(new PropertyValueFactory<>("fxnPlus"));
        colXn1.setCellValueFactory(new PropertyValueFactory<>("xn1"));
        colFxn1.setCellValueFactory(new PropertyValueFactory<>("fxn1"));
    }

    @FXML
    public void onCalcular() {
        try {
            String fx = fxField.getText();
            double x0 = Double.parseDouble(x0Field.getText());
            double tol = Double.parseDouble(tolField.getText());
            int maxIt = Integer.parseInt(maxItField.getText());

            List<Steffensen.Step> steps = new ArrayList<>();
            double raiz = model.resolver(fx, x0, tol, maxIt, steps);

            table.setItems(FXCollections.observableArrayList(steps));
            resultLabel.setText(String.format("Raíz: %.8f", raiz));
        } catch (Exception e) {
            resultLabel.setText("Error: " + e.getMessage());
        }
    }

    public void setFuncion(String funcion) {
        if (fxField != null) {
            fxField.setText(funcion);
        }
    }
}
