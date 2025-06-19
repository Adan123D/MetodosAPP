package com.ipn.metodosnumericosnvo.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for finding roots of mathematical functions.
 * This class provides methods for numerical root finding using various methods.
 */
public class RootFinder {

    // Function evaluator
    private FunctionEvaluator functionEvaluator;

    // Maximum number of iterations for root finding methods
    private int maxIterations = 100;

    // Tolerance for convergence
    private double tolerance = 1e-10;

    /**
     * Constructor for the RootFinder.
     */
    public RootFinder() {
        this.functionEvaluator = new FunctionEvaluator();
    }

    /**
     * Constructor for the RootFinder with a specific function evaluator.
     * 
     * @param functionEvaluator The function evaluator to use for function evaluation
     */
    public RootFinder(FunctionEvaluator functionEvaluator) {
        this.functionEvaluator = functionEvaluator;
    }

    /**
     * Sets the maximum number of iterations for root finding methods.
     * 
     * @param maxIterations The maximum number of iterations
     */
    public void setMaxIterations(int maxIterations) {
        if (maxIterations <= 0) {
            throw new IllegalArgumentException("Maximum number of iterations must be positive");
        }
        this.maxIterations = maxIterations;
    }

    /**
     * Gets the maximum number of iterations for root finding methods.
     * 
     * @return The maximum number of iterations
     */
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Sets the tolerance for convergence.
     * 
     * @param tolerance The tolerance for convergence
     */
    public void setTolerance(double tolerance) {
        if (tolerance <= 0) {
            throw new IllegalArgumentException("Tolerance must be positive");
        }
        this.tolerance = tolerance;
    }

    /**
     * Gets the tolerance for convergence.
     * 
     * @return The tolerance for convergence
     */
    public double getTolerance() {
        return tolerance;
    }

    /**
     * Finds a root of a function using the bisection method.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param a The lower bound of the interval
     * @param b The upper bound of the interval
     * @return The root of the function
     * @throws Exception If there's an error evaluating the function or if the function doesn't change sign in the interval
     */
    public double bisectionMethod(String functionText, double a, double b) throws Exception {
        // Check if the function changes sign in the interval
        double fa = functionEvaluator.evaluateFunction(functionText, a);
        double fb = functionEvaluator.evaluateFunction(functionText, b);

        if (fa * fb > 0) {
            throw new Exception("La función no cambia de signo en el intervalo [" + a + ", " + b + "]");
        }

        double c = a;
        double fc;

        for (int i = 0; i < maxIterations; i++) {
            // Calculate the midpoint
            c = (a + b) / 2;

            // Evaluate the function at the midpoint
            fc = functionEvaluator.evaluateFunction(functionText, c);

            // Check for convergence
            if (Math.abs(fc) < tolerance || (b - a) / 2 < tolerance) {
                return c;
            }

            // Update the interval
            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
        }

        return c;
    }

    /**
     * Finds a root of a function using the Newton-Raphson method.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param initialGuess The initial guess for the root
     * @return The root of the function
     * @throws Exception If there's an error evaluating the function or its derivative
     */
    public double newtonRaphsonMethod(String functionText, double initialGuess) throws Exception {
        DerivativeCalculator derivativeCalculator = new DerivativeCalculator(functionEvaluator);

        double x = initialGuess;

        for (int i = 0; i < maxIterations; i++) {
            // Evaluate the function and its derivative at x
            double fx = functionEvaluator.evaluateFunction(functionText, x);
            double fpx = derivativeCalculator.firstDerivative(functionText, x);

            // Check if the derivative is close to zero
            if (Math.abs(fpx) < 1e-10) {
                throw new Exception("La derivada es cercana a cero en x = " + x);
            }

            // Calculate the next approximation
            double nextX = x - fx / fpx;

            // Check for convergence
            if (Math.abs(nextX - x) < tolerance) {
                return nextX;
            }

            x = nextX;
        }

        return x;
    }

    /**
     * Finds a root of a function using the secant method.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param x0 The first initial guess
     * @param x1 The second initial guess
     * @return The root of the function
     * @throws Exception If there's an error evaluating the function
     */
    public double secantMethod(String functionText, double x0, double x1) throws Exception {
        double f0 = functionEvaluator.evaluateFunction(functionText, x0);
        double f1 = functionEvaluator.evaluateFunction(functionText, x1);

        for (int i = 0; i < maxIterations; i++) {
            // Check if the difference between f0 and f1 is close to zero
            if (Math.abs(f1 - f0) < 1e-10) {
                throw new Exception("La diferencia entre f(x0) y f(x1) es cercana a cero");
            }

            // Calculate the next approximation
            double x2 = x1 - f1 * (x1 - x0) / (f1 - f0);

            // Check for convergence
            if (Math.abs(x2 - x1) < tolerance) {
                return x2;
            }

            // Update values for the next iteration
            x0 = x1;
            f0 = f1;
            x1 = x2;
            f1 = functionEvaluator.evaluateFunction(functionText, x1);
        }

        return x1;
    }

    /**
     * Finds all roots of a function in a given interval using the bisection method.
     * This method divides the interval into subintervals and checks for sign changes.
     * 
     * @param functionText The function text (e.g., "sin(x)")
     * @param a The lower bound of the interval
     * @param b The upper bound of the interval
     * @param numSubintervals The number of subintervals to check
     * @return An array of roots found in the interval
     * @throws Exception If there's an error evaluating the function
     */
    public double[] findAllRoots(String functionText, double a, double b, int numSubintervals) throws Exception {
        if (a >= b) {
            throw new IllegalArgumentException("Lower bound must be less than upper bound");
        }
        if (numSubintervals <= 0) {
            throw new IllegalArgumentException("Number of subintervals must be positive");
        }

        List<Double> roots = new ArrayList<>();
        double step = (b - a) / numSubintervals;

        for (int i = 0; i < numSubintervals; i++) {
            double x0 = a + i * step;
            double x1 = a + (i + 1) * step;

            try {
                double f0 = functionEvaluator.evaluateFunction(functionText, x0);
                double f1 = functionEvaluator.evaluateFunction(functionText, x1);

                // Check if the function changes sign in this subinterval
                if (f0 * f1 <= 0) {
                    // Find the root in this subinterval using the bisection method
                    double root = bisectionMethod(functionText, x0, x1);

                    // Check if this root is already in the list (within tolerance)
                    boolean isDuplicate = false;
                    for (double existingRoot : roots) {
                        if (Math.abs(root - existingRoot) < tolerance) {
                            isDuplicate = true;
                            break;
                        }
                    }

                    if (!isDuplicate) {
                        roots.add(root);
                    }
                }
            } catch (Exception e) {
                // Skip this subinterval if there's an error
                System.err.println("Error al buscar raíces en el subintervalo [" + x0 + ", " + x1 + "]: " + e.getMessage());
            }
        }

        // Convert the list to an array
        double[] rootsArray = new double[roots.size()];
        for (int i = 0; i < roots.size(); i++) {
            rootsArray[i] = roots.get(i);
        }

        return rootsArray;
    }

    /**
     * Finds the intersection points of two functions in a given interval.
     * This method finds the roots of the difference function f(x) - g(x).
     * 
     * @param function1Text The first function text (e.g., "sin(x)")
     * @param function2Text The second function text (e.g., "cos(x)")
     * @param a The lower bound of the interval
     * @param b The upper bound of the interval
     * @param numSubintervals The number of subintervals to check
     * @return An array of intersection points found in the interval
     * @throws Exception If there's an error evaluating the functions
     */
    public double[] findIntersectionPoints(String function1Text, String function2Text, double a, double b, int numSubintervals) throws Exception {
        // Create a difference function: f(x) - g(x)
        String differenceFunction = "(" + function1Text + ") - (" + function2Text + ")";

        // Find the roots of the difference function
        return findAllRoots(differenceFunction, a, b, numSubintervals);
    }
}
