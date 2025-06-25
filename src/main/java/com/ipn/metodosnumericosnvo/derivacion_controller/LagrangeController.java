package com.ipn.metodosnumericosnvo.derivacion_controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.ipn.metodosnumericosnvo.metodo_derivacion.Lagrange;
import com.ipn.metodosnumericosnvo.modelo.PuntoInterpolado;

public class LagrangeController {
    @FXML private TextField inputX;
    @FXML private TextField inputY;
    @FXML private TextField inputXp;
    @FXML private Label resultadoLbl;
    @FXML private TextArea polinomiosArea;
    @FXML private TextArea polinomioInterpolanteArea;
    @FXML private TableView<PuntoInterpolado> resultadosTable;
    @FXML private TableColumn<PuntoInterpolado, Double> xpColumn;
    @FXML private TableColumn<PuntoInterpolado, Double> ypColumn;
    @FXML private Pane polinomiosDisplayPane;
    @FXML private Pane polinomioInterpolanteDisplayPane;

    private ObservableList<PuntoInterpolado> puntosInterpolados = FXCollections.observableArrayList();

    @FXML
    private void calcularInterpolacion() {
        try {
            // Limpiar resultados anteriores
            puntosInterpolados.clear();

            String[] xStr = inputX.getText().split(",");
            String[] yStr = inputY.getText().split(",");
            String[] xpStr = inputXp.getText().split(",");

            if (xStr.length != yStr.length) {
                throw new IllegalArgumentException("Los vectores X y Y deben tener la misma longitud.");
            }

            if (xStr.length < 2) {
                throw new IllegalArgumentException("Se requieren al menos 2 puntos para la interpolación.");
            }

            double[] x = new double[xStr.length];
            double[] y = new double[yStr.length];
            double[] xpValues = new double[xpStr.length];

            for (int i = 0; i < xStr.length; i++) {
                x[i] = Double.parseDouble(xStr[i].trim());
                y[i] = Double.parseDouble(yStr[i].trim());
            }

            for (int i = 0; i < xpStr.length; i++) {
                xpValues[i] = Double.parseDouble(xpStr[i].trim());
            }

            Lagrange modelo = new Lagrange(x, y);

            // Calcular y mostrar todos los puntos interpolados
            for (double xp : xpValues) {
                double resultado = modelo.calcularInterpolacion(xp);
                puntosInterpolados.add(new PuntoInterpolado(xp, resultado));
            }

            // Añadir mensaje informativo sobre el tipo de interpolación
            String tipoInterpolacion = "";
            if (x.length == 2) {
                tipoInterpolacion = "Interpolación Lineal (2 puntos)";
            } else if (x.length == 3) {
                tipoInterpolacion = "Interpolación Cuadrática (3 puntos)";
            } else {
                tipoInterpolacion = "Interpolación de Lagrange (" + x.length + " puntos)";
            }

            resultadoLbl.setText(tipoInterpolacion);
            polinomiosArea.setText(modelo.generarPolinomios());
            polinomioInterpolanteArea.setText(modelo.generarPolinomioInterpolante());

            // Visualizar polinomios con LaTeX
            visualizarLatex(modelo.generarPolinomiosLatex(), polinomiosDisplayPane);
            visualizarLatex(modelo.generarPolinomioInterpolanteLatex(), polinomioInterpolanteDisplayPane);

            // Mostrar mensaje si no hay puntos a interpolar
            if (xpValues.length == 0) {
                mostrarError("Por favor, ingrese al menos un punto X a interpolar.");
            }
        } catch (NumberFormatException e) {
            mostrarError("Por favor, ingrese solo números válidos.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Ocurrió un error inesperado.");
        }
    }

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        xpColumn.setCellValueFactory(cellData -> cellData.getValue().xProperty().asObject());
        ypColumn.setCellValueFactory(cellData -> cellData.getValue().yProperty().asObject());

        // Configurar el formateador de celdas para mostrar valores con 4 decimales
        xpColumn.setCellFactory(column -> new TableCell<PuntoInterpolado, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.4f", item));
                }
            }
        });

        ypColumn.setCellFactory(column -> new TableCell<PuntoInterpolado, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.4f", item));
                }
            }
        });

        // Asignar los datos a la tabla
        resultadosTable.setItems(puntosInterpolados);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Visualiza una fórmula matemática usando LaTeX en el panel especificado.
     * @param latexFormula La fórmula en formato LaTeX
     * @param displayPane El panel donde se mostrará la fórmula
     */
    private void visualizarLatex(String latexFormula, Pane displayPane) {
        try {
            // Limpiar panel
            displayPane.getChildren().clear();

            // Crear fórmula LaTeX
            TeXFormula formula = new TeXFormula(latexFormula);

            // Crear icono TeXFormula (usando un tamaño mayor para mejor visualización)
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);

            // Crear una imagen para renderizar la fórmula
            BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();

            // Fondo blanco
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());

            // Dibujar la fórmula en la imagen
            icon.paintIcon(null, g2, 0, 0);
            g2.dispose();

            // Convertir a imagen JavaFX
            WritableImage fxImage = SwingFXUtils.toFXImage(image, null);

            // Crear ImageView para mostrar la imagen
            ImageView imageView = new ImageView(fxImage);

            // Centrar la imagen en el panel
            imageView.setLayoutX((displayPane.getWidth() - icon.getIconWidth()) / 2);
            imageView.setLayoutY((displayPane.getHeight() - icon.getIconHeight()) / 2);

            // Añadir al panel
            displayPane.getChildren().add(imageView);

        } catch (Exception e) {
            // En caso de error, mostrar mensaje en el panel
            Label errorLabel = new Label("Error al renderizar la fórmula: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            displayPane.getChildren().add(errorLabel);
        }
    }
}
