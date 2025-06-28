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
            "(function() {" +
            "  if (typeof ggbApplet !== 'undefined') {" +
            "    try {" +
            "      %s;" +
            "      return true;" +
            "    } catch(e) {" +
            "      if (typeof javaApp !== 'undefined') {" +
            "        javaApp.recibirMensajeDeJS('Error: ' + e.message);" +
            "      }" +
            "      return false;" +
            "    }" +
            "  } else {" +
            "    return false;" +
            "  }" +
            "})();", comando);
            }

            /**
             * Genera un script para mostrar u ocultar el teclado algebraico de GeoGebra.
             * 
             * @param visible true para mostrar, false para ocultar
             * @return El script JavaScript
             */
            public static String toggleAlgebraInput(boolean visible) {
        return String.format(
            "(function() {" +
            "  if (typeof ggbApplet !== 'undefined') {" +
            "    try {" +
            "      ggbApplet.setShowAlgebraInput(%b);" +
            "      if (%b) {" +
            "        ggbApplet.evalCommand('SetPerspective(\"AD#\")');" +
            "      }" +
            "      return true;" +
            "    } catch(e) {" +
            "      console.error('Error al modificar teclado:', e);" +
            "      return false;" +
            "    }" +
            "  } else {" +
            "    return false;" +
            "  }" +
            "})();", visible, visible);
            }

            /**
             * Genera un script para mostrar u ocultar el menú y la barra de herramientas de GeoGebra.
             * 
             * @param visible true para mostrar, false para ocultar
             * @return El script JavaScript
             */
            public static String toggleMenuAndToolbar(boolean visible) {
        return String.format(
            "(function() {" +
            "  if (typeof ggbApplet !== 'undefined') {" +
            "    try {" +
            "      ggbApplet.showToolBar(%b);" +
            "      ggbApplet.showMenuBar(%b);" +
            "      if (%b) {" +
            "        ggbApplet.setMode(1);" +
            "      }" +
            "      return true;" +
            "    } catch(e) {" +
            "      console.error('Error al modificar menú:', e);" +
            "      return false;" +
            "    }" +
            "  } else {" +
            "    return false;" +
            "  }" +
            "})();", visible, visible, visible);
    }

    /**
     * Genera un script para habilitar o deshabilitar las funciones interactivas de GeoGebra.
     * Cuando está deshabilitado, el usuario no podrá arrastrar objetos, crear nuevos objetos o
     * interactuar con la gráfica excepto para hacer zoom o desplazarse.
     * 
     * @param enabled true para habilitar, false para deshabilitar
     * @return El script JavaScript
     */
    public static String toggleInteractiveFeatures(boolean enabled) {
        return String.format(
            "(function() {" +
            "  if (typeof ggbApplet !== 'undefined') {" +
            "    try {" +
            "      ggbApplet.setOnTheFlyPointCreationActive(%b);" +  // Habilitar/deshabilitar creación de puntos
            "      ggbApplet.setMode(%d);" +                        // Establecer modo (1 para mover, 0 para seleccionar)
            "      var allObjects = ggbApplet.getAllObjectNames();" +
            "      for (var i = 0; i < allObjects.length; i++) {" +
            "        var obj = allObjects[i];" +
            "        if (obj !== 'xAxis' && obj !== 'yAxis') {" +  // No afectar a los ejes
            "          ggbApplet.setFixed(obj, !%b);" +            // Fijar/liberar objetos
            "        }" +
            "      }" +
            "      return true;" +
            "    } catch(e) {" +
            "      console.error('Error al cambiar modo interactivo:', e);" +
            "      return false;" +
            "    }" +
            "  } else {" +
            "    return false;" +
            "  }" +
            "})();", enabled, enabled ? 1 : 0, enabled);
    }
}
