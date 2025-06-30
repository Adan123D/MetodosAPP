package com.ipn.metodosnumericosnvo.metodo_diferenciales;

public class minCuadrados {

    // Variable estática para almacenar la matriz del sistema
    private static double[][] matrizSistema;

    public static class Coeficientes {
        public final int grado;
        public final double[] coef;

        public Coeficientes(int grado, double[] coef) {
            this.grado = grado;
            this.coef = coef;
        }
    }

    /**
     * Obtiene la matriz del sistema generada en el último cálculo de coeficientes
     * @return La matriz del sistema
     */
    public static double[][] getMatrizSistema() {
        return matrizSistema;
    }

    public static Coeficientes obtenerCoeficientes(int grado, double[] X, double[] Y) {
        int n = grado + 1;
        double[][] matrizCoef = new double[n][n + 1];
        double sum;

        // Llenar la matriz del sistema normal
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sum = 0.0;
                for (int k = 0; k < X.length; k++) {
                    sum += Math.pow(X[k], i + j);
                }
                matrizCoef[i][j] = sum;
            }
            // Llenar el lado derecho (Y)
            sum = 0.0;
            for (int k = 0; k < X.length; k++) {
                sum += Y[k] * Math.pow(X[k], i);
            }
            matrizCoef[i][n] = sum;
        }

        // Guardar la matriz para que pueda ser accedida por el método de eliminación gaussiana
        matrizSistema = matrizCoef;

        // Para ahora, simplemente retornar los coeficientes
        double[] coef = new double[n];
        // Aquí deberías resolver la matriz y llenar 'coef'
        return new Coeficientes(grado, coef);
    }
}
