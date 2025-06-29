package com.ipn.metodosnumericosnvo.metodo_derivacion;

import org.mariuszgromada.math.mxparser.*;
import java.math.BigInteger;

public class Interpolacion {
    /**
     * Calcula la derivada de orden k de la función f en el punto x0
     * @param f Función a derivar
     * @param x0 Punto donde evaluar la derivada
     * @param k Orden de la derivada
     * @return Valor de la derivada k-ésima en x0
     */
    private double der(Function f, double x0, int k) {
        String expr = String.format("der(%s, x, %d)", f.getFunctionExpressionString(), k);
        Expression e = new Expression(expr, new Argument("x = " + x0), f);
        double result = e.calculate();
        if (Double.isNaN(result)) {
            throw new ArithmeticException("Error al calcular la derivada de orden " + k);
        }
        return result;
    }

    /**
     * Calcula el factorial de k usando BigInteger para evitar desbordamientos
     * @param k Número para calcular el factorial
     * @return k!
     */
    private BigInteger factorial(int k) {
        if (k < 0) throw new IllegalArgumentException("Factorial no definido para números negativos");
        if (k <= 1) return BigInteger.ONE;

        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= k; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    /**
     * Calcula el polinomio de Taylor de grado n para la función f(x) en el punto x
     * centrado en x0: Pn(x) = f(x0) + (x-x0)/1!*f'(x0) + ... + (x-x0)^n/n!*f^(n)(x0)
     * 
     * @param fxExpr Expresión de la función f(x)
     * @param x0 Punto base del polinomio
     * @param n Grado del polinomio
     * @param x Punto donde evaluar el polinomio
     * @return Valor del polinomio de Taylor en x
     * @throws IllegalArgumentException si la función es inválida o n es negativo
     * @throws ArithmeticException si hay error en el cálculo de derivadas
     */
    public double calcular(String fxExpr, double x0, int n, double x) {
        // Validaciones de entrada
        if (fxExpr == null || fxExpr.trim().isEmpty()) {
            throw new IllegalArgumentException("La expresión de la función no puede estar vacía");
        }
        if (n < 0) {
            throw new IllegalArgumentException("El grado del polinomio debe ser no negativo");
        }

        // Crear y validar la función
        Function f = new Function("f(x) = " + fxExpr);
        if (!f.checkSyntax()) {
            throw new IllegalArgumentException("Función inválida: " + f.getErrorMessage());
        }

        // Calcular el polinomio de Taylor
        double sum = 0;
        double diffX = x - x0; // Calculamos (x-x0) una sola vez
        double powDiffX = 1;   // Iniciamos con (x-x0)^0 = 1

        for (int k = 0; k <= n; k++) {
            // Calcular la derivada k-ésima en x0
            double derivada = der(f, x0, k);

            // Calcular (x-x0)^k / k!
            double termino = powDiffX / factorial(k).doubleValue();

            // Sumar el término al polinomio
            sum += derivada * termino;

            // Actualizar (x-x0)^k para la siguiente iteración
            powDiffX *= diffX;
        }

        return sum;
    }
}
