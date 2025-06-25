package com.ipn.metodosnumericosnvo.ejemplos;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Argument;

/**
 * Esta clase contiene un ejemplo completo del método de Extrapolación de Richardson
 * para la función f(x) = x*exp(x) evaluada en x=2 con h=0.2
 */
public class EjemploExtrapolacionRichardson {

    public static void main(String[] args) {
        // Ejemplo: f(x) = x*exp(x), encontrar f'(2) con h=0.2 usando el método de Extrapolación de Richardson

        // Parámetros del problema
        double x = 2.0;
        double h = 0.2;

        // Paso 1: Evaluar f(x) en los puntos necesarios
        double fxPlusH = evaluarFuncion(x + h);     // f(2.2)
        double fxMinusH = evaluarFuncion(x - h);    // f(1.8)
        System.out.printf("f(%.1f) = %.4f\n", x+h, fxPlusH);
        System.out.printf("f(%.1f) = %.4f\n", x-h, fxMinusH);

        // Paso 2: Calcular la primera aproximación usando h=0.2
        double A_h = (fxPlusH - fxMinusH) / (2 * h);
        System.out.printf("Aproximación con h=%.1f: %.4f\n", h, A_h);

        // Paso 3: Evaluar f(x) en los puntos para h/2 = 0.1
        double h2 = h / 2;  // h/2 = 0.1
        double fxPlusH2 = evaluarFuncion(x + h2);   // f(2.1)
        double fxMinusH2 = evaluarFuncion(x - h2);  // f(1.9)
        System.out.printf("f(%.1f) = %.4f\n", x+h2, fxPlusH2);
        System.out.printf("f(%.1f) = %.4f\n", x-h2, fxMinusH2);

        // Paso 4: Calcular la segunda aproximación usando h=0.1
        double A_h2 = (fxPlusH2 - fxMinusH2) / (2 * h2);
        System.out.printf("Aproximación con h=%.1f: %.4f\n", h2, A_h2);

        // Paso 5: Aplicar la extrapolación de Richardson (para diferencias centrales p=2)
        int p = 2;  // Orden del error para diferencias centrales
        double resultado = (Math.pow(2, p) * A_h2 - A_h) / (Math.pow(2, p) - 1);
        System.out.printf("Extrapolación de Richardson: %.4f\n", resultado);

        // La derivada exacta de f(x) = x*exp(x) es f'(x) = exp(x) + x*exp(x)
        double derivadaExacta = Math.exp(x) + x * Math.exp(x);
        System.out.printf("Derivada exacta en x=%.1f: %.4f\n", x, derivadaExacta);
        System.out.printf("Error absoluto: %.8f\n", Math.abs(derivadaExacta - resultado));
    }

    /**
     * Evalúa la función f(x) = x*exp(x)
     */
    private static double evaluarFuncion(double x) {
        return x * Math.exp(x);
    }

    /**
     * Evalúa cualquier función usando mXparser
     */
    private static double evaluarExpresion(String expresion, double x) {
        Argument xArg = new Argument("x", x);
        Expression exp = new Expression(expresion, xArg);
        return exp.calculate();
    }
}
