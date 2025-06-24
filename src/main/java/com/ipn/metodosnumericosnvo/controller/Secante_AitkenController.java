package com.ipn.metodosnumericosnvo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import com.ipn.metodosnumericosnvo.metodos_raices.Secante_Aitken;

import java.util.ArrayList;
import java.util.List;

public class Secante_AitkenController {
    @FXML private TextField txtFunc;
    @FXML private TextField txtX0;
    @FXML private TextField txtX1;
    @FXML private TextField txtTolerancia;
    @FXML private TextField txtMaxIter;
    @FXML private Label lblResultado;
    @FXML private Label lblError;

    // TableView y columnas para mostrar los pasos del método
    @FXML private TableView<Secante_Aitken.Step> tablaPasos;
    @FXML private TableColumn<Secante_Aitken.Step, Integer> colIteracion;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colXPrevPrev;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colFxPrevPrev;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colXPrev;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colFxPrev;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colXActual;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colFxActual;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colXAitken;
    @FXML private TableColumn<Secante_Aitken.Step, Double> colFxAitken;

    private final Secante_Aitken modelo = new Secante_Aitken();

    @FXML
    public void initialize() {
        // Inicializar las columnas de la tabla
        colIteracion.setCellValueFactory(new PropertyValueFactory<>("iteracion"));
        colXPrevPrev.setCellValueFactory(new PropertyValueFactory<>("xPrevPrev"));
        colFxPrevPrev.setCellValueFactory(new PropertyValueFactory<>("fxPrevPrev"));
        colXPrev.setCellValueFactory(new PropertyValueFactory<>("xPrev"));
        colFxPrev.setCellValueFactory(new PropertyValueFactory<>("fxPrev"));
        colXActual.setCellValueFactory(new PropertyValueFactory<>("xActual"));
        colFxActual.setCellValueFactory(new PropertyValueFactory<>("fxActual"));
        colXAitken.setCellValueFactory(new PropertyValueFactory<>("xAitken"));
        colFxAitken.setCellValueFactory(new PropertyValueFactory<>("fxAitken"));
    }

    // Método para establecer la función desde el menú principal
    public void setFuncion(String funcion) {
        if (txtFunc != null) {
            txtFunc.setText(funcion);
        }
    }

    @FXML
    private void calcularRaiz() {
        lblResultado.setText("");
        lblError.setText("");
        try {
            String funcionTxt = txtFunc.getText();
            if (funcionTxt == null || funcionTxt.isEmpty())
                throw new IllegalArgumentException("Introduce la función f(x).");

            double x0 = Double.parseDouble(txtX0.getText());
            double x1 = Double.parseDouble(txtX1.getText());
            double tolerancia = Double.parseDouble(txtTolerancia.getText());
            int maxIter = Integer.parseInt(txtMaxIter.getText());
            if (tolerancia <= 0) throw new IllegalArgumentException("La tolerancia debe ser positiva.");
            if (maxIter <= 0) throw new IllegalArgumentException("Las iteraciones deben ser mayor que 0.");

            Secante_Aitken.Funcion fx = val -> {
                Argument x = new Argument("x = " + val);
                Expression expr = new Expression(funcionTxt, x);
                double result = expr.calculate();
                if (Double.isNaN(result)) throw new Exception("La evaluación de la función no es válida para x=" + val);
                return result;
            };

            // Lista para almacenar los pasos
            List<Secante_Aitken.Step> pasos = new ArrayList<>();

            // Calcular la raíz y recopilar los pasos
            double raiz = modelo.calcularRaiz(fx, x0, x1, tolerancia, maxIter, pasos);

            // Mostrar los pasos en la tabla
            tablaPasos.setItems(FXCollections.observableArrayList(pasos));

            // Mostrar el resultado
            lblResultado.setText(String.format("Raíz aproximada: %.8f", raiz));
        } catch (NumberFormatException e) {
            lblError.setText("Entradas numéricas no válidas.");
        } catch (IllegalArgumentException e) {
            lblError.setText(e.getMessage());
        } catch (ArithmeticException e) {
            lblError.setText("Error numérico: " + e.getMessage());
        } catch (Exception e) {
            lblError.setText("Error: " + e.getMessage());
        }
    }
}
