package com.ipn.metodosnumericosnvo.metodo_derivacion;

import java.util.function.Function;

public class NumericalDifferentiationModel {

    public static double forwardDifference(Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - f.apply(x)) / h;
    }

    public static double backwardDifference(Function<Double, Double> f, double x, double h) {
        return (f.apply(x) - f.apply(x - h)) / h;
    }

    public static double centralDifference(Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - f.apply(x - h)) / (2 * h);
    }

    public static double secondOrderCentralDifference(Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - 2 * f.apply(x) + f.apply(x - h)) / (h * h);
    }

    // Métodos para 3, 4 y 5 puntos pueden ser implementados de manera similar
}
