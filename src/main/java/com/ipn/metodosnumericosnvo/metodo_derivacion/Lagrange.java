package com.ipn.metodosnumericosnvo.metodo_derivacion;

import org.mariuszgromada.math.mxparser.*;

public class Lagrange {
    private double[] x;
    private double[] y;

    // Constantes para formateo LaTeX
    private static final boolean USAR_FRACCIONES = true; // Si es true, usa \frac{a}{b} en lugar de a/b

    // Indicar si se deben expandir los polinomios de Lagrange
    private static final boolean EXPANDIR_POLINOMIOS = true; // Si es true, expande los polinomios en forma ax^2 + bx + c

    public Lagrange(double[] x, double[] y) {
        if (x == null || y == null || x.length != y.length) {
            throw new IllegalArgumentException("Los vectores X y Y deben tener la misma longitud.");
        }
        if (x.length < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 puntos para la interpolación.");
        }
        this.x = x;
        this.y = y;
    }

    public double calcularInterpolacion(double xp) {
        int n = x.length;

        // Optimización para interpolación lineal (2 puntos)
        if (n == 2) {
            return interpolarLineal(xp);
        }
        // Optimización para interpolación cuadrática (3 puntos)
        else if (n == 3) {
            return interpolarCuadratica(xp);
        }
        // Caso general para n puntos
        else {
            double yp = 0;
            for (int i = 0; i < n; i++) {
                double p = 1;
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        p *= (xp - x[j]) / (x[i] - x[j]);
                    }
                }
                yp += p * y[i];
            }
            return yp;
        }
    }

    /**
     * Método optimizado para interpolación lineal (2 puntos)
     * @param xp Punto a interpolar
     * @return Valor interpolado
     */
    private double interpolarLineal(double xp) {
        // Fórmula de interpolación lineal: y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
        return y[0] + (xp - x[0]) * (y[1] - y[0]) / (x[1] - x[0]);
    }

    /**
     * Método optimizado para interpolación cuadrática (3 puntos)
     * @param xp Punto a interpolar
     * @return Valor interpolado
     */
    private double interpolarCuadratica(double xp) {
        // Polinomios de Lagrange para 3 puntos
        double L0 = ((xp - x[1]) * (xp - x[2])) / ((x[0] - x[1]) * (x[0] - x[2]));
        double L1 = ((xp - x[0]) * (xp - x[2])) / ((x[1] - x[0]) * (x[1] - x[2]));
        double L2 = ((xp - x[0]) * (xp - x[1])) / ((x[2] - x[0]) * (x[2] - x[1]));

        // Polinomio interpolante
        return y[0] * L0 + y[1] * L1 + y[2] * L2;
    }

    public String generarPolinomios() {
        StringBuilder polinomios = new StringBuilder();
        int n = x.length;

        // Caso especial para 2 puntos (interpolación lineal)
        if (n == 2) {
            polinomios.append("L0(x) = (x - ").append(x[1]).append(") / (").append(x[0]).append(" - ").append(x[1]).append(")\n");
            polinomios.append("L1(x) = (x - ").append(x[0]).append(") / (").append(x[1]).append(" - ").append(x[0]).append(")\n");
            return polinomios.toString();
        }
        // Caso especial para 3 puntos (interpolación cuadrática)
        else if (n == 3) {
            polinomios.append("L0(x) = ((x - ").append(x[1]).append(") * (x - ").append(x[2]).append(")) / ((").append(x[0]).append(" - ").append(x[1]).append(") * (").append(x[0]).append(" - ").append(x[2]).append("))\n");
            polinomios.append("L1(x) = ((x - ").append(x[0]).append(") * (x - ").append(x[2]).append(")) / ((").append(x[1]).append(" - ").append(x[0]).append(") * (").append(x[1]).append(" - ").append(x[2]).append("))\n");
            polinomios.append("L2(x) = ((x - ").append(x[0]).append(") * (x - ").append(x[1]).append(")) / ((").append(x[2]).append(" - ").append(x[0]).append(") * (").append(x[2]).append(" - ").append(x[1]).append("))\n");
            return polinomios.toString();
        }

        // Caso general para n puntos
        for (int i = 0; i < n; i++) {
            StringBuilder li = new StringBuilder("L" + i + "(x) = ");
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    li.append("(x - ").append(x[j]).append(") / (").append(x[i]).append(" - ").append(x[j]).append(") ");
                }
            }
            polinomios.append(li).append("\n");
        }
        return polinomios.toString();
    }

    public String generarPolinomioInterpolante() {
        StringBuilder polinomio = new StringBuilder("P(x) = ");
        int n = x.length;

        // Caso especial para 2 puntos (interpolación lineal)
        if (n == 2) {
            // Forma del polinomio: P(x) = y1 + (y2-y1)/(x2-x1) * (x-x1)
            double pendiente = (y[1] - y[0]) / (x[1] - x[0]);
            polinomio.append(String.format("%.4f + %.4f * (x - %.4f)", y[0], pendiente, x[0]));
            polinomio.append("\n\nForma simplificada: ");
            polinomio.append(String.format("%.4f * x + %.4f", pendiente, y[0] - pendiente * x[0]));
            return polinomio.toString();
        }
        // Caso especial para 3 puntos (interpolación cuadrática)
        else if (n == 3) {
            // Construimos la forma factorizada del polinomio cuadrático
            polinomio.append(y[0]).append(" * ((x - ").append(x[1]).append(") * (x - ").append(x[2]).append(")) / ((").append(x[0]).append(" - ").append(x[1]).append(") * (").append(x[0]).append(" - ").append(x[2]).append(")) + ");
            polinomio.append(y[1]).append(" * ((x - ").append(x[0]).append(") * (x - ").append(x[2]).append(")) / ((").append(x[1]).append(" - ").append(x[0]).append(") * (").append(x[1]).append(" - ").append(x[2]).append(")) + ");
            polinomio.append(y[2]).append(" * ((x - ").append(x[0]).append(") * (x - ").append(x[1]).append(")) / ((").append(x[2]).append(" - ").append(x[0]).append(") * (").append(x[2]).append(" - ").append(x[1]).append("))");

            // Intento calcular la forma expandida ax² + bx + c si los valores numéricos lo permiten
            try {
                // Cálculo de coeficientes para la forma ax² + bx + c
                double x0 = x[0], x1 = x[1], x2 = x[2];
                double y0 = y[0], y1 = y[1], y2 = y[2];

                double denom0 = (x0 - x1) * (x0 - x2);
                double denom1 = (x1 - x0) * (x1 - x2);
                double denom2 = (x2 - x0) * (x2 - x1);

                double a = y0/denom0 + y1/denom1 + y2/denom2;
                double b = y0 * (-(x1+x2))/denom0 + y1 * (-(x0+x2))/denom1 + y2 * (-(x0+x1))/denom2;
                double c = y0 * (x1*x2)/denom0 + y1 * (x0*x2)/denom1 + y2 * (x0*x1)/denom2;

                polinomio.append("\n\nForma expandida: ");
                polinomio.append(String.format("%.4f * x² + %.4f * x + %.4f", a, b, c));
            } catch (Exception e) {
                // Si hay problemas numéricos, solo mostramos la forma factorizada
                polinomio.append("\n\nNo se pudo calcular la forma expandida debido a problemas numéricos.");
            }

            return polinomio.toString();
        }

        // Caso general para n puntos
        for (int i = 0; i < n; i++) {
            StringBuilder li = new StringBuilder();
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    li.append("(x - ").append(x[j]).append(") / (").append(x[i]).append(" - ").append(x[j]).append(") ");
                }
            }
            polinomio.append(y[i]).append(" * ").append(li).append(" + ");
        }
        polinomio.setLength(polinomio.length() - 3); // Eliminar el último " + "
        return polinomio.toString();
    }

    /**
     * Genera una representación LaTeX de los polinomios de Lagrange
     * @return Cadena con formato LaTeX para visualizar los polinomios
     */
    public String generarPolinomiosLatex() {
        StringBuilder latex = new StringBuilder();
        int n = x.length;

        // Si queremos ver los polinomios expandidos, usamos el método específico
        if (EXPANDIR_POLINOMIOS) {
            return generarPolinomiosLatexExpandidos();
        }

        // Caso especial para 2 puntos (interpolación lineal)
        if (n == 2) {
            latex.append("L_0(x) = ");
            if (USAR_FRACCIONES) {
                latex.append("\\frac{x - ").append(formatearNumero(x[1])).append("}");
                latex.append("{" + formatearNumero(x[0]) + " - " + formatearNumero(x[1]) + "}");
            } else {
                latex.append("\\frac{x - ").append(formatearNumero(x[1])).append("}{" + formatearNumero(x[0]) + " - " + formatearNumero(x[1]) + "}");
            }

            latex.append(" \\\\ L_1(x) = ");
            if (USAR_FRACCIONES) {
                latex.append("\\frac{x - ").append(formatearNumero(x[0])).append("}");
                latex.append("{" + formatearNumero(x[1]) + " - " + formatearNumero(x[0]) + "}");
            } else {
                latex.append("\\frac{x - ").append(formatearNumero(x[0])).append("}{" + formatearNumero(x[1]) + " - " + formatearNumero(x[0]) + "}");
            }

            return latex.toString();
        }
        // Caso especial para 3 puntos (interpolación cuadrática)
        else if (n == 3) {
            // L0
            latex.append("L_0(x) = ");
            if (USAR_FRACCIONES) {
                latex.append("\\frac{(x - ").append(formatearNumero(x[1])).append(")(x - ").append(formatearNumero(x[2])).append(")}");
                latex.append("{(" + formatearNumero(x[0]) + " - " + formatearNumero(x[1]) + ")(" + formatearNumero(x[0]) + " - " + formatearNumero(x[2]) + ")}");
            } else {
                latex.append("\\frac{(x - ").append(formatearNumero(x[1])).append(")(x - ").append(formatearNumero(x[2])).append(")}{(" + formatearNumero(x[0]) + " - " + formatearNumero(x[1]) + ")(" + formatearNumero(x[0]) + " - " + formatearNumero(x[2]) + ")}");
            }

            // L1
            latex.append(" \\\\ L_1(x) = ");
            if (USAR_FRACCIONES) {
                latex.append("\\frac{(x - ").append(formatearNumero(x[0])).append(")(x - ").append(formatearNumero(x[2])).append(")}");
                latex.append("{(" + formatearNumero(x[1]) + " - " + formatearNumero(x[0]) + ")(" + formatearNumero(x[1]) + " - " + formatearNumero(x[2]) + ")}");
            } else {
                latex.append("\\frac{(x - ").append(formatearNumero(x[0])).append(")(x - ").append(formatearNumero(x[2])).append(")}{(" + formatearNumero(x[1]) + " - " + formatearNumero(x[0]) + ")(" + formatearNumero(x[1]) + " - " + formatearNumero(x[2]) + ")}");
            }

            // L2
            latex.append(" \\\\ L_2(x) = ");
            if (USAR_FRACCIONES) {
                latex.append("\\frac{(x - ").append(formatearNumero(x[0])).append(")(x - ").append(formatearNumero(x[1])).append(")}");
                latex.append("{(" + formatearNumero(x[2]) + " - " + formatearNumero(x[0]) + ")(" + formatearNumero(x[2]) + " - " + formatearNumero(x[1]) + ")}");
            } else {
                latex.append("\\frac{(x - ").append(formatearNumero(x[0])).append(")(x - ").append(formatearNumero(x[1])).append(")}{(" + formatearNumero(x[2]) + " - " + formatearNumero(x[0]) + ")(" + formatearNumero(x[2]) + " - " + formatearNumero(x[1]) + ")}");
            }

            return latex.toString();
        }

        // Caso general para n puntos
        for (int i = 0; i < n; i++) {
            latex.append("L_" + i + "(x) = ");

            // Construir el numerador con todos los factores (x - xj)
            StringBuilder numerador = new StringBuilder();
            // Construir el denominador con todos los factores (xi - xj)
            StringBuilder denominador = new StringBuilder();

            for (int j = 0; j < n; j++) {
                if (i != j) {
                    if (numerador.length() > 0) {
                        numerador.append(")(x - ").append(formatearNumero(x[j]));
                        denominador.append(")(" + formatearNumero(x[i]) + " - " + formatearNumero(x[j]));
                    } else {
                        numerador.append("(x - ").append(formatearNumero(x[j]));
                        denominador.append("(" + formatearNumero(x[i]) + " - " + formatearNumero(x[j]));
                    }
                }
            }
            numerador.append(")");
            denominador.append(")");

            if (USAR_FRACCIONES) {
                latex.append("\\frac{" + numerador + "}{" + denominador + "}");
            } else {
                latex.append("\\frac{" + numerador + "}{" + denominador + "}");
            }

            if (i < n - 1) {
                latex.append(" \\\\ ");
            }
        }

        return latex.toString();
    }

    /**
     * Genera una representación LaTeX de los polinomios de Lagrange expandidos en la forma ax^2 + bx + c
     * @return Cadena con formato LaTeX para visualizar los polinomios expandidos
     */
    private String generarPolinomiosLatexExpandidos() {
        StringBuilder latex = new StringBuilder();
        int n = x.length;

        // Caso especial para 2 puntos (interpolación lineal)
        if (n == 2) {
            double denom0 = x[0] - x[1];
            double denom1 = x[1] - x[0];

            // L0(x) = (x - x1) / (x0 - x1) = ax + b
            double a0 = 1.0 / denom0;
            double b0 = -x[1] / denom0;

            // L1(x) = (x - x0) / (x1 - x0) = ax + b
            double a1 = 1.0 / denom1;
            double b1 = -x[0] / denom1;

            // Asegurarse de que los signos estén en el numerador para fracciones
            if (USAR_FRACCIONES) {
                // Para L0(x)
                if (a0 < 0) {
                    a0 = -a0;
                    a0 = -a0; // Mantener el signo en el numerador
                }
                if (b0 < 0) {
                    b0 = -b0;
                    b0 = -b0; // Mantener el signo en el numerador
                }

                // Para L1(x)
                if (a1 < 0) {
                    a1 = -a1;
                    a1 = -a1; // Mantener el signo en el numerador
                }
                if (b1 < 0) {
                    b1 = -b1;
                    b1 = -b1; // Mantener el signo en el numerador
                }
            }

            latex.append("L_0(x) = ");
            latex.append(formatearTerminoLineal(a0, "x", b0));

            latex.append(" \\\\ L_1(x) = ");
            latex.append(formatearTerminoLineal(a1, "x", b1));

            return latex.toString();
        }
        // Caso especial para 3 puntos (interpolación cuadrática)
        else if (n == 3) {
            // Para cada polinomio Li(x) vamos a calcular los coeficientes
            for (int i = 0; i < 3; i++) {
                latex.append("L_" + i + "(x) = ");

                // Calculamos los coeficientes de Li(x) expandido
                double[] coefs = calcularCoeficientesLagrange(i);

                // Asegurarse de que los signos están correctamente representados para fracciones
                if (USAR_FRACCIONES) {
                    // Asegurarse de que el signo esté en el numerador para todos los coeficientes
                    for (int j = 0; j < coefs.length; j++) {
                        if (coefs[j] < 0) {
                            coefs[j] = -Math.abs(coefs[j]); // Mantener el signo negativo
                        } else {
                            coefs[j] = Math.abs(coefs[j]); // Mantener positivo
                        }
                    }
                }

                // Formateamos el polinomio ax^2 + bx + c
                latex.append(formatearPolinomioCuadratico(coefs[0], coefs[1], coefs[2]));

                if (i < 2) {
                    latex.append(" \\\\ ");
                }
            }

            return latex.toString();
        }

        // Caso general para n puntos
        // Para polinomios de grado mayor a 2, podemos calcular los coeficientes de forma numérica
        for (int i = 0; i < n; i++) {
            latex.append("L_" + i + "(x) = ");

            // Calculamos los coeficientes de Li(x) expandido
            double[] coefs = calcularCoeficientesLagrange(i);

            // Formateamos el polinomio a_n x^n + ... + a_1 x + a_0
            StringBuilder polExp = new StringBuilder();
            boolean primero = true;

            for (int j = coefs.length - 1; j >= 0; j--) {
                double coef = coefs[j];
                if (Math.abs(coef) < 1e-10) continue; // Ignorar coeficientes cercanos a cero

                if (coef > 0 && !primero) {
                    polExp.append(" + ");
                } else if (coef < 0) {
                    polExp.append(" - ");
                    coef = Math.abs(coef);
                }

                if (j == 0) { // Término independiente
                    polExp.append(formatearNumero(coef));
                } else if (j == 1) { // Término lineal
                    if (Math.abs(coef - 1.0) < 1e-10) {
                        polExp.append("x");
                    } else {
                        polExp.append(formatearNumero(coef)).append("x");
                    }
                } else { // Términos con potencias
                    if (Math.abs(coef - 1.0) < 1e-10) {
                        polExp.append("x^{" + j + "}");
                    } else {
                        polExp.append(formatearNumero(coef)).append("x^{" + j + "}");
                    }
                }

                primero = false;
            }

            if (polExp.length() == 0) {
                polExp.append("0"); // En caso de que todos los coeficientes sean cero
            }

            latex.append(polExp);

            if (i < n - 1) {
                latex.append(" \\\\ ");
            }
        }

        return latex.toString();
    }

    /**
     * Genera una representación LaTeX del polinomio interpolante
     * @return Cadena con formato LaTeX para visualizar el polinomio
     */
    public String generarPolinomioInterpolanteLatex() {
        StringBuilder latex = new StringBuilder("P(x) = ");
        int n = x.length;

        // Caso especial para 2 puntos (interpolación lineal)
        if (n == 2) {
            // Calcular pendiente y término independiente para forma simplificada
            double num = y[1] - y[0];
            double denom = x[1] - x[0];
            double termino = y[0] - (num / denom) * x[0];

            // Si queremos usar fracciones y los números no son enteros
            if (USAR_FRACCIONES && (Math.abs(num - Math.round(num)) > 1e-10 || Math.abs(denom - Math.round(denom)) > 1e-10)) {
                // Manejar el signo del numerador y denominador
                if (num * denom < 0) {
                    // Si el resultado es negativo, aseguramos que el numerador sea negativo
                    num = -Math.abs(num);
                    denom = Math.abs(denom);
                } else {
                    // Si el resultado es positivo, ambos deben ser positivos
                    num = Math.abs(num);
                    denom = Math.abs(denom);
                }

                // Forma simplificada con fracciones: (num/denom)x + b
                latex.append("\\frac{" + formatearNumero(num) + "}{" + formatearNumero(denom) + "}x");

                if (termino >= 0) {
                    latex.append(" + ");
                    // Si el término independiente también debe ser fracción
                    if (Math.abs(termino - Math.round(termino)) > 1e-10) {
                        // Intentamos expresarlo como fracción
                        double[] fraccion = aproximarFraccion(termino);
                        if (fraccion != null && Math.abs(fraccion[1]) > 1e-10) {
                            latex.append("\\frac{" + formatearNumero(fraccion[0]) + "}{" + formatearNumero(fraccion[1]) + "}");
                        } else {
                            latex.append(formatearNumero(termino));
                        }
                    } else {
                        latex.append(formatearNumero(termino));
                    }
                } else {
                    latex.append(" - ");
                    // Si el término independiente también debe ser fracción
                    if (Math.abs(Math.abs(termino) - Math.round(Math.abs(termino))) > 1e-10) {
                        // Intentamos expresarlo como fracción
                        double[] fraccion = aproximarFraccion(Math.abs(termino));
                        if (fraccion != null && Math.abs(fraccion[1]) > 1e-10) {
                            latex.append("\\frac{" + formatearNumero(fraccion[0]) + "}{" + formatearNumero(fraccion[1]) + "}");
                        } else {
                            latex.append(formatearNumero(Math.abs(termino)));
                        }
                    } else {
                        latex.append(formatearNumero(Math.abs(termino)));
                    }
                }
            } else {
                // Forma decimal simple: ax + b
                double pendiente = num / denom;
                latex.append(formatearNumero(pendiente)).append("x");
                if (termino >= 0) {
                    latex.append(" + ").append(formatearNumero(termino));
                } else {
                    latex.append(" - ").append(formatearNumero(Math.abs(termino)));
                }
            }

            return latex.toString();
        }
        // Caso especial para 3 puntos (interpolación cuadrática)
        else if (n == 3) {
            try {
                // Calcular coeficientes para forma ax² + bx + c
                double x0 = x[0], x1 = x[1], x2 = x[2];
                double y0 = y[0], y1 = y[1], y2 = y[2];

                double denom0 = (x0 - x1) * (x0 - x2);
                double denom1 = (x1 - x0) * (x1 - x2);
                double denom2 = (x2 - x0) * (x2 - x1);

                // Si queremos usar fracciones, mostramos los coeficientes como fracciones
                if (USAR_FRACCIONES) {
                    // Numeradores y denominadores para los coeficientes
                    // Para el coeficiente de x^2
                    double anum = y0 * denom1 * denom2 + y1 * denom0 * denom2 + y2 * denom0 * denom1;
                    double adenom = denom0 * denom1 * denom2;

                    // Para el coeficiente de x
                    double bnum = -y0 * (x1+x2) * denom1 * denom2 - y1 * (x0+x2) * denom0 * denom2 - y2 * (x0+x1) * denom0 * denom1;
                    double bdenom = denom0 * denom1 * denom2;

                    // Para el término independiente
                    double cnum = y0 * x1 * x2 * denom1 * denom2 + y1 * x0 * x2 * denom0 * denom2 + y2 * x0 * x1 * denom0 * denom1;
                    double cdenom = denom0 * denom1 * denom2;

                    // Simplificar fracciones (calcular mcd)
                    double amcd = mcd(Math.round(anum), Math.round(adenom));
                    if (amcd > 1) {
                        anum /= amcd;
                        adenom /= amcd;
                    }

                    double bmcd = mcd(Math.round(bnum), Math.round(bdenom));
                    if (bmcd > 1) {
                        bnum /= bmcd;
                        bdenom /= bmcd;
                    }

                    double cmcd = mcd(Math.round(cnum), Math.round(cdenom));
                    if (cmcd > 1) {
                        cnum /= cmcd;
                        cdenom /= cmcd;
                    }

                    // Asegurarse de que el signo negativo esté en el numerador, no en el denominador
                    if (anum * adenom < 0) {
                        anum = -Math.abs(anum);
                        adenom = Math.abs(adenom);
                    } else {
                        anum = Math.abs(anum);
                        adenom = Math.abs(adenom);
                    }

                    if (bnum * bdenom < 0) {
                        bnum = -Math.abs(bnum);
                        bdenom = Math.abs(bdenom);
                    } else {
                        bnum = Math.abs(bnum);
                        bdenom = Math.abs(bdenom);
                    }

                    if (cnum * cdenom < 0) {
                        cnum = -Math.abs(cnum);
                        cdenom = Math.abs(cdenom);
                    } else {
                        cnum = Math.abs(cnum);
                        cdenom = Math.abs(cdenom);
                    }

                    // Forma expandida con fracciones: (a/d)x² + (b/d)x + (c/d)
                    boolean primero = true;

                    // Término x^2
                    if (Math.abs(anum) > 1e-10) {
                        if (anum < 0) {
                            latex.append("-");
                            anum = Math.abs(anum);
                        }

                        if (Math.abs(adenom - 1.0) < 1e-10) {
                            latex.append(formatearNumero(anum)).append("x^2");
                        } else {
                            latex.append("\\frac{" + formatearNumero(anum) + "}{" + formatearNumero(adenom) + "}x^2");
                        }
                        primero = false;
                    }

                    // Término x
                    if (Math.abs(bnum) > 1e-10) {
                        if (!primero) {
                            if (bnum > 0) {
                                latex.append(" + ");
                            } else {
                                latex.append(" - ");
                                bnum = Math.abs(bnum);
                            }
                        } else if (bnum < 0) {
                            latex.append("-");
                            bnum = Math.abs(bnum);
                        }

                        if (Math.abs(bdenom - 1.0) < 1e-10) {
                            latex.append(formatearNumero(bnum)).append("x");
                        } else {
                            latex.append("\\frac{" + formatearNumero(bnum) + "}{" + formatearNumero(bdenom) + "}x");
                        }
                        primero = false;
                    }

                    // Término independiente
                    if (Math.abs(cnum) > 1e-10 || primero) {
                        if (!primero) {
                            if (cnum > 0) {
                                latex.append(" + ");
                            } else {
                                latex.append(" - ");
                                cnum = Math.abs(cnum);
                            }
                        } else if (cnum < 0) {
                            latex.append("-");
                            cnum = Math.abs(cnum);
                        }

                        if (Math.abs(cdenom - 1.0) < 1e-10) {
                            latex.append(formatearNumero(cnum));
                        } else {
                            latex.append("\\frac{" + formatearNumero(cnum) + "}{" + formatearNumero(cdenom) + "}");
                        }
                    }
                } else {
                    // Valores decimales para los coeficientes
                    double a = y0/denom0 + y1/denom1 + y2/denom2;
                    double b = y0 * (-(x1+x2))/denom0 + y1 * (-(x0+x2))/denom1 + y2 * (-(x0+x1))/denom2;
                    double c = y0 * (x1*x2)/denom0 + y1 * (x0*x2)/denom1 + y2 * (x0*x1)/denom2;

                    // Forma expandida: ax² + bx + c
                    latex.append(formatearNumero(a)).append("x^2");

                    if (b >= 0) {
                        latex.append(" + ").append(formatearNumero(b)).append("x");
                    } else {
                        latex.append(" - ").append(formatearNumero(Math.abs(b))).append("x");
                    }

                    if (c >= 0) {
                        latex.append(" + ").append(formatearNumero(c));
                    } else {
                        latex.append(" - ").append(formatearNumero(Math.abs(c)));
                    }
                }

                return latex.toString();
            } catch (Exception e) {
                // Si hay problemas numéricos, usamos la forma con sumatorio
                return generarPolinomioInterpolanteLatexGeneral();
            }
        }

        // Caso general: sumatorio
        return generarPolinomioInterpolanteLatexGeneral();
    }

    /**
     * Genera el polinomio interpolante en formato LaTeX usando la forma general del sumatorio
     * @return Cadena con formato LaTeX
     */
    private String generarPolinomioInterpolanteLatexGeneral() {
        StringBuilder latex = new StringBuilder("P(x) = ");
        int n = x.length;

        for (int i = 0; i < n; i++) {
            // Gestionar el signo del coeficiente yi
            if (i > 0) {
                if (y[i] >= 0) {
                    latex.append(" + ");
                } else {
                    latex.append(" - ");
                }
                latex.append(formatearNumero(Math.abs(y[i]))).append(" \\cdot ");
            } else {
                // Para el primer término
                if (y[i] < 0) {
                    latex.append("-");
                }
                latex.append(formatearNumero(Math.abs(y[i]))).append(" \\cdot ");
            }

            // Construir L_i(x) para cada término
            StringBuilder numerador = new StringBuilder();
            StringBuilder denominador = new StringBuilder();

            // Variable para controlar el signo global del denominador
            boolean denominadorNegativo = false;

            for (int j = 0; j < n; j++) {
                if (i != j) {
                    if (numerador.length() > 0) {
                        numerador.append(")(x - ").append(formatearNumero(x[j]));

                        // Añadir factor al denominador y verificar su signo
                        double factor = x[i] - x[j];
                        if (factor < 0) {
                            denominadorNegativo = !denominadorNegativo; // Cambiar el signo global
                            denominador.append(")(" + formatearNumero(Math.abs(factor)) + ")");
                        } else {
                            denominador.append(")(" + formatearNumero(factor) + ")");
                        }
                    } else {
                        numerador.append("(x - ").append(formatearNumero(x[j]));

                        // Iniciar denominador y verificar su signo
                        double factor = x[i] - x[j];
                        if (factor < 0) {
                            denominadorNegativo = true; // Denominador negativo
                            denominador.append("(" + formatearNumero(Math.abs(factor)) + ")");
                        } else {
                            denominador.append("(" + formatearNumero(factor) + ")");
                        }
                    }
                }
            }
            numerador.append(")");

            // Si el denominador es negativo y estamos usando fracciones,
            // necesitamos invertir el signo del término completo
            if (USAR_FRACCIONES && denominadorNegativo) {
                latex.append("-"); // Cambiar el signo debido al denominador negativo
            }

            latex.append("\\frac{" + numerador + "}{" + denominador + "}");
        }

        return latex.toString();
    }

    /**
     * Aproxima un número decimal a una fracción (para visualización)
     * @param decimal Número decimal a aproximar
     * @return Array con [numerador, denominador]
     */
    private double[] aproximarFraccion(double decimal) {
        // Tolerancia de error
        final double EPSILON = 1.0E-10;
        // Límite máximo para el denominador
        final int MAX_DENOMINADOR = 1000;

        // Casos especiales
        if (Math.abs(decimal) < EPSILON) return new double[]{0, 1};
        if (Math.abs(decimal - Math.floor(decimal)) < EPSILON) return new double[]{Math.floor(decimal), 1};

        double signo = decimal < 0 ? -1 : 1;
        decimal = Math.abs(decimal);

        // Encontrar la mejor aproximación con denominador limitado
        double mejorError = Double.MAX_VALUE;
        double[] mejor = null;

        for (int denom = 1; denom <= MAX_DENOMINADOR; denom++) {
            // Calcular el numerador más cercano para este denominador
            int num = (int)Math.round(decimal * denom);
            double error = Math.abs(decimal - (double)num / denom);

            if (error < mejorError) {
                mejorError = error;
                mejor = new double[]{signo * num, denom};

                // Si el error es suficientemente pequeño, terminamos
                if (error < EPSILON) break;
            }
        }

        return mejor;
    }

    /**
     * Calcula el máximo común divisor de dos números
     * @param a Primer número
     * @param b Segundo número
     * @return Máximo común divisor
     */
    private double mcd(double a, double b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b > 0) {
            double temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    /**
     * Formatea un número para su presentación en LaTeX
     * @param numero El número a formatear
     * @return Cadena formateada
     */
    private String formatearNumero(double numero) {
        // Para números enteros, mostramos sin decimales
        if (Math.abs(numero - Math.round(numero)) < 1e-10) {
            return String.valueOf((int)Math.round(numero));
        }

        // Para números con parte decimal
        String formato = String.format("%.4f", numero);
        // Eliminar ceros a la derecha innecesarios
        while (formato.endsWith("0")) {
            formato = formato.substring(0, formato.length() - 1);
        }
        // Si termina con punto decimal, eliminarlo también
        if (formato.endsWith(".")) {
            formato = formato.substring(0, formato.length() - 1);
        }
        return formato;
    }

    /**
     * Calcula los coeficientes del polinomio de Lagrange Li(x) expandido
     * @param i Índice del polinomio Li(x)
     * @return Array con los coeficientes [a0, a1, a2, ..., an] para la forma a0 + a1*x + a2*x^2 + ... + an*x^n
     */
    private double[] calcularCoeficientesLagrange(int i) {
        int n = x.length;
        // Resultado: coeficientes a0, a1, a2, ..., a_{n-1}
        double[] coefs = new double[n];

        // Para casos especiales, podemos calcular analíticamente
        if (n == 2) {
            // Para L0(x) = (x - x1) / (x0 - x1)
            if (i == 0) {
                double denom = x[0] - x[1];
                coefs[0] = -x[1] / denom; // Término independiente
                coefs[1] = 1.0 / denom;   // Coeficiente de x
            }
            // Para L1(x) = (x - x0) / (x1 - x0)
            else {
                double denom = x[1] - x[0];
                coefs[0] = -x[0] / denom; // Término independiente
                coefs[1] = 1.0 / denom;   // Coeficiente de x
            }
            return coefs;
        }

        // Para n = 3, calculamos analíticamente para mayor precisión
        if (n == 3) {
            double x0 = x[0], x1 = x[1], x2 = x[2];

            // L0(x) = ((x-x1)(x-x2))/((x0-x1)(x0-x2))
            if (i == 0) {
                double denom = (x0 - x1) * (x0 - x2);
                coefs[0] = (x1 * x2) / denom;                // Término independiente
                coefs[1] = -(x1 + x2) / denom;               // Coeficiente de x
                coefs[2] = 1.0 / denom;                      // Coeficiente de x^2
            }
            // L1(x) = ((x-x0)(x-x2))/((x1-x0)(x1-x2))
            else if (i == 1) {
                double denom = (x1 - x0) * (x1 - x2);
                coefs[0] = (x0 * x2) / denom;                // Término independiente
                coefs[1] = -(x0 + x2) / denom;               // Coeficiente de x
                coefs[2] = 1.0 / denom;                      // Coeficiente de x^2
            }
            // L2(x) = ((x-x0)(x-x1))/((x2-x0)(x2-x1))
            else {
                double denom = (x2 - x0) * (x2 - x1);
                coefs[0] = (x0 * x1) / denom;                // Término independiente
                coefs[1] = -(x0 + x1) / denom;               // Coeficiente de x
                coefs[2] = 1.0 / denom;                      // Coeficiente de x^2
            }
            return coefs;
        }

        // Para polinomios de grado mayor a 2, usamos el método numérico
        // Vamos a evaluar el polinomio en n puntos diferentes y resolver el sistema lineal
        // Para simplificar, usamos los puntos x[0], x[1], ..., x[n-1]

        // Paso 1: Calcular los valores del polinomio Li(x) en los n puntos
        double[] valores = new double[n];
        for (int j = 0; j < n; j++) {
            if (j == i) {
                valores[j] = 1.0; // Li(xi) = 1
            } else {
                valores[j] = 0.0; // Li(xj) = 0 para j ≠ i
            }
        }

        // Paso 2: Construir y resolver el sistema lineal usando eliminación gaussiana
        // Matriz aumentada [A|b] donde A es la matriz de Vandermonde
        double[][] matriz = new double[n][n+1];
        for (int fila = 0; fila < n; fila++) {
            double xj = x[fila];
            // Llenamos la fila con las potencias de xj: 1, xj, xj^2, ..., xj^(n-1)
            for (int col = 0; col < n; col++) {
                matriz[fila][col] = Math.pow(xj, col);
            }
            // La última columna es el vector de valores
            matriz[fila][n] = valores[fila];
        }

        // Eliminación gaussiana
        for (int k = 0; k < n; k++) {
            // Pivoteo parcial
            int maxFila = k;
            double maxVal = Math.abs(matriz[k][k]);
            for (int fila = k + 1; fila < n; fila++) {
                if (Math.abs(matriz[fila][k]) > maxVal) {
                    maxFila = fila;
                    maxVal = Math.abs(matriz[fila][k]);
                }
            }
            // Intercambiar filas si es necesario
            if (maxFila != k) {
                double[] temp = matriz[k];
                matriz[k] = matriz[maxFila];
                matriz[maxFila] = temp;
            }

            // Eliminación
            for (int fila = k + 1; fila < n; fila++) {
                double factor = matriz[fila][k] / matriz[k][k];
                for (int col = k; col <= n; col++) {
                    matriz[fila][col] -= factor * matriz[k][col];
                }
            }
        }

        // Sustitución hacia atrás
        for (int i1 = n - 1; i1 >= 0; i1--) {
            double suma = 0.0;
            for (int j = i1 + 1; j < n; j++) {
                suma += matriz[i1][j] * coefs[j];
            }
            coefs[i1] = (matriz[i1][n] - suma) / matriz[i1][i1];
        }

        return coefs;
    }

    /**
     * Formatea un polinomio cuadrático en LaTeX a partir de sus coeficientes
     * @param a Coeficiente de x^2
     * @param b Coeficiente de x
     * @param c Término independiente
     * @return Cadena con formato LaTeX
     */
    private String formatearPolinomioCuadratico(double a, double b, double c) {
        StringBuilder resultado = new StringBuilder();
        boolean primero = true;

        // Término cuadrático
        if (Math.abs(a) > 1e-10) {
            if (Math.abs(a - 1.0) < 1e-10) {
                resultado.append("x^2");
            } else if (Math.abs(a + 1.0) < 1e-10) {
                resultado.append("-x^2");
            } else {
                // Si estamos usando fracciones y el valor no es entero
                if (USAR_FRACCIONES && Math.abs(a - Math.round(a)) > 1e-10) {
                    // Aproximar a fracción
                    double[] fraccion = aproximarFraccion(Math.abs(a));
                    if (fraccion != null && Math.abs(fraccion[1]) > 1e-10) {
                        if (a < 0) {
                            resultado.append("-");
                        }
                        resultado.append("\\frac{" + formatearNumero(fraccion[0]) + "}{" + formatearNumero(fraccion[1]) + "}").append("x^2");
                    } else {
                        resultado.append(formatearNumero(a)).append("x^2");
                    }
                } else {
                    resultado.append(formatearNumero(a)).append("x^2");
                }
            }
            primero = false;
        }

        // Término lineal
        if (Math.abs(b) > 1e-10) {
            if (!primero) {
                if (b > 0) {
                    resultado.append(" + ");
                } else {
                    resultado.append(" - ");
                    b = Math.abs(b);
                }
            } else if (b < 0) {
                resultado.append("-");
                b = Math.abs(b);
            }

            if (Math.abs(b - 1.0) < 1e-10) {
                resultado.append("x");
            } else {
                // Si estamos usando fracciones y el valor no es entero
                if (USAR_FRACCIONES && Math.abs(b - Math.round(b)) > 1e-10) {
                    // Aproximar a fracción
                    double[] fraccion = aproximarFraccion(b);
                    if (fraccion != null && Math.abs(fraccion[1]) > 1e-10) {
                        resultado.append("\\frac{" + formatearNumero(fraccion[0]) + "}{" + formatearNumero(fraccion[1]) + "}").append("x");
                    } else {
                        resultado.append(formatearNumero(b)).append("x");
                    }
                } else {
                    resultado.append(formatearNumero(b)).append("x");
                }
            }
            primero = false;
        }

        // Término independiente
        if (Math.abs(c) > 1e-10 || (primero && Math.abs(a) < 1e-10 && Math.abs(b) < 1e-10)) {
            if (!primero) {
                if (c > 0) {
                    resultado.append(" + ");
                } else {
                    resultado.append(" - ");
                    c = Math.abs(c);
                }
            } else if (c < 0) {
                resultado.append("-");
                c = Math.abs(c);
            }

            // Si estamos usando fracciones y el valor no es entero
            if (USAR_FRACCIONES && Math.abs(c - Math.round(c)) > 1e-10) {
                // Aproximar a fracción
                double[] fraccion = aproximarFraccion(c);
                if (fraccion != null && Math.abs(fraccion[1]) > 1e-10) {
                    resultado.append("\\frac{" + formatearNumero(fraccion[0]) + "}{" + formatearNumero(fraccion[1]) + "}");
                } else {
                    resultado.append(formatearNumero(c));
                }
            } else {
                resultado.append(formatearNumero(c));
            }
        }

        return resultado.toString();
    }

    /**
     * Formatea un término lineal en LaTeX
     * @param a Coeficiente de x
     * @param varName Nombre de la variable
     * @param b Término independiente
     * @return Cadena con formato LaTeX
     */
    private String formatearTerminoLineal(double a, String varName, double b) {
        StringBuilder resultado = new StringBuilder();
        boolean primero = true;

        // Término lineal
        if (Math.abs(a) > 1e-10) {
            if (Math.abs(a - 1.0) < 1e-10) {
                resultado.append(varName);
            } else if (Math.abs(a + 1.0) < 1e-10) {
                resultado.append("-").append(varName);
            } else {
                // Si estamos usando fracciones y el valor no es entero
                if (USAR_FRACCIONES && Math.abs(a - Math.round(a)) > 1e-10) {
                    // Aproximar a fracción
                    double[] fraccion = aproximarFraccion(Math.abs(a));
                    if (fraccion != null && Math.abs(fraccion[1]) > 1e-10) {
                        if (a < 0) {
                            resultado.append("-");
                        }
                        resultado.append("\\frac{" + formatearNumero(fraccion[0]) + "}{" + formatearNumero(fraccion[1]) + "}").append(varName);
                    } else {
                        resultado.append(formatearNumero(a)).append(varName);
                    }
                } else {
                    resultado.append(formatearNumero(a)).append(varName);
                }
            }
            primero = false;
        }

        // Término independiente
        if (Math.abs(b) > 1e-10 || (primero && Math.abs(a) < 1e-10)) {
            if (!primero) {
                if (b > 0) {
                    resultado.append(" + ");
                } else {
                    resultado.append(" - ");
                    b = Math.abs(b);
                }
            } else if (b < 0) {
                resultado.append("-");
                b = Math.abs(b);
            }

            // Si estamos usando fracciones y el valor no es entero
            if (USAR_FRACCIONES && Math.abs(b - Math.round(b)) > 1e-10) {
                // Aproximar a fracción
                double[] fraccion = aproximarFraccion(b);
                if (fraccion != null && Math.abs(fraccion[1]) > 1e-10) {
                    resultado.append("\\frac{" + formatearNumero(fraccion[0]) + "}{" + formatearNumero(fraccion[1]) + "}");
                } else {
                    resultado.append(formatearNumero(b));
                }
            } else {
                resultado.append(formatearNumero(b));
            }
        }

        return resultado.toString();
    }
}
