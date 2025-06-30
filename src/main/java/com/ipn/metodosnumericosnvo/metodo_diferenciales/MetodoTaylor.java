package com.ipn.metodosnumericosnvo.metodo_diferenciales;

import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import java.util.ArrayList;
import java.util.List;

public class MetodoTaylor {

    public static class Iteracion {
        private final int paso;
        private final double t;
        private final double y;

        public Iteracion(int paso, double t, double y) {
            this.paso = paso;
            this.t = t;
            this.y = y;
        }

        public int getPaso() { return paso; }
        public double getT() { return t; }
        public double getY() { return y; }
    }

    /**
     * Resuelve la EDO usando el método de Taylor de orden 2.
     * @param t0 Tiempo inicial
     * @param y0 Valor inicial de y
     * @param tf Tiempo final
     * @param n Número de pasos
     * @param funcion Cadena de la función (ej: "y - t^2 + 1")
     * @return Lista de iteraciones con paso, t, y
     */
    public static List<Iteracion> resolver(double t0, double y0, double tf, int n, String funcion) {
        F.initSymbols();
        EvalUtilities util = new EvalUtilities(false, true);

        List<Iteracion> pasos = new ArrayList<>();

        double h = (tf - t0) / n;
        double t = t0;
        double y = y0;

        String f = funcion;
        String f2_expr = "D(" + f + ", t)";  // Derivada simbólica respecto a t

        for (int i = 0; i <= n; i++) {
            pasos.add(new Iteracion(i, t, y));

            String f_eval = f.replace("t", String.format("%.10f", t))
                             .replace("y", String.format("%.10f", y));
            double f_val = util.evaluate(f_eval).evalDouble();

            IExpr f2_symbolic = util.evaluate(f2_expr);
            String f2_eval_str = f2_symbolic.toString()
                    .replace("t", String.format("%.10f", t))
                    .replace("y", String.format("%.10f", y));
            double f2_val = util.evaluate(f2_eval_str).evalDouble();

            y = y + h * f_val + (Math.pow(h, 2) / 2.0) * f2_val;
            t = t + h;
        }

        return pasos;
    }
}