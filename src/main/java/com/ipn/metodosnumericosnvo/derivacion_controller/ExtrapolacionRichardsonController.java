package com.ipn.metodosnumericosnvo.derivacion_controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.ipn.metodosnumericosnvo.metodo_derivacion.ExtrapolacionRichardson;

public class ExtrapolacionRichardsonController {
    @FXML private TextField funcionField;
    @FXML private TextField xField;
    @FXML private TextField hField;
    @FXML private TextArea resultadoLabel;

    private ExtrapolacionRichardson model = new ExtrapolacionRichardson();

    @FXML
    public void initialize() {
        // Establecer un valor por defecto para la función
        if (funcionField != null) {
            funcionField.setText("x*exp(x)");
        }
    }

    @FXML
    public void calcular() {
        try {
            String funcion = funcionField.getText().trim();
            if (funcion.isEmpty()) {
                resultadoLabel.setText("Por favor, ingrese una función.");
                return;
            }

            double x = Double.parseDouble(xField.getText());
            double h = Double.parseDouble(hField.getText());

            if (h <= 0) {
                resultadoLabel.setText("El valor de h debe ser positivo.");
                return;
            }

            // Verificar la función con una evaluación de prueba
            model.setFuncion(funcion);
            try {
                // Probar la evaluación de la función en el punto x
                double testValue = model.evaluarFuncion(x);
                if (Double.isNaN(testValue)) {
                    resultadoLabel.setText("Error: La función no se puede evaluar en x=" + x + ". Verifique la sintaxis.");
                    return;
                }
            } catch (Exception ex) {
                resultadoLabel.setText("Error al evaluar la función: " + ex.getMessage() + 
                                    "\nAsegúrese de usar la sintaxis correcta (ejemplo: x*exp(x), sin(x), etc).");
                return;
            }

            // Calcular las aproximaciones con diferente tamaño de paso
            double A_h = model.calcularDerivada(x, h);
            double A_h2 = model.calcularDerivada(x, h / 2);

            // Para diferencias centrales, el orden del error es 2
            double resultado = model.extrapolar(A_h, A_h2, 2);

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Función: %s\n\n", funcion));
            sb.append(String.format("Valor en x=%.4f: %.6f\n", x, model.evaluarFuncion(x)));
            sb.append(String.format("Derivada con h=%.4f: %.6f\n", h, A_h));
            sb.append(String.format("Derivada con h=%.4f: %.6f\n", h/2, A_h2));
            sb.append(String.format("\nExtrapolación de Richardson: %.6f", resultado));

            resultadoLabel.setText(sb.toString());
        } catch (NumberFormatException e) {
            resultadoLabel.setText("Por favor, ingrese valores numéricos válidos.");
        } catch (Exception e) {
            resultadoLabel.setText("Error al procesar la solicitud: " + e.getMessage() + 
                                "\n\nDetalles: " + e.toString());
        }
    }
}