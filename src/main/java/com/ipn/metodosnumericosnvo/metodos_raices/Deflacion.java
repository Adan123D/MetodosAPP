package com.ipn.metodosnumericosnvo.metodos_raices;

import com.ipn.metodosnumericosnvo.math.DeflacionCompaniona;
import org.apache.commons.math3.complex.Complex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Deflacion {

    private static final double EPSILON = 1e-12;

    // División sintética COMPLEJA mejorada
    private Complex[] syntheticDivide(Complex[] coeffs, Complex root) {
        if (coeffs.length <= 1) {
            throw new IllegalArgumentException("El polinomio debe tener al menos grado 1");
        }

        int n = coeffs.length - 1;
        Complex[] quotient = new Complex[n];

        if (n == 0) return quotient;

        quotient[0] = coeffs[0];
        for (int i = 1; i < n; i++) {
            quotient[i] = quotient[i - 1].multiply(root).add(coeffs[i]);
        }
        return quotient;
    }

    // Conversor de double[] a Complex[]
    private Complex[] toComplexArray(double[] arr) {
        Complex[] result = new Complex[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = new Complex(arr[i], 0.0);
        }
        return result;
    }

    // Conversor de Complex[] a double[] (solo parte real)
    private double[] toRealArray(Complex[] arr) {
        double[] result = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[i].getReal();
        }
        return result;
    }

    // Selecciona la raíz más estable numéricamente
    private Complex selectBestRoot(List<Complex> roots, Complex[] poly) {
        Complex bestRoot = roots.get(0);
        double minError = evaluatePolynomial(poly, bestRoot).abs();

        for (Complex root : roots) {
            double error = evaluatePolynomial(poly, root).abs();
            if (error < minError) {
                minError = error;
                bestRoot = root;
            }
        }
        return bestRoot;
    }

    // Evalúa el polinomio en un punto dado
    private Complex evaluatePolynomial(Complex[] coeffs, Complex x) {
        Complex result = Complex.ZERO;
        Complex xPower = Complex.ONE;

        for (int i = coeffs.length - 1; i >= 0; i--) {
            result = result.add(coeffs[i].multiply(xPower));
            xPower = xPower.multiply(x);
        }
        return result;
    }

    // Verifica si el número complejo es prácticamente real
    private boolean isRealNumber(Complex c) {
        return Math.abs(c.getImaginary()) < EPSILON;
    }

    /**
     * Encuentra todas las raíces secuencialmente usando la matriz compañera
     * y deflación sintética mejorada.
     *
     * @param coeffs Coeficientes del polinomio ordenados de mayor a menor grado
     * @return Lista de todas las raíces complejas del polinomio
     */
    public List<Complex> findRoots(double[] coeffs) {
        if (coeffs == null || coeffs.length == 0) {
            throw new IllegalArgumentException("Los coeficientes no pueden estar vacíos");
        }

        // Remover coeficientes cero iniciales
        int startIndex = 0;
        while (startIndex < coeffs.length && Math.abs(coeffs[startIndex]) < EPSILON) {
            startIndex++;
        }

        if (startIndex == coeffs.length) {
            throw new IllegalArgumentException("Todos los coeficientes son cero");
        }

        double[] trimmedCoeffs = Arrays.copyOfRange(coeffs, startIndex, coeffs.length);
        List<Complex> roots = new ArrayList<>();
        Complex[] poly = toComplexArray(trimmedCoeffs);

        // Casos especiales
        if (poly.length == 1) {
            return roots; // Polinomio constante, sin raíces
        }

        if (poly.length == 2) {
            // Polinomio lineal: ax + b = 0 -> x = -b/a
            if (poly[0].abs() < EPSILON) {
                throw new ArithmeticException("División por cero: coeficiente principal es cero");
            }
            roots.add(poly[1].negate().divide(poly[0]));
            return roots;
        }

        // Deflación iterativa
        while (poly.length > 2) {
            try {
                DeflacionCompaniona companion = new DeflacionCompaniona();

                // Si el polinomio actual es prácticamente real, usar solo parte real
                boolean isRealPoly = true;
                for (Complex c : poly) {
                    if (!isRealNumber(c)) {
                        isRealPoly = false;
                        break;
                    }
                }

                List<Complex> currentRoots;
                if (isRealPoly) {
                    currentRoots = companion.findRoots(toRealArray(poly));
                } else {
                    // Para polinomios complejos, usar método alternativo o aproximación
                    currentRoots = companion.findRoots(toRealArray(poly));
                }

                if (currentRoots.isEmpty()) {
                    throw new ArithmeticException("No se pudieron encontrar raíces");
                }

                // Seleccionar la mejor raíz
                Complex selectedRoot = selectBestRoot(currentRoots, poly);
                roots.add(selectedRoot);

                // Realizar deflación
                poly = syntheticDivide(poly, selectedRoot);

            } catch (Exception e) {
                throw new ArithmeticException("Error durante la deflación: " + e.getMessage());
            }
        }

        // Última raíz (polinomio lineal restante)
        if (poly.length == 2) {
            if (poly[0].abs() < EPSILON) {
                throw new ArithmeticException("División por cero en la última raíz");
            }
            roots.add(poly[1].negate().divide(poly[0]));
        }

        return roots;
    }

    /**
     * Verifica las raíces encontradas evaluándolas en el polinomio original
     *
     * @param coeffs Coeficientes originales del polinomio
     * @param roots Raíces encontradas
     * @return true si todas las raíces son válidas (error < EPSILON)
     */
    public boolean verifyRoots(double[] coeffs, List<Complex> roots) {
        Complex[] polyCoeffs = toComplexArray(coeffs);

        for (Complex root : roots) {
            Complex evaluation = evaluatePolynomial(polyCoeffs, root);
            if (evaluation.abs() > EPSILON) {
                System.out.println("Raíz inválida: " + root + " -> f(root) = " + evaluation);
                return false;
            }
        }
        return true;
    }
}
