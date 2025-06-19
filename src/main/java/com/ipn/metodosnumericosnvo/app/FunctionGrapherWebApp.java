package com.ipn.metodosnumericosnvo.app;

import com.ipn.metodosnumericosnvo.visualization.FunctionGrapherWeb;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Standalone application for the FunctionGrapherWeb.
 * This class provides a simple UI for using the FunctionGrapherWeb component directly.
 */
public class FunctionGrapherWebApp extends Application {

    private FunctionGrapherWeb grapher;
    private ComboBox<String> functionsComboBox;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Graficadora Web con Plotly.js");

        // Create the function input field
        TextField functionInput = new TextField("");
        functionInput.setPromptText("Introduce la función: ejemplo sin(x)*x");

        // Create a pane for the chart
        Pane chartPane = new Pane();
        chartPane.setPrefSize(800, 500);

        // Create the function grapher
        grapher = new FunctionGrapherWeb(chartPane);

        // Create the functions combo box
        functionsComboBox = new ComboBox<>();
        functionsComboBox.setPromptText("Funciones");
        functionsComboBox.setPrefWidth(200);
        functionsComboBox.setOnAction(e -> {
            String selected = functionsComboBox.getValue();
            if (selected != null) {
                functionInput.setText(selected);
            }
        });

        // Create the function buttons
        Button addButton = new Button("Añadir");
        addButton.setOnAction(e -> {
            String func = functionInput.getText();
            if (!func.isEmpty()) {
                grapher.addFunction(func);
                updateFunctionsList();
            }
        });

        Button removeButton = new Button("Eliminar");
        removeButton.setOnAction(e -> {
            String func = functionsComboBox.getValue();
            if (func != null) {
                grapher.removeFunction(func);
                updateFunctionsList();
            }
        });

        // Create the analysis buttons
        Button derivativeButton = new Button("Derivada");
        derivativeButton.setOnAction(e -> {
            String func = functionsComboBox.getValue();
            if (func != null) {
                grapher.addDerivative(func);
            }
        });

        Button integralButton = new Button("Integral");
        integralButton.setOnAction(e -> {
            String func = functionsComboBox.getValue();
            if (func != null) {
                grapher.addIntegral(func);
            }
        });

        Button rootsButton = new Button("Raíces");
        rootsButton.setOnAction(e -> {
            String func = functionsComboBox.getValue();
            if (func != null) {
                grapher.findAndDisplayRoots(func);
            }
        });

        // Create the zoom buttons
        Button zoomInButton = new Button("Zoom In");
        zoomInButton.setOnAction(e -> grapher.zoomIn());

        Button zoomOutButton = new Button("Zoom Out");
        zoomOutButton.setOnAction(e -> grapher.zoomOut());

        Button resetZoomButton = new Button("Reset Zoom");
        resetZoomButton.setOnAction(e -> grapher.resetZoom());

        // Create the axis range controls
        Label xMinLabel = new Label("X Min:");
        TextField xMinField = new TextField("-10");
        xMinField.setPrefWidth(60);

        Label xMaxLabel = new Label("X Max:");
        TextField xMaxField = new TextField("10");
        xMaxField.setPrefWidth(60);

        Label yMinLabel = new Label("Y Min:");
        TextField yMinField = new TextField("-10");
        yMinField.setPrefWidth(60);

        Label yMaxLabel = new Label("Y Max:");
        TextField yMaxField = new TextField("10");
        yMaxField.setPrefWidth(60);

        Button applyRangesButton = new Button("Aplicar Rangos");
        applyRangesButton.setOnAction(e -> {
            try {
                double xMin = Double.parseDouble(xMinField.getText());
                double xMax = Double.parseDouble(xMaxField.getText());
                double yMin = Double.parseDouble(yMinField.getText());
                double yMax = Double.parseDouble(yMaxField.getText());
                grapher.setAxisRanges(xMin, xMax, yMin, yMax);
            } catch (NumberFormatException ex) {
                showAlert("Por favor ingrese valores numéricos válidos para los rangos.");
            }
        });

        // Create the horizontal boxes for the controls
        HBox functionBox = new HBox(10, functionInput, addButton, removeButton);
        HBox analysisBox = new HBox(10, functionsComboBox, derivativeButton, integralButton, rootsButton);
        HBox zoomBox = new HBox(10, zoomInButton, zoomOutButton, resetZoomButton);

        // Create the grid for the axis range controls
        GridPane rangeGrid = new GridPane();
        rangeGrid.setHgap(10);
        rangeGrid.setVgap(5);
        rangeGrid.add(xMinLabel, 0, 0);
        rangeGrid.add(xMinField, 1, 0);
        rangeGrid.add(xMaxLabel, 2, 0);
        rangeGrid.add(xMaxField, 3, 0);
        rangeGrid.add(yMinLabel, 0, 1);
        rangeGrid.add(yMinField, 1, 1);
        rangeGrid.add(yMaxLabel, 2, 1);
        rangeGrid.add(yMaxField, 3, 1);
        rangeGrid.add(applyRangesButton, 4, 0, 1, 2);

        // Create the root layout
        VBox root = new VBox(10, functionBox, analysisBox, zoomBox, rangeGrid, chartPane);
        root.setPadding(new Insets(10));

        // Create the scene
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Updates the list of functions in the combo box.
     */
    private void updateFunctionsList() {
        String selected = functionsComboBox.getValue();
        functionsComboBox.getItems().clear();
        functionsComboBox.getItems().addAll(grapher.getFunctions());
        if (selected != null && functionsComboBox.getItems().contains(selected)) {
            functionsComboBox.setValue(selected);
        } else if (!functionsComboBox.getItems().isEmpty()) {
            functionsComboBox.setValue(functionsComboBox.getItems().get(0));
        }
    }

    /**
     * Shows an alert with the given message.
     * 
     * @param message The message to show
     */
    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Main entry point for the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
