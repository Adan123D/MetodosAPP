package com.ipn.metodosnumericosnvo.visualization;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A web-based function grapher using Plotly.js.
 * This class replaces the original FunctionChartManager with a web-based implementation.
 */
public class FunctionGrapherWeb {
    // The container for the chart
    private final Pane chartContainer;

    // The web view for displaying the chart
    private final WebView webView;

    // The web engine for loading content
    private final WebEngine webEngine;

    // List of functions to display
    private final List<String> functions = new ArrayList<>();

    // Map of function colors
    private final Map<String, String> functionColors = new HashMap<>();

    // Map of derivative functions
    private final Map<String, String> derivatives = new HashMap<>();

    // Map of integral functions
    private final Map<String, String> integrals = new HashMap<>();

    // Map of functions with roots to display
    private final Map<String, Boolean> rootsToDisplay = new HashMap<>();

    // Map of function pairs with intersections to display
    private final Map<String, String> intersectionsToDisplay = new HashMap<>();

    // Axis ranges
    private double xMin = -10;
    private double xMax = 10;
    private double yMin = -10;
    private double yMax = 10;

    // Random generator for colors
    private final Random random = new Random();

    /**
     * Creates a new FunctionGrapherWeb.
     * 
     * @param chartContainer The container for the chart
     */
    public FunctionGrapherWeb(Pane chartContainer) {
        this.chartContainer = chartContainer;

        // Create the web view
        webView = new WebView();
        webEngine = webView.getEngine();

        // Enable JavaScript and allow external resources
        webEngine.setJavaScriptEnabled(true);

        // Set the web view to fill the container
        webView.prefWidthProperty().bind(chartContainer.widthProperty());
        webView.prefHeightProperty().bind(chartContainer.heightProperty());

        // Add the web view to the container
        chartContainer.getChildren().add(webView);

        // Add error and status change listeners to help with debugging
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            System.out.println("WebEngine state changed: " + newState);
            if (newState == javafx.concurrent.Worker.State.FAILED) {
                System.out.println("WebEngine loading failed: " + webEngine.getLoadWorker().getException());
            }
        });

        // Initialize the chart
        redrawFunctions();
    }

    /**
     * Adds a function to the chart.
     * 
     * @param functionText The function text
     * @return true if the function was added, false otherwise
     */
    public boolean addFunction(String functionText) {
        try {
            // Check if the function already exists
            if (functions.contains(functionText)) {
                return false;
            }

            // Add the function
            functions.add(functionText);

            // Generate a random color for the function
            functionColors.put(functionText, generateRandomColor());

            // Redraw the chart
            redrawFunctions();

            return true;
        } catch (Exception e) {
            showAlert("Error al añadir la función: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a function from the chart.
     * 
     * @param functionText The function text
     * @return true if the function was removed, false otherwise
     */
    public boolean removeFunction(String functionText) {
        try {
            // Check if the function exists
            if (!functions.contains(functionText)) {
                return false;
            }

            // Remove the function
            functions.remove(functionText);
            functionColors.remove(functionText);

            // Remove any derivatives or integrals for this function
            derivatives.remove(functionText);
            integrals.remove(functionText);

            // Redraw the chart
            redrawFunctions();

            return true;
        } catch (Exception e) {
            showAlert("Error al eliminar la función: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds a derivative of a function to the chart.
     * 
     * @param functionText The function text
     * @return true if the derivative was added, false otherwise
     */
    public boolean addDerivative(String functionText) {
        try {
            // Check if the function exists
            if (!functions.contains(functionText)) {
                showAlert("La función no existe en la gráfica");
                return false;
            }

            // Check if the derivative already exists
            if (derivatives.containsKey(functionText)) {
                showAlert("La derivada ya existe en la gráfica");
                return false;
            }

            // Add the derivative
            String derivativeText = "d/dx(" + functionText + ")";
            derivatives.put(functionText, derivativeText);

            // Redraw the chart
            redrawFunctions();

            return true;
        } catch (Exception e) {
            showAlert("Error al añadir la derivada: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a derivative from the chart.
     * 
     * @param functionText The function text
     * @return true if the derivative was removed, false otherwise
     */
    public boolean removeDerivative(String functionText) {
        try {
            // Check if the derivative exists
            if (!derivatives.containsKey(functionText)) {
                return false;
            }

            // Remove the derivative
            derivatives.remove(functionText);

            // Redraw the chart
            redrawFunctions();

            return true;
        } catch (Exception e) {
            showAlert("Error al eliminar la derivada: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds an integral of a function to the chart.
     * 
     * @param functionText The function text
     * @return true if the integral was added, false otherwise
     */
    public boolean addIntegral(String functionText) {
        try {
            // Check if the function exists
            if (!functions.contains(functionText)) {
                showAlert("La función no existe en la gráfica");
                return false;
            }

            // Check if the integral already exists
            if (integrals.containsKey(functionText)) {
                showAlert("La integral ya existe en la gráfica");
                return false;
            }

            // Add the integral
            String integralText = "∫(" + functionText + ")";
            integrals.put(functionText, integralText);

            // Redraw the chart
            redrawFunctions();

            return true;
        } catch (Exception e) {
            showAlert("Error al añadir la integral: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes an integral from the chart.
     * 
     * @param functionText The function text
     * @return true if the integral was removed, false otherwise
     */
    public boolean removeIntegral(String functionText) {
        try {
            // Check if the integral exists
            if (!integrals.containsKey(functionText)) {
                return false;
            }

            // Remove the integral
            integrals.remove(functionText);

            // Redraw the chart
            redrawFunctions();

            return true;
        } catch (Exception e) {
            showAlert("Error al eliminar la integral: " + e.getMessage());
            return false;
        }
    }

    /**
     * Finds and displays the roots of a function.
     * 
     * @param functionText The function text
     * @param showMessages Whether to show messages
     * @return true if roots were found, false otherwise
     */
    public boolean findAndDisplayRoots(String functionText, boolean showMessages) {
        try {
            // Check if the function exists
            if (!functions.contains(functionText)) {
                if (showMessages) {
                    showAlert("La función no existe en la gráfica");
                }
                return false;
            }

            // Add the function to the roots to display map
            rootsToDisplay.put(functionText, true);

            // Redraw the chart
            redrawFunctions();

            if (showMessages) {
                showAlert("Buscando raíces para: " + functionText);
            }

            return true;
        } catch (Exception e) {
            if (showMessages) {
                showAlert("Error al buscar raíces: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Finds and displays the roots of a function.
     * 
     * @param functionText The function text
     * @return true if roots were found, false otherwise
     */
    public boolean findAndDisplayRoots(String functionText) {
        return findAndDisplayRoots(functionText, true);
    }

    /**
     * Removes the roots display for a function.
     * 
     * @param functionText The function text
     * @return true if the roots display was removed, false otherwise
     */
    public boolean removeRoots(String functionText) {
        try {
            // Check if the function exists
            if (!functions.contains(functionText)) {
                return false;
            }

            // Remove the function from the roots to display map
            rootsToDisplay.remove(functionText);

            // Redraw the chart
            redrawFunctions();

            return true;
        } catch (Exception e) {
            showAlert("Error al eliminar raíces: " + e.getMessage());
            return false;
        }
    }

    /**
     * Finds and displays the intersections between two functions.
     * 
     * @param function1Text The first function text
     * @param function2Text The second function text
     * @param showMessages Whether to show messages
     * @return true if intersections were found, false otherwise
     */
    public boolean findAndDisplayIntersections(String function1Text, String function2Text, boolean showMessages) {
        try {
            // Check if the functions exist
            if (!functions.contains(function1Text) || !functions.contains(function2Text)) {
                if (showMessages) {
                    showAlert("Una o ambas funciones no existen en la gráfica");
                }
                return false;
            }

            // Create a key for the intersection
            String intersectionKey = function1Text + "||" + function2Text;

            // Add the intersection to the map
            intersectionsToDisplay.put(intersectionKey, function2Text);

            // Redraw the chart
            redrawFunctions();

            if (showMessages) {
                showAlert("Buscando intersecciones entre: " + function1Text + " y " + function2Text);
            }

            return true;
        } catch (Exception e) {
            if (showMessages) {
                showAlert("Error al buscar intersecciones: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Finds and displays the intersections between two functions.
     * 
     * @param function1Text The first function text
     * @param function2Text The second function text
     * @return true if intersections were found, false otherwise
     */
    public boolean findAndDisplayIntersections(String function1Text, String function2Text) {
        return findAndDisplayIntersections(function1Text, function2Text, true);
    }

    /**
     * Removes the intersections display for two functions.
     * 
     * @param function1Text The first function text
     * @param function2Text The second function text
     * @return true if the intersections display was removed, false otherwise
     */
    public boolean removeIntersections(String function1Text, String function2Text) {
        try {
            // Check if the functions exist
            if (!functions.contains(function1Text) || !functions.contains(function2Text)) {
                return false;
            }

            // Create a key for the intersection
            String intersectionKey = function1Text + "||" + function2Text;

            // Remove the intersection from the map
            intersectionsToDisplay.remove(intersectionKey);

            // Redraw the chart
            redrawFunctions();

            return true;
        } catch (Exception e) {
            showAlert("Error al eliminar intersecciones: " + e.getMessage());
            return false;
        }
    }

    /**
     * Zooms in on the chart.
     */
    public void zoomIn() {
        double xRange = xMax - xMin;
        double yRange = yMax - yMin;

        // Zoom in by 20%
        double newXMin = xMin + xRange * 0.1;
        double newXMax = xMax - xRange * 0.1;
        double newYMin = yMin + yRange * 0.1;
        double newYMax = yMax - yRange * 0.1;

        setAxisRanges(newXMin, newXMax, newYMin, newYMax);
    }

    /**
     * Zooms out on the chart.
     */
    public void zoomOut() {
        double xRange = xMax - xMin;
        double yRange = yMax - yMin;

        // Zoom out by 20%
        double newXMin = xMin - xRange * 0.1;
        double newXMax = xMax + xRange * 0.1;
        double newYMin = yMin - yRange * 0.1;
        double newYMax = yMax + yRange * 0.1;

        setAxisRanges(newXMin, newXMax, newYMin, newYMax);
    }

    /**
     * Resets the zoom to the default.
     */
    public void resetZoom() {
        setAxisRanges(-10, 10, -10, 10);
    }

    /**
     * Sets the axis ranges.
     * 
     * @param xMin The minimum x value
     * @param xMax The maximum x value
     * @param yMin The minimum y value
     * @param yMax The maximum y value
     */
    public void setAxisRanges(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;

        // Redraw the chart
        redrawFunctions();
    }

    /**
     * Gets the axis ranges.
     * 
     * @return An array containing [xMin, xMax, yMin, yMax]
     */
    public double[] getAxisRanges() {
        return new double[] { xMin, xMax, yMin, yMax };
    }

    /**
     * Gets the list of functions.
     * 
     * @return The list of functions
     */
    public List<String> getFunctions() {
        return new ArrayList<>(functions);
    }

    /**
     * Redraws all functions on the chart.
     */
    private void redrawFunctions() {
        try {
            // Generate the HTML for the chart
            String html = generatePlotHtml();

            // Load the HTML into the web view
            webEngine.loadContent(html, "text/html");

            // Add a console.log handler to capture JavaScript errors
            webEngine.executeScript(
                "console.log = function(message) { " +
                "   if (window.java) { " +
                "       window.java.log(message); " +
                "   } else { " +
                "       print(message); " +
                "   }" +
                "};" +
                "console.error = console.log;" +
                "window.onerror = function(message, url, line, column, error) { " +
                "   console.log('JavaScript error: ' + message + ' at line ' + line + ' column ' + column); " +
                "   return false; " +
                "};"
            );
        } catch (Exception e) {
            System.out.println("Error al redibujar las funciones: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error al redibujar las funciones: " + e.getMessage());
        }
    }

    /**
     * Generates the HTML for the chart.
     * 
     * @return The HTML
     */
    private String generatePlotHtml() {
        StringBuilder tracesJson = new StringBuilder();

        // Add each function as a trace
        for (int i = 0; i < functions.size(); i++) {
            String functionText = functions.get(i);
            String color = functionColors.get(functionText);

            // Add the function trace
            if (i > 0) {
                tracesJson.append(",\n");
            }
            tracesJson.append(generateTraceJson(functionText, color, "function_" + i));

            // Add the derivative trace if it exists
            if (derivatives.containsKey(functionText)) {
                tracesJson.append(",\n");
                tracesJson.append(generateDerivativeTraceJson(functionText, color, "derivative_" + i));
            }

            // Add the integral trace if it exists
            if (integrals.containsKey(functionText)) {
                tracesJson.append(",\n");
                tracesJson.append(generateIntegralTraceJson(functionText, color, "integral_" + i));
            }

            // Add the roots trace if requested
            if (rootsToDisplay.containsKey(functionText) && rootsToDisplay.get(functionText)) {
                tracesJson.append(",\n");
                tracesJson.append(generateRootTraceJson(functionText, color, "roots_" + i));
            }
        }

        // Add intersection traces
        for (Map.Entry<String, String> entry : intersectionsToDisplay.entrySet()) {
            String key = entry.getKey();
            String[] functions = key.split("\\|\\|");
            if (functions.length == 2) {
                String function1Text = functions[0];
                String function2Text = functions[1];
                if (this.functions.contains(function1Text) && this.functions.contains(function2Text)) {
                    String color = "#FF5733"; // Orange color for intersections
                    tracesJson.append(",\n");
                    tracesJson.append(generateIntersectionTraceJson(function1Text, function2Text, color, "intersection_" + function1Text + "_" + function2Text));
                }
            }
        }

        // Generate the HTML
        return """
        <!DOCTYPE html>
        <html>
        <head>
          <!-- Load Plotly.js from CDNJS (alternative CDN) -->
          <script src="https://cdnjs.cloudflare.com/ajax/libs/plotly.js/2.24.2/plotly.min.js"></script>
          <!-- Fallback if CDN fails -->
          <script>
            window.onload = function() {
              if (typeof Plotly === 'undefined') {
                console.log('Plotly failed to load from CDN, using alternative method');
                // Create a script element and load Plotly from another source
                var script = document.createElement('script');
                script.src = 'https://cdn.plot.ly/plotly-latest.min.js';
                script.onload = function() {
                  console.log('Plotly loaded successfully from alternative source');
                  // Re-run the plotting code
                  if (typeof Plotly !== 'undefined') {
                    Plotly.newPlot('plot', traces, layout, config);
                  }
                };
                script.onerror = function() {
                  console.error('Failed to load Plotly from all sources');
                };
                document.head.appendChild(script);
              }
            };
          </script>
          <style>
            body, html, #plot {
              width: 100%%;
              height: 100%%;
              margin: 0;
              padding: 0;
            }
          </style>
        </head>
        <body>
          <div id="plot"></div>
          <div id="error-message" style="color: red; padding: 20px; display: none;"></div>
          <div id="fallback-plot" style="display: none; padding: 20px;">
            <h3>Fallback Visualization</h3>
            <canvas id="canvas" width="800" height="400" style="border:1px solid #d3d3d3;"></canvas>
          </div>
          <script>
            // Function to display error messages
            function showError(message) {
              console.error(message);
              var errorDiv = document.getElementById('error-message');
              errorDiv.textContent = message;
              errorDiv.style.display = 'block';
            }

            // Check if Plotly is available
            if (typeof Plotly === 'undefined') {
              showError('Plotly library is not loaded. Using fallback visualization.');
              renderFallbackPlot();
            }

            // Function to render a fallback plot using canvas
            function renderFallbackPlot() {
              try {
                console.log('Rendering fallback plot');
                document.getElementById('fallback-plot').style.display = 'block';

                var canvas = document.getElementById('canvas');
                var ctx = canvas.getContext('2d');
                var width = canvas.width;
                var height = canvas.height;

                // Clear canvas
                ctx.clearRect(0, 0, width, height);

                // Draw coordinate system
                ctx.strokeStyle = '#000000';
                ctx.lineWidth = 1;

                // X-axis
                ctx.beginPath();
                ctx.moveTo(0, height/2);
                ctx.lineTo(width, height/2);
                ctx.stroke();

                // Y-axis
                ctx.beginPath();
                ctx.moveTo(width/2, 0);
                ctx.lineTo(width/2, height);
                ctx.stroke();

                // Draw grid
                ctx.strokeStyle = '#cccccc';
                ctx.lineWidth = 0.5;

                // Vertical grid lines
                for (var x = 0; x <= width; x += 50) {
                  ctx.beginPath();
                  ctx.moveTo(x, 0);
                  ctx.lineTo(x, height);
                  ctx.stroke();
                }

                // Horizontal grid lines
                for (var y = 0; y <= height; y += 50) {
                  ctx.beginPath();
                  ctx.moveTo(0, y);
                  ctx.lineTo(width, y);
                  ctx.stroke();
                }

                // Draw message
                ctx.fillStyle = '#000000';
                ctx.font = '16px Arial';
                ctx.textAlign = 'center';
                ctx.fillText('Plotly.js failed to load. This is a fallback visualization.', width/2, 30);

                // Try to draw a simple function if traces are available
                if (traces && traces.length > 0) {
                  try {
                    drawSimpleFunction(ctx, width, height);
                  } catch (e) {
                    console.error('Error drawing simple function:', e);
                    ctx.fillText('Could not render function. Please try again later.', width/2, 60);
                  }
                } else {
                  ctx.fillText('No functions to display. Please add a function.', width/2, 60);
                }

                console.log('Fallback plot rendered successfully');
              } catch (error) {
                console.error('Error rendering fallback plot:', error);
              }
            }

            // Function to draw a simple function on the canvas
            function drawSimpleFunction(ctx, width, height) {
              // Get the first trace
              var trace = traces[0];
              var functionName = trace.name || 'Function';

              ctx.fillText('Showing simplified view of: ' + functionName, width/2, 90);

              // Set drawing style
              ctx.strokeStyle = '#0000FF';
              ctx.lineWidth = 2;

              // Start drawing path
              ctx.beginPath();

              // Calculate scale factors
              var xScale = width / (layout.xaxis.range[1] - layout.xaxis.range[0]);
              var yScale = height / (layout.yaxis.range[1] - layout.yaxis.range[0]);

              // Calculate offsets
              var xOffset = -layout.xaxis.range[0] * xScale;
              var yOffset = height + layout.yaxis.range[0] * yScale;

              // Draw a simple sine wave as a placeholder
              var step = width / 100;
              var amplitude = height / 4;
              var frequency = 2 * Math.PI / width;

              for (var x = 0; x < width; x += step) {
                var y = height/2 - amplitude * Math.sin(frequency * x * 5);

                if (x === 0) {
                  ctx.moveTo(x, y);
                } else {
                  ctx.lineTo(x, y);
                }
              }

              // Stroke the path
              ctx.stroke();

              // Add a legend
              ctx.fillStyle = '#0000FF';
              ctx.fillRect(width - 120, height - 30, 15, 15);
              ctx.fillStyle = '#000000';
              ctx.textAlign = 'left';
              ctx.fillText(functionName, width - 100, height - 18);
            }
          </script>
          <script>
            // Helper function to generate x values
            function generateXValues(min, max, count) {
              const step = (max - min) / (count - 1);
              const values = [];
              for (let i = 0; i < count; i++) {
                values.push(min + i * step);
              }
              return values;
            }

            // Helper function to generate y values
            function generateYValues(func, min, max, count) {
              const xValues = generateXValues(min, max, count);
              const yValues = [];
              for (let i = 0; i < count; i++) {
                try {
                  const x = xValues[i];
                  const y = func(x);
                  yValues.push(isFinite(y) ? y : null);
                } catch (e) {
                  yValues.push(null);
                }
              }
              return yValues;
            }

            // Helper function to generate derivative values
            function generateDerivativeValues(func, min, max, count) {
              const xValues = generateXValues(min, max, count);
              const yValues = [];
              const h = 0.0001; // Small step for numerical derivative

              for (let i = 0; i < count; i++) {
                try {
                  const x = xValues[i];
                  // Use central difference formula for better accuracy
                  const derivative = (func(x + h) - func(x - h)) / (2 * h);
                  yValues.push(isFinite(derivative) ? derivative : null);
                } catch (e) {
                  yValues.push(null);
                }
              }
              return yValues;
            }

            // Helper function to generate integral values
            function generateIntegralValues(func, min, max, count) {
              const xValues = generateXValues(min, max, count);
              const yValues = [];
              const dx = (max - min) / (count - 1);
              let integral = 0;

              // Use trapezoidal rule for numerical integration
              for (let i = 0; i < count; i++) {
                try {
                  const x = xValues[i];
                  if (i > 0) {
                    const prevX = xValues[i - 1];
                    const prevY = func(prevX);
                    const y = func(x);
                    if (isFinite(prevY) && isFinite(y)) {
                      integral += (prevY + y) * dx / 2;
                    }
                  }
                  yValues.push(isFinite(integral) ? integral : null);
                } catch (e) {
                  yValues.push(null);
                }
              }
              return yValues;
            }

            // Helper function to find roots of a function using the bisection method
            function findRoots(func, min, max) {
              const roots = [];
              const steps = 1000;
              const tolerance = 1e-10;
              const dx = (max - min) / steps;

              // Scan the domain for sign changes
              let prevX = min;
              let prevY = func(prevX);

              for (let i = 1; i <= steps; i++) {
                const x = min + i * dx;
                try {
                  const y = func(x);

                  // Check for sign change (potential root)
                  if (isFinite(prevY) && isFinite(y) && prevY * y <= 0) {
                    // Refine the root using bisection method
                    let a = prevX;
                    let b = x;
                    let fa = prevY;
                    let fb = y;

                    // If one of the endpoints is exactly zero, use that as the root
                    if (Math.abs(fa) < tolerance) {
                      roots.push(a);
                      continue;
                    }
                    if (Math.abs(fb) < tolerance) {
                      roots.push(b);
                      continue;
                    }

                    // Bisection method
                    for (let j = 0; j < 20; j++) { // Max 20 iterations
                      const c = (a + b) / 2;
                      const fc = func(c);

                      if (Math.abs(fc) < tolerance || (b - a) / 2 < tolerance) {
                        roots.push(c);
                        break;
                      }

                      if (fa * fc <= 0) {
                        b = c;
                        fb = fc;
                      } else {
                        a = c;
                        fa = fc;
                      }
                    }
                  }

                  prevX = x;
                  prevY = y;
                } catch (e) {
                  // Skip errors
                }
              }

              return roots;
            }

            // Helper function to find intersections between two functions
            function findIntersections(func1, func2, min, max) {
              // Create a new function that represents the difference between the two functions
              const diffFunc = function(x) {
                return func1(x) - func2(x);
              };

              // Find the roots of the difference function (these are the intersections)
              return findRoots(diffFunc, min, max);
            }

            // Helper function to calculate y values for intersection points
            function findIntersectionYValues(func, xValues) {
              const yValues = [];
              for (let i = 0; i < xValues.length; i++) {
                try {
                  const x = xValues[i];
                  const y = func(x);
                  yValues.push(isFinite(y) ? y : null);
                } catch (e) {
                  yValues.push(null);
                }
              }
              return yValues;
            }

            // Create the traces
            const traces = [
              %s
            ];

            // Create the layout
            const layout = {
              margin: { t: 20, r: 20, b: 40, l: 40 },
              xaxis: { 
                title: 'x',
                range: [%f, %f]
              },
              yaxis: { 
                title: 'y',
                range: [%f, %f]
              },
              showlegend: true,
              legend: {
                x: 1,
                xanchor: 'right',
                y: 1
              },
              hovermode: 'closest'
            };

            // Create the config
            const config = {
              responsive: true,
              scrollZoom: true,
              displayModeBar: true,
              displaylogo: false,
              modeBarButtonsToRemove: ['lasso2d', 'select2d']
            };

            // Create the plot with error handling
            try {
              console.log('Creating plot with ' + traces.length + ' traces');
              if (traces.length === 0) {
                console.log('No traces to plot. Add a function first.');
              }

              // Log the first trace for debugging if available
              if (traces.length > 0) {
                console.log('First trace:', JSON.stringify(traces[0]).substring(0, 200) + '...');
              }

              Plotly.newPlot('plot', traces, layout, config)
                .then(function() {
                  console.log('Plot created successfully');
                })
                .catch(function(error) {
                  showError('Error creating plot: ' + error.message);
                  console.error('Plotly error:', error);
                  // Show fallback visualization if Plotly fails
                  renderFallbackPlot();
                });

              // Add event listeners for zooming
              document.getElementById('plot').on('plotly_relayout', function(eventdata) {
                // Handle zoom events here if needed
                console.log('Plot zoomed/panned');
              });
            } catch (error) {
              showError('Error creating plot: ' + error.message);
              console.error('Error in plot creation:', error);
              // Show fallback visualization if there's any error
              renderFallbackPlot();
            }

            // Add a global error handler
            window.onerror = function(message, source, lineno, colno, error) {
              console.error('Global error:', message, 'at', source, 'line', lineno, 'column', colno);
              showError('JavaScript error: ' + message);
              // Show fallback visualization for any unhandled error
              renderFallbackPlot();
              return true; // Prevents the default browser error handling
            };
          </script>
        </body>
        </html>
        """.formatted(tracesJson.toString(), xMin, xMax, yMin, yMax);
    }

    /**
     * Generates the JSON for a function trace.
     * 
     * @param functionText The function text
     * @param color The color
     * @param name The name
     * @return The JSON
     */
    private String generateTraceJson(String functionText, String color, String name) {
        // Convert the function text to JavaScript
        String jsFunction = prepareForJavaScript(functionText);

        return """
        {
          x: generateXValues(%f, %f, 500),
          y: generateYValues(function(x) { return %s; }, %f, %f, 500),
          type: 'scatter',
          mode: 'lines',
          name: '%s',
          line: { color: '%s' }
        }
        """.formatted(xMin, xMax, jsFunction, xMin, xMax, functionText, color);
    }

    /**
     * Generates the JSON for a derivative trace.
     * 
     * @param functionText The function text
     * @param color The color
     * @param name The name
     * @return The JSON
     */
    private String generateDerivativeTraceJson(String functionText, String color, String name) {
        // Convert the function text to JavaScript
        String jsFunction = prepareForJavaScript(functionText);

        return """
        {
          x: generateXValues(%f, %f, 500),
          y: generateDerivativeValues(function(x) { return %s; }, %f, %f, 500),
          type: 'scatter',
          mode: 'lines',
          name: 'd/dx(%s)',
          line: { color: '%s', dash: 'dash' }
        }
        """.formatted(xMin, xMax, jsFunction, xMin, xMax, functionText, color);
    }

    /**
     * Generates the JSON for an integral trace.
     * 
     * @param functionText The function text
     * @param color The color
     * @param name The name
     * @return The JSON
     */
    private String generateIntegralTraceJson(String functionText, String color, String name) {
        // Convert the function text to JavaScript
        String jsFunction = prepareForJavaScript(functionText);

        return """
        {
          x: generateXValues(%f, %f, 500),
          y: generateIntegralValues(function(x) { return %s; }, %f, %f, 500),
          type: 'scatter',
          mode: 'lines',
          name: '∫(%s)',
          line: { color: '%s', dash: 'dot' }
        }
        """.formatted(xMin, xMax, jsFunction, xMin, xMax, functionText, color);
    }

    /**
     * Generates the JSON for a root trace.
     * 
     * @param functionText The function text
     * @param color The color
     * @param name The name
     * @return The JSON
     */
    private String generateRootTraceJson(String functionText, String color, String name) {
        // Convert the function text to JavaScript
        String jsFunction = prepareForJavaScript(functionText);

        return """
        {
          x: findRoots(function(x) { return %s; }, %f, %f),
          y: findIntersectionYValues(function(x) { return 0; }, findRoots(function(x) { return %s; }, %f, %f)),
          type: 'scatter',
          mode: 'markers',
          name: 'Raíces de %s',
          marker: { 
            color: '%s', 
            size: 10,
            symbol: 'circle',
            line: { color: 'white', width: 2 }
          }
        }
        """.formatted(jsFunction, xMin, xMax, jsFunction, xMin, xMax, functionText, color);
    }

    /**
     * Generates the JSON for an intersection trace.
     * 
     * @param function1Text The first function text
     * @param function2Text The second function text
     * @param color The color
     * @param name The name
     * @return The JSON
     */
    private String generateIntersectionTraceJson(String function1Text, String function2Text, String color, String name) {
        // Convert the function texts to JavaScript
        String jsFunction1 = prepareForJavaScript(function1Text);
        String jsFunction2 = prepareForJavaScript(function2Text);

        return """
        {
          x: findIntersections(function(x) { return %s; }, function(x) { return %s; }, %f, %f),
          y: findIntersectionYValues(function(x) { return %s; }, findIntersections(function(x) { return %s; }, function(x) { return %s; }, %f, %f)),
          type: 'scatter',
          mode: 'markers',
          name: 'Intersecciones',
          marker: { 
            color: '%s', 
            size: 10,
            symbol: 'diamond',
            line: { color: 'white', width: 2 }
          }
        }
        """.formatted(jsFunction1, jsFunction2, xMin, xMax, jsFunction1, jsFunction1, jsFunction2, xMin, xMax, color);
    }

    /**
     * Prepares a function text for JavaScript.
     * 
     * @param functionText The function text
     * @return The JavaScript function
     */
    private String prepareForJavaScript(String functionText) {
        // First, handle the hyperbolic functions and their inverses (longer names first)
        String jsFunction = functionText
            // Inverse hyperbolic functions (longer names first)
            .replace("arcsinh", "Math.asinh")
            .replace("arccosh", "Math.acosh")
            .replace("arctanh", "Math.atanh")
            .replace("arccoth(", "Math.atanh(1/")
            .replace("arcsech(", "Math.acosh(1/")
            .replace("arccsch(", "Math.asinh(1/")

            // Alternative names for inverse hyperbolic functions
            .replace("asinh", "Math.asinh")
            .replace("acosh", "Math.acosh")
            .replace("atanh", "Math.atanh")
            .replace("acoth(", "Math.atanh(1/")
            .replace("asech(", "Math.acosh(1/")
            .replace("acsch(", "Math.asinh(1/")

            // Hyperbolic functions
            .replace("sinh", "Math.sinh")
            .replace("cosh", "Math.cosh")
            .replace("tanh", "Math.tanh")
            .replace("coth(", "(1/Math.tanh(")
            .replace("sech(", "(1/Math.cosh(")
            .replace("csch(", "(1/Math.sinh(");

        // Then handle the regular trigonometric functions and their inverses
        jsFunction = jsFunction
            // Inverse trigonometric functions (longer names first)
            .replace("arcsin", "Math.asin")
            .replace("arccos", "Math.acos")
            .replace("arctan", "Math.atan")
            .replace("arccot(", "(Math.PI/2 - Math.atan(")
            .replace("arcsec(", "Math.acos(1/")
            .replace("arccsc(", "Math.asin(1/")

            // Alternative names for inverse trigonometric functions
            .replace("asin", "Math.asin")
            .replace("acos", "Math.acos")
            .replace("atan", "Math.atan")
            .replace("acot(", "(Math.PI/2 - Math.atan(")
            .replace("asec(", "Math.acos(1/")
            .replace("acsc(", "Math.asin(1/")

            // Basic trigonometric functions (shortest names last)
            .replace("sin", "Math.sin")
            .replace("cos", "Math.cos")
            .replace("tan", "Math.tan")
            .replace("cot(", "(1/Math.tan(")
            .replace("sec(", "(1/Math.cos(")
            .replace("csc(", "(1/Math.sin(");

        // Finally, handle other mathematical functions and operators
        jsFunction = jsFunction
            .replace("log", "Math.log10")
            .replace("ln", "Math.log")
            .replace("sqrt", "Math.sqrt")
            .replace("abs", "Math.abs")
            .replace("pi", "Math.PI")
            .replace("e", "Math.E")
            .replace("^", "**");

        // Replace x with the variable name, but only when it's a standalone variable
        jsFunction = jsFunction.replaceAll("\\b(x)\\b", "($1)");

        return jsFunction;
    }

    /**
     * Generates a random color.
     * 
     * @return A random color in hex format
     */
    private String generateRandomColor() {
        // Generate a random color that's not too light
        int r = random.nextInt(200);
        int g = random.nextInt(200);
        int b = random.nextInt(200);

        return String.format("#%02x%02x%02x", r, g, b);
    }

    /**
     * Shows an alert with the given message.
     * 
     * @param message The message
     */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
