package com.ipn.metodosnumericosnvo.controller;

import com.ipn.metodosnumericosnvo.metodos_raices.Biseccion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BiseccionController {

    @FXML private TextField funcionField;
    @FXML private TextField x0Field;
    @FXML private TextField x1Field;
    @FXML private TextField tolField;
    @FXML private TableView<Biseccion.Iteracion> tablaIteraciones;
    @FXML private TableColumn<Biseccion.Iteracion, Integer> colIteracion;
    @FXML private TableColumn<Biseccion.Iteracion, Double> colX0, colX1, colX2;
    @FXML private TableColumn<Biseccion.Iteracion, Double> colFx0, colFx1, colFx2, colError;
    @FXML private Button calcularBtn;
    @FXML private Label raizLabel;

    /**
     * Sets the function text in the function field.
     * @param function The function text to set
     */
    public void setFuncion(String function) {
        if (funcionField != null) {
            funcionField.setText(function);
        }
    }

    @FXML
    public void initialize() {
        colIteracion.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().iteracion).asObject());
        colX0.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().x0).asObject());
        colX1.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().x1).asObject());
        colX2.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().x2).asObject());
        colFx0.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().fx0).asObject());
        colFx1.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().fx1).asObject());
        colFx2.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().fx2).asObject());
        colError.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().error).asObject());
    }

    @FXML
    private void onCalcular() {
        String funcion = funcionField.getText();
        double x0 = Double.parseDouble(x0Field.getText());
        double x1 = Double.parseDouble(x1Field.getText());
        double tol = Double.parseDouble(tolField.getText());

        // Obtener las iteraciones
        java.util.List<Biseccion.Iteracion> iteraciones = Biseccion.resolver(funcion, x0, x1, tol);
        ObservableList<Biseccion.Iteracion> datos = FXCollections.observableArrayList(iteraciones);
        tablaIteraciones.setItems(datos);

        // Mostrar la raíz encontrada
        double raiz = Biseccion.obtenerRaiz(iteraciones);
        raizLabel.setText(String.format("%.10f", raiz));
    }
}
