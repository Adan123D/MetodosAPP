package com.ipn.metodosnumericosnvo.metodos_raices;

import org.mariuszgromada.math.mxparser.*;
import java.util.List;

public class Biseccion_Aitken {
    public static class Step {
        public int paso;
        public double a, b, c, fc, aitken;
        public Step(int paso, double a, double b, double c, double fc, double aitken) {
            this.paso = paso; this.a = a; this.b = b;
            this.c = c; this.fc = fc; this.aitken = aitken;
        }
        public int getPaso() { return paso; }
        public double getA() { return a; }
        public double getB() { return b; }
        public double getC() { return c; }
        public double getFc() { return fc; }
        public double getAitken() { return aitken; }
    }

    public double resolver(String exprF, double a, double b,
                           double tol, int maxIt, List<Step> steps) {
        Argument xArg = new Argument("x", 0);
        Expression f = new Expression(exprF, xArg);
        xArg.setArgumentValue(a);
        double fa = f.calculate();
        xArg.setArgumentValue(b);
        double fb = f.calculate();
        if (fa * fb > 0) throw new IllegalArgumentException("f(a) y f(b) deben tener signos opuestos");

        double c = 0, fc = 0;
        Double cPrev1 = null, cPrev2 = null;

        for (int i = 1; i <= maxIt; i++) {
            c = (a + b) / 2;
            xArg.setArgumentValue(c);
            fc = f.calculate();

            if (fa * fc <= 0) {
                b = c; fb = fc;
            } else {
                a = c; fa = fc;
            }

            double aitken = c;
            if (cPrev2 != null && cPrev1 != null) {
                double x0 = cPrev2, x1 = cPrev1, x2 = c;
                double delta1 = x1 - x0;
                double delta2 = x2 - x1;
                double denom = delta2 - delta1;
                if (Math.abs(denom) > 1e-14) {
                    aitken = x0 - (delta1 * delta1) / denom;
                }
            }

            steps.add(new Step(i, a, b, c, fc, aitken));
            if (Math.abs(b - a) / 2 < tol || Math.abs(fc) < tol) {
                return aitken;
            }

            cPrev2 = cPrev1;
            cPrev1 = c;
        }
        return c;
    }
}