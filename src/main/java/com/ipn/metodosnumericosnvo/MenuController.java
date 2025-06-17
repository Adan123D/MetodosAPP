package com.ipn.metodosnumericosnvo;

// JavaFX imports
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

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

        // Here you can add specific logic for each menu item
        // based on the menuText value
    }

    /**
     * Handles the click event for the Gráfica button.
     * This method will be used to display graphs of mathematical functions.
     */
    @FXML
    protected void onGraficaButtonClick() {
        welcomeText.setText("Función de Gráfica seleccionada");
        // Here you would add code to display a graph
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

            // Square root and nth root
            .replace("sqrt", "\\sqrt")

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
            .replace("inf", "\\infty"); // Infinity symbol

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
    // Utility Methods
    //--------------------------------------------------

    /**
     * Displays an error message to the user.
     * This method updates the UI to show an error message in the function visualizer.
     * It performs three actions:
     * 1. Sets the text of the error label
     * 2. Makes the error label visible
     * 3. Updates the welcome text to indicate an error
     * 
     * @param message The error message to display to the user
     */
    private void showError(String message) {
        // Set the error message text
        functionErrorLabel.setText(message);

        // Make the error label visible
        functionErrorLabel.setVisible(true);

        // Update the welcome text to indicate an error
        welcomeText.setText("Error en la función");
    }
}
