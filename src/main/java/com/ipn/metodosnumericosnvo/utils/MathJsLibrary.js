/**
 * MathJsLibrary.js - Biblioteca extendida de funciones matemáticas para el graficador
 * Esta biblioteca proporciona implementaciones JavaScript para funciones matemáticas
 * adicionales que no están disponibles de forma nativa en JavaScript.
 */

// Extensiones de Math para funciones trigonométricas adicionales
Math.sec = function(x) {
    return 1 / Math.cos(x);
};

Math.csc = function(x) {
    return 1 / Math.sin(x);
};

Math.cot = function(x) {
    return 1 / Math.tan(x);
};

// Extensiones para funciones trigonométricas inversas adicionales
Math.asec = function(x) {
    return Math.acos(1 / x);
};

Math.acsc = function(x) {
    return Math.asin(1 / x);
};

Math.acot = function(x) {
    return Math.PI / 2 - Math.atan(x);
};

// Alias para nombres alternativos de funciones trigonométricas inversas
Math.arcsec = Math.asec;
Math.arccsc = Math.acsc;
Math.arccot = Math.acot;

// Extensiones para funciones hiperbólicas adicionales
Math.sech = function(x) {
    return 1 / Math.cosh(x);
};

Math.csch = function(x) {
    return 1 / Math.sinh(x);
};

Math.coth = function(x) {
    return 1 / Math.tanh(x);
};

// Extensiones para funciones hiperbólicas inversas adicionales
Math.asech = function(x) {
    return Math.acosh(1 / x);
};

Math.acsch = function(x) {
    return Math.asinh(1 / x);
};

Math.acoth = function(x) {
    return Math.atanh(1 / x);
};

// Alias para nombres alternativos de funciones hiperbólicas inversas
Math.arcsech = Math.asech;
Math.arccsch = Math.acsch;
Math.arccoth = Math.acoth;
