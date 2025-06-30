package com.ipn.metodosnumericosnvo.metodos_integracion;

import java.util.ArrayList;
import java.util.List;

public class Simpson38Compuesto {

    public static class Iteracion {
        private final int i;
        private final double fx;

        public Iteracion(int i, double fx) {
            this.i = i;
            this.fx = fx;
        }

        public int getI() { return i; }
        public double getFx() { return fx; }
    }

    public static double resolver(double[] f, double h, List<Iteracion> pasos) {
        int n = f.length;
        if ((n - 1) % 3 != 0) {
            throw new IllegalArgumentException("(n - 1) debe ser divisible por 3");
        }

        int k = (n - 1) / 3;
        double sum_3i_2 = 0;
        double sum_3i_1 = 0;
        double sum_3i = 0;

        for (int i = 0; i < n; i++) {
            pasos.add(new Iteracion(i, f[i]));
        }

        for (int i = 1; i <= k; i++) {
            int pos1 = 3 * i - 2;
            int pos2 = 3 * i - 1;

            sum_3i_2 += f[pos1];
            sum_3i_1 += f[pos2];

            if (i < k) {
                int pos3 = 3 * i;
                sum_3i += f[pos3];
            }
        }

        return (3 * h / 8.0) * (f[0] + 3 * sum_3i_2 + 3 * sum_3i_1 + 2 * sum_3i + f[n - 1]);
    }
}
