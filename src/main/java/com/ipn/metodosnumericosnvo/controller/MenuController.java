package com.ipn.metodosnumericosnvo.controller;


// JavaFX imports
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

import com.ipn.metodosnumericosnvo.controller.BiseccionController;
import com.ipn.metodosnumericosnvo.controller.ChartVisualizerController;

// JLaTeXMath imports
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

// AWT imports
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Controller class for the main menu of the Numerical Methods application.
 * Handles user interactions with the menu items and the function visualizer.
 */
public class MenuController {
    // UI Components
    @FXML
    private Label welcomeText;

    @FXML
    private TextField functionTextField;

    @FXML
    private Pane functionDisplayPane;

    @FXML
    private Label functionErrorLabel;

    @FXML
    private javafx.scene.control.MenuButton symbolsMenuButton;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     * Sets the initial state of UI components.
     */
    @FXML
    public void initialize() {
        // Set initial state
        functionErrorLabel.setVisible(false);

        // Set a default welcome message
        welcomeText.setText("Bienvenido al Visualizador de Funciones");
    }

    //--------------------------------------------------
    // Navigation and Menu Event Handlers
    //--------------------------------------------------

    /**
     * Generic method to handle menu item clicks.
     * This method can be used for all menu items in the application.
     * 
     * @param event The action event from the menu item click
     */
    @FXML
    public void handleMenuItemClick(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String menuText = menuItem.getText();
        welcomeText.setText("Selected: " + menuText);

        // Handle specific menu items
        switch (menuText) {
            default:
                // For menu items that are not yet implemented
                break;
        }
    }

    /**
     * Handles the click event for the Método de Bisección menu item.
     * This method opens a new window for the bisection method.
     * If a function is entered in the main menu, it will be passed to the bisection window.
     */
    @FXML
    protected void onBiseccionMenuItemClick() {
        try {
            // Cargar el archivo FXML del método de bisección
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/Biseccion.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo Biseccion.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador y configurar la función inicial si existe
            BiseccionController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al método de bisección
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setFuncion(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage biseccionStage = new Stage();
            biseccionStage.setTitle("Método de Bisección");
            biseccionStage.setScene(new Scene(root, 800, 600));
            biseccionStage.initModality(Modality.NONE);
            biseccionStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al abrir la ventana de Bisección: " + e.getMessage());
        }
    }

    /**
     * Handles the click event for the Gráfica button.
     * This method opens a new window for visualizing mathematical functions as charts.
     * If a function is entered in the main menu, it will be passed to the chart visualizer.
     */
    @FXML
    protected void onGraficaButtonClick() {
        try {
            // Cargar el archivo FXML del visualizador de gráficas
            FXMLLoader loader = new FXMLLoader();

            // Asegurarse de usar la ruta correcta del archivo FXML
            loader.setLocation(getClass().getResource("/com/ipn/metodosnumericosnvo/ChartVisualizer.fxml"));

            if (loader.getLocation() == null) {
                showError("No se pudo encontrar el archivo ChartVisualizer.fxml");
                return;
            }

            Parent root = loader.load();

            // Obtener el controlador y configurar la función inicial si existe
            ChartVisualizerController controller = loader.getController();

            // Si hay una función en el campo de texto, pasarla al visualizador
            String functionText = functionTextField.getText().trim();
            if (!functionText.isEmpty()) {
                controller.setInitialFunction(functionText);
            }

            // Crear y mostrar la nueva ventana
            Stage chartStage = new Stage();
            chartStage.setTitle("Visualizador de Funciones");
            chartStage.setScene(new Scene(root, 1000, 700));
            chartStage.initModality(Modality.NONE);
            chartStage.show();

    } catch (IOException e) {
        e.printStackTrace();
        showError("Error al abrir el visualizador de gráficas: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        showError("Error inesperado al abrir el visualizador: " + e.getMessage());
    }
}

    /**
     * Handles the click event for the Home button.
     * This method will be used to return to the home screen of the application.
     */
    @FXML
    protected void onHomeButtonClick() {
        welcomeText.setText("Página principal");
        // Here you would add code to return to the home screen
    }

    /**
     * Handles the click event for the Exit button.
     * This method will close the application safely.
     */
    @FXML
    protected void onExitButtonClick() {
        Platform.exit();
    }

    //--------------------------------------------------
    // Function Visualizer Event Handlers
    //--------------------------------------------------

    /**
     * Handles the action when a function is entered in the text field.
     * This method is called when the user presses Enter in the function text field.
     * It triggers the function visualization process.
     */
    @FXML
    protected void onFunctionEntered() {
        visualizeFunction();
    }

    /**
     * Handles the click event for the Visualizar button.
     * This method is called when the user clicks the Visualizar button.
     * It triggers the function visualization process.
     */
    @FXML
    protected void onVisualizeFunctionClick() {
        visualizeFunction();
    }

    //--------------------------------------------------
    // Function Visualization Core Methods
    //--------------------------------------------------

    /**
     * Visualizes the function entered in the text field.
     * This method renders the function as a LaTeX formula using the JLaTeXMath library.
     * The process involves:
     * 1. Converting the function text to LaTeX format
     * 2. Creating a TeXFormula from the LaTeX string
     * 3. Rendering the formula to an image
     * 4. Displaying the image in the UI
     */
    private void visualizeFunction() {
        String functionText = functionTextField.getText().trim();

        // Validate input
        if (functionText.isEmpty()) {
            showError("Por favor ingrese una función.");
            return;
        }

        try {
            // Clear previous content and hide any error messages
            functionDisplayPane.getChildren().clear();
            functionErrorLabel.setVisible(false);

            // Step 1: Convert the function text to LaTeX format
            String latexFormula = convertToLatex(functionText);

            // Step 2: Create a TeXFormula from the LaTeX string
            TeXFormula formula = new TeXFormula(latexFormula);

            // Step 3: Create a TeXIcon from the formula (STYLE_DISPLAY for equation mode, size 20)
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);

            // Step 4: Create a BufferedImage to render the formula
            BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();

            // Set white background for the image
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());

            // Paint the LaTeX formula on the image
            icon.paintIcon(null, g2, 0, 0);
            g2.dispose();  // Release graphics resources

            // Step 5: Convert the BufferedImage to a JavaFX WritableImage
            WritableImage fxImage = SwingFXUtils.toFXImage(image, null);

            // Step 6: Create an ImageView to display the image in JavaFX
            ImageView imageView = new ImageView(fxImage);

            // Step 7: Center the image in the display pane
            imageView.setLayoutX((functionDisplayPane.getPrefWidth() - icon.getIconWidth()) / 2);
            imageView.setLayoutY((functionDisplayPane.getPrefHeight() - icon.getIconHeight()) / 2);

            // Step 8: Add the ImageView to the display pane
            functionDisplayPane.getChildren().add(imageView);

            // Update welcome text to show the visualized function
            welcomeText.setText("Función visualizada: " + functionText);
        } catch (Exception e) {
            // Handle any errors that occur during visualization
            showError("Error al visualizar la función: " + e.getMessage());
        }
    }

    /**
     * Converts a function string to LaTeX format.
     * This method applies a series of transformations to convert standard mathematical
     * notation into proper LaTeX syntax for rendering with JLaTeXMath.
     * 
     * The transformations include:
     * 1. Converting mathematical functions (sin, cos, etc.) to LaTeX commands
     * 2. Converting operators and symbols to their LaTeX equivalents
     * 3. Handling fractions (a/b → \frac{a}{b})
     * 4. Handling exponents (x^n → x^{n})
     * 
     * @param function The function string to convert (e.g., "sin(x^2) + 5/x")
     * @return The LaTeX representation of the function (e.g., "\sin(x^{2}) + \frac{5}{x}")
     */
    private String convertToLatex(String function) {
        // Initialize with the original function
        String latexFormula = function;

        //--------------------------------------------------
        // Step 1: Replace mathematical functions and symbols with LaTeX commands
        //--------------------------------------------------
        latexFormula = latexFormula
            // Trigonometric functions
            .replace("sin", "\\sin")
            .replace("cos", "\\cos")
            .replace("tan", "\\tan")
            .replace("cot", "\\cot")
            .replace("sec", "\\sec")
            .replace("csc", "\\csc")

            // Inverse trigonometric functions
            .replace("arcsin", "\\arcsin")
            .replace("arccos", "\\arccos")
            .replace("arctan", "\\arctan")

            // Hyperbolic functions
            .replace("sinh", "\\sinh")
            .replace("cosh", "\\cosh")
            .replace("tanh", "\\tanh")

            // Logarithmic functions
            .replace("log", "\\log")
            .replace("ln", "\\ln")

            // Square root, cube root and nth root
            .replace("sqrt", "\\sqrt")
            .replace("cbrt", "\\sqrt[3]")

            // Absolute value - convert |x| to \left|x\right|
            .replace("|", "\\left|")
            .replace("|", "\\right|")

            // Common operations and symbols
            .replace("*", " \\cdot ")  // Multiplication dot
            .replace("<=", " \\leq ")  // Less than or equal
            .replace(">=", " \\geq ")  // Greater than or equal
            .replace("!=", " \\neq ")  // Not equal
            .replace("==", " = ")      // Equal
            .replace("pi", "\\pi")     // Pi symbol
            .replace("e^", "\\mathrm{e}^")  // Exponential function
            .replace("inf", "\\infty"); // Infinity symbol

        //--------------------------------------------------
        // Step 1.5: Handle nth root (convert root(n,x) to \sqrt[n]{x})
        //--------------------------------------------------
        if (latexFormula.contains("root(")) {
            StringBuilder result = new StringBuilder();
            int i = 0;

            while (i < latexFormula.length()) {
                // Look for "root(" pattern
                if (i + 5 <= latexFormula.length() && latexFormula.substring(i, i + 5).equals("root(")) {
                    // Found the start of a root function
                    result.append("\\sqrt[");
                    i += 5; // Move past "root("

                    // Find the index of the comma
                    int commaIndex = -1;
                    int parenCount = 1; // We're already inside one parenthesis
                    int j = i;

                    while (j < latexFormula.length() && commaIndex == -1) {
                        char c = latexFormula.charAt(j);
                        if (c == '(') parenCount++;
                        else if (c == ')') parenCount--;
                        else if (c == ',' && parenCount == 1) commaIndex = j;
                        j++;
                    }

                    if (commaIndex != -1) {
                        // Extract the n value
                        String nValue = latexFormula.substring(i, commaIndex);
                        result.append(nValue).append("]{");

                        // Find the closing parenthesis
                        int closeParenIndex = -1;
                        parenCount = 1;
                        j = commaIndex + 1;

                        while (j < latexFormula.length() && closeParenIndex == -1) {
                            char c = latexFormula.charAt(j);
                            if (c == '(') parenCount++;
                            else if (c == ')') {
                                parenCount--;
                                if (parenCount == 0) closeParenIndex = j;
                            }
                            j++;
                        }

                        if (closeParenIndex != -1) {
                            // Extract the x value
                            String xValue = latexFormula.substring(commaIndex + 1, closeParenIndex);
                            result.append(xValue).append("}");
                            i = closeParenIndex + 1; // Move past the closing parenthesis
                        } else {
                            // No closing parenthesis found, just append the rest
                            result.append(latexFormula.substring(commaIndex + 1));
                            i = latexFormula.length();
                        }
                    } else {
                        // No comma found, just append the rest
                        result.append(latexFormula.substring(i));
                        i = latexFormula.length();
                    }
                } else {
                    // Not a root function, just append the character
                    result.append(latexFormula.charAt(i));
                    i++;
                }
            }

            latexFormula = result.toString();
        }

        //--------------------------------------------------
        // Step 2: Handle fractions (convert a/b to \frac{a}{b})
        //--------------------------------------------------
        if (latexFormula.contains("/")) {
            StringBuilder result = new StringBuilder();
            String[] parts = latexFormula.split("/");

            if (parts.length >= 2) {
                // Convert to LaTeX fraction notation
                // Example: "5/x" becomes "\frac{5}{x}"

                // First part is the numerator
                result.append("\\frac{").append(parts[0]).append("}{");

                // Last part is the denominator
                result.append(parts[parts.length - 1]).append("}");

                // Note: This is a simplified approach that works for basic fractions
                // More complex expressions with multiple division operators would need
                // a more sophisticated parser
            } else {
                // If splitting didn't work as expected, keep the original
                result.append(latexFormula);
            }

            latexFormula = result.toString();
        }

        //--------------------------------------------------
        // Step 3: Handle exponents (convert x^n to x^{n})
        //--------------------------------------------------
        if (latexFormula.contains("^")) {
            StringBuilder result = new StringBuilder();
            int i = 0;

            // Process the string character by character
            while (i < latexFormula.length()) {
                char c = latexFormula.charAt(i);

                // When we find a caret (^), we need special handling
                if (c == '^' && i + 1 < latexFormula.length()) {
                    result.append('^');

                    // Case 1: The exponent is already enclosed in braces
                    if (latexFormula.charAt(i + 1) == '{') {
                        // Add the opening brace
                        result.append(latexFormula.charAt(i + 1));
                        i += 2;

                        // Copy everything until the matching closing brace
                        // We need to keep track of nested braces
                        int braceCount = 1;
                        while (i < latexFormula.length() && braceCount > 0) {
                            char nextChar = latexFormula.charAt(i);
                            result.append(nextChar);

                            // Update brace counter
                            if (nextChar == '{') braceCount++;
                            if (nextChar == '}') braceCount--;

                            i++;
                        }
                    } 
                    // Case 2: The exponent is not in braces, so we need to add them
                    else {
                        result.append('{');

                        // Case 2a: If the exponent is a number, include all consecutive digits
                        // Example: x^123 becomes x^{123}
                        if (Character.isDigit(latexFormula.charAt(i + 1))) {
                            int j = i + 1;
                            while (j < latexFormula.length() && Character.isDigit(latexFormula.charAt(j))) {
                                result.append(latexFormula.charAt(j));
                                j++;
                            }
                            i = j;
                        } 
                        // Case 2b: If the exponent is a single character, just include it
                        // Example: x^y becomes x^{y}
                        else {
                            result.append(latexFormula.charAt(i + 1));
                            i += 2;
                        }

                        // Close the braces for the exponent
                        result.append('}');
                    }
                } 
                // For any character that's not part of an exponent, just copy it
                else {
                    result.append(c);
                    i++;
                }
            }

            latexFormula = result.toString();
        }

        return latexFormula;
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

    // Math Operation Button Handlers have been replaced by the Symbol Menu Handlers

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

private void showError(String message) {
    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
}
