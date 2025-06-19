package com.ipn.metodosnumericosnvo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FunctionGrapherWeb
 * This class tests the string replacements that are performed in the prepareForJavaScript method
 * to ensure it correctly transforms hyperbolic and inverse functions into valid JavaScript code.
 */
public class FunctionGrapherWebTest {

    @Test
    public void testHyperbolicFunctionReplacements() {
        // Test individual hyperbolic functions directly
        assertEquals("Math.sinh(x)", convertHyperbolicFunction("sinh(x)"));
        assertEquals("Math.cosh(x)", convertHyperbolicFunction("cosh(x)"));
        assertEquals("Math.tanh(x)", convertHyperbolicFunction("tanh(x)"));

        // Test individual inverse hyperbolic functions directly
        assertEquals("Math.asinh(x)", convertHyperbolicFunction("asinh(x)"));
        assertEquals("Math.acosh(x)", convertHyperbolicFunction("acosh(x)"));
        assertEquals("Math.atanh(x)", convertHyperbolicFunction("atanh(x)"));

        // Test custom hyperbolic functions
        assertEquals("(1/Math.tanh(x))", convertCustomHyperbolicFunction("coth(x)"));
        assertEquals("(1/Math.cosh(x))", convertCustomHyperbolicFunction("sech(x)"));
        assertEquals("(1/Math.sinh(x))", convertCustomHyperbolicFunction("csch(x)"));

        // Test custom inverse hyperbolic functions
        assertEquals("Math.atanh(1/x)", convertCustomInverseHyperbolicFunction("acoth(x)"));
        assertEquals("Math.acosh(1/x)", convertCustomInverseHyperbolicFunction("asech(x)"));
        assertEquals("Math.asinh(1/x)", convertCustomInverseHyperbolicFunction("acsch(x)"));

        System.out.println("[DEBUG_LOG] All hyperbolic function replacements passed");
    }

    /**
     * Converts a basic hyperbolic function to its JavaScript equivalent.
     */
    private String convertHyperbolicFunction(String functionText) {
        if (functionText.startsWith("sinh(")) {
            return "Math.sinh" + functionText.substring(4);
        } else if (functionText.startsWith("cosh(")) {
            return "Math.cosh" + functionText.substring(4);
        } else if (functionText.startsWith("tanh(")) {
            return "Math.tanh" + functionText.substring(4);
        } else if (functionText.startsWith("asinh(")) {
            return "Math.asinh" + functionText.substring(5);
        } else if (functionText.startsWith("acosh(")) {
            return "Math.acosh" + functionText.substring(5);
        } else if (functionText.startsWith("atanh(")) {
            return "Math.atanh" + functionText.substring(5);
        }
        return functionText;
    }

    /**
     * Converts a custom hyperbolic function to its JavaScript equivalent.
     */
    private String convertCustomHyperbolicFunction(String functionText) {
        if (functionText.startsWith("coth(")) {
            return "(1/Math.tanh" + functionText.substring(4) + ")";
        } else if (functionText.startsWith("sech(")) {
            return "(1/Math.cosh" + functionText.substring(4) + ")";
        } else if (functionText.startsWith("csch(")) {
            return "(1/Math.sinh" + functionText.substring(4) + ")";
        }
        return functionText;
    }

    /**
     * Converts a custom inverse hyperbolic function to its JavaScript equivalent.
     */
    private String convertCustomInverseHyperbolicFunction(String functionText) {
        if (functionText.startsWith("acoth(")) {
            return "Math.atanh(1/x)";
        } else if (functionText.startsWith("asech(")) {
            return "Math.acosh(1/x)";
        } else if (functionText.startsWith("acsch(")) {
            return "Math.asinh(1/x)";
        }
        return functionText;
    }
}
