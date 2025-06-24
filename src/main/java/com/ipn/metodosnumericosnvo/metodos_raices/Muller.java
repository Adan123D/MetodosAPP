package com.ipn.metodosnumericosnvo.metodos_raices;

import org.mariuszgromada.math.mxparser.*;
import java.util.ArrayList;
import java.util.List;

public class Muller {

    public static class Step {
        public int paso;
        public double x1, x2, x3, x4, fx1, fx2, fx3, fx4, a, b, c;
        public Step(int paso, double x1, double x2, double x3, double x4, double fx1, double fx2, double fx3, double fx4, double a, double b, double c) {
            this.paso = paso;
            this.x1 = x1;
            this.x2 = x2;
            this.x3 = x3;
            this.x4 = x4;
            this.fx1 = fx1;
            this.fx2 = fx2;
            this.fx3 = fx3;
            this.fx4 = fx4;
            this.a = a;
            this.b = b;
            this.c = c;
        }
        public int getPaso() { return paso; }
        public double getX1() { return x1; }
        public double getX2() { return x2; }
        public double getX3() { return x3; }
        public double getX4() { return x4; }
        public double getFx1() { return fx1; }
        public double getFx2() { return fx2; }
        public double getFx3() { return fx3; }
        public double getFx4() { return fx4; }
        public double getA() { return a; }
        public double getB() { return b; }
        public double getC() { return c; }
    }

    /**
     * Método de Müller
     * @param exprF   Expresión de la función en formato mXparser (ej: "x^3-x-1")
     * @param x1      Primer valor inicial
     * @param x2      Segundo valor inicial
     * @param x3      Tercer valor inicial
     * @param tol     Tolerancia
     * @param maxIt   Máximo de iteraciones
     * @param pasos   Lista donde se guardan los pasos (opcional)
     * @return        Raíz aproximada
     */
    public double resolver(String exprF, double x1, double x2, double x3, double tol, int maxIt, List<Step> pasos) {
        Argument argX = new Argument("x", x1);
        Expression f = new Expression(exprF, argX);

        double x4 = x3;
        for (int i = 1; i <= maxIt; i++) {
            // Evaluar f(x1), f(x2), f(x3)
            argX.setArgumentValue(x1);
            double f1 = f.calculate();
            argX.setArgumentValue(x2);
            double f2 = f.calculate();
            argX.setArgumentValue(x3);
            double f3 = f.calculate();

            double h1 = x2 - x1;
            double h2 = x3 - x2;
            double d1 = (f2 - f1) / h1;
            double d2 = (f3 - f2) / h2;
            double A = (d2 - d1) / (h2 + h1);
            double B = A * h2 + d2;
            double C = f3;
            double raiz = Math.sqrt(B * B - 4.0 * A * C);

            double denom;
            if (Math.abs(B + raiz) > Math.abs(B - raiz)) {
                denom = B + raiz;
            } else {
                denom = B - raiz;
            }
            if (denom == 0) denom = 1e-12; // Para evitar división por cero

            x4 = x3 - 2 * C / denom;

            argX.setArgumentValue(x4);
            double fx4 = f.calculate();

            pasos.add(new Step(i, x1, x2, x3, x4, f1, f2, f3, fx4, A, B, C));

            if (Math.abs(fx4) < tol) break;
            x1 = x2;
            x2 = x3;
            x3 = x4;
        }
        return x4;
    }
}
