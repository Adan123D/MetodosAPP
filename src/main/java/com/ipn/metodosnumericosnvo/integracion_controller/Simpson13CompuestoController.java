package com.ipn.metodosnumericosnvo.integracion_controller;

import com.ipn.metodosnumericosnvo.metodos_integracion.Simpson13Compuesto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class Simpson13CompuestoController {

    @FXML private TextField txtTamano;
    @FXML private TextField txtH;
    @FXML private TextArea txtValores;
    @FXML private TableView<Simpson13Compuesto.Iteracion> tabla;
    @FXML private TableColumn<Simpson13Compuesto.Iteracion, Integer> colI;
    @FXML private TableColumn<Simpson13Compuesto.Iteracion, Double> colFx;
    @FXML private Label lblResultado;

    @FXML
    public void initialize() {
        colI.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getI()).asObject());
        colFx.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getFx()).asObject());
    }

    @FXML
    private void onCalcular() {
        int n = Integer.parseInt(txtTamano.getText());
        double h = Double.parseDouble(txtH.getText());
        String[] entradas = txtValores.getText().split("\\s+|,");
        if (entradas.length != n) {
            lblResultado.setText("Cantidad de valores incorrecta.");
            return;
        }

        double[] f = new double[n];
        for (int i = 0; i < n; i++) {
            f[i] = Double.parseDouble(entradas[i]);
        }

        List<Simpson13Compuesto.Iteracion> pasos = new ArrayList<>();
        double resultado = Simpson13Compuesto.resolver(f, h, pasos);

        ObservableList<Simpson13Compuesto.Iteracion> datos = FXCollections.observableArrayList(pasos);
        tabla.setItems(datos);
        lblResultado.setText(String.format("Resultado: %.10f", resultado));
    }
    
    @FXML
    protected void onHomeButtonClick() {
        try {
            // Obtener la ventana actual
            Stage currentStage = (Stage) txtTamano.getScene().getWindow();

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