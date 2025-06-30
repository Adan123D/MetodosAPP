package com.ipn.metodosnumericosnvo.derivacion_controller;
import com.ipn.metodosnumericosnvo.metodo_derivacion.DerivacionNumerica;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class DerivacionNumericaController {

    @FXML private TextField funcionInput;
    @FXML private TextField aInput;
    @FXML private TextField bInput;
    @FXML private TextField nInput;
    @FXML private TableView<FilaDerivacion> tablaResultado;
    @FXML private TableColumn<FilaDerivacion, String> columnaX;
    @FXML private TableColumn<FilaDerivacion, String> columnaFx;
    @FXML private TableColumn<FilaDerivacion, String> columnaDerivada;

    @FXML
    public void initialize() {
        columnaX.setCellValueFactory(new PropertyValueFactory<>("x"));
        columnaFx.setCellValueFactory(new PropertyValueFactory<>("fx"));
        columnaDerivada.setCellValueFactory(new PropertyValueFactory<>("derivada"));
    }

    @FXML
    public void onCalcularClick() {
        try {
            String funcion = funcionInput.getText();
            double a = Double.parseDouble(aInput.getText());
            double b = Double.parseDouble(bInput.getText());
            int n = Integer.parseInt(nInput.getText());

            Object[][] datos = DerivacionNumerica.calcularDerivadas(funcion, a, b, n);
            ObservableList<FilaDerivacion> filas = FXCollections.observableArrayList();

            for (Object[] fila : datos) {
                filas.add(new FilaDerivacion((String) fila[0], (String) fila[1], (String) fila[2]));
            }

            tablaResultado.setItems(filas);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error en los datos ingresados.");
            alert.show();
        }
    }

    // Clase interna del modelo
    public static class FilaDerivacion {
        private final String x;
        private final String fx;
        private final String derivada;

        public FilaDerivacion(String x, String fx, String derivada) {
            this.x = x;
            this.fx = fx;
            this.derivada = derivada;
        }

        public String getX() { return x; }
        public String getFx() { return fx; }
        public String getDerivada() { return derivada;}
    }
}