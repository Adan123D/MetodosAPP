package com.ipn.metodosnumericosnvo.visualization;

import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.AmbientLight;
import javafx.scene.PointLight;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// Direct Rhino imports for fallback
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Manager class for creating and displaying 3D function charts.
 * This class uses JavaFX 3D to visualize mathematical functions in 3D.
 */
public class Function3DChartManager {

    // The container for the chart
    private Pane chartContainer;

    // 3D scene components
    private SubScene subScene;
    private Group root3D;
    private PerspectiveCamera camera;
    private Group meshGroup;

    // Rotation variables for mouse control
    private double mouseOldX, mouseOldY;
    private double mousePosX, mousePosY;
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-45, Rotate.Y_AXIS);
    private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

    // The list of functions
    private List<String> functions;

    // Map to store function names and their corresponding mesh views
    private Map<String, MeshView> functionMeshes;

    // JavaScript engine for evaluating functions
    private ScriptEngine engine;
    private boolean useDirectRhino = false;

    // Range for the 3D plot
    private double xMin = -5.0;
    private double xMax = 5.0;
    private double yMin = -5.0;
    private double yMax = 5.0;
    private double zMin = -5.0;
    private double zMax = 5.0;

    // Resolution of the mesh (number of points in each direction)
    private int resolution = 50;

    // Random color generator
    private Random random = new Random();

    /**
     * Constructor for the Function3DChartManager.
     * 
     * @param chartContainer The JavaFX pane that will contain the 3D chart
     */
    public Function3DChartManager(Pane chartContainer) {
        this.chartContainer = chartContainer;
        this.functions = new ArrayList<>();
        this.functionMeshes = new HashMap<>();

        // Initialize the JavaScript engine
        initializeScriptEngine();

        // Create the 3D scene
        create3DScene();

        // Add the scene to the container
        this.chartContainer.getChildren().add(subScene);
    }

    /**
     * Initializes the JavaScript engine with fallback options.
     */
    private void initializeScriptEngine() {
        try {
            // First, try to use Rhino directly as it's the most reliable method
            try {
                // Test if we can use Rhino directly
                Context context = Context.enter();
                try {
                    Scriptable scope = context.initStandardObjects();
                    Object result = context.evaluateString(scope, "1+1", "test", 1, null);

                    // If we get here, we can use Rhino directly
                    useDirectRhino = true;
                    return;
                } finally {
                    Context.exit();
                }
            } catch (Exception e) {
                useDirectRhino = false;
                // Continue with other methods if direct Rhino fails
            }

            // If direct Rhino fails, try to create a ScriptEngineManager
            ScriptEngineManager manager = new ScriptEngineManager();

            // Try different engine names in order of preference
            String[] engineNames = {"rhino", "Rhino", "js", "JavaScript", "nashorn", "graal.js"};

            for (String engineName : engineNames) {
                this.engine = manager.getEngineByName(engineName);
                if (this.engine != null) {
                    // Test the engine with a simple expression
                    try {
                        Object result = this.engine.eval("1+1");
                        return;
                    } catch (Exception e) {
                        this.engine = null; // Reset and try next engine
                    }
                }
            }
        } catch (Exception e) {
            // If all methods fail, we'll use the fallback evaluator
        }
    }

    /**
     * Creates the 3D scene with camera, lights, and initial setup.
     */
    private void create3DScene() {
        // Create the root 3D group
        root3D = new Group();

        // Create a group for the meshes
        meshGroup = new Group();

        // Add transformations to the mesh group
        meshGroup.getTransforms().addAll(rotateX, rotateY, rotateZ);

        // Add the mesh group to the root
        root3D.getChildren().add(meshGroup);

        // Create and position the camera
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-30);

        // Create lights
        AmbientLight ambientLight = new AmbientLight(Color.color(0.2, 0.2, 0.2));

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(100);
        pointLight.setTranslateY(-100);
        pointLight.setTranslateZ(-100);

        // Add lights to the root
        root3D.getChildren().addAll(ambientLight, pointLight);

        // Create the subscene
        subScene = new SubScene(root3D, chartContainer.getPrefWidth(), chartContainer.getPrefHeight(), true, javafx.scene.SceneAntialiasing.BALANCED);
        subScene.setFill(Color.LIGHTGRAY);
        subScene.setCamera(camera);

        // Make the subscene resize with the container
        subScene.widthProperty().bind(chartContainer.widthProperty());
        subScene.heightProperty().bind(chartContainer.heightProperty());

        // Add mouse handling for rotation
        setupMouseControl();

        // Add coordinate axes
        addCoordinateAxes();
    }

    /**
     * Adds coordinate axes to the 3D scene.
     */
    private void addCoordinateAxes() {
        // Create a group for the axes
        Group axesGroup = new Group();

        // X-axis (red)
        javafx.scene.shape.Box xAxis = new javafx.scene.shape.Box(10, 0.1, 0.1);
        xAxis.setTranslateX(5);
        PhongMaterial xMaterial = new PhongMaterial();
        xMaterial.setDiffuseColor(Color.RED);
        xAxis.setMaterial(xMaterial);

        // Y-axis (green)
        javafx.scene.shape.Box yAxis = new javafx.scene.shape.Box(0.1, 10, 0.1);
        yAxis.setTranslateY(5);
        PhongMaterial yMaterial = new PhongMaterial();
        yMaterial.setDiffuseColor(Color.GREEN);
        yAxis.setMaterial(yMaterial);

        // Z-axis (blue)
        javafx.scene.shape.Box zAxis = new javafx.scene.shape.Box(0.1, 0.1, 10);
        zAxis.setTranslateZ(5);
        PhongMaterial zMaterial = new PhongMaterial();
        zMaterial.setDiffuseColor(Color.BLUE);
        zAxis.setMaterial(zMaterial);

        // Add axes to the group
        axesGroup.getChildren().addAll(xAxis, yAxis, zAxis);

        // Add the axes group to the mesh group
        meshGroup.getChildren().add(axesGroup);
    }

    /**
     * Sets up mouse control for rotating, zooming, and panning the 3D scene.
     */
    private void setupMouseControl() {
        // Mouse pressed event
        subScene.setOnMousePressed((MouseEvent event) -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        // Mouse dragged event for rotation
        subScene.setOnMouseDragged((MouseEvent event) -> {
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();

            // Calculate rotation angles
            double deltaX = (mousePosX - mouseOldX);
            double deltaY = (mousePosY - mouseOldY);

            if (event.isPrimaryButtonDown()) {
                // Primary button: rotate around Y and X axes
                rotateY.setAngle(rotateY.getAngle() + deltaX * 0.2);
                rotateX.setAngle(rotateX.getAngle() - deltaY * 0.2);
            } else if (event.isSecondaryButtonDown()) {
                // Secondary button: rotate around Z axis
                rotateZ.setAngle(rotateZ.getAngle() + deltaX * 0.2);
            }

            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
        });

        // Mouse scroll event for zooming
        subScene.setOnScroll((ScrollEvent event) -> {
            double delta = event.getDeltaY();
            camera.setTranslateZ(camera.getTranslateZ() + delta * 0.1);
        });
    }

    /**
     * Adds a function to the 3D chart.
     * 
     * @param functionText The function text (e.g., "sin(x)*cos(y)")
     * @return true if the function was added successfully, false otherwise
     */
    public boolean addFunction(String functionText) {
        try {
            // Check if the function is already added
            if (functions.contains(functionText)) {
                showAlert("La función ya está en la gráfica.");
                return false;
            }

            // Create a mesh for the function
            MeshView meshView = createFunctionMesh(functionText);

            // Add the mesh to the group
            meshGroup.getChildren().add(meshView);

            // Add the function to the list
            functions.add(functionText);

            // Store the mesh for later reference
            functionMeshes.put(functionText, meshView);

            return true;
        } catch (Exception e) {
            showAlert("Error al añadir la función: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a function from the 3D chart.
     * 
     * @param functionText The function text to remove
     * @return true if the function was removed successfully, false otherwise
     */
    public boolean removeFunction(String functionText) {
        try {
            // Check if the function exists
            if (!functions.contains(functionText)) {
                showAlert("La función no está en la gráfica.");
                return false;
            }

            // Get the mesh for the function
            MeshView meshView = functionMeshes.get(functionText);

            // Remove the mesh from the group
            meshGroup.getChildren().remove(meshView);

            // Remove the function from the list
            functions.remove(functionText);

            // Remove the mesh from the map
            functionMeshes.remove(functionText);

            return true;
        } catch (Exception e) {
            showAlert("Error al eliminar la función: " + e.getMessage());
            return false;
        }
    }

    /**
     * Creates a mesh for a function.
     * 
     * @param functionText The function text
     * @return The MeshView for the function
     * @throws Exception If there's an error creating the mesh
     */
    private MeshView createFunctionMesh(String functionText) throws Exception {
        // Create a triangle mesh
        TriangleMesh mesh = new TriangleMesh();

        // Calculate step sizes
        float xStep = (float) ((xMax - xMin) / (resolution - 1));
        float yStep = (float) ((yMax - yMin) / (resolution - 1));

        // Create points
        for (int y = 0; y < resolution; y++) {
            float yPos = (float) (yMin + y * yStep);
            for (int x = 0; x < resolution; x++) {
                float xPos = (float) (xMin + x * xStep);

                // Evaluate the function at (xPos, yPos)
                float zPos;
                try {
                    zPos = (float) evaluateFunction(functionText, xPos, yPos);
                    // Handle NaN or infinity values
                    if (Float.isNaN(zPos) || Float.isInfinite(zPos)) {
                        zPos = 0.0f; // Default value for undefined points
                    }
                } catch (Exception e) {
                    zPos = 0.0f; // Default value for errors
                }

                // Normalize to [-1, 1] range for better visualization
                float xNorm = (float) (2.0 * (xPos - xMin) / (xMax - xMin) - 1.0);
                float yNorm = (float) (2.0 * (yPos - yMin) / (yMax - yMin) - 1.0);

                // Prevent division by zero when normalizing z values
                float zNorm;
                if (Math.abs(zMax - zMin) < 1e-6) {
                    zNorm = 0.0f; // Default value when range is too small
                } else {
                    zNorm = (float) (2.0 * (zPos - zMin) / (zMax - zMin) - 1.0);
                    // Clamp to [-1, 1] range to prevent rendering issues
                    zNorm = Math.max(-1.0f, Math.min(1.0f, zNorm));
                }

                // Add the point to the mesh
                mesh.getPoints().addAll(xNorm, yNorm, zNorm);
            }
        }

        // Create texture coordinates (not used, but required)
        mesh.getTexCoords().addAll(0, 0);

        // Create faces
        for (int y = 0; y < resolution - 1; y++) {
            for (int x = 0; x < resolution - 1; x++) {
                int p00 = y * resolution + x;
                int p10 = y * resolution + x + 1;
                int p01 = (y + 1) * resolution + x;
                int p11 = (y + 1) * resolution + x + 1;

                // Add two triangles for each grid cell
                // Triangle 1: p00, p10, p11
                mesh.getFaces().addAll(p00, 0, p10, 0, p11, 0);
                // Triangle 2: p00, p11, p01
                mesh.getFaces().addAll(p00, 0, p11, 0, p01, 0);
            }
        }

        // Create a mesh view
        MeshView meshView = new MeshView(mesh);

        // Create a material with a random color
        PhongMaterial material = new PhongMaterial();
        Color color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256), 0.7);
        material.setDiffuseColor(color);
        material.setSpecularColor(Color.WHITE);

        // Set the material
        meshView.setMaterial(material);

        // Set draw mode (FILL for solid, LINE for wireframe)
        meshView.setDrawMode(DrawMode.FILL);

        // Set cull face (NONE to see both sides of the surface)
        meshView.setCullFace(CullFace.NONE);

        return meshView;
    }

    /**
     * Evaluates a function at a point (x, y).
     * 
     * @param functionText The function text
     * @param x The x value
     * @param y The y value
     * @return The function value at (x, y)
     * @throws Exception If there's an error evaluating the function
     */
    private double evaluateFunction(String functionText, double x, double y) throws Exception {
        // Clean up the function text
        String cleanFunction = cleanFunctionText(functionText);

        // Prepare the function text for JavaScript evaluation
        String jsFunction = prepareForJavaScript(cleanFunction);

        if (useDirectRhino) {
            // Use Rhino directly
            return evaluateWithRhinoDirect(jsFunction, x, y);
        } else if (engine != null) {
            // Use the ScriptEngine
            // Set the variables in the engine
            engine.put("x", x);
            engine.put("y", y);

            // Evaluate the function
            Object result = engine.eval(jsFunction);

            // Convert the result to a double
            if (result instanceof Number) {
                return ((Number) result).doubleValue();
            } else {
                throw new ScriptException("La función no devuelve un número: " + result);
            }
        } else {
            // Use fallback evaluator for simple functions
            return evaluateWithFallback(functionText, x, y);
        }
    }

    /**
     * Evaluates a JavaScript function using Rhino directly.
     * 
     * @param jsFunction The JavaScript function to evaluate
     * @param x The x value
     * @param y The y value
     * @return The result of the evaluation
     * @throws Exception If there's an error evaluating the function
     */
    private double evaluateWithRhinoDirect(String jsFunction, double x, double y) throws Exception {
        Context context = Context.enter();
        try {
            // Initialize the standard objects and define Math functions
            Scriptable scope = context.initStandardObjects();

            // Set the x and y variables
            ScriptableObject.putProperty(scope, "x", Context.javaToJS(x, scope));
            ScriptableObject.putProperty(scope, "y", Context.javaToJS(y, scope));

            // Define Math object with all necessary functions if not already defined
            String mathSetup = 
                "if (typeof Math === 'undefined') {" +
                "  var Math = {" +
                "    // Trigonometric functions" +
                "    sin: function(x) { return java.lang.Math.sin(x); }," +
                "    cos: function(x) { return java.lang.Math.cos(x); }," +
                "    tan: function(x) { return java.lang.Math.tan(x); }," +
                "    cot: function(x) { return 1/java.lang.Math.tan(x); }," +
                "    sec: function(x) { return 1/java.lang.Math.cos(x); }," +
                "    csc: function(x) { return 1/java.lang.Math.sin(x); }," +
                "    // Inverse trigonometric functions" +
                "    asin: function(x) { return java.lang.Math.asin(x); }," +
                "    acos: function(x) { return java.lang.Math.acos(x); }," +
                "    atan: function(x) { return java.lang.Math.atan(x); }," +
                "    atan2: function(y, x) { return java.lang.Math.atan2(y, x); }," +
                "    // Hyperbolic functions" +
                "    sinh: function(x) { return java.lang.Math.sinh(x); }," +
                "    cosh: function(x) { return java.lang.Math.cosh(x); }," +
                "    tanh: function(x) { return java.lang.Math.tanh(x); }," +
                "    // Inverse hyperbolic functions" +
                "    asinh: function(x) { return java.lang.Math.log(x + java.lang.Math.sqrt(x*x + 1)); }," +
                "    acosh: function(x) { return java.lang.Math.log(x + java.lang.Math.sqrt(x*x - 1)); }," +
                "    atanh: function(x) { return 0.5 * java.lang.Math.log((1+x)/(1-x)); }," +
                "    // Root functions" +
                "    sqrt: function(x) { return java.lang.Math.sqrt(x); }," +
                "    cbrt: function(x) { return java.lang.Math.cbrt(x); }," +
                "    // Logarithmic functions" +
                "    log: function(x) { return java.lang.Math.log10(x); }," +
                "    log10: function(x) { return java.lang.Math.log10(x); }," +
                "    ln: function(x) { return java.lang.Math.log(x); }," +
                "    log2: function(x) { return java.lang.Math.log(x) / java.lang.Math.log(2); }," +
                "    // Exponential functions" +
                "    exp: function(x) { return java.lang.Math.exp(x); }," +
                "    // Other functions" +
                "    abs: function(x) { return java.lang.Math.abs(x); }," +
                "    sign: function(x) { return java.lang.Math.signum(x); }," +
                "    floor: function(x) { return java.lang.Math.floor(x); }," +
                "    ceil: function(x) { return java.lang.Math.ceil(x); }," +
                "    round: function(x) { return java.lang.Math.round(x); }," +
                "    trunc: function(x) { return java.lang.Math.floor(x); }," +
                "    min: function(x, y) { return java.lang.Math.min(x, y); }," +
                "    max: function(x, y) { return java.lang.Math.max(x, y); }," +
                "    pow: function(x, y) { return java.lang.Math.pow(x, y); }," +
                "    // Constants" +
                "    PI: " + Math.PI + "," +
                "    E: " + Math.E +
                "  };" +
                "}";

            // Execute the Math setup
            context.evaluateString(scope, mathSetup, "mathSetupScript", 1, null);

            // Evaluate the function
            Object result = context.evaluateString(scope, jsFunction, "function", 1, null);

            // Convert the result to a double
            if (result instanceof Number) {
                return Context.toNumber(result);
            } else {
                throw new Exception("La función no devuelve un número: " + result);
            }
        } finally {
            Context.exit();
        }
    }

    /**
     * Fallback method to evaluate simple functions when the JavaScript engine is not available.
     * 
     * @param functionText The function text
     * @param x The x value
     * @param y The y value
     * @return The function value at (x, y)
     * @throws Exception If there's an error evaluating the function
     */
    private double evaluateWithFallback(String functionText, double x, double y) throws Exception {
        // This is a simplified fallback that handles only a few common functions
        // For a real application, you would want to implement a more robust parser

        // Replace x and y with their values
        String expr = functionText.replace("x", String.valueOf(x)).replace("y", String.valueOf(y));

        // Handle some common functions
        // Trigonometric functions
        if (expr.contains("sin(")) {
            double arg = extractArgument(expr, "sin(");
            return Math.sin(arg);
        } else if (expr.contains("cos(")) {
            double arg = extractArgument(expr, "cos(");
            return Math.cos(arg);
        } else if (expr.contains("tan(")) {
            double arg = extractArgument(expr, "tan(");
            return Math.tan(arg);
        } else if (expr.contains("cot(")) {
            double arg = extractArgument(expr, "cot(");
            return 1.0 / Math.tan(arg);
        } else if (expr.contains("sec(")) {
            double arg = extractArgument(expr, "sec(");
            return 1.0 / Math.cos(arg);
        } else if (expr.contains("csc(")) {
            double arg = extractArgument(expr, "csc(");
            return 1.0 / Math.sin(arg);
        } 
        // Inverse trigonometric functions
        else if (expr.contains("arcsin(") || expr.contains("asin(")) {
            String funcName = expr.contains("arcsin(") ? "arcsin(" : "asin(";
            double arg = extractArgument(expr, funcName);
            return Math.asin(arg);
        } else if (expr.contains("arccos(") || expr.contains("acos(")) {
            String funcName = expr.contains("arccos(") ? "arccos(" : "acos(";
            double arg = extractArgument(expr, funcName);
            return Math.acos(arg);
        } else if (expr.contains("arctan(") || expr.contains("atan(")) {
            String funcName = expr.contains("arctan(") ? "arctan(" : "atan(";
            double arg = extractArgument(expr, funcName);
            return Math.atan(arg);
        } else if (expr.contains("atan2(")) {
            // atan2 takes two arguments, so we need special handling
            int start = expr.indexOf("atan2(") + 6;
            int end = findClosingParenthesis(expr, start);
            if (end == -1) {
                throw new Exception("Paréntesis no balanceados en: " + expr);
            }
            String argsStr = expr.substring(start, end);
            String[] args = argsStr.split(",");
            if (args.length != 2) {
                throw new Exception("atan2 requiere dos argumentos: " + expr);
            }
            double yArg = evaluateSimpleExpression(args[0].trim());
            double xArg = evaluateSimpleExpression(args[1].trim());
            return Math.atan2(yArg, xArg);
        } 
        // Hyperbolic functions
        else if (expr.contains("sinh(")) {
            double arg = extractArgument(expr, "sinh(");
            return Math.sinh(arg);
        } else if (expr.contains("cosh(")) {
            double arg = extractArgument(expr, "cosh(");
            return Math.cosh(arg);
        } else if (expr.contains("tanh(")) {
            double arg = extractArgument(expr, "tanh(");
            return Math.tanh(arg);
        } 
        // Inverse hyperbolic functions
        else if (expr.contains("asinh(")) {
            double arg = extractArgument(expr, "asinh(");
            return Math.log(arg + Math.sqrt(arg * arg + 1));
        } else if (expr.contains("acosh(")) {
            double arg = extractArgument(expr, "acosh(");
            return Math.log(arg + Math.sqrt(arg * arg - 1));
        } else if (expr.contains("atanh(")) {
            double arg = extractArgument(expr, "atanh(");
            return 0.5 * Math.log((1 + arg) / (1 - arg));
        } 
        // Root functions
        else if (expr.contains("sqrt(")) {
            double arg = extractArgument(expr, "sqrt(");
            return Math.sqrt(arg);
        } else if (expr.contains("cbrt(")) {
            double arg = extractArgument(expr, "cbrt(");
            return Math.cbrt(arg);
        } 
        // Logarithmic functions
        else if (expr.contains("log10(")) {
            double arg = extractArgument(expr, "log10(");
            return Math.log10(arg);
        } else if (expr.contains("log(")) {
            double arg = extractArgument(expr, "log(");
            return Math.log10(arg);
        } else if (expr.contains("ln(")) {
            double arg = extractArgument(expr, "ln(");
            return Math.log(arg);
        } else if (expr.contains("log2(")) {
            double arg = extractArgument(expr, "log2(");
            return Math.log(arg) / Math.log(2);
        } 
        // Exponential functions
        else if (expr.contains("exp(")) {
            double arg = extractArgument(expr, "exp(");
            return Math.exp(arg);
        } 
        // Other functions
        else if (expr.contains("abs(")) {
            double arg = extractArgument(expr, "abs(");
            return Math.abs(arg);
        } else if (expr.contains("sign(")) {
            double arg = extractArgument(expr, "sign(");
            return Math.signum(arg);
        } else if (expr.contains("floor(")) {
            double arg = extractArgument(expr, "floor(");
            return Math.floor(arg);
        } else if (expr.contains("ceil(")) {
            double arg = extractArgument(expr, "ceil(");
            return Math.ceil(arg);
        } else if (expr.contains("round(")) {
            double arg = extractArgument(expr, "round(");
            return Math.round(arg);
        } else if (expr.contains("trunc(")) {
            double arg = extractArgument(expr, "trunc(");
            return Math.floor(arg);
        } else if (expr.contains("min(")) {
            // min takes two arguments, so we need special handling
            int start = expr.indexOf("min(") + 4;
            int end = findClosingParenthesis(expr, start);
            if (end == -1) {
                throw new Exception("Paréntesis no balanceados en: " + expr);
            }
            String argsStr = expr.substring(start, end);
            String[] args = argsStr.split(",");
            if (args.length != 2) {
                throw new Exception("min requiere dos argumentos: " + expr);
            }
            double a = evaluateSimpleExpression(args[0].trim());
            double b = evaluateSimpleExpression(args[1].trim());
            return Math.min(a, b);
        } else if (expr.contains("max(")) {
            // max takes two arguments, so we need special handling
            int start = expr.indexOf("max(") + 4;
            int end = findClosingParenthesis(expr, start);
            if (end == -1) {
                throw new Exception("Paréntesis no balanceados en: " + expr);
            }
            String argsStr = expr.substring(start, end);
            String[] args = argsStr.split(",");
            if (args.length != 2) {
                throw new Exception("max requiere dos argumentos: " + expr);
            }
            double a = evaluateSimpleExpression(args[0].trim());
            double b = evaluateSimpleExpression(args[1].trim());
            return Math.max(a, b);
        } else if (expr.contains("pow(")) {
            // pow takes two arguments, so we need special handling
            int start = expr.indexOf("pow(") + 4;
            int end = findClosingParenthesis(expr, start);
            if (end == -1) {
                throw new Exception("Paréntesis no balanceados en: " + expr);
            }
            String argsStr = expr.substring(start, end);
            String[] args = argsStr.split(",");
            if (args.length != 2) {
                throw new Exception("pow requiere dos argumentos: " + expr);
            }
            double base = evaluateSimpleExpression(args[0].trim());
            double exponent = evaluateSimpleExpression(args[1].trim());
            return Math.pow(base, exponent);
        }

        // For simple expressions, try a basic evaluation
        try {
            return evaluateSimpleExpression(expr);
        } catch (Exception e) {
            throw new Exception("No se puede evaluar la expresión: " + expr);
        }
    }

    /**
     * Extracts the argument from a function call.
     * 
     * @param function The function text
     * @param funcName The function name with opening parenthesis
     * @return The argument value
     * @throws Exception If there's an error extracting the argument
     */
    private double extractArgument(String function, String funcName) throws Exception {
        int start = function.indexOf(funcName) + funcName.length();
        int end = findClosingParenthesis(function, start);
        if (end == -1) {
            throw new Exception("Paréntesis no balanceados en: " + function);
        }

        String argStr = function.substring(start, end);
        try {
            return evaluateSimpleExpression(argStr);
        } catch (Exception e) {
            throw new Exception("No se puede evaluar el argumento: " + argStr);
        }
    }

    /**
     * Finds the index of the closing parenthesis that matches the opening parenthesis at the given start index.
     * 
     * @param str The string to search in
     * @param start The index after the opening parenthesis
     * @return The index of the matching closing parenthesis, or -1 if not found
     */
    private int findClosingParenthesis(String str, int start) {
        int count = 1;
        for (int i = start; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                count++;
            } else if (str.charAt(i) == ')') {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Evaluates a simple mathematical expression.
     * 
     * @param expr The expression to evaluate
     * @return The result of the evaluation
     * @throws Exception If there's an error evaluating the expression
     */
    private double evaluateSimpleExpression(String expr) throws Exception {
        // This is a very simplified expression evaluator
        // For a real application, you would want to use a proper expression parser

        // Handle addition
        if (expr.contains("+")) {
            String[] parts = expr.split("\\+");
            double result = 0;
            for (String part : parts) {
                result += evaluateSimpleExpression(part.trim());
            }
            return result;
        }

        // Handle subtraction
        if (expr.contains("-")) {
            String[] parts = expr.split("-");
            double result = evaluateSimpleExpression(parts[0].trim());
            for (int i = 1; i < parts.length; i++) {
                result -= evaluateSimpleExpression(parts[i].trim());
            }
            return result;
        }

        // Handle multiplication
        if (expr.contains("*")) {
            String[] parts = expr.split("\\*");
            double result = 1;
            for (String part : parts) {
                result *= evaluateSimpleExpression(part.trim());
            }
            return result;
        }

        // Handle division
        if (expr.contains("/")) {
            String[] parts = expr.split("/");
            double result = evaluateSimpleExpression(parts[0].trim());
            for (int i = 1; i < parts.length; i++) {
                double divisor = evaluateSimpleExpression(parts[i].trim());
                if (divisor == 0) {
                    throw new Exception("División por cero");
                }
                result /= divisor;
            }
            return result;
        }

        // Handle power
        if (expr.contains("^")) {
            String[] parts = expr.split("\\^");
            if (parts.length == 2) {
                double base = evaluateSimpleExpression(parts[0].trim());
                double exponent = evaluateSimpleExpression(parts[1].trim());
                return Math.pow(base, exponent);
            }
        }

        // If it's a simple number, parse it
        try {
            return Double.parseDouble(expr.trim());
        } catch (NumberFormatException e) {
            throw new Exception("No se puede convertir a número: " + expr);
        }
    }

    /**
     * Cleans up the function text to make it more suitable for JavaScript evaluation.
     * 
     * @param functionText The function text to clean
     * @return The cleaned function text
     */
    private String cleanFunctionText(String functionText) {
        // Replace ^ with ** for exponentiation in JavaScript
        String cleaned = functionText.replace("^", "**");

        // Replace multiple operators with a single operator
        cleaned = cleaned.replaceAll("\\+\\s*\\+", "+") // ++ -> +
                        .replaceAll("-\\s*-", "+")      // -- -> +
                        .replaceAll("\\+\\s*-", "-")    // +- -> -
                        .replaceAll("-\\s*\\+", "-")    // -+ -> -
                        .replaceAll("/\\s*/", "/");     // // -> /

        // Trim whitespace
        cleaned = cleaned.trim();

        return cleaned;
    }

    /**
     * Prepares a function text for JavaScript evaluation.
     * 
     * @param functionText The function text
     * @return The prepared function text
     */
    private String prepareForJavaScript(String functionText) {
        // Replace mathematical functions with their JavaScript equivalents
        String jsFunction = functionText
            // Trigonometric functions
            .replace("sin", "Math.sin")
            .replace("cos", "Math.cos")
            .replace("tan", "Math.tan")
            .replace("cot", "Math.cot")
            .replace("sec", "Math.sec")
            .replace("csc", "Math.csc")

            // Inverse trigonometric functions
            .replace("asin", "Math.asin")
            .replace("acos", "Math.acos")
            .replace("atan", "Math.atan")
            .replace("atan2", "Math.atan2")
            .replace("arcsin", "Math.asin")
            .replace("arccos", "Math.acos")
            .replace("arctan", "Math.atan")

            // Hyperbolic functions
            .replace("sinh", "Math.sinh")
            .replace("cosh", "Math.cosh")
            .replace("tanh", "Math.tanh")

            // Inverse hyperbolic functions
            .replace("asinh", "Math.asinh")
            .replace("acosh", "Math.acosh")
            .replace("atanh", "Math.atanh")

            // Root functions
            .replace("sqrt", "Math.sqrt")
            .replace("cbrt", "Math.cbrt")

            // Logarithmic functions
            .replace("log", "Math.log10")
            .replace("log10", "Math.log10")
            .replace("log2", "Math.log2")
            .replace("ln", "Math.log")

            // Other functions
            .replace("exp", "Math.exp")
            .replace("abs", "Math.abs")
            .replace("sign", "Math.sign")
            .replace("floor", "Math.floor")
            .replace("ceil", "Math.ceil")
            .replace("round", "Math.round")
            .replace("trunc", "Math.trunc")
            .replace("min", "Math.min")
            .replace("max", "Math.max")
            .replace("pow", "Math.pow")

            // Constants
            .replace("pi", "Math.PI")
            .replace("e", "Math.E")
            .replace("infinity", "Infinity")
            .replace("inf", "Infinity");

        return jsFunction;
    }

    /**
     * Sets the axis ranges and updates the chart.
     * 
     * @param xMin The minimum x value
     * @param xMax The maximum x value
     * @param yMin The minimum y value
     * @param yMax The maximum y value
     * @param zMin The minimum z value
     * @param zMax The maximum z value
     */
    public void setAxisRanges(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;

        // Redraw all functions with the new ranges
        redrawFunctions();
    }

    /**
     * Redraws all functions with the current ranges.
     */
    private void redrawFunctions() {
        // Clear all meshes
        meshGroup.getChildren().clear();

        // Add coordinate axes
        addCoordinateAxes();

        // Redraw each function
        for (String functionText : functions) {
            try {
                MeshView meshView = createFunctionMesh(functionText);
                meshGroup.getChildren().add(meshView);
                functionMeshes.put(functionText, meshView);
            } catch (Exception e) {
                showAlert("Error al redibujar la función " + functionText + ": " + e.getMessage());
            }
        }
    }

    /**
     * Gets the current axis ranges.
     * 
     * @return An array containing [xMin, xMax, yMin, yMax, zMin, zMax]
     */
    public double[] getAxisRanges() {
        return new double[] {xMin, xMax, yMin, yMax, zMin, zMax};
    }

    /**
     * Zooms in on the chart by reducing the axis ranges.
     */
    public void zoomIn() {
        double xRange = xMax - xMin;
        double yRange = yMax - yMin;
        double zRange = zMax - zMin;

        // Calculate new ranges (zoom in by 20%)
        double newXRange = xRange * 0.8;
        double newYRange = yRange * 0.8;
        double newZRange = zRange * 0.8;

        // Calculate new min and max values
        double xCenter = (xMax + xMin) / 2;
        double yCenter = (yMax + yMin) / 2;
        double zCenter = (zMax + zMin) / 2;

        double newXMin = xCenter - newXRange / 2;
        double newXMax = xCenter + newXRange / 2;
        double newYMin = yCenter - newYRange / 2;
        double newYMax = yCenter + newYRange / 2;
        double newZMin = zCenter - newZRange / 2;
        double newZMax = zCenter + newZRange / 2;

        // Update the ranges
        setAxisRanges(newXMin, newXMax, newYMin, newYMax, newZMin, newZMax);
    }

    /**
     * Zooms out on the chart by increasing the axis ranges.
     */
    public void zoomOut() {
        double xRange = xMax - xMin;
        double yRange = yMax - yMin;
        double zRange = zMax - zMin;

        // Calculate new ranges (zoom out by 20%)
        double newXRange = xRange * 1.2;
        double newYRange = yRange * 1.2;
        double newZRange = zRange * 1.2;

        // Calculate new min and max values
        double xCenter = (xMax + xMin) / 2;
        double yCenter = (yMax + yMin) / 2;
        double zCenter = (zMax + zMin) / 2;

        double newXMin = xCenter - newXRange / 2;
        double newXMax = xCenter + newXRange / 2;
        double newYMin = yCenter - newYRange / 2;
        double newYMax = yCenter + newYRange / 2;
        double newZMin = zCenter - newZRange / 2;
        double newZMax = zCenter + newZRange / 2;

        // Update the ranges
        setAxisRanges(newXMin, newXMax, newYMin, newYMax, newZMin, newZMax);
    }

    /**
     * Resets the chart to the default zoom level.
     */
    public void resetZoom() {
        setAxisRanges(-5.0, 5.0, -5.0, 5.0, -5.0, 5.0);
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
     * Shows an alert with the given message.
     * 
     * @param message The message to show
     */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
