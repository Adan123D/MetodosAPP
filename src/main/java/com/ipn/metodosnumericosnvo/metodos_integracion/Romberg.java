package com.ipn.metodosnumericosnvo.metodos_integracion;

import org.mariuszgromada.math.mxparser.Function;

public class Romberg {
    public double calcularRomberg(String fxExpr, double a, double b, int maxIter, double tol, double[][] rTable) {
        Function f = new Function("f(x) = " + fxExpr);
        if (!f.checkSyntax()) throw new IllegalArgumentException("Función inválida: " + f.getErrorMessage());
        if (maxIter <= 0) throw new IllegalArgumentException("Iteraciones debe ser > 0");
        if (tol <= 0) throw new IllegalArgumentException("Tolerancia debe ser > 0");

        double h = b - a;
        rTable[0][0] = (h / 2) * (f.calculate(a) + f.calculate(b));

        for (int i = 1; i < maxIter; i++) {
            h /= 2;
            double sum = 0;
            int m = (int) Math.pow(2, i - 1);
            for (int k = 1; k <= m; k++) {
                sum += f.calculate(a + (2 * k - 1) * h);
            }
            rTable[i][0] = 0.5 * rTable[i - 1][0] + sum * h;

            for (int j = 1; j <= i; j++) {
                rTable[i][j] = rTable[i][j - 1] +
                    (rTable[i][j - 1] - rTable[i - 1][j - 1]) / (Math.pow(4, j) - 1);
            }
            if (Math.abs(rTable[i][i] - rTable[i - 1][i - 1]) < tol) {
                return rTable[i][i];
            }
        }
        return rTable[maxIter - 1][maxIter - 1];
    }
}