package com.ipn.metodosnumericosnvo.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Clase de utilidad para validar y manejar funciones matemáticas.
 * Proporciona métodos para verificar la validez de expresiones matemáticas y 
 * funciones comúnmente utilizadas en la graficación.
 */
public class MathFunctionHelper {

    // Mapa de funciones soportadas y su equivalente en JavaScript
    private static final Map<String, String> SUPPORTED_FUNCTIONS = new HashMap<>();

    static {
        // Funciones trigonométricas
        SUPPORTED_FUNCTIONS.put("sin", "Math.sin");
        SUPPORTED_FUNCTIONS.put("cos", "Math.cos");
        SUPPORTED_FUNCTIONS.put("tan", "Math.tan");
        SUPPORTED_FUNCTIONS.put("cot", "1/Math.tan");
        SUPPORTED_FUNCTIONS.put("sec", "1/Math.cos");
        SUPPORTED_FUNCTIONS.put("csc", "1/Math.sin");

        // Funciones trigonométricas inversas
        SUPPORTED_FUNCTIONS.put("arcsin", "Math.asin");
        SUPPORTED_FUNCTIONS.put("arccos", "Math.acos");
        SUPPORTED_FUNCTIONS.put("arctan", "Math.atan");
        SUPPORTED_FUNCTIONS.put("arccot", "Math.PI/2 - Math.atan");
        SUPPORTED_FUNCTIONS.put("arcsec", "Math.acos(1/");
        SUPPORTED_FUNCTIONS.put("arccsc", "Math.asin(1/");
        SUPPORTED_FUNCTIONS.put("asin", "Math.asin");
        SUPPORTED_FUNCTIONS.put("acos", "Math.acos");
        SUPPORTED_FUNCTIONS.put("atan", "Math.atan");
        SUPPORTED_FUNCTIONS.put("acot", "Math.PI/2 - Math.atan");
        SUPPORTED_FUNCTIONS.put("asec", "Math.acos(1/");
        SUPPORTED_FUNCTIONS.put("acsc", "Math.asin(1/");

        // Funciones hiperbólicas
        SUPPORTED_FUNCTIONS.put("sinh", "Math.sinh");
        SUPPORTED_FUNCTIONS.put("cosh", "Math.cosh");
        SUPPORTED_FUNCTIONS.put("tanh", "Math.tanh");
        SUPPORTED_FUNCTIONS.put("coth", "1/Math.tanh");
        SUPPORTED_FUNCTIONS.put("sech", "1/Math.cosh");
        SUPPORTED_FUNCTIONS.put("csch", "1/Math.sinh");

        // Funciones hiperbólicas inversas
        SUPPORTED_FUNCTIONS.put("arcsinh", "Math.asinh");
        SUPPORTED_FUNCTIONS.put("arccosh", "Math.acosh");
        SUPPORTED_FUNCTIONS.put("arctanh", "Math.atanh");
        SUPPORTED_FUNCTIONS.put("asinh", "Math.asinh");
        SUPPORTED_FUNCTIONS.put("acosh", "Math.acosh");
        SUPPORTED_FUNCTIONS.put("atanh", "Math.atanh");

        // Otras funciones matemáticas
        SUPPORTED_FUNCTIONS.put("log", "Math.log10");
        SUPPORTED_FUNCTIONS.put("ln", "Math.log");
        SUPPORTED_FUNCTIONS.put("sqrt", "Math.sqrt");
        SUPPORTED_FUNCTIONS.put("cbrt", "Math.cbrt");
        SUPPORTED_FUNCTIONS.put("abs", "Math.abs");
        SUPPORTED_FUNCTIONS.put("exp", "Math.exp");
    }

    /**
     * Verifica si una función es soportada por la graficadora.
     * @param functionName Nombre de la función a verificar
     * @return true si la función es soportada, false en caso contrario
     */
    public static boolean isFunctionSupported(String functionName) {
        return SUPPORTED_FUNCTIONS.containsKey(functionName);
    }

    /**
     * Obtiene el equivalente en JavaScript de una función matemática.
     * @param functionName Nombre de la función
     * @return El equivalente en JavaScript o null si no está soportada
     */
    public static String getJavaScriptEquivalent(String functionName) {
        return SUPPORTED_FUNCTIONS.get(functionName);
    }

    /**
     * Valida si una expresión matemática tiene una sintaxis correcta básica.
     * Esta es una validación simple que verifica paréntesis balanceados y operadores básicos.
     * 
     * @param expression La expresión a validar
     * @return true si la expresión parece válida, false en caso contrario
     */
    public static boolean validateBasicSyntax(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return false;
        }

        // Verificar paréntesis balanceados
        int parenthesisCount = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') parenthesisCount++;
            else if (c == ')') parenthesisCount--;

            // Si en algún momento hay más paréntesis cerrados que abiertos, es inválido
            if (parenthesisCount < 0) return false;
        }

        // Al final, todos los paréntesis deben estar balanceados
        if (parenthesisCount != 0) return false;

        // Verificar operadores seguidos (error común)
        if (Pattern.compile("[+\\-*/^]{2,}").matcher(expression).find()) {
            return false;
        }

        return true;
    }

    /**
     * Proporciona sugerencias para corregir errores comunes en funciones matemáticas.
     * 
     * @param expression La expresión con posibles errores
     * @return Una expresión corregida o la original si no se detectaron problemas
     */
    public static String suggestCorrections(String expression) {
        if (expression == null) return null;

        String corrected = expression;

        // Corregir errores comunes de sintaxis
        corrected = corrected.replace("sen", "sin"); // Español a inglés
        corrected = corrected.replace("tg", "tan");  // Abreviatura común

        // Añadir paréntesis a funciones si faltan
        for (String func : SUPPORTED_FUNCTIONS.keySet()) {
            // Buscar patrones como "sin x" y convertirlos a "sin(x)"
            corrected = corrected.replaceAll(func + "\\s+([a-zA-Z0-9])", func + "($1)");
        }

        return corrected;
    }
}
