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
        String f1_expr = f; // Primera derivada f(t,y)
        String f2_expr = "D(" + f + ", t) + D(" + f + ", y)*" + f; // Segunda derivada completa df/dt + df/dy * f

        pasos.add(new Iteracion(0, t, y)); // Agregar condición inicial

        for (int i = 1; i <= n; i++) {
            // Evaluar f(t, y) - primera derivada
            String f1_eval = f1_expr.replace("t", String.format("%.10f", t))
                                 .replace("y", String.format("%.10f", y));
            double f1_val = util.evaluate(f1_eval).evalDouble();

            // Evaluar f'(t, y) - segunda derivada
            IExpr f2_symbolic = util.evaluate(f2_expr);
            String f2_eval_str = f2_symbolic.toString()
                    .replace("t", String.format("%.10f", t))
                    .replace("y", String.format("%.10f", y));
            double f2_val = util.evaluate(f2_eval_str).evalDouble();

            // Serie de Taylor de orden 2
            y = y + h * f1_val + (Math.pow(h, 2) / 2.0) * f2_val;
            t = t + h;

            pasos.add(new Iteracion(i, t, y));
        }

        return pasos;
    }

    /**
     * Obtiene la expansión simbólica de la serie de Taylor para una EDO.
     * @param funcion Función de la EDO en la forma dy/dt = f(t,y)
     * @return Representación textual de la expansión de Taylor
     */
    public static String obtenerExpansionTaylor(String funcion) {
        F.initSymbols();
        EvalUtilities util = new EvalUtilities(false, true);

        try {
            // Primera derivada f(t,y)
            String f1 = funcion;

            // Segunda derivada df/dt + df/dy * f
            String f2 = "D(" + f1 + ", t) + D(" + f1 + ", y)*" + f1;
            IExpr f2_symbolic = util.evaluate(f2);

            // Tercera derivada (opcional para expansiones de orden superior)
            String f3 = "D(" + f2 + ", t) + D(" + f2 + ", y)*" + f1;
            IExpr f3_symbolic = util.evaluate(f3);

            StringBuilder expansion = new StringBuilder();
            expansion.append("Expansión de Taylor para dy/dt = ").append(funcion).append("\n\n");
            expansion.append("y(t + h) = y(t) + h·f(t,y) + (h²/2!)·f'(t,y) + (h³/3!)·f''(t,y) + ...\n\n");
            expansion.append("Donde:\n");
            expansion.append("f(t,y) = ").append(f1).append("\n");
            expansion.append("f'(t,y) = ").append(f2_symbolic.toString()).append("\n");
            expansion.append("f''(t,y) = ").append(f3_symbolic.toString()).append("\n\n");
            expansion.append("Para el método de Taylor de orden 2 implementado se usa:\n");
            expansion.append("y(t + h) = y(t) + h·f(t,y) + (h²/2!)·f'(t,y)\n");

            return expansion.toString();
        } catch (Exception e) {
            return "Error al generar la expansión: " + e.getMessage();
        }
    }
}