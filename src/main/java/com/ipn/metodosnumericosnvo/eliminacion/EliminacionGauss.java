package com.ipn.metodosnumericosnvo.eliminacion;

public class EliminacionGauss {

    public double[][] eliminacionGaussSimple(double[][] matriz) {
        int n = matriz.length;
        double[][] m = copiarMatriz(matriz);

        for (int k = 0; k < n - 1; k++) {
            for (int i = k + 1; i < n; i++) {
                double factor = m[i][k] / m[k][k];
                for (int j = k; j <= n; j++) {
                    m[i][j] -= factor * m[k][j];
                }
            }
        }
        return m;
    }

    public double[][] eliminacionGaussPivoteo(double[][] matriz) {
        int n = matriz.length;
        double[][] m = copiarMatriz(matriz);

        for (int k = 0; k < n - 1; k++) {
            int max = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(m[i][k]) > Math.abs(m[max][k])) {
                    max = i;
                }
            }
            double[] temp = m[k];
            m[k] = m[max];
            m[max] = temp;

            for (int i = k + 1; i < n; i++) {
                double factor = m[i][k] / m[k][k];
                for (int j = k; j <= n; j++) {
                    m[i][j] -= factor * m[k][j];
                }
            }
        }
        return m;
    }

    public double[][] eliminacionGaussEscalada(double[][] matriz) {
        // Implementación opcional si la quieres más adelante
        return eliminacionGaussPivoteo(matriz); // Por ahora lo mismo
    }

    public double[] sustitucionRegresiva(double[][] m) {
        int n = m.length;
        double[] x = new double[n];

        for (int i = n - 1; i >= 0; i--) {
            x[i] = m[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= m[i][j] * x[j];
            }
            x[i] /= m[i][i];
        }

        return x;
    }

    private double[][] copiarMatriz(double[][] original) {
        double[][] copia = new double[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            copia[i] = original[i].clone();
        }
        return copia;
    }
}