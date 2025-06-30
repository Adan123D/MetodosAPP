package com.ipn.metodosnumericosnvo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import com.ipn.metodosnumericosnvo.metodos_raices.Muller;
import org.apache.commons.math3.complex.Complex;

public class MullerController {

    @FXML private TextField fxField, x1Field, x2Field, x3Field, tolField, maxItField;
    @FXML private TableView<Muller.Step> tablaPasos;
    @FXML private TableColumn<Muller.Step, Integer> colPaso;
    @FXML private TableColumn<Muller.Step, Double> colX1, colX2, colX3, colX4, colFx1, colFx2, colFx3, colFx4, colA, colB, colC;
    @FXML private Label resultadoLabel;
    @FXML private VBox rootPane;

    private final Muller modelo = new Muller();

    @FXML
    public void initialize() {
        colPaso.setCellValueFactory(new PropertyValueFactory<>("paso"));
        colX1.setCellValueFactory(new PropertyValueFactory<>("x1"));
        colX2.setCellValueFactory(new PropertyValueFactory<>("x2"));
        colX3.setCellValueFactory(new PropertyValueFactory<>("x3"));
        colX4.setCellValueFactory(new PropertyValueFactory<>("x4"));
        colFx1.setCellValueFactory(new PropertyValueFactory<>("fx1"));
        colFx2.setCellValueFactory(new PropertyValueFactory<>("fx2"));
        colFx3.setCellValueFactory(new PropertyValueFactory<>("fx3"));
        colFx4.setCellValueFactory(new PropertyValueFactory<>("fx4"));
        colA.setCellValueFactory(new PropertyValueFactory<>("a"));
        colB.setCellValueFactory(new PropertyValueFactory<>("b"));
        colC.setCellValueFactory(new PropertyValueFactory<>("c"));

        // Valores predeterminados para polinomios de alto grado
        tolField.setText("1.0E-10");
        maxItField.setText("200");

        // Para polinomios complejos, usar valores iniciales diferentes
        x1Field.setText("1");
        x2Field.setText("0.5");
        x3Field.setText("0");

        // Establecer la imagen de fondo
        try {
            Image backgroundImage = new Image(getClass().getResourceAsStream("/imgs/background.png"));

            BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );

            rootPane.setBackground(new Background(background));
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de fondo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets the function to be evaluated.
     * This method is called when a function is passed from the main menu.
     * 
     * @param funcion The function to be evaluated
     */
    public void setFuncion(String funcion) {
        if (funcion != null && !funcion.isEmpty() && fxField != null) {
            fxField.setText(funcion);

            // Si detectamos un polinomio de alto grado, ajustar la tolerancia
            if (funcion.matches(".*x\\^[5-9].*")) {
                tolField.setText("1.0E-12");
                maxItField.setText("500");
            }
        }
    }

    @FXML
    public void onEncontrarTodasLasRaices() {
        try {
            String fx = fxField.getText();
            if (fx == null || fx.trim().isEmpty()) {
                resultadoLabel.setText("Debe ingresar una función");
                return;
            }

            // Usar tolerancia y máximas iteraciones ingresadas
            double tol;
            int maxIt;

            try {
                tol = Double.parseDouble(tolField.getText());
                if (tol <= 0) {
                    resultadoLabel.setText("La tolerancia debe ser un número positivo");
                    return;
                }
            } catch (NumberFormatException e) {
                resultadoLabel.setText("La tolerancia debe ser un número válido");
                return;
            }

            try {
                maxIt = Integer.parseInt(maxItField.getText());
                if (maxIt <= 0) {
                    resultadoLabel.setText("El número máximo de iteraciones debe ser positivo");
                    return;
                }
            } catch (NumberFormatException e) {
                resultadoLabel.setText("El número máximo de iteraciones debe ser un entero válido");
                return;
            }

            // Mensaje de procesamiento
            resultadoLabel.setText("Buscando todas las raíces... (puede tardar un momento)");

            // Para polinomios de alto grado, usar una tolerancia más pequeña
            if (fx.matches(".*x\\^[5-9].*") && tol > 1e-10) {
                tol = 1e-12;
                tolField.setText(String.valueOf(tol));
            }

            // Usar la nueva función para encontrar todas las raíces
            List<Complex> raices = modelo.encontrarTodasLasRaices(fx, tol, maxIt);

            if (raices.isEmpty()) {
                resultadoLabel.setText("No se encontraron raíces. Intente con otros valores iniciales.");
                return;
            }

            // Mostrar las raíces encontradas
            StringBuilder mensaje = new StringBuilder("Raíces encontradas:\n");
            String formatoNumerico = (fx.matches(".*x\\^[5-9].*")) ? "%.12f" : "%.8f";

            for (int i = 0; i < raices.size(); i++) {
                Complex raiz = raices.get(i);
                Complex valorFuncion = modelo.evaluateComplexPublic(fx, raiz);

                // Formatear la raíz
                if (Math.abs(raiz.getImaginary()) < 1e-10) {
                    mensaje.append(String.format("Raíz %d: " + formatoNumerico, i+1, raiz.getReal()));
                } else {
                    String signo = raiz.getImaginary() < 0 ? "" : "+";
                    mensaje.append(String.format("Raíz %d: " + formatoNumerico + "%s" + formatoNumerico + "i", 
                                                i+1, raiz.getReal(), signo, Math.abs(raiz.getImaginary())));
                }

                // Agregar valor de la función en la raíz
                mensaje.append(String.format(" (f(raíz) = %.2e)", valorFuncion.abs()));

                if (i < raices.size() - 1) {
                    mensaje.append("\n");
                }
            }

            // Mostrar el resultado en un diálogo aparte para mejor visualización
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Raíces del polinomio");
            alert.setHeaderText("Se encontraron " + raices.size() + " raíces");

            // Usar TextArea para permitir selección y copiar el texto
            TextArea textArea = new TextArea(mensaje.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);

            alert.getDialogPane().setContent(textArea);
            alert.getDialogPane().setPrefSize(500, 400);
            alert.showAndWait();

            // Actualizar también la etiqueta principal
            resultadoLabel.setText("Se encontraron " + raices.size() + " raíces. Ver diálogo para detalles.");

        } catch (Exception e) {
            resultadoLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onCalcular() {
        try {
            String fx = fxField.getText();
            if (fx == null || fx.trim().isEmpty()) {
                resultadoLabel.setText("Debe ingresar una función");
                return;
            }

            // Validar puntos iniciales
            String x1 = x1Field.getText();
            String x2 = x2Field.getText();
            String x3 = x3Field.getText();

            if (x1 == null || x1.trim().isEmpty() ||
                x2 == null || x2.trim().isEmpty() ||
                x3 == null || x3.trim().isEmpty()) {
                resultadoLabel.setText("Debe ingresar los tres puntos iniciales");
                return;
            }

            // Validar parámetros numéricos
            double tol;
            int maxIt;

            try {
                tol = Double.parseDouble(tolField.getText());
                if (tol <= 0) {
                    resultadoLabel.setText("La tolerancia debe ser un número positivo");
                    return;
                }
            } catch (NumberFormatException e) {
                resultadoLabel.setText("La tolerancia debe ser un número válido");
                return;
            }

            try {
                maxIt = Integer.parseInt(maxItField.getText());
                if (maxIt <= 0) {
                    resultadoLabel.setText("El número máximo de iteraciones debe ser positivo");
                    return;
                }
            } catch (NumberFormatException e) {
                resultadoLabel.setText("El número máximo de iteraciones debe ser un entero válido");
                return;
            }

            // Para polinomios de alto grado, usar una tolerancia más pequeña
            if (fx.matches(".*x\\^[5-9].*") && tol > 1e-10) {
                tol = 1e-12; // Ajustar la tolerancia automáticamente
                tolField.setText(String.valueOf(tol));
            }

            // Mostrar mensaje mientras calcula
            resultadoLabel.setText("Calculando...");

            List<Muller.Step> pasos = new ArrayList<>();
            Complex raiz = modelo.resolver(fx, x1, x2, x3, tol, maxIt, pasos);

            tablaPasos.setItems(FXCollections.observableArrayList(pasos));

            // Verificar la calidad de la raíz evaluando la función en ese punto
            Complex resultado = evaluarFuncionEnRaiz(fx, raiz);
            boolean esRaizPrecisa = resultado.abs() < tol * 10;

            // Mostrar la raíz con formato de mayor precisión para polinomios de alto grado
            String formatoNumerico = (fx.matches(".*x\\^[5-9].*")) ? "%.14f" : "%.8f";

            if (Math.abs(raiz.getImaginary()) < 1e-12) {
                // Si la parte imaginaria es prácticamente cero, mostrar solo la parte real
                String mensaje = String.format("Raíz encontrada: " + formatoNumerico, raiz.getReal());
                if (!esRaizPrecisa) {
                    mensaje += String.format(" (f(raíz) = %.2e)", resultado.abs());
                }
                resultadoLabel.setText(mensaje);
            } else {
                // Mostrar en formato a+bi o a-bi
                String signo = raiz.getImaginary() < 0 ? "" : "+";
                String mensaje = String.format("Raíz encontrada: " + formatoNumerico + "%s" + formatoNumerico + "i", 
                                               raiz.getReal(), signo, Math.abs(raiz.getImaginary()));
                if (!esRaizPrecisa) {
                    mensaje += String.format(" (f(raíz) = %.2e)", resultado.abs());
                }
                resultadoLabel.setText(mensaje);
            }
        } catch (NumberFormatException e) {
            resultadoLabel.setText("Verifica los números y la función: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            resultadoLabel.setText(e.getMessage());
        } catch (Exception e) {
            // Mostrar mensaje de error más detallado
            String mensaje = "Error: " + e.getMessage();
            if (e.getCause() != null) {
                mensaje += " (" + e.getCause().getMessage() + ")";
            }
            resultadoLabel.setText(mensaje);
            e.printStackTrace(); // Para depuración
        }
    }

    /**
     * Evalúa la función en la raíz encontrada para verificar su precisión
     */
    private Complex evaluarFuncionEnRaiz(String fx, Complex raiz) {
        try {
            // Usar el mismo método de evaluación que en Muller
            return modelo.evaluateComplexPublic(fx, raiz);
        } catch (Exception e) {
            return new Complex(Double.MAX_VALUE, 0); // Indicar error
        }
    }
}
