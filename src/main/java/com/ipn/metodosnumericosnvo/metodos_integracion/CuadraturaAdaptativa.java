package com.ipn.metodosnumericosnvo.metodos_integracion;

import java.util.ArrayList;
import java.util.List;
import org.mariuszgromada.math.mxparser.*;

public class CuadraturaAdaptativa {

    public static class Iteracion {
        public int nivel;
        public double a, b, s, s2, error;

        public Iteracion(int nivel, double a, double b, double s, double s2, double error) {
            this.nivel = nivel;
            this.a = a;
            this.b = b;
            this.s = s;
            this.s2 = s2;
            this.error = error;
        }

        public int getNivel() { return nivel; }
        public double getA() { return a; }
        public double getB() { return b; }
        public double getS() { return s; }
        public double getS2() { return s2; }
        public double getError() { return error; }
    }

    public static double resolver(String funcionStr, double a, double b, double tol, int maxRec, List<Iteracion> pasos) {
        Argument x = new Argument("x", 0);
        Expression f = new Expression(funcionStr, x);

        double s = simpsonCompuesta(f, x, a, b);
        return simpsonRecursiva(f, x, a, b, tol, s, maxRec, 1, pasos);
    }

    private static double simpsonCompuesta(Expression f, Argument x, double a, double b) {
        double h = (b - a) / 2.0;
        double x0 = a;
        double x1 = a + h / 2;
        double x2 = a + h;
        double x3 = a + 3 * h / 2;
        double x4 = b;
        return (h / 6.0) * (eval(f, x, x0) + 4 * (eval(f, x, x1) + eval(f, x, x3)) + 2 * (eval(f, x, x2) + eval(f, x, x4)));
    }

    private static double simpsonRecursiva(Expression f, Argument x, double a, double b, double tol, double s, int rec, int nivel, List<Iteracion> pasos) {
        double m = (a + b) / 2.0;
        double sl = simpsonCompuesta(f, x, a, m);
        double sr = simpsonCompuesta(f, x, m, b);
        double s2 = sl + sr;
        double error = Math.abs(s2 - s);

        pasos.add(new Iteracion(nivel, a, b, s, s2, error));

        if (rec <= 0 || error < 15 * tol) {
            return s2 + (s2 - s) / 15.0;
        }

        return simpsonRecursiva(f, x, a, m, tol / 2, sl, rec - 1, nivel + 1, pasos)
             + simpsonRecursiva(f, x, m, b, tol / 2, sr, rec - 1, nivel + 1, pasos);
    }

    private static double eval(Expression f, Argument x, double val) {
        x.setArgumentValue(val);
        return f.calculate();
    }
}
