package com.ipn.metodosnumericosnvo.metodos_integracion;

import java.util.ArrayList;
import java.util.List;

public class Simpson13Compuesto {

    public static class Iteracion {
        private final int i;
        private final double fx;

        public Iteracion(int i, double fx) {
            this.i = i;
            this.fx = fx;
        }

        public int getI() {
            return i;
        }

        public double getFx() {
            return fx;
        }
    }

    /**
     * Método Simpson 1/3 compuesto con puntos ya evaluados
     */
    public static double resolver(double[] f, double h, List<Iteracion> pasos) {
        int n = f.length;

        if ((n - 1) % 2 != 0) {
            throw new IllegalArgumentException("El número de puntos debe cumplir que (n-1) sea par.");
        }

        double sumImpares = 0;
        double sumPares = 0;

        for (int i = 1; i < n - 1; i++) {
            pasos.add(new Iteracion(i, f[i]));
            if (i % 2 == 0) {
                sumPares += f[i];
            } else {
                sumImpares += f[i];
            }
        }

        pasos.add(0, new Iteracion(0, f[0]));
        pasos.add(new Iteracion(n - 1, f[n - 1]));

        return (h / 3) * (f[0] + 2 * sumPares + 4 * sumImpares + f[n - 1]);
    }
}
