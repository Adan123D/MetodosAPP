package com.ipn.metodosnumericosnvo.metodos_raices;

import org.mariuszgromada.math.mxparser.*;
import java.util.List;

public class Secante_Aitken {

    @FunctionalInterface
    public interface Funcion {
        double evaluar(double x) throws Exception;
    }

    public static class Step {
        private int iteracion;
        private double xPrevPrev, fxPrevPrev;
        private double xPrev, fxPrev;
        private double xActual, fxActual;
        private double xAitken, fxAitken;

        public Step(int iteracion, double xPrevPrev, double fxPrevPrev, double xPrev, double fxPrev, 
                   double xActual, double fxActual, double xAitken, double fxAitken) {
            this.iteracion = iteracion;
            this.xPrevPrev = xPrevPrev;
            this.fxPrevPrev = fxPrevPrev;
            this.xPrev = xPrev;
            this.fxPrev = fxPrev;
            this.xActual = xActual;
            this.fxActual = fxActual;
            this.xAitken = xAitken;
            this.fxAitken = fxAitken;
        }

        // Getters para JavaFX TableView
        public int getIteracion() { return iteracion; }
        public double getXPrevPrev() { return xPrevPrev; }
        public double getFxPrevPrev() { return fxPrevPrev; }
        public double getXPrev() { return xPrev; }
        public double getFxPrev() { return fxPrev; }
        public double getXActual() { return xActual; }
        public double getFxActual() { return fxActual; }
        public double getXAitken() { return xAitken; }
        public double getFxAitken() { return fxAitken; }
    }

    public double calcularRaiz(Funcion f, double x0, double x1, double tolerancia, int maxIter, List<Step> pasos) throws Exception {
        double xPrevPrev = x0;
        double xPrev = x1;
        double xActual = x1;
        double xAitken = x1;

        int iter = 0;
        while (iter < maxIter) {
            double fxPrevPrev = f.evaluar(xPrevPrev);
            double fxPrev = f.evaluar(xPrev);

            if (Math.abs(fxPrev - fxPrevPrev) < 1e-14)
                throw new ArithmeticException("División por cero en la iteración " + iter);

            xActual = xPrev - fxPrev * (xPrev - xPrevPrev) / (fxPrev - fxPrevPrev);
            double fxActual = f.evaluar(xActual);

            // Aitken Δ² para acelerar con tres valores (xPrevPrev, xPrev, xActual)
            if (iter >= 2) {
                double den = xActual - 2 * xPrev + xPrevPrev;
                double num = (xActual - xPrev) * (xActual - xPrev);
                if (Math.abs(den) > 1e-14) {
                    xAitken = xActual - num / den;
                } else {
                    xAitken = xActual;
                }
            } else {
                xAitken = xActual;
            }

            double fxAitken = f.evaluar(xAitken);

            // Agregar el paso actual a la lista
            pasos.add(new Step(iter + 1, xPrevPrev, fxPrevPrev, xPrev, fxPrev, xActual, fxActual, xAitken, fxAitken));

            if (Math.abs(fxAitken) < tolerancia)
                return xAitken;

            xPrevPrev = xPrev;
            xPrev = xActual;
            iter++;
        }
        throw new Exception("No se encontró una raíz en el número máximo de iteraciones.");
    }

    // Mantener el método original para compatibilidad
    public double calcularRaiz(Funcion f, double x0, double x1, double tolerancia, int maxIter) throws Exception {
        return calcularRaiz(f, x0, x1, tolerancia, maxIter, new java.util.ArrayList<>());
    }
}
