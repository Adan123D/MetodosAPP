package com.ipn.metodosnumericosnvo.controller;

import com.ipn.metodosnumericosnvo.visualization.FunctionGrapherWeb;
import com.ipn.metodosnumericosnvo.visualization.Function3DChartManager;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for the chart visualizer window.
 * This class handles the UI interactions for the function chart visualization.
 */
public class ChartVisualizerController {

    @FXML
    private Pane chartPane;

    @FXML
    private TextField functionTextField;

    @FXML
    private ComboBox<String> functionsComboBox;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private javafx.scene.control.MenuButton analysisMenuButton;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField xMinTextField;

    @FXML
    private TextField xMaxTextField;

    @FXML
    private TextField yMinTextField;

    @FXML
    private TextField yMaxTextField;

    // New fields for Z range
    private TextField zMinTextField;
    private TextField zMaxTextField;

    @FXML
    private javafx.scene.control.MenuButton symbolsMenuButton;

    // The function chart managers
    private FunctionGrapherWeb chartManager2D;
    private Function3DChartManager chartManager3D;

    // Flag to track if we're in 3D mode
    private boolean is3DMode = false;

    // Flag to track if initialization is complete
    private boolean initialized = false;

    // Toggle button for switching between 2D and 3D modes
    @FXML
    private javafx.scene.control.ToggleButton modeToggleButton;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
    try {
        // Inicializar los administradores de gráficas
        chartManager3D = new Function3DChartManager(chartPane);
        chartManager2D = new FunctionGrapherWeb(chartPane);

        // Configurar el toggle button
        modeToggleButton.setSelected(is3DMode);
        modeToggleButton.setText(is3DMode ? "Modo 3D" : "Modo 2D");

        // Mostrar el administrador correcto según el modo actual
        updateChartManagerVisibility();

        // Configurar los campos de texto de rango con valores predeterminados
        xMinTextField.setText("-5");
        xMaxTextField.setText("5");
        yMinTextField.setText("-5");
        yMaxTextField.setText("5");

        // Crear campos para el rango Z
        zMinTextField = new TextField("-5");
        zMaxTextField = new TextField("5");
        zMinTextField.setPrefWidth(60);
        zMaxTextField.setPrefWidth(60);

        // Añadir los nuevos campos al layout
        HBox rangeBox = (HBox) xMinTextField.getParent();
        if (rangeBox != null) {
            rangeBox.getChildren().add(new Label("Z Min:"));
            rangeBox.getChildren().add(zMinTextField);
            rangeBox.getChildren().add(new Label("Z Max:"));
            rangeBox.getChildren().add(zMaxTextField);
        }

        // Actualizar la visibilidad de los campos Z según el modo
        updateZFieldsVisibility();

        // Configurar el ComboBox de funciones
        functionsComboBox.getItems().clear();

        // Agregar funciones de ejemplo
        addExampleFunctions();

        // Configurar listeners para actualización automática
        functionsComboBox.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    functionTextField.setText(newSelection);
                }
            }
        );

        // Marcar como inicializado
        initialized = true;

        System.out.println("ChartVisualizerController inicializado correctamente");

    } catch (Exception e) {
        e.printStackTrace();
        if (statusLabel != null) {
            statusLabel.setText("Error en la inicialización: " + e.getMessage());
            statusLabel.setVisible(true);
        }

        // Mostrar alerta de error
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error de Inicialización");
        alert.setHeaderText("Error al inicializar el visualizador de gráficas");
        alert.setContentText("Detalles: " + e.getMessage());
        alert.showAndWait();
    }
}

    /**
     * Adds example functions to help users get started.
     */
    private void addExampleFunctions() {
        try {
            if (is3DMode) {
                // Show a helpful message for 3D mode
                statusLabel.setText("Modo 3D activado. Puede mover la gráfica arrastrando con el cursor y hacer zoom con la rueda del ratón.");
            } else {
                // Show a helpful message for 2D mode
                statusLabel.setText("Modo 2D activado. Puede usar las herramientas de análisis para calcular derivadas, integrales, etc.");
            }

            // Update the functions list
            updateFunctionsList();
            statusLabel.setVisible(true);
        } catch (Exception e) {
            // If there's an error, just log it and continue
            System.err.println("Error al añadir funciones de ejemplo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets the initial function to be displayed in the chart.
     * This method is called from the MenuController when opening the chart visualizer.
     * 
     * @param functionText The function text to display
     */
    public void setInitialFunction(String functionText) {
        if (functionText != null && !functionText.isEmpty()) {
            try {
                // If the controller is not yet initialized, wait for it
                if (!initialized) {
                    // Use Platform.runLater to ensure this runs after initialization
                    javafx.application.Platform.runLater(() -> {
                        try {
                            functionTextField.setText(functionText);
                            onAddButtonClick();
                        } catch (Exception e) {
                            statusLabel.setText("Error al añadir la función: " + e.getMessage());
                            statusLabel.setVisible(true);
                        }
                    });
                } else {
                    // Controller is already initialized, add the function directly
                    functionTextField.setText(functionText);
                    onAddButtonClick();
                }
            } catch (Exception e) {
                // Handle any errors that occur when setting the function
                statusLabel.setText("Error al añadir la función: " + e.getMessage());
                statusLabel.setVisible(true);
            }
        }
    }

    /**
     * Handles the action when the Add button is clicked.
     * This method adds the function from the text field to the chart.
     */
    @FXML
    protected void onAddButtonClick() {
        String functionText = functionTextField.getText().trim();

        if (functionText.isEmpty()) {
            statusLabel.setText("Por favor ingrese una función");
            statusLabel.setVisible(true);
            return;
        }

        boolean added;
        if (is3DMode) {
            added = chartManager3D.addFunction(functionText);
        } else {
            added = chartManager2D.addFunction(functionText);
        }

        if (added) {
            statusLabel.setText("Función añadida: " + functionText);
            statusLabel.setVisible(true);
            functionTextField.clear();
            updateFunctionsList();
        }
    }

    /**
     * Handles the action when the Remove button is clicked.
     * This method removes the selected function from the chart.
     */
    @FXML
    protected void onRemoveButtonClick() {
        String functionText = functionsComboBox.getSelectionModel().getSelectedItem();

        if (functionText == null || functionText.isEmpty()) {
            statusLabel.setText("Por favor seleccione una función para eliminar");
            statusLabel.setVisible(true);
            return;
        }

        boolean removed;
        if (is3DMode) {
            removed = chartManager3D.removeFunction(functionText);
        } else {
            removed = chartManager2D.removeFunction(functionText);
        }

        if (removed) {
            statusLabel.setText("Función eliminada: " + functionText);
            statusLabel.setVisible(true);
            updateFunctionsList();
        }
    }

    /**
     * Handles the action when the Close button is clicked.
     * This method closes the chart visualizer window.
     */
    @FXML
    protected void onCloseButtonClick() {
        // Get the stage from any control
        Stage stage = (Stage) chartPane.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the action when the Zoom In button is clicked.
     * This method zooms in on the chart.
     */
    @FXML
    protected void onZoomInClick() {
        if (is3DMode) {
            chartManager3D.zoomIn();
        } else {
            chartManager2D.zoomIn();
        }
        updateRangeTextFields();
        statusLabel.setText("Zoom aplicado");
        statusLabel.setVisible(true);
    }

    /**
     * Handles the action when the Zoom Out button is clicked.
     * This method zooms out on the chart.
     */
    @FXML
    protected void onZoomOutClick() {
        if (is3DMode) {
            chartManager3D.zoomOut();
        } else {
            chartManager2D.zoomOut();
        }
        updateRangeTextFields();
        statusLabel.setText("Zoom aplicado");
        statusLabel.setVisible(true);
    }

    /**
     * Handles the action when the Reset Zoom button is clicked.
     * This method resets the chart to the default zoom level.
     */
    @FXML
    protected void onResetZoomClick() {
        if (is3DMode) {
            chartManager3D.resetZoom();
        } else {
            chartManager2D.resetZoom();
        }
        updateRangeTextFields();
        statusLabel.setText("Zoom restablecido");
        statusLabel.setVisible(true);
    }

    /**
     * Handles the action when the Derivative button is clicked.
     * This method calculates and displays the derivative of the selected function in 2D mode,
     * or shows a message that derivatives are not available in 3D mode.
     */
    @FXML
    protected void onDerivativeClick() {
        if (is3DMode) {
            // In 3D mode, derivatives are not available
            statusLabel.setText("Las derivadas no están disponibles en el modo 3D");
            statusLabel.setVisible(true);

            // Show an alert with more information
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Función no disponible");
            alert.setHeaderText("Derivadas en 3D");
            alert.setContentText("El cálculo de derivadas no está disponible en el modo de visualización 3D. Cambie a modo 2D para usar esta función.");
            alert.showAndWait();
        } else {
            // In 2D mode, calculate the derivative
            String functionText = functionsComboBox.getSelectionModel().getSelectedItem();

            if (functionText == null || functionText.isEmpty()) {
                statusLabel.setText("Por favor seleccione una función para calcular su derivada");
                statusLabel.setVisible(true);
                return;
            }

            boolean added = chartManager2D.addDerivative(functionText);
            if (added) {
                statusLabel.setText("Derivada añadida para: " + functionText);
                statusLabel.setVisible(true);
                updateFunctionsList();
            }
        }
    }

    /**
     * Handles the action when the Integral button is clicked.
     * This method calculates and displays the integral of the selected function in 2D mode,
     * or shows a message that integrals are not available in 3D mode.
     */
    @FXML
    protected void onIntegralClick() {
        if (is3DMode) {
            // In 3D mode, integrals are not available
            statusLabel.setText("Las integrales no están disponibles en el modo 3D");
            statusLabel.setVisible(true);

            // Show an alert with more information
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Función no disponible");
            alert.setHeaderText("Integrales en 3D");
            alert.setContentText("El cálculo de integrales no está disponible en el modo de visualización 3D. Cambie a modo 2D para usar esta función.");
            alert.showAndWait();
        } else {
            // In 2D mode, calculate the integral
            String functionText = functionsComboBox.getSelectionModel().getSelectedItem();

            if (functionText == null || functionText.isEmpty()) {
                statusLabel.setText("Por favor seleccione una función para calcular su integral");
                statusLabel.setVisible(true);
                return;
            }

            boolean added = chartManager2D.addIntegral(functionText);
            if (added) {
                statusLabel.setText("Integral añadida para: " + functionText);
                statusLabel.setVisible(true);
                updateFunctionsList();
            }
        }
    }

    /**
     * Handles the action when the Roots button is clicked.
     * This method finds and displays the roots of the selected function in 2D mode,
     * or shows a message that finding roots is not available in 3D mode.
     */
    @FXML
    protected void onRootsClick() {
        if (is3DMode) {
            // In 3D mode, finding roots is not available
            statusLabel.setText("La búsqueda de raíces no está disponible en el modo 3D");
            statusLabel.setVisible(true);

            // Show an alert with more information
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Función no disponible");
            alert.setHeaderText("Raíces en 3D");
            alert.setContentText("La búsqueda de raíces no está disponible en el modo de visualización 3D. Cambie a modo 2D para usar esta función.");
            alert.showAndWait();
        } else {
            // In 2D mode, find and display roots
            String functionText = functionsComboBox.getSelectionModel().getSelectedItem();

            if (functionText == null || functionText.isEmpty()) {
                statusLabel.setText("Por favor seleccione una función para encontrar sus raíces");
                statusLabel.setVisible(true);
                return;
            }

            boolean found = chartManager2D.findAndDisplayRoots(functionText);
            if (found) {
                statusLabel.setText("Raíces encontradas para: " + functionText);
                statusLabel.setVisible(true);
            }
        }
    }

    /**
     * Handles the action when the Intersections button is clicked.
     * This method finds and displays the intersections between the selected function and another function in 2D mode,
     * or shows a message that finding intersections is not available in 3D mode.
     */
    @FXML
    protected void onIntersectionsClick() {
        if (is3DMode) {
            // In 3D mode, finding intersections is not available
            statusLabel.setText("La búsqueda de intersecciones no está disponible en el modo 3D");
            statusLabel.setVisible(true);

            // Show an alert with more information
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Función no disponible");
            alert.setHeaderText("Intersecciones en 3D");
            alert.setContentText("La búsqueda de intersecciones no está disponible en el modo de visualización 3D. Cambie a modo 2D para usar esta función.");
            alert.showAndWait();
        } else {
            // In 2D mode, find and display intersections
            String function1Text = functionsComboBox.getSelectionModel().getSelectedItem();

            if (function1Text == null || function1Text.isEmpty()) {
                statusLabel.setText("Por favor seleccione una función para encontrar intersecciones");
                statusLabel.setVisible(true);
                return;
            }

            // Get all functions except the selected one
            List<String> otherFunctions = new ArrayList<>(chartManager2D.getFunctions());
            otherFunctions.remove(function1Text);

            if (otherFunctions.isEmpty()) {
                statusLabel.setText("Necesita al menos dos funciones para encontrar intersecciones");
                statusLabel.setVisible(true);
                return;
            }

            // For simplicity, use the first other function
            // In a more advanced implementation, you could show a dialog to select which function to intersect with
            String function2Text = otherFunctions.get(0);

            boolean found = chartManager2D.findAndDisplayIntersections(function1Text, function2Text);
            if (found) {
                statusLabel.setText("Intersecciones encontradas entre: " + function1Text + " y " + function2Text);
                statusLabel.setVisible(true);
            }
        }
    }

    /**
     * Handles the action when the Apply Ranges button is clicked.
     * This method applies the ranges entered in the text fields.
     */
    @FXML
    protected void onApplyRangesClick() {
        try {
            // Parse the values from the text fields
            double xMin = Double.parseDouble(xMinTextField.getText());
            double xMax = Double.parseDouble(xMaxTextField.getText());
            double yMin = Double.parseDouble(yMinTextField.getText());
            double yMax = Double.parseDouble(yMaxTextField.getText());
            double zMin = Double.parseDouble(zMinTextField.getText());
            double zMax = Double.parseDouble(zMaxTextField.getText());

            // Validate the ranges
            if (xMin >= xMax) {
                statusLabel.setText("Error: X Min debe ser menor que X Max");
                statusLabel.setVisible(true);
                return;
            }

            if (yMin >= yMax) {
                statusLabel.setText("Error: Y Min debe ser menor que Y Max");
                statusLabel.setVisible(true);
                return;
            }

            if (zMin >= zMax) {
                statusLabel.setText("Error: Z Min debe ser menor que Z Max");
                statusLabel.setVisible(true);
                return;
            }

            // Apply the ranges
            if (is3DMode) {
                chartManager3D.setAxisRanges(xMin, xMax, yMin, yMax, zMin, zMax);
            } else {
                chartManager2D.setAxisRanges(xMin, xMax, yMin, yMax);
            }

            statusLabel.setText("Rangos aplicados");
            statusLabel.setVisible(true);
        } catch (NumberFormatException e) {
            statusLabel.setText("Error: Ingrese valores numéricos válidos");
            statusLabel.setVisible(true);
        }
    }

    /**
     * Updates the range text fields with the current axis ranges.
     */
    private void updateRangeTextFields() {
        // Get the current ranges from the chart manager
        double[] ranges;
        if (is3DMode) {
            ranges = chartManager3D.getAxisRanges();
        } else {
            ranges = chartManager2D.getAxisRanges();
        }

        // Update the text fields
        xMinTextField.setText(String.format("%.2f", ranges[0]));
        xMaxTextField.setText(String.format("%.2f", ranges[1]));
        yMinTextField.setText(String.format("%.2f", ranges[2]));
        yMaxTextField.setText(String.format("%.2f", ranges[3]));

        // Update Z range fields if they exist and we're in 3D mode
        if (zMinTextField != null && zMaxTextField != null && is3DMode && ranges.length >= 6) {
            zMinTextField.setText(String.format("%.2f", ranges[4]));
            zMaxTextField.setText(String.format("%.2f", ranges[5]));
        }
    }

    /**
     * Updates the visibility of the chart managers based on the current mode.
     */
    private void updateChartManagerVisibility() {
        // Clear the chart pane
        chartPane.getChildren().clear();

        // Add the appropriate chart manager to the pane
        if (is3DMode) {
            // In 3D mode, we need to recreate the 3D scene
            chartManager3D = new Function3DChartManager(chartPane);
        } else {
            // In 2D mode, we need to recreate the 2D chart
            chartManager2D = new FunctionGrapherWeb(chartPane);
        }

        // Update the analysis menu items based on the mode
        updateAnalysisMenuItems();
    }

    /**
     * Updates the visibility of the Z range fields based on the current mode.
     */
    private void updateZFieldsVisibility() {
        if (zMinTextField != null && zMaxTextField != null) {
            // In 3D mode, show the Z range fields; in 2D mode, hide them
            boolean visible = is3DMode;

            // Get the parent HBox
            HBox rangeBox = (HBox) xMinTextField.getParent();
            if (rangeBox != null) {
                // Find the Z Min label and Z Max label
                for (int i = 0; i < rangeBox.getChildren().size(); i++) {
                    if (rangeBox.getChildren().get(i) instanceof Label) {
                        Label label = (Label) rangeBox.getChildren().get(i);
                        if (label.getText().equals("Z Min:") || label.getText().equals("Z Max:")) {
                            label.setVisible(visible);
                            label.setManaged(visible);
                        }
                    }
                }

                // Set visibility of Z range fields
                zMinTextField.setVisible(visible);
                zMinTextField.setManaged(visible);
                zMaxTextField.setVisible(visible);
                zMaxTextField.setManaged(visible);
            }
        }
    }

    /**
     * Updates the analysis menu items based on the current mode.
     */
    private void updateAnalysisMenuItems() {
        // In 3D mode, disable the analysis menu items; in 2D mode, enable them
        boolean enabled = !is3DMode;

        // Get all menu items from the analysis menu button
        for (MenuItem item : analysisMenuButton.getItems()) {
            item.setDisable(!enabled);
        }
    }

    /**
     * Handles the action when the Mode Toggle button is clicked.
     * This method switches between 2D and 3D modes.
     */
    @FXML
    protected void onModeToggleButtonClick() {
        // Toggle the mode
        is3DMode = !is3DMode;

        // Update the button text
        modeToggleButton.setText(is3DMode ? "Modo 3D" : "Modo 2D");

        // Update the chart manager visibility
        updateChartManagerVisibility();

        // Update the Z range fields visibility
        updateZFieldsVisibility();

        // Update the range text fields
        updateRangeTextFields();

        // Clear the functions list
        functionsComboBox.getItems().clear();

        // Add example functions for the current mode
        addExampleFunctions();

        // Show a message
        statusLabel.setText("Cambiado a modo " + (is3DMode ? "3D" : "2D"));
        statusLabel.setVisible(true);
    }

    /**
     * Handles the action when the Export Image button is clicked.
     * This method exports the chart as a PNG image.
     */
    @FXML
    protected void onExportImageClick() {
        try {
            // Create a file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Imagen");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes PNG", "*.png")
            );
            fileChooser.setInitialFileName("grafica_funciones.png");

            // Show save dialog
            File file = fileChooser.showSaveDialog(chartPane.getScene().getWindow());

            if (file != null) {
                // Create a snapshot of the chart
                WritableImage image = chartPane.snapshot(null, null);

                // Save the image to the file
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

                // Show success message
                statusLabel.setText("Imagen guardada como: " + file.getName());
                statusLabel.setVisible(true);
            }
        } catch (IOException e) {
            // Show error message
            statusLabel.setText("Error al guardar la imagen: " + e.getMessage());
            statusLabel.setVisible(true);
        }
    }

    /**
     * Updates the list of functions in the combo box.
     */
    private void updateFunctionsList() {
        // Get the current selection
        String selectedFunction = functionsComboBox.getSelectionModel().getSelectedItem();

        // Clear the combo box
        functionsComboBox.getItems().clear();

        // Add all functions to the combo box
        if (is3DMode) {
            functionsComboBox.getItems().addAll(chartManager3D.getFunctions());
        } else {
            functionsComboBox.getItems().addAll(chartManager2D.getFunctions());
        }

        // Restore the selection if possible
        if (selectedFunction != null && functionsComboBox.getItems().contains(selectedFunction)) {
            functionsComboBox.getSelectionModel().select(selectedFunction);
        } else if (!functionsComboBox.getItems().isEmpty()) {
            functionsComboBox.getSelectionModel().selectFirst();
        }
    }

    //--------------------------------------------------
    // Symbol Menu Handlers
    //--------------------------------------------------

    /**
     * Handles the click event for items in the symbols dropdown menu.
     * Extracts the symbol or function from the menu item text and inserts it at the cursor position.
     * 
     * @param event The action event from the menu item click
     */
    @FXML
    protected void onSymbolMenuItemClick(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String menuText = menuItem.getText();

        // Extract the symbol or function from the menu text
        // The format is typically "Description (symbol)"
        if (menuText.contains("(") && menuText.contains(")")) {
            String symbol = menuText.substring(menuText.indexOf("(") + 1, menuText.indexOf(")"));

            // Handle different types of symbols and functions
            switch (symbol) {
                // Basic operators
                case "^":
                case "*":
                case "/":
                case "+":
                case "-":
                    insertTextAtCursor(symbol);
                    break;

                // Functions that need parentheses
                case "sin":
                case "cos":
                case "tan":
                case "cot":
                case "sec":
                case "csc":
                case "arcsin":
                case "arccos":
                case "arctan":
                case "sinh":
                case "cosh":
                case "tanh":
                case "ln":
                case "log":
                case "sqrt":
                case "cbrt":
                case "abs":
                    insertTextAtCursor(symbol + "(", ")");
                    break;

                // Special case for exponential
                case "e^":
                    insertTextAtCursor("e^(", ")");
                    break;

                // Special case for root
                case "root":
                    insertTextAtCursor("root(n,", ")");
                    break;

                // Constants
                case "π":
                    insertTextAtCursor("pi");
                    break;
                case "e":
                    insertTextAtCursor("e");
                    break;
                case "inf":
                    insertTextAtCursor("inf");
                    break;

                // Parentheses and brackets
                case "()":
                    insertTextAtCursor("(", ")");
                    break;
                case "[]":
                    insertTextAtCursor("[", "]");
                    break;
                case "{}":
                    insertTextAtCursor("{", "}");
                    break;

                // Special case for factorial
                case "!":
                    insertTextAtCursor("!");
                    break;

                // Special case for fraction
                case "a/b":
                    insertTextAtCursor("(", ")/()");
                    break;

                default:
                    // If we can't identify the symbol, just insert the text as is
                    insertTextAtCursor(symbol);
                    break;
            }
        } else {
            // If the menu text doesn't follow the expected format, just use it as is
            insertTextAtCursor(menuText);
        }
    }

    //--------------------------------------------------
    // Utility Methods
    //--------------------------------------------------

    /**
     * Inserts text at the current cursor position in the function text field.
     * If there is a selection, the text will replace the selection.
     * 
     * @param textToInsert The text to insert at the cursor position
     */
    private void insertTextAtCursor(String textToInsert) {
        insertTextAtCursor(textToInsert, "");
    }

    /**
     * Inserts text at the current cursor position in the function text field,
     * with optional suffix text. If there is a selection, the text will wrap around the selection.
     * 
     * @param prefixText The text to insert before the cursor or selection
     * @param suffixText The text to insert after the cursor or selection
     */
    private void insertTextAtCursor(String prefixText, String suffixText) {
        // Get the current text and selection
        String currentText = functionTextField.getText();
        int caretPosition = functionTextField.getCaretPosition();
        int selectionStart = functionTextField.getSelection().getStart();
        int selectionEnd = functionTextField.getSelection().getEnd();

        // Check if there is a selection
        if (selectionStart != selectionEnd) {
            // There is a selection, wrap it with prefix and suffix
            String selectedText = currentText.substring(selectionStart, selectionEnd);
            String newText = currentText.substring(0, selectionStart) + 
                             prefixText + selectedText + suffixText + 
                             currentText.substring(selectionEnd);

            // Update the text field
            functionTextField.setText(newText);

            // Set the caret position after the inserted text
            functionTextField.positionCaret(selectionStart + prefixText.length() + selectedText.length() + suffixText.length());
        } else {
            // No selection, just insert at caret position
            String newText = currentText.substring(0, caretPosition) + 
                             prefixText + suffixText + 
                             currentText.substring(caretPosition);

            // Update the text field
            functionTextField.setText(newText);

            // Set the caret position between prefix and suffix
            functionTextField.positionCaret(caretPosition + prefixText.length());
        }

        // Set focus back to the text field
        functionTextField.requestFocus();
    }
}
