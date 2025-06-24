package com.ipn.metodosnumericosnvo.metodos_raices;

import org.apache.commons.math3.complex.Complex;
import java.util.ArrayList;
import java.util.List;

import com.ipn.metodosnumericosnvo.math.DeflacionCompaniona;

public class Deflacion {

    /** Divide el polinomio por (x - root) usando división sintética. */
    private double[] syntheticDivide(double[] coeffs, Complex root) {
        int n = coeffs.length - 1;
        double[] quotient = new double[n];
        Complex b = new Complex(coeffs[0], 0);

        quotient[0] = b.getReal();
        for (int i = 1; i < n; i++) {
            b = b.multiply(root).add(new Complex(coeffs[i], 0));
            quotient[i] = b.getReal();
        }
        return quotient;
    }

    /**
     * Encuentra las raíces secuencialmente usando Companion Matrix en cada paso.
     */
    public List<Complex> findRoots(double[] coeffs) {
        List<Complex> roots = new ArrayList<>();
        double[] poly = coeffs.clone();

        for (int k = poly.length - 1; k > 1; k--) {
            DeflacionCompaniona companion = new DeflacionCompaniona();
            List<Complex> someRoots = companion.findRoots(poly);
            Complex root = someRoots.get(0);
            roots.add(root);
            poly = syntheticDivide(poly, root);
        }
        // Última raíz (de polinomio lineal)
        roots.add(new Complex(-poly[1] / poly[0], 0));
        return roots;
    }
}
