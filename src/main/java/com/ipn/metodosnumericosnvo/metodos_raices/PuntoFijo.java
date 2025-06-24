package com.ipn.metodosnumericosnvo.metodos_raices;

import org.mariuszgromada.math.mxparser.*;
import java.util.ArrayList;
import java.util.List;

public class PuntoFijo {

    public static class Step {
        public int paso;
        public double x0, fx0, x1, fx1;
        public Step(int paso, double x0, double fx0, double x1, double fx1) {
            this.paso = paso;
            this.x0 = x0;
            this.fx0 = fx0;
            this.x1 = x1;
            this.fx1 = fx1;
        }
        public int getPaso() { return paso; }
        public double getX0() { return x0; }
        public double getFx0() { return fx0; }
        public double getX1() { return x1; }
        public double getFx1() { return fx1; }
    }

    /**
     * Realiza el método de Punto Fijo con las expresiones de f(x) y g(x).
     * @param exprF   Expresión de f(x) (ejemplo: "cos(x)-3*x+1")
     * @param exprG   Expresión de g(x) (ejemplo: "(1+cos(x))/3")
     * @param x0      Valor inicial
     * @param tol     Tolerancia
     * @param maxIter Máximo de iteraciones
     * @param pasos   Lista para guardar pasos (opcional)
     * @return        Raíz aproximada
     */
    public double resolver(String exprF, String exprG, double x0, double tol, int maxIter, List<Step> pasos) {
        Argument argX = new Argument("x", x0);
        Expression f = new Expression(exprF, argX);
        Expression g = new Expression(exprG, argX);

        double x1 = x0;
        int paso = 1;

        do {
            argX.setArgumentValue(x0);
            double fx0 = f.calculate();

            argX.setArgumentValue(x0);
            x1 = g.calculate();

            argX.setArgumentValue(x1);
            double fx1 = f.calculate();

            pasos.add(new Step(paso, x0, fx0, x1, fx1));

            paso++;
            if (paso > maxIter) {
                throw new IllegalArgumentException("No converge en el número de iteraciones especificado.");
            }
            x0 = x1;
        } while (Math.abs(f.calculate()) > tol);

        return x1;
    }
}
