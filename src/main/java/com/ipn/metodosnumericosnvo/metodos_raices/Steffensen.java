package com.ipn.metodosnumericosnvo.metodos_raices;

import org.mariuszgromada.math.mxparser.*;
import java.util.List;

public class Steffensen {
    public static class Step {
        public int paso;
        public double xn, fxn, fxnPlus, xn1, fxn1;
        public Step(int paso, double xn, double fxn, double fxnPlus, double xn1, double fxn1) {
            this.paso = paso;
            this.xn = xn;
            this.fxn = fxn;
            this.fxnPlus = fxnPlus;
            this.xn1 = xn1;
            this.fxn1 = fxn1;
        }
        public int getPaso() { return paso; }
        public double getXn() { return xn; }
        public double getFxn() { return fxn; }
        public double getFxnPlus() { return fxnPlus; }
        public double getXn1() { return xn1; }
        public double getFxn1() { return fxn1; }
    }

    public double resolver(String exprF, double x0, double tol, int maxIter, List<Step> steps) {
        Argument argX = new Argument("x", x0);
        Expression f = new Expression(exprF, argX);
        double xn = x0;
        for (int i = 1; i <= maxIter; i++) {
            argX.setArgumentValue(xn);
            double fxn = f.calculate();
            argX.setArgumentValue(xn + fxn);
            double fxnPlus = f.calculate();
            double denom = fxnPlus - fxn;
            if (denom == 0) denom = 1e-12;
            double xn1 = xn - (fxn*fxn)/denom;
            argX.setArgumentValue(xn1);
            double fxn1 = f.calculate();
            steps.add(new Step(i, xn, fxn, fxnPlus, xn1, fxn1));
            if (Math.abs(fxn1) < tol) return xn1;
            xn = xn1;
        }
        throw new IllegalArgumentException("No converge en " + maxIter + " iteraciones.");
    }
}
