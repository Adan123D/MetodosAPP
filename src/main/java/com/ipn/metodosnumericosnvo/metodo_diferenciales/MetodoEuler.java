package com.ipn.metodosnumericosnvo.metodo_diferenciales;

import org.mariuszgromada.math.mxparser.*;
import java.util.ArrayList;
import java.util.List;

public class MetodoEuler {

    public static class Step {
        private int paso;
        private double t;
        private double y;

        public Step(int paso, double t, double y) {
            this.paso = paso;
            this.t = t;
            this.y = y;
        }

        public int getPaso() { return paso; }
        public double getT() { return t; }
        public double getY() { return y; }
    }

    public void resolver(String exprStr, double t0, double y0, double tf, int n, List<Step> pasos) {
        double h = (tf - t0) / n;
        Argument tVar = new Argument("t", t0);
        Argument yVar = new Argument("y", y0);
        Expression expr = new Expression(exprStr, tVar, yVar);

        double t = t0;
        double y = y0;

        for (int i = 0; i <= n; i++) {
            pasos.add(new Step(i, t, y));
            tVar.setArgumentValue(t);
            yVar.setArgumentValue(y);
            double f = expr.calculate();
            y = y + h * f;
            t = t + h;
        }
    }
}