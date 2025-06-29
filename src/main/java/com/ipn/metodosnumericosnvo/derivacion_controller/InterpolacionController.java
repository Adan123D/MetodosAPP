package com.ipn.metodosnumericosnvo.derivacion_controller;

import com.ipn.metodosnumericosnvo.metodo_derivacion.Interpolacion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

public class InterpolacionController {
    @FXML private TextField funcTxt, x0Txt, nTxt, xTxt;
    @FXML private Label resultLbl;
    private final Interpolacion model = new Interpolacion();

    /**
     * Sets the function text in the function text field.
     * This method is called from the main menu when a function is entered there.
     * 
     * @param funcion The function text to set
     */
    public void setFuncion(String funcion) {
        if (funcTxt != null) {
            funcTxt.setText(funcion);
        }
    }

    /**
     * Valida los campos de entrada y muestra mensajes de error apropiados
     * 
     * @return true si todos los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {
        if (funcTxt.getText().trim().isEmpty()) {
            mostrarError("Debe ingresar una función");
            funcTxt.requestFocus();
            return false;
        }

        try {
            Double.parseDouble(x0Txt.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("El punto base x₀ debe ser un número válido");
            x0Txt.requestFocus();
            return false;
        }

        try {
            int n = Integer.parseInt(nTxt.getText().trim());
            if (n < 0) {
                mostrarError("El grado n debe ser un entero no negativo");
                nTxt.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El grado n debe ser un entero válido");
            nTxt.requestFocus();
            return false;
        }

        try {
            Double.parseDouble(xTxt.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("El punto x debe ser un número válido");
            xTxt.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Muestra un mensaje de error en el label de resultado
     * 
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarError(String mensaje) {
        resultLbl.setText("Error: " + mensaje);
        resultLbl.setTextFill(Color.RED);
    }

    @FXML
    private void onCalcular() {
        // Resetear el color del texto de resultado
        resultLbl.setTextFill(Color.BLACK);

        // Validar campos antes de procesar
        if (!validarCampos()) {
            return;
        }

        try {
            String fx = funcTxt.getText().trim();
            double x0 = Double.parseDouble(x0Txt.getText().trim());
            int n = Integer.parseInt(nTxt.getText().trim());
            double x = Double.parseDouble(xTxt.getText().trim());

            // Calcular el polinomio de Taylor
            double res = model.calcular(fx, x0, n, x);

            // Mostrar el resultado con formato científico para números muy grandes o muy pequeños
            if (Math.abs(res) > 1e6 || (Math.abs(res) < 1e-6 && res != 0)) {
                resultLbl.setText(String.format("Pₙ(%.4f) ≈ %e", x, res));
            } else {
                resultLbl.setText(String.format("Pₙ(%.4f) ≈ %.10f", x, res));
            }
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        } catch (ArithmeticException e) {
            mostrarError("Error en el cálculo: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }
}
