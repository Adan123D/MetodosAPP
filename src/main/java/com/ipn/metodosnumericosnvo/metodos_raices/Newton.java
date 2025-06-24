package com.ipn.metodosnumericosnvo.metodos_raices;

import org.mariuszgromada.math.mxparser.*;
import java.util.List;
import com.ipn.metodosnumericosnvo.math.DerivativeCalculator;

public class Newton {
    private DerivativeCalculator derivativeCalculator;
    private String derivadaCalculada; // Para almacenar la derivada calculada

    /**
     * Constructor por defecto.
     * Inicializa el calculador de derivadas.
     */
    public Newton() {
        this.derivativeCalculator = new DerivativeCalculator();
    }

    public static class Step {
        public int paso;
        public double x0, fx0, x1, fx1;

        public Step(int paso, double x0, double fx0, double x1, double fx1) {
            this.paso = paso;
            this.x0 = x0;
            this.fx0 = fx0;
            this.x1 = x1;
            this.fx1 = fx1;
        }
        public int getPaso() { return paso; }
        public double getX0() { return x0; }
        public double getFx0() { return fx0; }
        public double getX1() { return x1; }
        public double getFx1() { return fx1; }
    }

    /**
     * Método de Newton-Raphson.
     * @param exprF  Función f(x) en texto
     * @param exprG  Derivada f'(x) en texto
     * @param x0     Valor inicial
     * @param tol    Tolerancia
     * @param maxIt  Iteraciones máximas
     * @param pasos  Lista donde se llenan los pasos para la tabla
     * @return       Raíz aproximada encontrada
     */
    public double resolver(String exprF, String exprG, double x0, double tol, int maxIt, List<Step> pasos) {
        Argument argX = new Argument("x", x0);
        Expression f = new Expression(exprF, argX);
        Expression g = new Expression(exprG, argX);

        int paso = 1;
        double fx0, gx0, x1 = x0, fx1 = 0;

        while (paso <= maxIt) {
            fx0 = evalExpr(f, argX, x0);
            gx0 = evalExpr(g, argX, x0);

            if (Double.isNaN(fx0) || Double.isNaN(gx0)) {
                throw new IllegalArgumentException("Error evaluando función o derivada. Revisa las expresiones.");
            }
            if (gx0 == 0.0) {
                throw new IllegalArgumentException("Error matemático: la derivada es cero.");
            }

            x1 = x0 - fx0 / gx0;
            fx1 = evalExpr(f, argX, x1);

            pasos.add(new Step(paso, x0, fx0, x1, fx1));

            if (Math.abs(fx1) <= tol) break;

            x0 = x1;
            paso++;
        }

        if (paso > maxIt && Math.abs(fx1) > tol) {
            throw new IllegalArgumentException("No converge en el número de iteraciones especificado.");
        }

        return x1;
    }

    private double evalExpr(Expression expr, Argument argX, double x) {
        argX.setArgumentValue(x);
        return expr.calculate();
    }

    /**
     * Método de Newton-Raphson con cálculo automático de la derivada.
     * @param exprF  Función f(x) en texto
     * @param x0     Valor inicial
     * @param tol    Tolerancia
     * @param maxIt  Iteraciones máximas
     * @param pasos  Lista donde se llenan los pasos para la tabla
     * @return       Raíz aproximada encontrada
     */
    public double resolverConDerivadaAutomatica(String exprF, double x0, double tol, int maxIt, List<Step> pasos) {
        // Calcular la derivada simbólica
        String exprG = derivativeCalculator.getDerivativeExpression(exprF, "x", 1);

        // Almacenar la derivada calculada
        this.derivadaCalculada = exprG;

        // Usar el método resolver existente con la derivada calculada
        return resolver(exprF, exprG, x0, tol, maxIt, pasos);
    }

    /**
     * Obtiene la derivada calculada automáticamente.
     * @return La expresión de la derivada calculada
     */
    public String getDerivadaCalculada() {
        return derivadaCalculada;
    }
}
