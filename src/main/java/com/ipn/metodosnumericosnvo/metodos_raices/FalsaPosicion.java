package com.ipn.metodosnumericosnvo.metodos_raices;

import org.mariuszgromada.math.mxparser.*;

import java.util.ArrayList;
import java.util.List;

public class FalsaPosicion {

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
        // Getters para JavaFX TableView
        public int getPaso() { return paso; }
        public double getX0() { return x0; }
        public double getX1() { return x1; }
        public double getX2() { return x2; }
        public double getFx2() { return fx2; }
    }

    /**
     * Resuelve por falsa posición.
     * @param expr Expresión de la función (como la escribe el usuario)
     * @param x0 Primer punto
     * @param x1 Segundo punto
     * @param tol Tolerancia
     * @param pasos Lista de pasos para mostrar en tabla
     * @return La raíz encontrada
     */
    public double resolver(String expr, double x0, double x1, double tol, List<Step> pasos) {
        Argument argX = new Argument("x", x0); // Se actualiza valor en cada llamada
        Expression funcion = new Expression(expr, argX);

        double f0 = evalFunc(funcion, argX, x0);
        double f1 = evalFunc(funcion, argX, x1);

        if (Double.isNaN(f0) || Double.isNaN(f1)) {
            throw new IllegalArgumentException("Error evaluando la función. Revisa la expresión.");
        }
        if (f0 * f1 > 0.0) {
            throw new IllegalArgumentException("Las conjeturas iniciales no encierran la raíz.");
        }

        double x2 = 0, f2;
        int paso = 1;
        do {
            x2 = x0 - (x0 - x1) * f0 / (f0 - f1);
            f2 = evalFunc(funcion, argX, x2);

            pasos.add(new Step(paso, x0, x1, x2, f2));
            if (f0 * f2 < 0) {
                x1 = x2; f1 = f2;
            } else {
                x0 = x2; f0 = f2;
            }
            paso++;
        } while (Math.abs(f2) > tol);

        return x2;
    }

    private double evalFunc(Expression expr, Argument argX, double x) {
        argX.setArgumentValue(x);
        return expr.calculate();
    }
}
