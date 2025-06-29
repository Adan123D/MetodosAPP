package com.ipn.metodosnumericosnvo.metodos_raices;

import org.mariuszgromada.math.mxparser.*;
import java.util.ArrayList;
import java.util.List;

public class PuntoFijo {

    public static class Iteration {
        public final int i;
        public final double xi;
        public final double gxi;
        public final double error;
        public Iteration(int i, double xi, double gxi, double error) {
            this.i = i; this.xi = xi; this.gxi = gxi; this.error = error;
        }
    }

    public List<Iteration> resolver(String gExpr, double x0, int maxIter, double tol) {
        Function g = new Function("g(x) = " + gExpr);
        if (!g.checkSyntax()) throw new IllegalArgumentException("Función g(x) inválida: " + g.getErrorMessage());

        List<Iteration> iters = new ArrayList<>();
        double xi = x0;
        double prevError = Double.MAX_VALUE;

        for (int i = 1; i <= maxIter; i++) {
            double gxi = g.calculate(xi);

            // Check for NaN or infinite values
            if (Double.isNaN(gxi) || Double.isInfinite(gxi)) {
                throw new ArithmeticException("La función diverge: g(" + xi + ") = " + gxi);
            }

            double err = Math.abs(gxi - xi);
            iters.add(new Iteration(i, xi, gxi, err));

            // Check for convergence
            if (err <= tol) break;

            // Check for divergence (error is increasing)
            if (i > 1 && err > prevError * 2) {
                throw new ArithmeticException("El método está divergiendo. Intente con otro valor inicial o una función diferente.");
            }

            prevError = err;
            xi = gxi;
        }

        // Check if we reached max iterations without converging
        if (iters.size() == maxIter && iters.get(maxIter-1).error > tol) {
            throw new ArithmeticException("El método no convergió después de " + maxIter + " iteraciones.");
        }

        return iters;
    }
}
