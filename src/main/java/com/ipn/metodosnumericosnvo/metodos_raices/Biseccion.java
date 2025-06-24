package com.ipn.metodosnumericosnvo.metodos_raices;

import java.util.ArrayList;
import java.util.List;
import org.mariuszgromada.math.mxparser.*;

public class Biseccion {
    public static class Iteracion {
        public final int iteracion;
        public final double x0, x1, x2, fx0, fx1, fx2, error;

        public Iteracion(int iter, double x0, double x1, double x2, double fx0, double fx1, double fx2, double error) {
            this.iteracion = iter;
            this.x0 = x0;
            this.x1 = x1;
            this.x2 = x2;
            this.fx0 = fx0;
            this.fx1 = fx1;
            this.fx2 = fx2;
            this.error = error;
        }
    }

    private static double eval(String funcion, double x) {
        Argument arg = new Argument("x", x);
        Expression e = new Expression(funcion, arg);
        return e.calculate();
    }

    public static List<Iteracion> resolver(String funcion, double x0, double x1, double tol) {
        List<Iteracion> pasos = new ArrayList<>();
        double error = Double.MAX_VALUE;
        int iter = 0;
        double x2 = 0;

        while (error > tol && iter < 100) {
            double fx0 = eval(funcion, x0);
            double fx1 = eval(funcion, x1);
            x2 = (x0 + x1) / 2.0;
            double fx2 = eval(funcion, x2);

            if (iter > 0)
                error = Math.abs((x2 - pasos.get(pasos.size()-1).x2) / x2);
            else
                error = Math.abs(x1 - x0);

            pasos.add(new Iteracion(iter+1, x0, x1, x2, fx0, fx1, fx2, error));

            if (fx0 * fx2 < 0)
                x1 = x2;
            else
                x0 = x2;

            iter++;
        }
        return pasos;
    }
}
