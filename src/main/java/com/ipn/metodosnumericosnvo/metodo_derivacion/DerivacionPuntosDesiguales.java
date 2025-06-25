package com.ipn.metodosnumericosnvo.metodo_derivacion;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Modelo para el cálculo de derivadas numéricas usando puntos desigualmente espaciados.
 * Implementa métodos de diferencias finitas y diferencias divididas de Newton.
 */
public class DerivacionPuntosDesiguales {
    
    /**
     * Calcula la primera derivada usando diferencias divididas de Newton
     * para puntos desigualmente espaciados.
     * 
     * @param x array de valores x (puntos independientes)
     * @param y array de valores y = f(x) correspondientes
     * @param puntoEvaluacion punto donde se evaluará la derivada
     * @return valor de la primera derivada en el punto especificado
     */
    public static double primeraDerivadaDiferenciaDividida(double[] x, double[] y, double puntoEvaluacion) {
        if (x.length != y.length || x.length < 2) {
            throw new IllegalArgumentException("Los arrays deben tener la misma longitud y al menos 2 elementos");
        }
        
        int n = x.length;
        double[][] tabla = new double[n][n];
        
        // Inicializar primera columna con valores y
        for (int i = 0; i < n; i++) {
            tabla[i][0] = y[i];
        }
        
        // Calcular diferencias divididas
        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                tabla[i][j] = (tabla[i + 1][j - 1] - tabla[i][j - 1]) / (x[i + j] - x[i]);
            }
        }
        
        // La primera derivada es aproximadamente la primera diferencia dividida
        // evaluada en el punto más cercano
        int indiceCercano = encontrarIndiceMasCercano(x, puntoEvaluacion);
        
        // Para mejor aproximación, usamos interpolación linear de las diferencias divididas
        if (indiceCercano < n - 1) {
            return tabla[indiceCercano][1];
        } else {
            return tabla[indiceCercano - 1][1];
        }
    }
    
    /**
     * Calcula la segunda derivada usando diferencias divididas de Newton
     * para puntos desigualmente espaciados.
     * 
     * @param x array de valores x (puntos independientes)
     * @param y array de valores y = f(x) correspondientes
     * @param puntoEvaluacion punto donde se evaluará la derivada
     * @return valor de la segunda derivada en el punto especificado
     */
    public static double segundaDerivadaDiferenciaDividida(double[] x, double[] y, double puntoEvaluacion) {
        if (x.length != y.length || x.length < 3) {
            throw new IllegalArgumentException("Los arrays deben tener la misma longitud y al menos 3 elementos para la segunda derivada");
        }
        
        int n = x.length;
        double[][] tabla = new double[n][n];
        
        // Inicializar primera columna con valores y
        for (int i = 0; i < n; i++) {
            tabla[i][0] = y[i];
        }
        
        // Calcular diferencias divididas
        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                tabla[i][j] = (tabla[i + 1][j - 1] - tabla[i][j - 1]) / (x[i + j] - x[i]);
            }
        }
        
        // La segunda derivada es aproximadamente 2 veces la segunda diferencia dividida
        int indiceCercano = encontrarIndiceMasCercano(x, puntoEvaluacion);
        
        if (indiceCercano < n - 2) {
            return 2 * tabla[indiceCercano][2];
        } else if (indiceCercano >= 2) {
            return 2 * tabla[indiceCercano - 2][2];
        } else {
            return 2 * tabla[0][2];
        }
    }
    
    /**
     * Calcula la derivada usando el método de diferencias finitas hacia adelante
     * adaptado para puntos desigualmente espaciados.
     * 
     * @param x array de valores x ordenados
     * @param y array de valores y correspondientes
     * @param indice índice del punto donde calcular la derivada
     * @return valor de la derivada en el punto especificado
     */
    public static double derivadaHaciaAdelante(double[] x, double[] y, int indice) {
        if (indice >= x.length - 1) {
            throw new IllegalArgumentException("Índice fuera de rango para diferencia hacia adelante");
        }
        
        double h = x[indice + 1] - x[indice];
        return (y[indice + 1] - y[indice]) / h;
    }
    
    /**
     * Calcula la derivada usando el método de diferencias finitas hacia atrás
     * adaptado para puntos desigualmente espaciados.
     * 
     * @param x array de valores x ordenados
     * @param y array de valores y correspondientes
     * @param indice índice del punto donde calcular la derivada
     * @return valor de la derivada en el punto especificado
     */
    public static double derivadaHaciaAtras(double[] x, double[] y, int indice) {
        if (indice <= 0) {
            throw new IllegalArgumentException("Índice fuera de rango para diferencia hacia atrás");
        }
        
        double h = x[indice] - x[indice - 1];
        return (y[indice] - y[indice - 1]) / h;
    }
    
    /**
     * Calcula la derivada usando el método de diferencias finitas centradas
     * adaptado para puntos desigualmente espaciados.
     * 
     * @param x array de valores x ordenados
     * @param y array de valores y correspondientes
     * @param indice índice del punto donde calcular la derivada
     * @return valor de la derivada en el punto especificado
     */
    public static double derivadaCentrada(double[] x, double[] y, int indice) {
        if (indice <= 0 || indice >= x.length - 1) {
            throw new IllegalArgumentException("Índice fuera de rango para diferencia centrada");
        }
        
        double h1 = x[indice] - x[indice - 1];
        double h2 = x[indice + 1] - x[indice];
        
        // Fórmula para diferencias centradas con espaciado desigual
        return (h1 * h1 * y[indice + 1] - (h1 * h1 - h2 * h2) * y[indice] - h2 * h2 * y[indice - 1]) 
               / (h1 * h2 * (h1 + h2));
    }
    
    /**
     * Encuentra el índice del punto más cercano al valor dado.
     * 
     * @param x array de valores x
     * @param valor valor de referencia
     * @return índice del punto más cercano
     */
    private static int encontrarIndiceMasCercano(double[] x, double valor) {
        int indice = 0;
        double distanciaMinima = Math.abs(x[0] - valor);
        
        for (int i = 1; i < x.length; i++) {
            double distancia = Math.abs(x[i] - valor);
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                indice = i;
            }
        }
        
        return indice;
    }
    
    /**
     * Calcula la tabla completa de diferencias divididas para análisis.
     * 
     * @param x array de valores x
     * @param y array de valores y
     * @return matriz con la tabla de diferencias divididas
     */
    public static double[][] calcularTablaDiferenciasDivididas(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Los arrays deben tener la misma longitud");
        }
        
        int n = x.length;
        double[][] tabla = new double[n][n];
        
        // Inicializar primera columna
        for (int i = 0; i < n; i++) {
            tabla[i][0] = y[i];
        }
        
        // Calcular diferencias divididas
        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                tabla[i][j] = (tabla[i + 1][j - 1] - tabla[i][j - 1]) / (x[i + j] - x[i]);
            }
        }
        
        return tabla;
    }
    
    /**
     * Valida que los arrays de entrada sean válidos.
     * 
     * @param x array de valores x
     * @param y array de valores y
     * @throws IllegalArgumentException si los arrays no son válidos
     */
    public static void validarDatos(double[] x, double[] y) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("Los arrays no pueden ser nulos");
        }
        
        if (x.length != y.length) {
            throw new IllegalArgumentException("Los arrays deben tener la misma longitud");
        }
        
        if (x.length < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 puntos");
        }
        
        // Verificar que los puntos x estén ordenados
        for (int i = 1; i < x.length; i++) {
            if (x[i] <= x[i - 1]) {
                throw new IllegalArgumentException("Los valores x deben estar en orden estrictamente creciente");
            }
        }
    }
}