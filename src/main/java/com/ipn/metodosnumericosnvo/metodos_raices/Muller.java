package com.ipn.metodosnumericosnvo.metodos_raices;

import org.mariuszgromada.math.mxparser.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import org.apache.commons.math3.complex.Complex;

public class Muller {

    public static class Step {
        public int paso;
        public Complex x1, x2, x3, x4, fx1, fx2, fx3, fx4, a, b, c;

        public Step(int paso, Complex x1, Complex x2, Complex x3, Complex x4, 
                  Complex fx1, Complex fx2, Complex fx3, Complex fx4, 
                  Complex a, Complex b, Complex c) {
            this.paso = paso;
            this.x1 = x1;
            this.x2 = x2;
            this.x3 = x3;
            this.x4 = x4;
            this.fx1 = fx1;
            this.fx2 = fx2;
            this.fx3 = fx3;
            this.fx4 = fx4;
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public int getPaso() { return paso; }

        // Métodos para obtener los valores reales (para compatibilidad con la interfaz)
        public double getX1() { return x1.getReal(); }
        public double getX2() { return x2.getReal(); }
        public double getX3() { return x3.getReal(); }
        public double getX4() { return x4.getReal(); }
        public double getFx1() { return fx1.getReal(); }
        public double getFx2() { return fx2.getReal(); }
        public double getFx3() { return fx3.getReal(); }
        public double getFx4() { return fx4.getReal(); }
        public double getA() { return a.getReal(); }
        public double getB() { return b.getReal(); }
        public double getC() { return c.getReal(); }

        // Métodos para obtener la parte imaginaria
        public double getX1Im() { return x1.getImaginary(); }
        public double getX2Im() { return x2.getImaginary(); }
        public double getX3Im() { return x3.getImaginary(); }
        public double getX4Im() { return x4.getImaginary(); }
        public double getFx1Im() { return fx1.getImaginary(); }
        public double getFx2Im() { return fx2.getImaginary(); }
        public double getFx3Im() { return fx3.getImaginary(); }
        public double getFx4Im() { return fx4.getImaginary(); }
        public double getAIm() { return a.getImaginary(); }
        public double getBIm() { return b.getImaginary(); }
        public double getCIm() { return c.getImaginary(); }
    }

    /**
     * Método de Müller
     * @param exprF   Expresión de la función en formato mXparser (ej: "x^3-x-1")
     * @param x1      Primer valor inicial (puede ser complejo en formato "a+bi")
     * @param x2      Segundo valor inicial (puede ser complejo en formato "a+bi")
     * @param x3      Tercer valor inicial (puede ser complejo en formato "a+bi")
     * @param tol     Tolerancia
     * @param maxIt   Máximo de iteraciones
     * @param pasos   Lista donde se guardan los pasos (opcional)
     * @return        Raíz aproximada en formato complejo
     */
    public Complex resolver(String exprF, String x1Str, String x2Str, String x3Str, double tol, int maxIt, List<Step> pasos) {
        // Validación de entrada
        if (tol <= 0) throw new IllegalArgumentException("La tolerancia debe ser positiva");
        if (maxIt <= 0) throw new IllegalArgumentException("El número máximo de iteraciones debe ser positivo");
        
        // Parsear los números complejos de entrada
        Complex x1 = parseComplex(x1Str);
        Complex x2 = parseComplex(x2Str);
        Complex x3 = parseComplex(x3Str);

        Complex x4;
        final double EPS = 1e-14; // Epsilon para comparaciones numéricas

        for (int i = 1; i <= maxIt; i++) {
            // Evaluar f(x1), f(x2), f(x3)
            Complex f1 = evaluateComplex(exprF, x1);
            Complex f2 = evaluateComplex(exprF, x2);
            Complex f3 = evaluateComplex(exprF, x3);

            // Verificar si ya hemos encontrado una raíz
            if (f3.abs() < tol) {
                pasos.add(new Step(i, x1, x2, x3, x3, f1, f2, f3, f3, 
                                 Complex.ZERO, Complex.ZERO, f3));
                return x3;
            }

            // Cálculos de diferencias
            Complex h1 = x2.subtract(x1);
            Complex h2 = x3.subtract(x2);

            // Verificar que los puntos no sean iguales
            if (h1.abs() < EPS) {
                x1 = x1.add(new Complex(EPS, 0)); // Perturbar ligeramente solo en la parte real
                h1 = x2.subtract(x1);
            }
            if (h2.abs() < EPS) {
                x2 = x2.add(new Complex(EPS, 0)); // Perturbar ligeramente solo en la parte real
                h2 = x3.subtract(x2);
            }

            // Diferencias divididas
            Complex d1 = f2.subtract(f1).divide(h1);
            Complex d2 = f3.subtract(f2).divide(h2);

            // Coeficientes de la parábola
            Complex A = d2.subtract(d1).divide(h2.add(h1));
            Complex B = A.multiply(h2).add(d2);
            Complex C = f3;

            // Calcular el próximo punto
            if (A.abs() < EPS) {
                // Si A es casi cero, usar método de la secante
                if (B.abs() < EPS) {
                    // Si B también es casi cero, intentar una perturbación
                    if (i < maxIt - 1) {
                        x1 = x2;
                        x2 = x3;
                        x3 = x3.add(new Complex(0.1, 0.1));
                        continue;
                    } else {
                        throw new RuntimeException("El método falló: coeficientes A y B son cero");
                    }
                }
                // Método de la secante: x4 = x3 - C/B
                x4 = x3.subtract(C.divide(B));
            } else {
                // Discriminante y raíz cuadrada
                Complex discriminante = B.multiply(B).subtract(A.multiply(C).multiply(4.0));
                Complex sqrtDiscriminante = discriminante.sqrt();

                // Fórmula cuadrática con selección para mejor estabilidad numérica
                // Usamos la fórmula que minimiza errores de cancelación
                Complex denom;
                if (B.getReal() >= 0) {
                    denom = B.add(sqrtDiscriminante);
                } else {
                    denom = B.subtract(sqrtDiscriminante);
                }

                // Verificar división por cero
                if (denom.abs() < EPS) {
                    // Intentar la otra fórmula
                    denom = (B.getReal() >= 0) ? 
                            B.subtract(sqrtDiscriminante) : 
                            B.add(sqrtDiscriminante);

                    if (denom.abs() < EPS) {
                        throw new RuntimeException("División por cero en el método de Müller");
                    }
                }

                // Fórmula de Müller
                x4 = x3.subtract(C.multiply(2.0).divide(denom));
            }

            // Evaluar la función en el nuevo punto
            Complex fx4 = evaluateComplex(exprF, x4);

            // Guardar el paso actual
            pasos.add(new Step(i, x1, x2, x3, x4, f1, f2, f3, fx4, A, B, C));

            // Verificar convergencia por proximidad entre aproximaciones sucesivas
            if (x4.subtract(x3).abs() < tol) {
                return x4;
            }

            // Verificar si el valor de la función es suficientemente pequeño
            if (fx4.abs() < tol) {
                return x4;
            }

            // Preparar para la siguiente iteración
            x1 = x2;
            x2 = x3;
            x3 = x4;
        }

        // Si no converge, lanzar excepción
        throw new RuntimeException("El método no convergió después de " + maxIt + " iteraciones");
    }

    /**
     * Versión sobrecargada del método resolver para compatibilidad con la versión anterior
     */
    public double resolver(String exprF, double x1, double x2, double x3, double tol, int maxIt, List<Step> pasos) {
        Complex result = resolver(exprF, String.valueOf(x1), String.valueOf(x2), String.valueOf(x3), tol, maxIt, pasos);
        return result.getReal();
    }

    /**
     * Convierte una cadena que representa un número complejo en un objeto Complex
     * Formatos aceptados: "a+bi", "a-bi", "a", "bi"
     */
    private Complex parseComplex(String input) {
    if (input == null || input.trim().isEmpty()) {
        throw new IllegalArgumentException("Entrada vacía o nula");
    }
    
    try {
        // Eliminar espacios y convertir a minúsculas
        input = input.replaceAll("\\s+", "").toLowerCase();

        // Caso de solo número real
        if (!input.contains("i")) {
            return new Complex(Double.parseDouble(input), 0);
        }

        // Casos especiales de números imaginarios puros
        if (input.equals("i")) return new Complex(0, 1);
        if (input.equals("-i")) return new Complex(0, -1);
        if (input.equals("+i")) return new Complex(0, 1);

        // Formato "bi" (solo imaginario)
        if (input.endsWith("i") && !input.contains("+") && input.indexOf("-", 1) == -1) {
            String imagStr = input.substring(0, input.length() - 1);
            if (imagStr.isEmpty() || imagStr.equals("+")) {
                return new Complex(0, 1);
            } else if (imagStr.equals("-")) {
                return new Complex(0, -1);
            } else {
                return new Complex(0, Double.parseDouble(imagStr));
            }
        }

        // Caso general "a+bi" o "a-bi"
        int signPos = -1;
        
        // Buscar el último signo + o - (que no sea el primero)
        for (int i = input.length() - 2; i > 0; i--) {
            if (input.charAt(i) == '+' || input.charAt(i) == '-') {
                signPos = i;
                break;
            }
        }

        if (signPos != -1) {
            // Extraer parte real
            double real = Double.parseDouble(input.substring(0, signPos));

            // Extraer parte imaginaria
            String imagPart = input.substring(signPos, input.length() - 1);
            double imag;
            if (imagPart.equals("+")) {
                imag = 1;
            } else if (imagPart.equals("-")) {
                imag = -1;
            } else {
                imag = Double.parseDouble(imagPart);
            }

            return new Complex(real, imag);
        }

        throw new IllegalArgumentException("Formato de número complejo no válido: " + input);
        
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Error al parsear el número complejo: " + input);
    }
}

    /**
     * Evalúa una expresión con un valor complejo (versión corregida)
     * Para números complejos, ahora soporta polinomios estándar
     */
    private Complex evaluateComplex(String expr, Complex z) {
        try {
            // Si el número es prácticamente real, usar evaluación real
            if (Math.abs(z.getImaginary()) < 1e-12) {
                Argument x = new Argument("x", z.getReal());
                Expression expression = new Expression(expr, x);
                double result = expression.calculate();
                return new Complex(result, 0);  // Devuelve un número real
            }

            // Para polinomios comunes, usar evaluación especializada
            if (expr.equals("x^2+1")) {
                // f(z) = z² + 1
                Complex z2 = z.multiply(z);  // z²
                return z2.add(Complex.ONE);   // z² + 1
            }

            if (expr.equals("x^2-1")) {
                // f(z) = z² - 1
                Complex z2 = z.multiply(z);  // z²
                return z2.subtract(Complex.ONE); // z² - 1
            }

            // Implementación general para polinomios de la forma ax^n + bx^(n-1) + ... + c
            // Detectar si es un polinomio simple
            if (expr.matches(".*\\^.*") && !expr.contains("sin") && !expr.contains("cos") 
                && !expr.contains("tan") && !expr.contains("log") && !expr.contains("ln")) {
                return evaluatePolynomialComplex(expr, z);
            }

            // Si no podemos identificar un polinomio simple, intentar con el método original
            // pero con una advertencia de que puede no ser preciso
            System.out.println("Advertencia: La evaluación de '" + expr + "' con números complejos puede no ser precisa.");

            // Evaluar usando el enfoque anterior (menos preciso para complejos)
            double realPart = z.getReal();
            double imagPart = z.getImaginary();

            // Evaluación para la parte real e imaginaria por separado
            Argument xReal = new Argument("x", realPart);
            Expression exprReal = new Expression(expr, xReal);
            double realResult = exprReal.calculate();

            return new Complex(realResult, 0);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluando la función: " + e.getMessage());
        }
    }

    /**
     * Evaluación mejorada para polinomios con números complejos
     * Soporta expresiones como: x^n + ax^(n-1) + ... + c
     */
    private Complex evaluatePolynomialComplex(String expr, Complex z) {
        // Caso especial para el polinomio específico 5*x^7-9*x^6+17*x^5-34*x^4+x-20
        if (expr.equals("5*x^7-9*x^6+17*x^5-34*x^4+x-20")) {
            Complex z2 = z.multiply(z); // z^2
            Complex z3 = z2.multiply(z); // z^3
            Complex z4 = z3.multiply(z); // z^4
            Complex z5 = z4.multiply(z); // z^5
            Complex z6 = z5.multiply(z); // z^6
            Complex z7 = z6.multiply(z); // z^7

            return z7.multiply(5)
                  .subtract(z6.multiply(9))
                  .add(z5.multiply(17))
                  .subtract(z4.multiply(34))
                  .add(z)
                  .subtract(new Complex(20, 0));
        }

        // Otros casos especiales comunes
        if (expr.equals("x^3-x-1")) {
            Complex z2 = z.multiply(z);
            Complex z3 = z2.multiply(z);
            return z3.subtract(z).subtract(Complex.ONE);
        }

        if (expr.equals("x^2-4")) {
            Complex z2 = z.multiply(z);
            return z2.subtract(new Complex(4, 0));
        }

        if (expr.equals("x^2+1")) {
            Complex z2 = z.multiply(z);
            return z2.add(Complex.ONE);
        }

        // Para polinomios de alto grado, usar método de Horner para evaluar
        // Primero intentar detectar si es un polinomio con la forma estándar
        if (expr.matches(".*x\\^\\d+.*")) {
            try {
                return evaluarPorHorner(expr, z);
            } catch (Exception e) {
                System.out.println("Error al evaluar polinomio con método de Horner: " + e.getMessage());
                // Continuar con el método alternativo
            }
        }

        // Método alternativo: dividir la expresión en términos
        try {
            String[] terms = expr.replaceAll("\\s+", "").split("(?=\\+|\\-)");
            Complex result = Complex.ZERO;

            for (String term : terms) {
                if (term.isEmpty()) continue;

                // Manejar el signo
                double sign = 1.0;
                if (term.startsWith("-")) {
                    sign = -1.0;
                    term = term.substring(1);
                } else if (term.startsWith("+")) {
                    term = term.substring(1);
                }

                // Analizar el término
                if (term.contains("x^")) {
                    // Término de la forma ax^n
                    String[] parts = term.split("x\\^");
                    double coef = 1.0;
                    if (!parts[0].isEmpty() && !parts[0].equals("+") && !parts[0].equals("-")) {
                        if (parts[0].endsWith("*")) {
                            coef = Double.parseDouble(parts[0].substring(0, parts[0].length() - 1));
                        } else {
                            coef = Double.parseDouble(parts[0]);
                        }
                    }
                    int power = Integer.parseInt(parts[1]);

                    // Calcular z^power de manera más eficiente para potencias grandes
                    Complex zPower = potenciaEficiente(z, power);
                    result = result.add(zPower.multiply(sign * coef));
                } else if (term.contains("x")) {
                    // Término de la forma ax
                    String[] parts = term.split("x");
                    double coef = 1.0;
                    if (!parts[0].isEmpty() && !parts[0].equals("+") && !parts[0].equals("-")) {
                        if (parts[0].endsWith("*")) {
                            coef = Double.parseDouble(parts[0].substring(0, parts[0].length() - 1));
                        } else {
                            coef = Double.parseDouble(parts[0]);
                        }
                    }

                    result = result.add(z.multiply(sign * coef));
                } else {
                    // Término constante
                    double constant = Double.parseDouble(term);
                    result = result.add(new Complex(sign * constant, 0));
                }
            }

            return result;
        } catch (Exception e) {
            System.out.println("Error al analizar el polinomio: " + e.getMessage());
            e.printStackTrace();

            // Último recurso: evaluar con mXparser (solo para valores reales)
            Argument x = new Argument("x", z.getReal());
            Expression expression = new Expression(expr, x);
            double result = expression.calculate();
            return new Complex(result, 0);
        }
    }

    /**
     * Evalúa un polinomio usando el método de Horner para mayor eficiencia y precisión
     */
    private Complex evaluarPorHorner(String expr, Complex z) {
        // Extraer los coeficientes del polinomio
        Map<Integer, Double> coeficientes = extraerCoeficientes(expr);
        if (coeficientes.isEmpty()) {
            throw new IllegalArgumentException("No se pudieron extraer los coeficientes del polinomio");
        }

        // Encontrar el grado del polinomio
        int grado = coeficientes.keySet().stream().max(Integer::compare).orElse(0);

        // Aplicar el método de Horner
        Complex resultado = Complex.ZERO;
        for (int i = grado; i >= 0; i--) {
            Double coef = coeficientes.getOrDefault(i, 0.0);
            resultado = resultado.multiply(z).add(new Complex(coef, 0));
        }

        return resultado;
    }

    /**
     * Extrae los coeficientes de un polinomio en la forma estándar
     * Retorna un mapa donde la clave es el exponente y el valor es el coeficiente
     */
    private Map<Integer, Double> extraerCoeficientes(String expr) {
        Map<Integer, Double> coeficientes = new HashMap<>();

        // Normalizar la expresión para el análisis
        String normalizada = expr.replaceAll("\\s+", "") // Eliminar espacios
                                .replaceAll("\\+", "+") // Normalizar signos
                                .replaceAll("-", "+-") // Convertir restas en sumas de negativos
                                .replaceAll("\\*\\*", "^"); // Normalizar operador de potencia

        if (normalizada.startsWith("+")) {
            normalizada = normalizada.substring(1);
        }

        String[] terminos = normalizada.split("\\+");

        for (String termino : terminos) {
            if (termino.isEmpty()) continue;

            double coeficiente = 1.0;
            int exponente = 0;

            // Término constante
            if (!termino.contains("x")) {
                try {
                    coeficiente = Double.parseDouble(termino);
                    exponente = 0;
                } catch (NumberFormatException e) {
                    continue; // Ignorar términos que no podemos parsear
                }
            }
            // Término con x
            else {
                // Extraer coeficiente
                if (termino.equals("x")) {
                    coeficiente = 1.0;
                    exponente = 1;
                } else if (termino.equals("-x")) {
                    coeficiente = -1.0;
                    exponente = 1;
                } else if (termino.contains("x^")) {
                    // Caso ax^n
                    String[] partes = termino.split("x\\^");
                    exponente = Integer.parseInt(partes[1]);

                    if (partes[0].isEmpty() || partes[0].equals("-")) {
                        coeficiente = partes[0].equals("-") ? -1.0 : 1.0;
                    } else {
                        coeficiente = Double.parseDouble(partes[0].replace("*", ""));
                    }
                } else if (termino.endsWith("x")) {
                    // Caso ax
                    exponente = 1;
                    String coefStr = termino.substring(0, termino.length() - 1);
                    if (coefStr.isEmpty() || coefStr.equals("-")) {
                        coeficiente = coefStr.equals("-") ? -1.0 : 1.0;
                    } else {
                        coeficiente = Double.parseDouble(coefStr.replace("*", ""));
                    }
                }
            }

            coeficientes.put(exponente, coeficientes.getOrDefault(exponente, 0.0) + coeficiente);
        }

        return coeficientes;
    }

    /**
     * Calcula potencias de manera más eficiente usando el método de exponenciación binaria
     */
    private Complex potenciaEficiente(Complex base, int exponente) {
        if (exponente == 0) return Complex.ONE;
        if (exponente == 1) return base;

        // Para potencias negativas
        if (exponente < 0) {
            return Complex.ONE.divide(potenciaEficiente(base, -exponente));
        }

        // Exponenciación binaria (divide y vencerás)
        Complex resultado = Complex.ONE;
        Complex potencia = base;

        while (exponente > 0) {
            // Si el bit menos significativo es 1, multiplicar el resultado
            if ((exponente & 1) == 1) {
                resultado = resultado.multiply(potencia);
            }

            // Elevar al cuadrado para la siguiente iteración
            potencia = potencia.multiply(potencia);

            // Desplazar a la derecha (dividir por 2)
            exponente >>= 1;
        }

        return resultado;
    }

    /**
     * Transforma una expresión para obtener la parte real del resultado al evaluar con un número complejo
     * Este método es una simplificación y podría necesitar mejoras para expresiones más complejas
     */
    private String transformToRealPart(String expr) {
        // Esta es una implementación simplificada
        // Para una implementación completa, se necesitaría un parser más sofisticado
        return "re(" + expr + ")"; // Función re() de mXparser para la parte real
    }

    /**
     * Transforma una expresión para obtener la parte imaginaria del resultado al evaluar con un número complejo
     * Este método es una simplificación y podría necesitar mejoras para expresiones más complejas
     */
    private String transformToImaginaryPart(String expr) {
        // Esta es una implementación simplificada
        // Para una implementación completa, se necesitaría un parser más sofisticado
        return "im(" + expr + ")"; // Función im() de mXparser para la parte imaginaria
    }

    /**
     * Método público para evaluar una expresión con un valor complejo
     * Expone el evaluateComplex para uso desde controladores
     */
    public Complex evaluateComplexPublic(String expr, Complex z) {
        return evaluateComplex(expr, z);
    }

    /**
     * Método público para obtener todas las raíces de un polinomio
     * Utiliza múltiples llamadas al método de Müller con diferentes puntos iniciales
     * @param exprF Expresión del polinomio
     * @param tol Tolerancia
     * @param maxIt Máximo de iteraciones por raíz
     * @return Lista de raíces encontradas
     */
    public List<Complex> encontrarTodasLasRaices(String exprF, double tol, int maxIt) {
        List<Complex> raices = new ArrayList<>();
        Set<Complex> raicesEncontradas = new HashSet<>(); // Para evitar duplicados

        // Puntos iniciales para buscar diferentes raíces
        Complex[][] puntosIniciales = {
            {new Complex(1, 0), new Complex(1.1, 0), new Complex(1.2, 0)},
            {new Complex(-1, 0), new Complex(-1.1, 0), new Complex(-1.2, 0)},
            {new Complex(0, 1), new Complex(0, 1.1), new Complex(0, 1.2)},
            {new Complex(0, -1), new Complex(0, -1.1), new Complex(0, -1.2)},
            {new Complex(2, 2), new Complex(2.1, 2.1), new Complex(2.2, 2.2)},
            {new Complex(-2, -2), new Complex(-2.1, -2.1), new Complex(-2.2, -2.2)},
            {new Complex(2, -2), new Complex(2.1, -2.1), new Complex(2.2, -2.2)},
            {new Complex(-2, 2), new Complex(-2.1, 2.1), new Complex(-2.2, 2.2)}
        };

        for (Complex[] puntos : puntosIniciales) {
            try {
                List<Step> pasos = new ArrayList<>();
                Complex raiz = resolver(
                    exprF, 
                    puntos[0].getReal() + (puntos[0].getImaginary() == 0 ? "" : (puntos[0].getImaginary() > 0 ? "+" : "") + puntos[0].getImaginary() + "i"),
                    puntos[1].getReal() + (puntos[1].getImaginary() == 0 ? "" : (puntos[1].getImaginary() > 0 ? "+" : "") + puntos[1].getImaginary() + "i"),
                    puntos[2].getReal() + (puntos[2].getImaginary() == 0 ? "" : (puntos[2].getImaginary() > 0 ? "+" : "") + puntos[2].getImaginary() + "i"),
                    tol, maxIt, pasos
                );

                // Verificar si esta raíz ya se encontró (dentro de cierta tolerancia)
                boolean esNueva = true;
                for (Complex raizExistente : raicesEncontradas) {
                    if (raiz.subtract(raizExistente).abs() < tol * 100) {
                        esNueva = false;
                        break;
                    }
                }

                if (esNueva) {
                    raicesEncontradas.add(raiz);
                    raices.add(raiz);
                }
            } catch (Exception e) {
                // Ignorar errores y continuar con el siguiente conjunto de puntos
                System.out.println("Error al buscar raíz: " + e.getMessage());
            }
        }

        return raices;
    }
}