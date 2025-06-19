package com.ipn.metodosnumericosnvo.math;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.ipn.metodosnumericosnvo.math.DerivativeCalculator;
import com.ipn.metodosnumericosnvo.math.IntegralCalculator;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

// For derivative and integral calculations
import java.util.function.Function;

/**
 * Utility class for evaluating mathematical functions.
 * This class provides methods for evaluating functions using JavaScript engines or fallback methods.
 */
public class FunctionEvaluator {

    // The JavaScript engine for evaluating functions
    private ScriptEngine engine;

    // Flag to indicate if we're using direct Rhino evaluation
    private boolean useDirectRhino = false;

    // Calculators for derivatives and integrals
    private DerivativeCalculator derivativeCalculator;
    private IntegralCalculator integralCalculator;

    // Constants for special series naming
    private static final String DERIVATIVE_PREFIX = "d/dx[";
    private static final String INTEGRAL_PREFIX = "∫";

    // Range for integral calculation
    private double xMin = -10.0;

    /**
     * Constructor for the FunctionEvaluator.
     * Initializes the JavaScript engine with fallback options.
     */
    public FunctionEvaluator() {
        initializeScriptEngine();

        // Initialize calculators
        this.derivativeCalculator = new DerivativeCalculator(this);
        this.integralCalculator = new IntegralCalculator();
    }

    /**
     * Sets the minimum x value for integral calculations.
     * 
     * @param xMin The minimum x value
     */
    public void setXMin(double xMin) {
        this.xMin = xMin;
    }

    /**
     * Initializes the JavaScript engine with fallback options.
     * Tries different engine names in case the default one is not available.
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
     * Evaluates a function at a point x.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param x The x value
     * @return The function value at x
     * @throws Exception If there's an error evaluating the function
     */
    public double evaluateFunction(String functionText, double x) throws Exception {
        try {
            // Check if this is a derivative function
            if (functionText.startsWith(DERIVATIVE_PREFIX) && functionText.endsWith("]")) {
                // Extract the inner function
                String innerFunction = functionText.substring(DERIVATIVE_PREFIX.length(), functionText.length() - 1);
                try {
                    // Use the derivative calculator to evaluate the derivative
                    return derivativeCalculator.firstDerivative(innerFunction, x);
                } catch (Exception e) {
                    throw new Exception("Error al evaluar la función '" + functionText + "' en x=" + x + ": " + e.getMessage());
                }
            }

            // Check if this is an integral function
            if (functionText.startsWith(INTEGRAL_PREFIX)) {
                // Extract the inner function
                String innerFunction = functionText.substring(INTEGRAL_PREFIX.length());
                try {
                    // Use the integral calculator to evaluate the integral from a reference point
                    return integralCalculator.adaptiveQuadrature(innerFunction, xMin, x, 1e-6, 10);
                } catch (Exception e) {
                    throw new Exception("Error al evaluar la función '" + functionText + "' en x=" + x + ": " + e.getMessage());
                }
            }

            // For regular functions, proceed with normal evaluation
            // Clean up the function text
            String cleanFunction = cleanFunctionText(functionText);

            // Prepare the function text for JavaScript evaluation
            String jsFunction = prepareForJavaScript(cleanFunction);

            if (useDirectRhino) {
                // Use Rhino directly
                return evaluateWithRhinoDirect(jsFunction, x);
            } else if (engine != null) {
                // Use the ScriptEngine
                // Set the variables in the engine
                engine.put("x", x);

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
                return evaluateWithFallback(functionText, x);
            }
        } catch (Exception e) {
            // Try fallback evaluator if all other methods fail
            return evaluateWithFallback(functionText, x);
        }
    }

    /**
     * Evaluates a JavaScript function using Rhino directly.
     * 
     * @param jsFunction The JavaScript function to evaluate
     * @param x The x value
     * @return The result of the evaluation
     * @throws Exception If there's an error evaluating the function
     */
    private double evaluateWithRhinoDirect(String jsFunction, double x) throws Exception {
        Context context = Context.enter();
        try {
            // Initialize the standard objects and define Math functions
            Scriptable scope = context.initStandardObjects();

            // Set the x variable
            ScriptableObject.putProperty(scope, "x", Context.javaToJS(x, scope));

            // Define Math object with all necessary functions if not already defined
            String mathSetup = 
                "if (typeof Math === 'undefined') {" +
                "  var Math = {" +
                "    // Trigonometric functions" +
                "    sin: function(x) { return java.lang.Math.sin(x); }," +
                "    cos: function(x) { return java.lang.Math.cos(x); }," +
                "    tan: function(x) { return java.lang.Math.tan(x); }," +
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
     * This method can handle basic arithmetic operations and common mathematical functions.
     * 
     * @param functionText The function text
     * @param x The x value
     * @return The function value at x
     * @throws Exception If there's an error evaluating the function
     */
    private double evaluateWithFallback(String functionText, double x) throws Exception {
        // Clean up the function text
        String cleanFunction = cleanFunctionText(functionText);

        // Replace x with its value
        cleanFunction = cleanFunction.replace("x", String.valueOf(x));

        // Handle basic trigonometric functions
        if (cleanFunction.contains("sin(")) {
            return Math.sin(extractArgument(cleanFunction, "sin("));
        } else if (cleanFunction.contains("cos(")) {
            return Math.cos(extractArgument(cleanFunction, "cos("));
        } else if (cleanFunction.contains("tan(")) {
            return Math.tan(extractArgument(cleanFunction, "tan("));
        } 
        // Handle inverse trigonometric functions
        else if (cleanFunction.contains("asin(")) {
            return Math.asin(extractArgument(cleanFunction, "asin("));
        } else if (cleanFunction.contains("arcsin(")) {
            return Math.asin(extractArgument(cleanFunction, "arcsin("));
        } else if (cleanFunction.contains("acos(")) {
            return Math.acos(extractArgument(cleanFunction, "acos("));
        } else if (cleanFunction.contains("arccos(")) {
            return Math.acos(extractArgument(cleanFunction, "arccos("));
        } else if (cleanFunction.contains("atan(")) {
            return Math.atan(extractArgument(cleanFunction, "atan("));
        } else if (cleanFunction.contains("arctan(")) {
            return Math.atan(extractArgument(cleanFunction, "arctan("));
        } 
        // Handle hyperbolic functions
        else if (cleanFunction.contains("sinh(")) {
            return Math.sinh(extractArgument(cleanFunction, "sinh("));
        } else if (cleanFunction.contains("cosh(")) {
            return Math.cosh(extractArgument(cleanFunction, "cosh("));
        } else if (cleanFunction.contains("tanh(")) {
            return Math.tanh(extractArgument(cleanFunction, "tanh("));
        } 
        // Handle inverse hyperbolic functions
        else if (cleanFunction.contains("asinh(")) {
            double arg = extractArgument(cleanFunction, "asinh(");
            return Math.log(arg + Math.sqrt(arg*arg + 1));
        } else if (cleanFunction.contains("acosh(")) {
            double arg = extractArgument(cleanFunction, "acosh(");
            return Math.log(arg + Math.sqrt(arg*arg - 1));
        } else if (cleanFunction.contains("atanh(")) {
            double arg = extractArgument(cleanFunction, "atanh(");
            return 0.5 * Math.log((1+arg)/(1-arg));
        } 
        // Handle other functions
        else if (cleanFunction.contains("sqrt(")) {
            return Math.sqrt(extractArgument(cleanFunction, "sqrt("));
        } else if (cleanFunction.contains("cbrt(")) {
            return Math.cbrt(extractArgument(cleanFunction, "cbrt("));
        } else if (cleanFunction.contains("log(")) {
            return Math.log10(extractArgument(cleanFunction, "log("));
        } else if (cleanFunction.contains("log10(")) {
            return Math.log10(extractArgument(cleanFunction, "log10("));
        } else if (cleanFunction.contains("ln(")) {
            return Math.log(extractArgument(cleanFunction, "ln("));
        } else if (cleanFunction.contains("log2(")) {
            double arg = extractArgument(cleanFunction, "log2(");
            return Math.log(arg) / Math.log(2);
        } else if (cleanFunction.contains("abs(")) {
            return Math.abs(extractArgument(cleanFunction, "abs("));
        } else if (cleanFunction.contains("exp(")) {
            return Math.exp(extractArgument(cleanFunction, "exp("));
        } else if (cleanFunction.contains("^")) {
            // Handle power operation (e.g., 2^3)
            String[] parts = cleanFunction.split("\\^");
            if (parts.length == 2) {
                try {
                    double base = Double.parseDouble(parts[0]);
                    double exponent = Double.parseDouble(parts[1]);
                    return Math.pow(base, exponent);
                } catch (NumberFormatException e) {
                    throw new Exception("Error al evaluar la potencia: " + cleanFunction);
                }
            }
        }

        // For simple expressions like "2*x+1", try to evaluate directly
        try {
            // Replace ^ with Math.pow
            cleanFunction = cleanFunction.replace("^", "**");

            // Create a simple expression evaluator
            return evaluateExpression(cleanFunction);
        } catch (Exception e) {
            throw new Exception("El evaluador alternativo no puede procesar esta función: " + functionText);
        }
    }

    /**
     * Extracts the argument from a function call.
     * For example, extractArgument("sin(0.5)", "sin(") returns 0.5.
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
            return Double.parseDouble(argStr);
        } catch (NumberFormatException e) {
            throw new Exception("No se puede convertir a número: " + argStr);
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
     * Simple expression evaluator for basic arithmetic operations.
     * 
     * @param expression The expression to evaluate
     * @return The result of the evaluation
     * @throws Exception If there's an error evaluating the expression
     */
    private double evaluateExpression(String expression) throws Exception {
        try {
            // Very simple expression evaluator for basic operations
            // This is a fallback when the JavaScript engine is not available
            // It can handle only simple expressions like "2+3*4"

            // Replace ** with Math.pow for exponentiation
            if (expression.contains("**")) {
                String[] parts = expression.split("\\*\\*");
                if (parts.length == 2) {
                    double base = Double.parseDouble(parts[0]);
                    double exponent = Double.parseDouble(parts[1]);
                    return Math.pow(base, exponent);
                }
            }

            // Handle basic arithmetic operations
            if (expression.contains("+")) {
                String[] parts = expression.split("\\+");
                double result = 0;
                for (String part : parts) {
                    result += evaluateExpression(part);
                }
                return result;
            } else if (expression.contains("-")) {
                String[] parts = expression.split("-");
                double result = evaluateExpression(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    result -= evaluateExpression(parts[i]);
                }
                return result;
            } else if (expression.contains("*")) {
                String[] parts = expression.split("\\*");
                double result = 1;
                for (String part : parts) {
                    result *= evaluateExpression(part);
                }
                return result;
            } else if (expression.contains("/")) {
                String[] parts = expression.split("/");
                double result = evaluateExpression(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    double divisor = evaluateExpression(parts[i]);
                    if (divisor == 0) {
                        throw new Exception("División por cero");
                    }
                    result /= divisor;
                }
                return result;
            } else {
                // It's a simple number
                return Double.parseDouble(expression);
            }
        } catch (Exception e) {
            throw new Exception("Error al evaluar la expresión: " + expression + " - " + e.getMessage());
        }
    }

    /**
     * Cleans up the function text to make it more suitable for JavaScript evaluation.
     * 
     * @param functionText The function text to clean
     * @return The cleaned function text
     */
    private String cleanFunctionText(String functionText) {
        // First, temporarily replace ^ with a placeholder to avoid conflicts
        String cleaned = functionText.replace("^", "###POWER###");

        // Replace multiple operators with a single operator
        cleaned = cleaned.replaceAll("\\+\\s*\\+", "+") // ++ -> +
                        .replaceAll("-\\s*-", "+")      // -- -> +
                        .replaceAll("\\+\\s*-", "-")    // +- -> -
                        .replaceAll("-\\s*\\+", "-")    // -+ -> -
                        .replaceAll("/\\s*/", "/");     // // -> /

        // Restore the power operator
        cleaned = cleaned.replace("###POWER###", "^");

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
            .replace("cot", "1/Math.tan")
            .replace("sec", "1/Math.cos")
            .replace("csc", "1/Math.sin")

            // Inverse trigonometric functions
            .replace("arcsin", "Math.asin")
            .replace("arccos", "Math.acos")
            .replace("arctan", "Math.atan")
            .replace("asin", "Math.asin")
            .replace("acos", "Math.acos")
            .replace("atan", "Math.atan")
            .replace("atan2", "Math.atan2")

            // Hyperbolic functions
            .replace("sinh", "Math.sinh")
            .replace("cosh", "Math.cosh")
            .replace("tanh", "Math.tanh")

            // Inverse hyperbolic functions
            .replace("asinh", "Math.log(x + Math.sqrt(x*x + 1))")
            .replace("acosh", "Math.log(x + Math.sqrt(x*x - 1))")
            .replace("atanh", "0.5 * Math.log((1+x)/(1-x))")

            // Root functions
            .replace("sqrt", "Math.sqrt")
            .replace("cbrt", "Math.cbrt")

            // Logarithmic functions
            .replace("log", "Math.log10")
            .replace("log10", "Math.log10")
            .replace("ln", "Math.log")
            .replace("log2", "Math.log(x) / Math.log(2)")

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
            .replace("random", "Math.random")

            // Constants
            .replace("pi", "Math.PI")
            .replace("e", "Math.E")
            .replace("infinity", "Infinity")
            .replace("inf", "Infinity")

            // Replace ^ with ** for exponentiation in JavaScript
            .replace("^", "**");

        // Handle multiplication
        jsFunction = handleMultiplication(jsFunction);

        return jsFunction;
    }

    /**
     * Handles multiplication in the function text.
     * In JavaScript, implicit multiplication (like 2x) doesn't work,
     * so we need to explicitly add the * operator.
     * 
     * @param functionText The function text
     * @return The function text with explicit multiplication
     */
    private String handleMultiplication(String functionText) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < functionText.length(); i++) {
            char current = functionText.charAt(i);
            result.append(current);

            // If current character is a number or closing parenthesis
            if ((Character.isDigit(current) || current == ')') && i < functionText.length() - 1) {
                char next = functionText.charAt(i + 1);

                // If next character is a variable or opening parenthesis, insert *
                if (next == 'x' || next == '(') {
                    result.append('*');
                }
            }
        }

        return result.toString();
    }
}
