package com.ipn.metodosnumericosnvo.metodo_diferenciales;

import java.util.ArrayList;
import java.util.List;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

public class MetodoRungeKutta3 {

    public static class Iteracion {
        private final int paso;
        private final double t;
        private final double y;

        public Iteracion(int paso, double t, double y) {
            this.paso = paso;
            this.t = t;
            this.y = y;
        }

        public int getPaso() {
            return paso;
        }

        public double getT() {
            return t;
        }

        public double getY() {
            return y;
        }
    }

    /**
     * Método de Runge-Kutta de orden 3.
     * @param t0 Tiempo inicial
     * @param y0 Valor inicial de y
     * @param tf Tiempo final
     * @param n  Número de pasos
     * @param funcion La función en formato string: ejemplo "y - t^2 + 1"
     * @return Lista de iteraciones
     */
    public static List<Iteracion> resolver(double t0, double y0, double tf, int n, String funcion) {
        Argument argT = new Argument("t");
        Argument argY = new Argument("y");
        Expression expr = new Expression(funcion, argT, argY);

        List<Iteracion> pasos = new ArrayList<>();

        double h = (tf - t0) / n;
        double t = t0;
        double y = y0;

        for (int i = 0; i <= n; i++) {
            pasos.add(new Iteracion(i, t, y));

            double k1 = h * eval(expr, argT, argY, t, y);
            double k2 = h * eval(expr, argT, argY, t + h / 2.0, y + k1 / 2.0);
            double k3 = h * eval(expr, argT, argY, t + h, y - k1 + 2 * k2);

            y = y + (1.0 / 6.0) * (k1 + 4 * k2 + k3);
            t = t + h;
        }

        return pasos;
    }

    private static double eval(Expression expr, Argument tArg, Argument yArg, double t, double y) {
        tArg.setArgumentValue(t);
        yArg.setArgumentValue(y);
        return expr.calculate();
    }
}
