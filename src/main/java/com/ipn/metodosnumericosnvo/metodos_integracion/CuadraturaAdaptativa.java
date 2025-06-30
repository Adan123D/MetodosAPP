package com.ipn.metodosnumericosnvo.metodos_integracion;

import java.util.ArrayList;
import java.util.List;
import org.mariuszgromada.math.mxparser.*;

public class CuadraturaAdaptativa {

    public static class Iteracion {
        public int nivel;
        public double a, b, s, s2, error;

        public Iteracion(int nivel, double a, double b, double s, double s2, double error) {
            this.nivel = nivel;
            this.a = a;
            this.b = b;
            this.s = s;
            this.s2 = s2;
            this.error = error;
        }

        public int getNivel() { return nivel; }
        public double getA() { return a; }
        public double getB() { return b; }
        public double getS() { return s; }
        public double getS2() { return s2; }
        public double getError() { return error; }
    }

    public static double resolver(String funcionStr, double a, double b, double tol, int maxRec, List<Iteracion> pasos) {
        Argument x = new Argument("x", 0);
        Expression f = new Expression(funcionStr, x);

        // Verificar si la función es válida
        if (!f.checkSyntax()) {
            throw new IllegalArgumentException("Error en la sintaxis de la función: " + f.getErrorMessage());
        }

        try {
            // Verificar si hay singularidades en el intervalo [a,b]
            double potencialSingularidad = 0.0;
            if (a <= 0 && b >= 0) {
                // Si el intervalo contiene x=0 y la función tiene términos como x^(-n), dividir el intervalo
                if (funcionStr.contains("x^(-") || funcionStr.contains("1/x") || 
                    funcionStr.contains("1/(x") || funcionStr.contains("/x")) {

                    if (Math.abs(a) < 1e-10) {
                        // Si a está muy cerca de 0, moverse un poco
                        a = 1e-10;
                    }

                    if (Math.abs(b) < 1e-10) {
                        // Si b está muy cerca de 0, moverse un poco
                        b = -1e-10;
                    }

                    // Si 0 está en el intervalo, dividir la integración
                    if (a < 0 && b > 0) {
                        List<Iteracion> pasosIzq = new ArrayList<>();
                        List<Iteracion> pasosDer = new ArrayList<>();

                        double epsilon = 1e-10;
                        double resultadoIzq = 0;
                        double resultadoDer = 0;

                        try {
                            resultadoIzq = simpsonRecursiva(f, x, a, -epsilon, tol/2, 
                                          simpsonCompuesta(f, x, a, -epsilon), maxRec/2, 1, pasosIzq);
                        } catch (Exception e) {
                            // Manejar error en la parte izquierda
                        }

                        try {
                            resultadoDer = simpsonRecursiva(f, x, epsilon, b, tol/2, 
                                          simpsonCompuesta(f, x, epsilon, b), maxRec/2, 1, pasosDer);
                        } catch (Exception e) {
                            // Manejar error en la parte derecha
                        }

                        // Combinar los pasos
                        pasos.addAll(pasosIzq);
                        pasos.addAll(pasosDer);

                        return resultadoIzq + resultadoDer;
                    }
                }
            }

            double s = simpsonCompuesta(f, x, a, b);
            return simpsonRecursiva(f, x, a, b, tol, s, maxRec, 1, pasos);
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular la integral: " + e.getMessage(), e);
        }
    }

    private static double simpsonCompuesta(Expression f, Argument x, double a, double b) {
        double h = (b - a) / 2.0;
        double x0 = a;
        double x1 = a + h / 2;
        double x2 = a + h;
        double x3 = a + 3 * h / 2;
        double x4 = b;
        return (h / 6.0) * (eval(f, x, x0) + 4 * (eval(f, x, x1) + eval(f, x, x3)) + 2 * (eval(f, x, x2) + eval(f, x, x4)));
    }

    private static double simpsonRecursiva(Expression f, Argument x, double a, double b, double tol, double s, int rec, int nivel, List<Iteracion> pasos) {
        // Verificar si el intervalo es demasiado pequeño para evitar problemas de precisión
        if (Math.abs(b - a) < 1.0e-10) {
            return s;
        }

        try {
            double m = (a + b) / 2.0;
            double sl = simpsonCompuesta(f, x, a, m);
            double sr = simpsonCompuesta(f, x, m, b);
            double s2 = sl + sr;
            double error = Math.abs(s2 - s);

            pasos.add(new Iteracion(nivel, a, b, s, s2, error));

            // Límite en el nivel de recursión para evitar desbordamiento de pila
            if (rec <= 0 || nivel >= 50 || error < 15 * tol) {
                return s2 + (s2 - s) / 15.0;
            }

            return simpsonRecursiva(f, x, a, m, tol / 2, sl, rec - 1, nivel + 1, pasos)
                 + simpsonRecursiva(f, x, m, b, tol / 2, sr, rec - 1, nivel + 1, pasos);
        } catch (Exception e) {
            // Si hay un error en este intervalo, dividirlo más o retornar una estimación
            if (nivel < 20 && rec > 0) {
                double m = (a + b) / 2.0;
                try {
                    return simpsonRecursiva(f, x, a, m, tol / 2, s / 2, rec - 1, nivel + 1, pasos) +
                           simpsonRecursiva(f, x, m, b, tol / 2, s / 2, rec - 1, nivel + 1, pasos);
                } catch (Exception ex) {
                    // Si sigue fallando, devolver la mejor estimación disponible
                    return s;
                }
            } else {
                return s; // Retornar la mejor estimación disponible
            }
        }
    }

    private static double eval(Expression f, Argument x, double val) {
        try {
            x.setArgumentValue(val);
            double result = f.calculate();

            // Verificar si el resultado es un valor válido
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new ArithmeticException("Resultado no definido en x = " + val);
            }

            return result;
        } catch (Exception e) {
            throw new ArithmeticException("Error al evaluar la función en x = " + val + ": " + e.getMessage());
        }
    }
}
