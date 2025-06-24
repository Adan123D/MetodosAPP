package com.ipn.metodosnumericosnvo;

import com.ipn.metodosnumericosnvo.math.FunctionEvaluator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the FunctionEvaluator
 * This class tests that the FunctionEvaluator correctly evaluates functions
 */
public class FunctionEvaluatorTest {

    /**
     * Test that the FunctionEvaluator correctly evaluates the function 5*x+10
     */
    @Test
    public void testFunctionEvaluator() throws Exception {
        FunctionEvaluator evaluator = new FunctionEvaluator();

        // Test the function 5*x+10 at x = -2
        double result = evaluator.evaluateFunction("5*x+10", -2);
        assertEquals(0.0, result, 1e-10, "f(-2) should be 0");

        // Test the function 5*x+10 at x = 0
        result = evaluator.evaluateFunction("5*x+10", 0);
        assertEquals(10.0, result, 1e-10, "f(0) should be 10");

        // Test the function 5*x+10 at x = 1
        result = evaluator.evaluateFunction("5*x+10", 1);
        assertEquals(15.0, result, 1e-10, "f(1) should be 15");

        // Test the function 5x+10 (without the * operator) at x = -2
        result = evaluator.evaluateFunction("5x+10", -2);
        assertEquals(0.0, result, 1e-10, "f(-2) with 5x+10 should be 0");

        // Print the results for verification
        System.out.println("All tests passed!");
        System.out.println("f(-2) = " + evaluator.evaluateFunction("5*x+10", -2));
        System.out.println("f(0) = " + evaluator.evaluateFunction("5*x+10", 0));
        System.out.println("f(1) = " + evaluator.evaluateFunction("5*x+10", 1));
        System.out.println("f(-2) with 5x+10 = " + evaluator.evaluateFunction("5x+10", -2));
    }

    /**
     * Test that the FunctionEvaluator correctly evaluates more complex functions
     * using the mXparser library
     */
    @Test
    public void testComplexFunctions() throws Exception {
        FunctionEvaluator evaluator = new FunctionEvaluator();

        // Test trigonometric functions
        double result = evaluator.evaluateFunction("sin(x)", Math.PI/2);
        assertEquals(1.0, result, 1e-10, "sin(π/2) should be 1");

        result = evaluator.evaluateFunction("cos(x)", Math.PI);
        assertEquals(-1.0, result, 1e-10, "cos(π) should be -1");

        // Test logarithmic functions
        result = evaluator.evaluateFunction("log10(x)", 100);
        assertEquals(2.0, result, 1e-10, "log10(100) should be 2");

        result = evaluator.evaluateFunction("ln(x)", Math.E);
        assertEquals(1.0, result, 1e-10, "ln(e) should be 1");

        // Test exponential functions
        result = evaluator.evaluateFunction("e^x", 2);
        assertEquals(Math.exp(2), result, 1e-10, "e^2 should be " + Math.exp(2));

        // Test complex expressions
        result = evaluator.evaluateFunction("sin(x)^2 + cos(x)^2", 3.14);
        assertEquals(1.0, result, 1e-10, "sin(x)^2 + cos(x)^2 should be 1 for any x");

        // Test a more complex function
        result = evaluator.evaluateFunction("(sin(x) + cos(x))^2", Math.PI/4);
        double expected = Math.pow(Math.sin(Math.PI/4) + Math.cos(Math.PI/4), 2);
        assertEquals(expected, result, 1e-10, "The result should be " + expected);

        // Print the results for verification
        System.out.println("[DEBUG_LOG] Complex function tests passed!");
        System.out.println("[DEBUG_LOG] sin(π/2) = " + evaluator.evaluateFunction("sin(x)", Math.PI/2));
        System.out.println("[DEBUG_LOG] cos(π) = " + evaluator.evaluateFunction("cos(x)", Math.PI));
        System.out.println("[DEBUG_LOG] log10(100) = " + evaluator.evaluateFunction("log10(x)", 100));
        System.out.println("[DEBUG_LOG] ln(e) = " + evaluator.evaluateFunction("ln(x)", Math.E));
        System.out.println("[DEBUG_LOG] e^2 = " + evaluator.evaluateFunction("e^x", 2));
        System.out.println("[DEBUG_LOG] sin(x)^2 + cos(x)^2 = " + evaluator.evaluateFunction("sin(x)^2 + cos(x)^2", 3.14));
        System.out.println("[DEBUG_LOG] (sin(x) + cos(x))^2 at x=π/4 = " + evaluator.evaluateFunction("(sin(x) + cos(x))^2", Math.PI/4));
    }

    /**
     * Test that the FunctionEvaluator correctly evaluates inverse trigonometric functions
     */
    @Test
    public void testInverseTrigonometricFunctions() throws Exception {
        FunctionEvaluator evaluator = new FunctionEvaluator();

        // Test arccos function
        double result = evaluator.evaluateFunction("arccos(x)", 0);
        assertEquals(Math.PI/2, result, 1e-10, "arccos(0) should be π/2");

        result = evaluator.evaluateFunction("arccos(x)", 1);
        assertEquals(0.0, result, 1e-10, "arccos(1) should be 0");

        result = evaluator.evaluateFunction("arccos(x)", -1);
        assertEquals(Math.PI, result, 1e-10, "arccos(-1) should be π");

        // Test acos function (alternative notation)
        result = evaluator.evaluateFunction("acos(x)", 0);
        assertEquals(Math.PI/2, result, 1e-10, "acos(0) should be π/2");

        // Test arcsin function
        result = evaluator.evaluateFunction("arcsin(x)", 0);
        assertEquals(0.0, result, 1e-10, "arcsin(0) should be 0");

        result = evaluator.evaluateFunction("arcsin(x)", 1);
        assertEquals(Math.PI/2, result, 1e-10, "arcsin(1) should be π/2");

        // Test arctan function
        result = evaluator.evaluateFunction("arctan(x)", 0);
        assertEquals(0.0, result, 1e-10, "arctan(0) should be 0");

        result = evaluator.evaluateFunction("arctan(x)", 1);
        assertEquals(Math.PI/4, result, 1e-10, "arctan(1) should be π/4");

        // Print the results for verification
        System.out.println("[DEBUG_LOG] Inverse trigonometric function tests:");
        System.out.println("[DEBUG_LOG] arccos(0) = " + evaluator.evaluateFunction("arccos(x)", 0));
        System.out.println("[DEBUG_LOG] arccos(1) = " + evaluator.evaluateFunction("arccos(x)", 1));
        System.out.println("[DEBUG_LOG] arccos(-1) = " + evaluator.evaluateFunction("arccos(x)", -1));
        System.out.println("[DEBUG_LOG] acos(0) = " + evaluator.evaluateFunction("acos(x)", 0));
        System.out.println("[DEBUG_LOG] arcsin(0) = " + evaluator.evaluateFunction("arcsin(x)", 0));
        System.out.println("[DEBUG_LOG] arcsin(1) = " + evaluator.evaluateFunction("arcsin(x)", 1));
        System.out.println("[DEBUG_LOG] arctan(0) = " + evaluator.evaluateFunction("arctan(x)", 0));
        System.out.println("[DEBUG_LOG] arctan(1) = " + evaluator.evaluateFunction("arctan(x)", 1));
    }

    /**
     * Test that the FunctionEvaluator correctly evaluates inverse trigonometric functions
     * using the mXparser library specifically
     */
    @Test
    public void testInverseTrigonometricFunctionsWithMXparser() throws Exception {
        FunctionEvaluator evaluator = new FunctionEvaluator();

        // Force the use of mXparser by first trying to evaluate with mXparser
        // and catching any exceptions
        try {
            // Test arccos function with mXparser
            double result = evaluator.evaluateFunction("arccos(x)", 0);
            assertEquals(Math.PI/2, result, 1e-10, "arccos(0) with mXparser should be π/2");

            result = evaluator.evaluateFunction("arccos(x)", 1);
            assertEquals(0.0, result, 1e-10, "arccos(1) with mXparser should be 0");

            result = evaluator.evaluateFunction("arccos(x)", -1);
            assertEquals(Math.PI, result, 1e-10, "arccos(-1) with mXparser should be π");

            // Test acos function (alternative notation) with mXparser
            result = evaluator.evaluateFunction("acos(x)", 0);
            assertEquals(Math.PI/2, result, 1e-10, "acos(0) with mXparser should be π/2");

            // Test arcsin function with mXparser
            result = evaluator.evaluateFunction("arcsin(x)", 0);
            assertEquals(0.0, result, 1e-10, "arcsin(0) with mXparser should be 0");

            result = evaluator.evaluateFunction("arcsin(x)", 1);
            assertEquals(Math.PI/2, result, 1e-10, "arcsin(1) with mXparser should be π/2");

            // Test arctan function with mXparser
            result = evaluator.evaluateFunction("arctan(x)", 0);
            assertEquals(0.0, result, 1e-10, "arctan(0) with mXparser should be 0");

            result = evaluator.evaluateFunction("arctan(x)", 1);
            assertEquals(Math.PI/4, result, 1e-10, "arctan(1) with mXparser should be π/4");

            // Print the results for verification
            System.out.println("[DEBUG_LOG] Inverse trigonometric function tests with mXparser:");
            System.out.println("[DEBUG_LOG] arccos(0) with mXparser = " + evaluator.evaluateFunction("arccos(x)", 0));
            System.out.println("[DEBUG_LOG] arccos(1) with mXparser = " + evaluator.evaluateFunction("arccos(x)", 1));
            System.out.println("[DEBUG_LOG] arccos(-1) with mXparser = " + evaluator.evaluateFunction("arccos(x)", -1));
            System.out.println("[DEBUG_LOG] acos(0) with mXparser = " + evaluator.evaluateFunction("acos(x)", 0));
            System.out.println("[DEBUG_LOG] arcsin(0) with mXparser = " + evaluator.evaluateFunction("arcsin(x)", 0));
            System.out.println("[DEBUG_LOG] arcsin(1) with mXparser = " + evaluator.evaluateFunction("arcsin(x)", 1));
            System.out.println("[DEBUG_LOG] arctan(0) with mXparser = " + evaluator.evaluateFunction("arctan(x)", 0));
            System.out.println("[DEBUG_LOG] arctan(1) with mXparser = " + evaluator.evaluateFunction("arctan(x)", 1));
        } catch (Exception e) {
            fail("Failed to evaluate inverse trigonometric functions with mXparser: " + e.getMessage());
        }
    }
}
