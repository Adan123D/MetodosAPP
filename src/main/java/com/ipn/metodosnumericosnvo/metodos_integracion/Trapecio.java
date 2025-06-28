package com.ipn.metodosnumericosnvo.metodos_integracion;

import org.mariuszgromada.math.mxparser.Function;

public class Trapecio {
    public double integrar(String fxExpr, double a, double b, int n) {
        Function f = new Function("f(x) = " + fxExpr);
        if (!f.checkSyntax()) {
            throw new IllegalArgumentException("Función inválida: " + f.getErrorMessage());
        }
        if (n <= 0) {
            throw new IllegalArgumentException("El número de subintervalos debe ser > 0");
        }
        double h = (b - a) / n;
        double sum = 0.5 * (f.calculate(a) + f.calculate(b));
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            sum += f.calculate(x);
        }
        return sum * h;
    }
}
