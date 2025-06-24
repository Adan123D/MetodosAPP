package com.ipn.metodosnumericosnvo.math;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class DerivativeCalculator {

    private ExprEvaluator evaluator;
    private FunctionEvaluator functionEvaluator;

    /**
     * Constructor initializes the Symja evaluator.
     */
    public DerivativeCalculator() {
        F.initSymbols(); // Requerido por Symja en algunos entornos
        evaluator = new ExprEvaluator(false, (short)100);
        this.functionEvaluator = null;
    }

    /**
     * Constructor initializes the Symja evaluator with a function evaluator.
     * 
     * @param functionEvaluator The function evaluator to use
     */
    public DerivativeCalculator(FunctionEvaluator functionEvaluator) {
        F.initSymbols(); // Requerido por Symja en algunos entornos
        evaluator = new ExprEvaluator(false, (short)100);
        this.functionEvaluator = functionEvaluator;
    }

    /**
     * Obtiene la derivada simbólica de orden n de una función como texto.
     * Ejemplo: getDerivativeExpression("sin(x)", "x", 2) => "-Sin(x)"
     * @param functionText La función, ej "x^2+cos(x)"
     * @param variable La variable, ej "x"
     * @param order Orden de la derivada (1 = primera, 2 = segunda, ...)
     * @return String con la derivada simbólica
     */
    public String getDerivativeExpression(String functionText, String variable, int order) {
        String expr = "D(" + functionText + "," + variable;
        if (order > 1) {
            expr += "," + order;
        }
        expr += ")";
        IExpr deriv = evaluator.evaluate(expr);
        return deriv.toString();
    }

    /**
     * Evalúa la derivada de orden n de una función en un punto dado.
     * @param functionText La función, ej "x^2+cos(x)"
     * @param variable La variable, ej "x"
     * @param x Punto a evaluar
     * @param order Orden de la derivada
     * @return Valor numérico de la derivada en x
     */
    public double nthDerivativeAt(String functionText, String variable, double x, int order) {
        // Obtiene la expresión simbólica de la derivada
        String derivativeExpr = getDerivativeExpression(functionText, variable, order);

        // Evalúa la derivada en el punto
        String evalExpr = derivativeExpr + "| " + variable + " = " + x;
        IExpr result = evaluator.evaluate(evalExpr);
        return result.evalDouble();
    }

    /**
     * Evalúa la función original en un punto dado.
     * @param functionText La función, ej "x^2+cos(x)"
     * @param variable La variable, ej "x"
     * @param x Punto a evaluar
     * @return Valor de la función en x
     */
    public double evaluateFunction(String functionText, String variable, double x) {
        String expr = functionText + "| " + variable + " = " + x;
        IExpr result = evaluator.evaluate(expr);
        return result.evalDouble();
    }

    /**
     * Calcula la primera derivada de una función en un punto dado.
     * @param functionText La función, ej "x^2+cos(x)"
     * @param x Punto a evaluar
     * @return Valor numérico de la primera derivada en x
     */
    public double firstDerivative(String functionText, double x) {
        return nthDerivativeAt(functionText, "x", x, 1);
    }

    /**
     * Calcula la derivada de la función en varios puntos (útil para graficar).
     * @param functionText La función, ej "x^2+cos(x)"
     * @param variable La variable, ej "x"
     * @param xMin Valor mínimo de x
     * @param xMax Valor máximo de x
     * @param order Orden de la derivada
     * @param numPoints Número de puntos
     * @return Arreglo de pares [x, f^(n)(x)]
     */
    public double[][] derivativeFunction(String functionText, String variable, double xMin, double xMax, int order, int numPoints) {
        double[][] result = new double[numPoints][2];
        double step = (xMax - xMin) / (numPoints - 1);

        for (int i = 0; i < numPoints; i++) {
            double x = xMin + i * step;
            double y = nthDerivativeAt(functionText, variable, x, order);
            result[i][0] = x;
            result[i][1] = y;
        }
        return result;
    }
}
