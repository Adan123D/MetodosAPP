package com.ipn.metodosnumericosnvo.integracion_controller;

import com.ipn.metodosnumericosnvo.metodos_integracion.TrapecioCompuesto;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrapecioCompuestoController {

    @FXML private TextField txtA;
    @FXML private TextField txtB;
    @FXML private TextField txtN;
    @FXML private TableView<TrapecioCompuesto.Iteracion> tablaIteraciones;
    @FXML private TableColumn<TrapecioCompuesto.Iteracion, Integer> colI;
    @FXML private TableColumn<TrapecioCompuesto.Iteracion, Double> colXi;
    @FXML private TableColumn<TrapecioCompuesto.Iteracion, Double> colFxi;
    @FXML private Label lblResultado;

    @FXML
    public void initialize() {
        colI.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getI()).asObject());
        colXi.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getXi()).asObject());
        colFxi.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getFxi()).asObject());
    }

    @FXML
    private void onEjecutar() {
        try {
            double a = Double.parseDouble(txtA.getText());
            double b = Double.parseDouble(txtB.getText());
            int n = Integer.parseInt(txtN.getText());

            List<TrapecioCompuesto.Iteracion> pasos = new ArrayList<>();
            double resultado = TrapecioCompuesto.resolver(a, b, n, pasos);

            tablaIteraciones.setItems(FXCollections.observableArrayList(pasos));
            lblResultado.setText(String.format("Resultado: %.10f", resultado));
        } catch (Exception e) {
            mostrarAlerta("Error", "Verifica los datos ingresados:\n" + e.getMessage());
        }
    }

    @FXML
    protected void onHomeButtonClick() {
        try {
            // Obtener la ventana actual
            Stage currentStage = (Stage) txtA.getScene().getWindow();

            // Cargar el archivo FXML del menú principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ipn/metodosnumericosnvo/Menu.fxml"));
            Parent root = loader.load();

            // Crear y mostrar la nueva ventana
            Stage menuStage = new Stage();
            menuStage.setTitle("Métodos Numéricos");
            menuStage.setScene(new Scene(root, 1000, 660));
            menuStage.show();

            // Cerrar la ventana actual
            currentStage.close();
        } catch (IOException e) {
            mostrarAlerta("Error", "Error al abrir el menú principal: " + e.getMessage());
        }
    }

    @FXML
    protected void onExitButtonClick() {
        Platform.exit();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}