package com.ipn.metodosnumericosnvo.metodo_derivacion;

import java.util.function.Function;
import org.mariuszgromada.math.mxparser.Expression;

public class ExtrapolacionRichardson {
    private String expresionFuncion;

    /**
     * Constructor sin parámetros, inicializa con la función por defecto x*e^x
     */
    public ExtrapolacionRichardson() {
        this.expresionFuncion = "x*exp(x)";
    }

    /**
     * Constructor que permite especificar la función a derivar
     * @param expresionFuncion La expresión matemática en formato de texto
     */
    public ExtrapolacionRichardson(String expresionFuncion) {
        this.expresionFuncion = expresionFuncion;
    }

    /**
     * Establece la función a derivar
     * @param expresionFuncion La expresión matemática en formato de texto
     */
    public void setFuncion(String expresionFuncion) {
        this.expresionFuncion = expresionFuncion;
    }

    /**
     * Evalúa la función en un punto específico
     * @param x Punto donde se evalúa la función
     * @return Resultado de la función en el punto x
     */
    public double evaluarFuncion(double x) {
        // Crear una expresión con argumento x explícito
        org.mariuszgromada.math.mxparser.Argument xArg = new org.mariuszgromada.math.mxparser.Argument("x", x);
        Expression exp = new Expression(expresionFuncion, xArg);
        double result = exp.calculate();

        if (Double.isNaN(result)) {
            throw new IllegalArgumentException("Error al evaluar la función en x=" + x + ". Verifique la sintaxis.");
        }

        return result;
    }

    /**
     * Calcula la aproximación de la derivada utilizando diferencias centrales
     * @param f Función a derivar
     * @param x Punto donde se evalúa la derivada
     * @param h Tamaño del paso
     * @return Aproximación de la derivada en el punto x con paso h
     */
    public double calcularDerivada(Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - f.apply(x - h)) / (2 * h);
    }

    /**
     * Calcula la derivada numérica usando diferencias centrales para la función actual
     * @param x Punto donde se evalúa la derivada
     * @param h Tamaño del paso
     * @return Aproximación de la derivada
     */
    public double calcularDerivada(double x, double h) {
        Function<Double, Double> f = this::evaluarFuncion;
        return calcularDerivada(f, x, h);
    }

    /**
     * Aplica la extrapolación de Richardson para mejorar la precisión
     * @param A_h Aproximación de la derivada con paso h
     * @param A_h2 Aproximación de la derivada con paso h/2
     * @param p Orden del error (2 para diferencias centrales)
     * @return Valor mejorado de la derivada
     */
    public double extrapolar(double A_h, double A_h2, int p) {
        return (Math.pow(2, p) * A_h2 - A_h) / (Math.pow(2, p) - 1);
    }
}
