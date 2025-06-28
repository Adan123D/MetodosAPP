package com.ipn.metodosnumericosnvo.metodos_integracion;

import org.mariuszgromada.math.mxparser.*;

public class Simpson3_8 {
    public double integrar(String fxExpr, double a, double b, int n) {
        Function f = new Function("f(x) = " + fxExpr);
        if (!f.checkSyntax()) {
            throw new IllegalArgumentException("Función inválida: " + f.getErrorMessage());
        }
        if (n <= 0 || n % 3 != 0) {
            throw new IllegalArgumentException("Subintervalos debe ser múltiplo de 3 y > 0");
        }
        double h = (b - a) / n;
        double sum = f.calculate(a) + f.calculate(b);

        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            double fx = f.calculate(x);
            sum += (i % 3 == 0) ? 2 * fx : 3 * fx;
        }
        return sum * h * 3 / 8;
    }
}
