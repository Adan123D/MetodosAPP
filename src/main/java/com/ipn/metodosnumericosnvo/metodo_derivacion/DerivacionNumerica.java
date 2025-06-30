package com.ipn.metodosnumericosnvo.metodo_derivacion;



import org.mariuszgromada.math.mxparser.Expression;

public class DerivacionNumerica {

    public static double f(String funcion, double x) {
        Expression e = new Expression(funcion);
        e.defineArgument("x", x);
        return e.calculate();
    }

    public static Object[][] calcularDerivadas(String funcion, double a, double b, int n) {
        double h = (b - a) / (n - 1);
        Object[][] datos = new Object[n][3];

        for (int i = 0; i < n; i++) {
            double x_i = a + i * h;
            double fx_i = f(funcion, x_i);
            double derivada;

            if (i == 0) {
                double fx1 = f(funcion, x_i + h);
                double fx2 = f(funcion, x_i + 2 * h);
                derivada = (-3 * fx_i + 4 * fx1 - fx2) / (2 * h);
            } else if (i == n - 1) {
                double fx_1 = f(funcion, x_i - h);
                double fx_2 = f(funcion, x_i - 2 * h);
                derivada = (3 * fx_i - 4 * fx_1 + fx_2) / (2 * h);
            } else {
                double fx_next = f(funcion, x_i + h);
                double fx_prev = f(funcion, x_i - h);
                derivada = (fx_next - fx_prev) / (2 * h);
            }

            datos[i][0] = String.format("%.6f", x_i);
            datos[i][1] = String.format("%.6f", fx_i);
            datos[i][2] = String.format("%.6f", derivada);
        }

        return datos;
    }
}