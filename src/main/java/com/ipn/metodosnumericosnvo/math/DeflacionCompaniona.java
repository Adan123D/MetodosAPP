package com.ipn.metodosnumericosnvo.math;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que implementa el método de la matriz compañera para encontrar raíces de polinomios.
 * Este método convierte un polinomio en una matriz compañera y encuentra sus valores propios,
 * que son las raíces del polinomio.
 */
public class DeflacionCompaniona {

    /**
     * Encuentra las raíces de un polinomio usando el método de la matriz compañera.
     * 
     * @param coeffs Coeficientes del polinomio, ordenados de mayor a menor grado
     * @return Lista de raíces complejas del polinomio
     */
    public List<Complex> findRoots(double[] coeffs) {
        // Normalizar los coeficientes dividiendo por el coeficiente principal
        double[] normalizedCoeffs = normalizeCoefficients(coeffs);
        
        // Crear la matriz compañera
        RealMatrix companionMatrix = createCompanionMatrix(normalizedCoeffs);
        
        // Calcular los valores propios (eigenvalues) de la matriz
        EigenDecomposition eigenDecomposition = new EigenDecomposition(companionMatrix);
        
        // Obtener los valores propios como raíces del polinomio
        List<Complex> roots = new ArrayList<>();
        for (int i = 0; i < eigenDecomposition.getRealEigenvalues().length; i++) {
            double realPart = eigenDecomposition.getRealEigenvalue(i);
            double imagPart = eigenDecomposition.getImagEigenvalue(i);
            roots.add(new Complex(realPart, imagPart));
        }
        
        return roots;
    }
    
    /**
     * Normaliza los coeficientes del polinomio dividiendo por el coeficiente principal.
     * 
     * @param coeffs Coeficientes originales
     * @return Coeficientes normalizados
     */
    private double[] normalizeCoefficients(double[] coeffs) {
        double[] normalized = new double[coeffs.length];
        double leadingCoeff = coeffs[0];
        
        for (int i = 0; i < coeffs.length; i++) {
            normalized[i] = coeffs[i] / leadingCoeff;
        }
        
        return normalized;
    }
    
    /**
     * Crea la matriz compañera para un polinomio con coeficientes normalizados.
     * 
     * @param normalizedCoeffs Coeficientes normalizados del polinomio
     * @return Matriz compañera
     */
    private RealMatrix createCompanionMatrix(double[] normalizedCoeffs) {
        int n = normalizedCoeffs.length - 1;
        double[][] matrixData = new double[n][n];
        
        // Llenar la primera fila con los coeficientes negativos
        for (int i = 0; i < n; i++) {
            matrixData[0][i] = -normalizedCoeffs[i + 1];
        }
        
        // Llenar el resto de la matriz con 1's en la subdiagonal
        for (int i = 1; i < n; i++) {
            matrixData[i][i - 1] = 1.0;
        }
        
        return new Array2DRowRealMatrix(matrixData);
    }
}