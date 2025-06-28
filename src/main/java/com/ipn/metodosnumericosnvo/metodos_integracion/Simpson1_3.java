package com.ipn.metodosnumericosnvo.metodos_integracion;

import org.mariuszgromada.math.mxparser.*;

public class Simpson1_3 {
    public double integrar(String fxExpr, double a, double b, int n) {
        Function f = new Function("f(x) = " + fxExpr);
        if (!f.checkSyntax()) {
            throw new IllegalArgumentException("Función inválida: " + f.getErrorMessage());
        }
        if (n <= 0 || n % 2 != 0) {
            throw new IllegalArgumentException("El número de subintervalos debe ser par y mayor que 0.");
        }
        double h = (b - a) / n;
        double sum = f.calculate(a) + f.calculate(b);

        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            double fx = f.calculate(x);
            sum += (i % 2 == 0) ? 2 * fx : 4 * fx;
        }
        return sum * h / 3.0;
    }
}
