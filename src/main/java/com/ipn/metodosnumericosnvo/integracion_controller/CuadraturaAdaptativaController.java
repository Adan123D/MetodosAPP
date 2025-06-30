package com.ipn.metodosnumericosnvo.integracion_controller;

import com.ipn.metodosnumericosnvo.metodos_integracion.CuadraturaAdaptativa;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CuadraturaAdaptativaController {

    @FXML private TextField txtFuncion;
    @FXML private TextField txtA;
    @FXML private TextField txtB;
    @FXML private TextField txtTol;
    @FXML private TextField txtMaxRec;
    @FXML private TableView<CuadraturaAdaptativa.Iteracion> tablaIteraciones;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Integer> colNivel;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Double> colA;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Double> colB;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Double> colS;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Double> colS2;
    @FXML private TableColumn<CuadraturaAdaptativa.Iteracion, Double> colError;
    @FXML private Label lblResultado;

    @FXML
    public void initialize() {
        colNivel.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getNivel()).asObject());
        colA.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getA()).asObject());
        colB.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getB()).asObject());
        colS.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getS()).asObject());
        colS2.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getS2()).asObject());
        colError.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getError()).asObject());
    }

    @FXML
    private void onCalcular() {
        try {
            String funcion = txtFuncion.getText();
            double a = Double.parseDouble(txtA.getText());
            double b = Double.parseDouble(txtB.getText());
            double tol = Double.parseDouble(txtTol.getText());
            int maxRec = Integer.parseInt(txtMaxRec.getText());

            List<CuadraturaAdaptativa.Iteracion> pasos = new ArrayList<>();
            double resultado = CuadraturaAdaptativa.resolver(funcion, a, b, tol, maxRec, pasos);

            tablaIteraciones.setItems(FXCollections.observableArrayList(pasos));
            lblResultado.setText(String.format("Resultado: %.10f", resultado));
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

    @FXML
    protected void onHomeButtonClick() {
        try {
            // Obtener la ventana actual
            Stage currentStage = (Stage) txtFuncion.getScene().getWindow();

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
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir el menú principal: " + e.getMessage());
        }
    }

    @FXML
    protected void onExitButtonClick() {
        Platform.exit();
    }
}
