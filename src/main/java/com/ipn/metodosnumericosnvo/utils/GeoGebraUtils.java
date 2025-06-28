package com.ipn.metodosnumericosnvo.utils;

/**
 * Clase utilitaria para trabajar con la API de GeoGebra.
 * Proporciona métodos para convertir expresiones matemáticas al formato
 * requerido por GeoGebra.
 */
public class GeoGebraUtils {

    /**
     * Convierte una función ingresada por el usuario al formato de GeoGebra.
     * 
     * @param funcion La función a convertir
     * @return La función convertida al formato de GeoGebra
     */
    public static String convertirFuncionParaGeoGebra(String funcion) {
        if (funcion == null || funcion.trim().isEmpty()) {
            return "";
        }

        String resultado = funcion;

        // Reemplazar operadores y funciones comunes
        resultado = resultado.replace("^2", "²");
        resultado = resultado.replace("^3", "³");
        resultado = resultado.replace("sin", "sin");
        resultado = resultado.replace("cos", "cos");
        resultado = resultado.replace("tan", "tan");
        resultado = resultado.replace("sqrt", "√");
        resultado = resultado.replace("pi", "π");

        // Reemplazar potencias con formato adecuado
        resultado = resultado.replaceAll("\\^(\\d+)", "^($1)");

        return resultado;
    }

    /**
     * Genera un script JavaScript para enviar a GeoGebra.
     * 
     * @param comando El comando a ejecutar en GeoGebra
     * @return El script JavaScript completo
     */
    public static String generarScriptGeoGebra(String comando) {
        return String.format(
            "if (typeof ggbApplet !== 'undefined') {" +
            "  try {" +
            "    %s;" +
            "    return true;" +
            "  } catch(e) {" +
            "    if (typeof javaApp !== 'undefined') {" +
            "      javaApp.recibirMensajeDeJS('Error: ' + e.message);" +
            "    }" +
            "    return false;" +
            "  }" +
            "} else {" +
            "  return false;" +
            "}", comando);
    }
}
