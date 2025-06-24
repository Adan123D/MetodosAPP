package com.ipn.metodosnumericosnvo.metodos_raices;

import org.mariuszgromada.math.mxparser.*;
import java.util.List;

public class Secante {

    public static class Step {
        public int paso;
        public double x0, x1, x2, fx2;

        public Step(int paso, double x0, double x1, double x2, double fx2) {
            this.paso = paso;
            this.x0 = x0;
            this.x1 = x1;
            this.x2 = x2;
            this.fx2 = fx2;
        }

        public int getPaso() { return paso; }
        public double getX0() { return x0; }
        public double getX1() { return x1; }
        public double getX2() { return x2; }
        public double getFx2() { return fx2; }
    }

    /**
     * Evalúa la función usando mXparser.
     */
    private double evalFunc(String func, double x) {
        Argument argX = new Argument("x", x);
        Expression expr = new Expression(func, argX);
        return expr.calculate();
    }

    /**
     * Método de la secante usando mXparser.
     * @param funcText  Función (ejemplo: "x^3-2*x-5")
     * @param x0        Primer valor inicial
     * @param x1        Segundo valor inicial
     * @param tol       Tolerancia
     * @param maxIt     Máximo de iteraciones
     * @param pasos     Lista para almacenar los pasos
     * @return          Raíz aproximada
     */
    public double resolver(String funcText, double x0, double x1, double tol, int maxIt, List<Step> pasos) {
        double f0, f1, x2 = x0, f2 = 0;
        int paso = 1;

        do {
            f0 = evalFunc(funcText, x0);
            f1 = evalFunc(funcText, x1);

            if (f0 == f1) {
                throw new IllegalArgumentException("Error matemático: f(x0) = f(x1)");
            }

            x2 = x1 - (x1 - x0) * f1 / (f1 - f0);
            f2 = evalFunc(funcText, x2);

            pasos.add(new Step(paso, x0, x1, x2, f2));

            x0 = x1;
            x1 = x2;

            paso++;
            if (paso > maxIt) {
                throw new IllegalArgumentException("No converge en el número de iteraciones especificado.");
            }
        } while (Math.abs(f2) > tol);

        return x2;
    }
}
