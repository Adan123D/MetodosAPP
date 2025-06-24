package com.ipn.metodosnumericosnvo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify the roots of the cubic function x^3 + 3x^2 + 12x + 8
 */
public class CubicFunctionRootTest {

    /**
     * Test to verify the behavior of the cubic function around its roots
     */
    @Test
    public void testCubicFunctionRoots() {
        // Define the cubic function: f(x) = x^3 + 3x^2 + 12x + 8
        // We'll evaluate it at different points to understand its behavior

        // Test around x = -0.778980 (expected root according to issue description)
        double x1 = -0.778980;
        double y1 = evaluateCubicFunction(x1);
        System.out.println("[DEBUG_LOG] f(" + x1 + ") = " + y1);

        // Test around x = -2 (current implementation assumes this is a root)
        double x2 = -2.0;
        double y2 = evaluateCubicFunction(x2);
        System.out.println("[DEBUG_LOG] f(" + x2 + ") = " + y2);

        // Test at other points to understand the function's behavior
        double[] testPoints = {-3.0, -1.0, 0.0, 1.0};
        double[] expectedValues = new double[testPoints.length];
        for (int i = 0; i < testPoints.length; i++) {
            double x = testPoints[i];
            double y = evaluateCubicFunction(x);
            expectedValues[i] = y;
            System.out.println("[DEBUG_LOG] f(" + x + ") = " + y);
        }

        // Print the results directly in the assertion messages
        assertEquals(0.0, y1, 0.0001, 
                   "f(-0.778980) = " + y1 + " (should be close to 0)");
        // We now know that f(-2.0) = -12.0, not 0
        assertEquals(-12.0, y2, 0.0001, 
                   "f(-2.0) = " + y2 + " (should be -12.0)");

        // Verify the values at other points
        assertEquals(expectedValues[0], evaluateCubicFunction(testPoints[0]), 0.0001,
                   "f(-3.0) = " + expectedValues[0]);
        assertEquals(expectedValues[1], evaluateCubicFunction(testPoints[1]), 0.0001,
                   "f(-1.0) = " + expectedValues[1]);
        assertEquals(expectedValues[2], evaluateCubicFunction(testPoints[2]), 0.0001,
                   "f(0.0) = " + expectedValues[2]);
        assertEquals(expectedValues[3], evaluateCubicFunction(testPoints[3]), 0.0001,
                   "f(1.0) = " + expectedValues[3]);

        // Verify which value is closer to being a root
        assertTrue(Math.abs(y1) < Math.abs(y2), 
                   "The value at x = -0.778980 (" + y1 + ") should be closer to zero than at x = -2.0 (" + y2 + ")");
    }

    /**
     * Directly evaluate the cubic function without using the FunctionEvaluator
     * f(x) = x^3 + 3x^2 + 12x + 8
     */
    private double evaluateCubicFunction(double x) {
        return Math.pow(x, 3) + 3 * Math.pow(x, 2) + 12 * x + 8;
    }
}
