package com.ipn.metodosnumericosnvo.derivacion_controller;

import com.ipn.metodosnumericosnvo.metodo_derivacion.NumericalDifferentiationModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;

import java.util.function.DoubleFunction;

public class NumericalDifferentiationController {

    @FXML private TextField functionField;
    @FXML private TextField xValueField;
    @FXML private TextField hValueField;
    @FXML private ComboBox<String> pointsComboBox;
    @FXML private ComboBox<String> directionComboBox;
    @FXML private Label resultLabel;

    @FXML
    private void initialize() {
        // Inicialización de los ComboBox
        if (pointsComboBox != null) {
            pointsComboBox.getItems().addAll("2", "3", "4", "5");
            pointsComboBox.setValue("2");
        }
        
        if (directionComboBox != null) {
            directionComboBox.getItems().addAll("Adelante", "Atrás", "Centro");
            directionComboBox.setValue("Centro");
        }
    }

    @FXML
    private void calculateDerivative() {
        try {
            String functionText = functionField.getText();
            double x = Double.parseDouble(xValueField.getText());
            double h = Double.parseDouble(hValueField.getText());
            String direction = directionComboBox.getValue();

            // Convertir la función ingresada a un objeto Function
            java.util.function.Function<Double, Double> f = createFunction(functionText);

            double result = 0.0;
            switch (direction) {
                case "Adelante":
                    result = NumericalDifferentiationModel.forwardDifference(f, x, h);
                    break;
                case "Atrás":
                    result = NumericalDifferentiationModel.backwardDifference(f, x, h);
                    break;
                case "Centro":
                    result = NumericalDifferentiationModel.centralDifference(f, x, h);
                    break;
            }

            resultLabel.setText("Resultado: " + result);
        } catch (Exception e) {
            resultLabel.setText("Error en los datos ingresados: " + e.getMessage());
        }
    }

    private java.util.function.Function<Double, Double> createFunction(String functionText) {
        // Utilizamos mXparser para evaluar la función
        return x -> {
            String expression = functionText.replace("x", String.valueOf(x));
            Expression exp = new Expression(expression);
            return exp.calculate();
        };
    }
}