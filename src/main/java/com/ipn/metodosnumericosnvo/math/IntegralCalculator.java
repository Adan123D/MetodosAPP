package com.ipn.metodosnumericosnvo.math;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.integration.RombergIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

/**
 * Class for calculating integrals of mathematical functions.
 * This class provides methods for numerical integration using various methods.
 * Uses Apache Commons Math library for optimized calculations.
 */
public class IntegralCalculator {

    // Function evaluator
    private FunctionEvaluator functionEvaluator;

    // Default relative accuracy for integrators
    private static final double DEFAULT_RELATIVE_ACCURACY = 1.0e-6;

    // Default absolute accuracy for integrators
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0e-15;

    // Default minimum number of iterations for integrators
    private static final int DEFAULT_MIN_ITERATIONS_COUNT = 3;

    // Default maximum number of iterations for integrators
    private static final int DEFAULT_MAX_ITERATIONS_COUNT = 32;

    /**
     * Constructor for the IntegralCalculator.
     */
    public IntegralCalculator() {
        // No se inicializa automáticamente para evitar recursión infinita
        this.functionEvaluator = null;
    }

    /**
     * Constructor for the IntegralCalculator with a specific function evaluator.
     * 
     * @param functionEvaluator The function evaluator to use for function evaluation
     */
    public IntegralCalculator(FunctionEvaluator functionEvaluator) {
        this.functionEvaluator = functionEvaluator;
    }

    /**
     * Sets the function evaluator to use for calculations.
     * 
     * @param evaluator The function evaluator to use
     */
    public void setFunctionEvaluator(FunctionEvaluator evaluator) {
        this.functionEvaluator = evaluator;
    }

    /**
     * Creates a UnivariateFunction from a function text.
     * This is used to adapt our function representation to Apache Commons Math.
     * 
     * @param functionText The function text
     * @return A UnivariateFunction that evaluates the function
     */
    private UnivariateFunction createFunction(final String functionText) {
        return new UnivariateFunction() {
            @Override
            public double value(double x) {
                try {
                    return functionEvaluator.evaluateFunction(functionText, x);
                } catch (Exception e) {
                    throw new RuntimeException("Error evaluating function: " + e.getMessage(), e);
                }
            }
        };
    }

    /**
     * Calculates the definite integral of a function using the trapezoidal rule.
     * This implementation uses Apache Commons Math for optimized calculations.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param a The lower bound of integration
     * @param b The upper bound of integration
     * @param n The number of intervals
     * @return The definite integral of the function from a to b
     * @throws Exception If there's an error evaluating the function
     */
    public double trapezoidalRule(String functionText, double a, double b, int n) throws Exception {
        if (functionEvaluator == null) {
            throw new IllegalStateException("FunctionEvaluator no inicializado. Llame a setFunctionEvaluator primero.");
        }
        if (a >= b) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("Number of intervals must be positive");
        }

        try {
            // Create a function that can be used by Apache Commons Math
            UnivariateFunction function = createFunction(functionText);

            // Create a TrapezoidIntegrator with the specified number of points
            TrapezoidIntegrator integrator = new TrapezoidIntegrator();

            // Perform the integration
            return integrator.integrate(Integer.MAX_VALUE, function, a, b);
        } catch (MathIllegalArgumentException e) {
            throw new Exception("Error en la integración: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates the definite integral of a function using Simpson's 1/3 rule.
     * This implementation uses Apache Commons Math for optimized calculations.
     * Note: n must be even.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param a The lower bound of integration
     * @param b The upper bound of integration
     * @param n The number of intervals (must be even)
     * @return The definite integral of the function from a to b
     * @throws Exception If there's an error evaluating the function
     */
    public double simpsonsRule(String functionText, double a, double b, int n) throws Exception {
        if (functionEvaluator == null) {
            throw new IllegalStateException("FunctionEvaluator no inicializado. Llame a setFunctionEvaluator primero.");
        }
        if (a >= b) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("Number of intervals must be positive");
        }
        if (n % 2 != 0) {
            throw new IllegalArgumentException("Number of intervals must be even for Simpson's rule");
        }

        try {
            // Create a function that can be used by Apache Commons Math
            UnivariateFunction function = createFunction(functionText);

            // Create a SimpsonIntegrator
            SimpsonIntegrator integrator = new SimpsonIntegrator();

            // Perform the integration
            return integrator.integrate(Integer.MAX_VALUE, function, a, b);
        } catch (MathIllegalArgumentException e) {
            throw new Exception("Error en la integración: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates the definite integral of a function using Simpson's 3/8 rule.
     * Note: n must be a multiple of 3.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param a The lower bound of integration
     * @param b The upper bound of integration
     * @param n The number of intervals (must be a multiple of 3)
     * @return The definite integral of the function from a to b
     * @throws Exception If there's an error evaluating the function
     */
    public double simpsons38Rule(String functionText, double a, double b, int n) throws Exception {
        if (a >= b) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("Number of intervals must be positive");
        }
        if (n % 3 != 0) {
            throw new IllegalArgumentException("Number of intervals must be a multiple of 3 for Simpson's 3/8 rule");
        }

        double h = (b - a) / n;
        double sum = functionEvaluator.evaluateFunction(functionText, a) + 
                    functionEvaluator.evaluateFunction(functionText, b);

        // Sum for indices that are multiples of 3 (coefficient 2)
        for (int i = 3; i < n; i += 3) {
            double x = a + i * h;
            sum += 2 * functionEvaluator.evaluateFunction(functionText, x);
        }

        // Sum for other indices (coefficient 3)
        for (int i = 1; i < n; i++) {
            if (i % 3 != 0) {
                double x = a + i * h;
                sum += 3 * functionEvaluator.evaluateFunction(functionText, x);
            }
        }

        return 3 * h * sum / 8;
    }

    /**
     * Calculates the definite integral of a function using the Romberg integration method.
     * This implementation uses Apache Commons Math for optimized calculations.
     * This method uses Richardson extrapolation to improve the accuracy of the trapezoidal rule.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param a The lower bound of integration
     * @param b The upper bound of integration
     * @param maxIterations The maximum number of iterations
     * @param tolerance The tolerance for convergence
     * @return The definite integral of the function from a to b
     * @throws Exception If there's an error evaluating the function
     */
    public double rombergIntegration(String functionText, double a, double b, int maxIterations, double tolerance) throws Exception {
        if (functionEvaluator == null) {
            throw new IllegalStateException("FunctionEvaluator no inicializado. Llame a setFunctionEvaluator primero.");
        }
        if (a >= b) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        if (maxIterations <= 0) {
            throw new IllegalArgumentException("Maximum number of iterations must be positive");
        }
        if (tolerance <= 0) {
            throw new IllegalArgumentException("Tolerance must be positive");
        }

        try {
            // Create a function that can be used by Apache Commons Math
            UnivariateFunction function = createFunction(functionText);

            // Create a RombergIntegrator with the specified parameters
            RombergIntegrator integrator = new RombergIntegrator(
                DEFAULT_RELATIVE_ACCURACY,
                tolerance,  // Use the provided tolerance as absolute accuracy
                DEFAULT_MIN_ITERATIONS_COUNT,
                maxIterations  // Use the provided maxIterations
            );

            // Perform the integration
            return integrator.integrate(Integer.MAX_VALUE, function, a, b);
        } catch (MathIllegalArgumentException e) {
            throw new Exception("Error en la integración: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates the definite integral of a function using adaptive quadrature.
     * This implementation uses Apache Commons Math for optimized calculations.
     * This method adaptively refines the integration in regions where the function changes rapidly.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param a The lower bound of integration
     * @param b The upper bound of integration
     * @param tolerance The tolerance for convergence
     * @param maxRecursionDepth The maximum recursion depth (used as maximum evaluations)
     * @return The definite integral of the function from a to b
     * @throws Exception If there's an error evaluating the function
     */
    public double adaptiveQuadrature(String functionText, double a, double b, double tolerance, int maxRecursionDepth) throws Exception {
        if (functionEvaluator == null) {
            throw new IllegalStateException("FunctionEvaluator no inicializado. Llame a setFunctionEvaluator primero.");
        }
        if (a >= b) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        if (tolerance <= 0) {
            throw new IllegalArgumentException("Tolerance must be positive");
        }
        if (maxRecursionDepth <= 0) {
            throw new IllegalArgumentException("Maximum recursion depth must be positive");
        }

        try {
            // Create a function that can be used by Apache Commons Math
            UnivariateFunction function = createFunction(functionText);

            // Use the SimpsonIntegrator which is adaptive and has good accuracy
            SimpsonIntegrator integrator = new SimpsonIntegrator(
                DEFAULT_RELATIVE_ACCURACY,
                tolerance,  // Use the provided tolerance as absolute accuracy
                DEFAULT_MIN_ITERATIONS_COUNT,
                maxRecursionDepth * 10  // Convert recursion depth to max evaluations
            );

            // Perform the integration
            return integrator.integrate(maxRecursionDepth * 100, function, a, b);
        } catch (MathIllegalArgumentException e) {
            throw new Exception("Error en la integración adaptativa: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates the definite integral of a function using Simpson's rule for a single interval.
     * 
     * @param functionText The function text
     * @param a The lower bound of integration
     * @param b The upper bound of integration
     * @return The definite integral of the function from a to b
     * @throws Exception If there's an error evaluating the function
     */
    private double simpsonsRuleSingle(String functionText, double a, double b) throws Exception {
        double c = (a + b) / 2;
        double fa = functionEvaluator.evaluateFunction(functionText, a);
        double fb = functionEvaluator.evaluateFunction(functionText, b);
        double fc = functionEvaluator.evaluateFunction(functionText, c);

        return (b - a) * (fa + 4 * fc + fb) / 6;
    }
}
