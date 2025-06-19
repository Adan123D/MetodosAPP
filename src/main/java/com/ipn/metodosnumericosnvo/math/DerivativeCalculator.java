package com.ipn.metodosnumericosnvo.math;

import java.util.function.Function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

/**
 * Class for calculating derivatives of mathematical functions.
 * This class provides methods for numerical differentiation using various methods.
 * Uses Apache Commons Math library for optimized calculations.
 */
public class DerivativeCalculator {

    // Step size for numerical differentiation
    private double h = 0.0001;

    // Function evaluator
    private FunctionEvaluator functionEvaluator;

    // Default number of points to use for finite differences
    private static final int DEFAULT_POINTS = 5;

    // Default maximum evaluations
    private static final int DEFAULT_MAX_EVALUATIONS = 1000;

    /**
     * Constructor for the DerivativeCalculator.
     */
    public DerivativeCalculator() {
        this.functionEvaluator = new FunctionEvaluator();
    }

    /**
     * Constructor for the DerivativeCalculator with a specific function evaluator.
     * 
     * @param functionEvaluator The function evaluator to use for function evaluation
     */
    public DerivativeCalculator(FunctionEvaluator functionEvaluator) {
        this.functionEvaluator = functionEvaluator;
    }

    /**
     * Sets the step size for numerical differentiation.
     * 
     * @param h The step size
     */
    public void setStepSize(double h) {
        if (h <= 0) {
            throw new IllegalArgumentException("Step size must be positive");
        }
        this.h = h;
    }

    /**
     * Gets the current step size for numerical differentiation.
     * 
     * @return The current step size
     */
    public double getStepSize() {
        return h;
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
     * Calculates the first derivative of a function at a point using finite differences.
     * This implementation uses Apache Commons Math for optimized calculations.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param x The point at which to calculate the derivative
     * @return The first derivative of the function at the point
     * @throws Exception If there's an error evaluating the function
     */
    public double firstDerivative(String functionText, double x) throws Exception {
        try {
            // Create a function that can be used by Apache Commons Math
            UnivariateFunction function = createFunction(functionText);

            // Create a FiniteDifferencesDifferentiator with the specified parameters
            FiniteDifferencesDifferentiator differentiator = new FiniteDifferencesDifferentiator(
                DEFAULT_POINTS, h);

            // Create a differentiable function
            UnivariateDifferentiableFunction differentiableFunction = differentiator.differentiate(function);

            // Calculate the derivative
            DerivativeStructure xDS = new DerivativeStructure(1, 1, 0, x);
            DerivativeStructure yDS = differentiableFunction.value(xDS);

            // Return the first derivative
            return yDS.getPartialDerivative(1);
        } catch (MathIllegalArgumentException e) {
            throw new Exception("Error al calcular la derivada: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates the second derivative of a function at a point using finite differences.
     * This implementation uses Apache Commons Math for optimized calculations.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param x The point at which to calculate the derivative
     * @return The second derivative of the function at the point
     * @throws Exception If there's an error evaluating the function
     */
    public double secondDerivative(String functionText, double x) throws Exception {
        try {
            // Create a function that can be used by Apache Commons Math
            UnivariateFunction function = createFunction(functionText);

            // Create a FiniteDifferencesDifferentiator with the specified parameters
            FiniteDifferencesDifferentiator differentiator = new FiniteDifferencesDifferentiator(
                DEFAULT_POINTS, h);

            // Create a differentiable function
            UnivariateDifferentiableFunction differentiableFunction = differentiator.differentiate(function);

            // Calculate the derivative
            DerivativeStructure xDS = new DerivativeStructure(1, 2, 0, x);
            DerivativeStructure yDS = differentiableFunction.value(xDS);

            // Return the second derivative
            return yDS.getPartialDerivative(2);
        } catch (MathIllegalArgumentException e) {
            throw new Exception("Error al calcular la segunda derivada: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates the nth derivative of a function at a point using finite differences.
     * This implementation uses Apache Commons Math for optimized calculations.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param x The point at which to calculate the derivative
     * @param n The order of the derivative
     * @return The nth derivative of the function at the point
     * @throws Exception If there's an error evaluating the function or if n is negative
     */
    public double nthDerivative(String functionText, double x, int n) throws Exception {
        if (n < 0) {
            throw new IllegalArgumentException("Order of derivative must be non-negative");
        }

        if (n == 0) {
            // 0th derivative is the function itself
            return functionEvaluator.evaluateFunction(functionText, x);
        }

        try {
            // Create a function that can be used by Apache Commons Math
            UnivariateFunction function = createFunction(functionText);

            // Create a FiniteDifferencesDifferentiator with the specified parameters
            // For higher order derivatives, we need more points
            int points = Math.max(DEFAULT_POINTS, n + 3);
            FiniteDifferencesDifferentiator differentiator = new FiniteDifferencesDifferentiator(
                points, h);

            // Create a differentiable function
            UnivariateDifferentiableFunction differentiableFunction = differentiator.differentiate(function);

            // Calculate the derivative
            DerivativeStructure xDS = new DerivativeStructure(1, n, 0, x);
            DerivativeStructure yDS = differentiableFunction.value(xDS);

            // Return the nth derivative
            return yDS.getPartialDerivative(n);
        } catch (MathIllegalArgumentException e) {
            throw new Exception("Error al calcular la derivada de orden " + n + ": " + e.getMessage(), e);
        }
    }

    /**
     * Calculates the derivative of a function over a range of x values.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param xMin The minimum x value
     * @param xMax The maximum x value
     * @param n The order of the derivative
     * @param numPoints The number of points to calculate
     * @return An array of x-y pairs representing the derivative function
     * @throws Exception If there's an error evaluating the function
     */
    public double[][] derivativeFunction(String functionText, double xMin, double xMax, int n, int numPoints) throws Exception {
        if (xMin >= xMax) {
            throw new IllegalArgumentException("xMin must be less than xMax");
        }
        if (numPoints <= 0) {
            throw new IllegalArgumentException("Number of points must be positive");
        }

        double[][] result = new double[numPoints][2];
        double step = (xMax - xMin) / (numPoints - 1);

        for (int i = 0; i < numPoints; i++) {
            double x = xMin + i * step;
            double y = nthDerivative(functionText, x, n);

            result[i][0] = x;
            result[i][1] = y;
        }

        return result;
    }
}
