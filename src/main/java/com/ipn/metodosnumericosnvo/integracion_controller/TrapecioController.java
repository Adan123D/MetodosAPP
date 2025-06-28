package com.ipn.metodosnumericosnvo.integracion_controller;

import com.ipn.metodosnumericosnvo.metodos_integracion.Trapecio;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TrapecioController {
    @FXML private TextField funcTxt;
    @FXML private TextField lowerTxt;
    @FXML private TextField upperTxt;
    @FXML private TextField subsTxt;
    @FXML private Label resultLbl;

    private final Trapecio model = new Trapecio();

    /**
     * Sets the function text in the function text field.
     * This method is used to pass a function from the main menu to this controller.
     * 
     * @param funcion The function text to set
     */
    public void setFuncion(String funcion) {
        if (funcTxt != null) {
            funcTxt.setText(funcion);
        }
    }

    @FXML
    private void onIntegrar() {
        try {
            String fx = funcTxt.getText().trim();
            double a = Double.parseDouble(lowerTxt.getText().trim());
            double b = Double.parseDouble(upperTxt.getText().trim());
            int n = Integer.parseInt(subsTxt.getText().trim());
            double res = model.integrar(fx, a, b, n);
            resultLbl.setText(String.format("Resultado: %.8f", res));
        } catch (NumberFormatException e) {
            resultLbl.setText("Error: valores numéricos inválidos.");
        } catch (IllegalArgumentException e) {
            resultLbl.setText("Error: " + e.getMessage());
        }
    }
}
