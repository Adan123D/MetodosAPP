package com.ipn.metodosnumericosnvo.metodo_diferenciales;

import java.util.ArrayList;
import java.util.List;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

public class MetodoSEDO {

    public static class Iteracion {
        private final int paso;
        private final double t, u1, u2;

        public Iteracion(int paso, double t, double u1, double u2) {
            this.paso = paso;
            this.t = t;
            this.u1 = u1;
            this.u2 = u2;
        }

        public int getPaso() { return paso; }
        public double getT() { return t; }
        public double getU1() { return u1; }
        public double getU2() { return u2; }
    }

    public static List<Iteracion> resolver(double t0, double u1, double u2, double tf, int n,
                                           String expr1, String expr2) {
        Argument tArg = new Argument("t");
        Argument u1Arg = new Argument("u1");
        Argument u2Arg = new Argument("u2");

        Expression f1 = new Expression(expr1, tArg, u1Arg, u2Arg);
        Expression f2 = new Expression(expr2, tArg, u1Arg, u2Arg);

        double h = (tf - t0) / n;
        List<Iteracion> pasos = new ArrayList<>();
        double t = t0;

        for (int i = 0; i <= n; i++) {
            pasos.add(new Iteracion(i, t, u1, u2));

            double k11 = h * eval(f1, tArg, u1Arg, u2Arg, t, u1, u2);
            double k12 = h * eval(f2, tArg, u1Arg, u2Arg, t, u1, u2);

            double k21 = h * eval(f1, tArg, u1Arg, u2Arg, t + h / 2.0, u1 + k11 / 2.0, u2 + k12 / 2.0);
            double k22 = h * eval(f2, tArg, u1Arg, u2Arg, t + h / 2.0, u1 + k11 / 2.0, u2 + k12 / 2.0);

            double k31 = h * eval(f1, tArg, u1Arg, u2Arg, t + h / 2.0, u1 + k21 / 2.0, u2 + k22 / 2.0);
            double k32 = h * eval(f2, tArg, u1Arg, u2Arg, t + h / 2.0, u1 + k21 / 2.0, u2 + k22 / 2.0);

            double k41 = h * eval(f1, tArg, u1Arg, u2Arg, t + h, u1 + k31, u2 + k32);
            double k42 = h * eval(f2, tArg, u1Arg, u2Arg, t + h, u1 + k31, u2 + k32);

            u1 += (1.0 / 6.0) * (k11 + 2 * k21 + 2 * k31 + k41);
            u2 += (1.0 / 6.0) * (k12 + 2 * k22 + 2 * k32 + k42);
            t += h;
        }

        return pasos;
    }

    private static double eval(Expression expr,
                               Argument tArg,
                               Argument u1Arg,
                               Argument u2Arg,
                               double t, double u1, double u2) {
        tArg.setArgumentValue(t);
        u1Arg.setArgumentValue(u1);
        u2Arg.setArgumentValue(u2);
        return expr.calculate();
    }
}