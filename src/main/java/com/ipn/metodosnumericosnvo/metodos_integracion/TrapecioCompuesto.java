package com.ipn.metodosnumericosnvo.metodos_integracion;

import java.util.ArrayList;
import java.util.List;

public class TrapecioCompuesto {

    public static class Iteracion {
        private final int i;
        private final double xi;
        private final double fxi;

        public Iteracion(int i, double xi, double fxi) {
            this.i = i;
            this.xi = xi;
            this.fxi = fxi;
        }

        public int getI() { return i; }
        public double getXi() { return xi; }
        public double getFxi() { return fxi; }
    }

    public static double resolver(double a, double b, int n, List<Iteracion> pasos) {
        double h = (b - a) / n;
        double suma = f(a) + f(b);
        pasos.add(new Iteracion(0, a, f(a)));
        pasos.add(new Iteracion(n, b, f(b)));

        for (int i = 1; i < n; i++) {
            double xi = a + i * h;
            double fxi = f(xi);
            pasos.add(new Iteracion(i, xi, fxi));
            suma += 2 * fxi;
        }

        return (h / 2) * suma;
    }

    public static double f(double x) {
        return Math.pow(x, 2); // Puede cambiarse a una función del usuario si se desea
    }
}
