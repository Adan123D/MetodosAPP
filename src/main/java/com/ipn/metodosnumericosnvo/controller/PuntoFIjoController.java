package com.ipn.metodosnumericosnvo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import java.util.List;
import com.ipn.metodosnumericosnvo.metodos_raices.PuntoFijo;

public class PuntoFIjoController {

    @FXML private TextField fxField, gxField, x0Field, tolField, maxItField;
    @FXML private TableView<PuntoFijo.Step> tablaPasos;
    @FXML private TableColumn<PuntoFijo.Step, Integer> colPaso;
    @FXML private TableColumn<PuntoFijo.Step, Double> colX0, colFx0, colX1, colFx1;
    @FXML private Label resultadoLabel;

    private final PuntoFijo modelo = new PuntoFijo();

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
            String fx = fxField.getText();
            String gx = gxField.getText();
            double x0 = Double.parseDouble(x0Field.getText());
            double tol = Double.parseDouble(tolField.getText());
            int maxIt = Integer.parseInt(maxItField.getText());

            List<PuntoFijo.Step> pasos = new ArrayList<>();
            double raiz = modelo.resolver(fx, gx, x0, tol, maxIt, pasos);

            tablaPasos.setItems(FXCollections.observableArrayList(pasos));
            resultadoLabel.setText(String.format("La raíz es: %.8f", raiz));
        } catch (NumberFormatException e) {
            resultadoLabel.setText("Verifica que los números estén bien escritos.");
        } catch (IllegalArgumentException e) {
            resultadoLabel.setText(e.getMessage());
        } catch (Exception e) {
            resultadoLabel.setText("Error: " + e.getMessage());
        }
    }

    public void setFuncion(String funcion) {
        if (fxField != null) {
            fxField.setText(funcion);
        }
    }
}
