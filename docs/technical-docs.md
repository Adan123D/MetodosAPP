# Documentación Técnica - Métodos Numéricos

Esta documentación técnica proporciona información detallada sobre la arquitectura, estructura del código e implementación de los métodos numéricos en la aplicación.

## Arquitectura

La aplicación Métodos Numéricos está desarrollada utilizando JavaFX, un framework para crear aplicaciones de escritorio con interfaces gráficas ricas. La arquitectura sigue el patrón Modelo-Vista-Controlador (MVC):

- **Modelo**: Clases que implementan los algoritmos de métodos numéricos y manejan los datos.
- **Vista**: Definida mediante archivos FXML que describen la interfaz de usuario.
- **Controlador**: Clases Java que conectan la interfaz de usuario con la lógica de negocio.

### Diagrama de Componentes

```
+------------------+     +------------------+     +------------------+
|      Vista       |     |   Controlador    |     |      Modelo      |
|                  |     |                  |     |                  |
|  - Menu.fxml     |<--->| - MenuController |<--->| - Algoritmos de  |
|                  |     |                  |     |   métodos        |
+------------------+     +------------------+     +------------------+
```

## Estructura del Código

### Paquetes Principales

- `com.ipn.metodosnumericosnvo`: Paquete principal de la aplicación.
  - `Menu.java`: Clase principal que inicia la aplicación JavaFX.
  - `MenuController.java`: Controlador principal que maneja los eventos de la interfaz de usuario.

### Recursos

- `src/main/resources/com/ipn/metodosnumericosnvo/Menu.fxml`: Define la interfaz de usuario principal.

### Dependencias

La aplicación utiliza las siguientes dependencias principales:

- JavaFX: Para la interfaz gráfica de usuario.
- ControlsFX: Para componentes adicionales de UI.
- FormsFX: Para la creación de formularios.
- ValidatorFX: Para la validación de entrada de datos.
- TilesFX: Para componentes de visualización de datos.
- FXGL: Para funcionalidades adicionales de JavaFX.

## Implementación de los Métodos Numéricos

A continuación, se describe la implementación de cada categoría de métodos numéricos disponibles en la aplicación.

### Raíces de Ecuaciones

#### Método de Bisección

El método de bisección es un algoritmo de búsqueda de raíces que divide repetidamente un intervalo y selecciona el subintervalo donde la raíz debe estar.

```java
/**
 * Implementa el método de bisección para encontrar raíces de ecuaciones.
 * 
 * @param function La función f(x) para la cual se busca la raíz
 * @param a Límite inferior del intervalo
 * @param b Límite superior del intervalo
 * @param tolerance Tolerancia para el criterio de parada
 * @param maxIterations Número máximo de iteraciones
 * @return La aproximación de la raíz encontrada
 */
public static double bisection(Function<Double, Double> function, double a, double b, 
                              double tolerance, int maxIterations) {
    if (function.apply(a) * function.apply(b) >= 0) {
        throw new IllegalArgumentException("La función debe tener signos opuestos en los extremos del intervalo");
    }

    double c = a;
    int iteration = 0;

    while ((b - a) > tolerance && iteration < maxIterations) {
        c = (a + b) / 2;

        if (function.apply(c) == 0.0) {
            break;
        } else if (function.apply(c) * function.apply(a) < 0) {
            b = c;
        } else {
            a = c;
        }

        iteration++;
    }

    return c;
}
```

#### Método de Newton

El método de Newton utiliza la derivada de la función para encontrar mejores aproximaciones a las raíces.

```java
/**
 * Implementa el método de Newton para encontrar raíces de ecuaciones.
 * 
 * @param function La función f(x) para la cual se busca la raíz
 * @param derivative La derivada de la función f'(x)
 * @param x0 Valor inicial
 * @param tolerance Tolerancia para el criterio de parada
 * @param maxIterations Número máximo de iteraciones
 * @return La aproximación de la raíz encontrada
 */
public static double newton(Function<Double, Double> function, Function<Double, Double> derivative,
                           double x0, double tolerance, int maxIterations) {
    double x = x0;
    int iteration = 0;

    while (Math.abs(function.apply(x)) > tolerance && iteration < maxIterations) {
        double derivativeValue = derivative.apply(x);

        if (Math.abs(derivativeValue) < 1e-10) {
            throw new ArithmeticException("Derivada cercana a cero, posible divergencia");
        }

        x = x - function.apply(x) / derivativeValue;
        iteration++;
    }

    return x;
}
```

### Derivación

#### Derivación Numérica

La derivación numérica aproxima la derivada de una función utilizando diferencias finitas.

```java
/**
 * Implementa la derivación numérica utilizando diferencias finitas centradas.
 * 
 * @param function La función f(x) a derivar
 * @param x El punto donde se calcula la derivada
 * @param h El tamaño del paso
 * @return La aproximación de la derivada f'(x)
 */
public static double centralDifference(Function<Double, Double> function, double x, double h) {
    return (function.apply(x + h) - function.apply(x - h)) / (2 * h);
}

/**
 * Implementa la derivación numérica utilizando diferencias finitas hacia adelante.
 * 
 * @param function La función f(x) a derivar
 * @param x El punto donde se calcula la derivada
 * @param h El tamaño del paso
 * @return La aproximación de la derivada f'(x)
 */
public static double forwardDifference(Function<Double, Double> function, double x, double h) {
    return (function.apply(x + h) - function.apply(x)) / h;
}

/**
 * Implementa la derivación numérica utilizando diferencias finitas hacia atrás.
 * 
 * @param function La función f(x) a derivar
 * @param x El punto donde se calcula la derivada
 * @param h El tamaño del paso
 * @return La aproximación de la derivada f'(x)
 */
public static double backwardDifference(Function<Double, Double> function, double x, double h) {
    return (function.apply(x) - function.apply(x - h)) / h;
}
```

### Integración

#### Método del Trapecio Compuesto

El método del trapecio compuesto aproxima el valor de una integral definida dividiendo el intervalo en subintervalos.

```java
/**
 * Implementa el método del trapecio compuesto para integración numérica.
 * 
 * @param function La función f(x) a integrar
 * @param a Límite inferior de integración
 * @param b Límite superior de integración
 * @param n Número de subintervalos
 * @return La aproximación de la integral definida
 */
public static double compositeTrapezoid(Function<Double, Double> function, double a, double b, int n) {
    double h = (b - a) / n;
    double sum = 0.5 * (function.apply(a) + function.apply(b));

    for (int i = 1; i < n; i++) {
        double x = a + i * h;
        sum += function.apply(x);
    }

    return h * sum;
}
```

### Ecuaciones Diferenciales

#### Método de Runge-Kutta

El método de Runge-Kutta de cuarto orden es un método numérico para resolver ecuaciones diferenciales ordinarias.

```java
/**
 * Implementa el método de Runge-Kutta de cuarto orden para resolver ecuaciones diferenciales.
 * 
 * @param f La función f(x,y) que representa la ecuación diferencial y' = f(x,y)
 * @param x0 Valor inicial de x
 * @param y0 Valor inicial de y
 * @param h Tamaño del paso
 * @param steps Número de pasos
 * @return Un arreglo con los valores de y calculados
 */
public static double[] rungeKutta4(BiFunction<Double, Double, Double> f, double x0, double y0, 
                                  double h, int steps) {
    double[] y = new double[steps + 1];
    double[] x = new double[steps + 1];

    y[0] = y0;
    x[0] = x0;

    for (int i = 0; i < steps; i++) {
        double k1 = h * f.apply(x[i], y[i]);
        double k2 = h * f.apply(x[i] + 0.5 * h, y[i] + 0.5 * k1);
        double k3 = h * f.apply(x[i] + 0.5 * h, y[i] + 0.5 * k2);
        double k4 = h * f.apply(x[i] + h, y[i] + k3);

        y[i + 1] = y[i] + (k1 + 2 * k2 + 2 * k3 + k4) / 6;
        x[i + 1] = x[i] + h;
    }

    return y;
}
```

### Sistema de Ecuaciones

#### Eliminación Gaussiana

La eliminación gaussiana es un método para resolver sistemas de ecuaciones lineales.

```java
/**
 * Implementa el método de eliminación gaussiana para resolver sistemas de ecuaciones lineales.
 * 
 * @param A Matriz de coeficientes
 * @param b Vector de términos independientes
 * @return Vector solución
 */
public static double[] gaussianElimination(double[][] A, double[] b) {
    int n = b.length;

    // Crear matriz aumentada
    double[][] augmentedMatrix = new double[n][n + 1];
    for (int i = 0; i < n; i++) {
        System.arraycopy(A[i], 0, augmentedMatrix[i], 0, n);
        augmentedMatrix[i][n] = b[i];
    }

    // Eliminación hacia adelante
    for (int k = 0; k < n - 1; k++) {
        for (int i = k + 1; i < n; i++) {
            double factor = augmentedMatrix[i][k] / augmentedMatrix[k][k];
            for (int j = k; j <= n; j++) {
                augmentedMatrix[i][j] -= factor * augmentedMatrix[k][j];
            }
        }
    }

    // Sustitución hacia atrás
    double[] x = new double[n];
    for (int i = n - 1; i >= 0; i--) {
        double sum = 0.0;
        for (int j = i + 1; j < n; j++) {
            sum += augmentedMatrix[i][j] * x[j];
        }
        x[i] = (augmentedMatrix[i][n] - sum) / augmentedMatrix[i][i];
    }

    return x;
}
```

## Interfaz Gráfica

### Estructura del FXML

El archivo `Menu.fxml` define la estructura de la interfaz de usuario utilizando un enfoque declarativo. A continuación, se muestra una descripción de los componentes principales:

- `BorderPane`: Contenedor principal que divide la interfaz en regiones (top, center, bottom, left, right).
- `StackPane`: Contenedor para centrar elementos en la región central.
- `HBox`: Contenedor horizontal para la barra de menú y los botones de acción.
- `MenuBar`: Barra de menú con las categorías de métodos numéricos.
- `Menu`: Cada categoría de métodos numéricos.
- `MenuItem`: Cada método numérico específico.
- `Button`: Botones de acción (Gráfica, Home, Exit).
- `VBox`: Contenedor vertical para el área de trabajo central y el visualizador de funciones.
- `TextField`: Campo de texto para ingresar funciones matemáticas.
- `Pane`: Contenedor para mostrar la visualización de funciones.
- `Label`: Etiqueta para mostrar mensajes al usuario y errores.

### Visualizador de Funciones

La aplicación incluye un visualizador de funciones matemáticas que permite a los usuarios ingresar una función y verla renderizada en notación matemática. Esta funcionalidad utiliza las siguientes tecnologías:

- **JLaTeXMath**: Biblioteca para renderizar fórmulas LaTeX.
- **FXGraphics2D**: Biblioteca para integrar gráficos AWT con JavaFX.

#### Implementación del Visualizador

El visualizador de funciones está implementado en el controlador principal (`MenuController.java`) y consta de los siguientes componentes:

1. **Interfaz de Usuario**:
   - Campo de texto para ingresar la función (`functionTextField`)
   - Botón para visualizar la función
   - Área de visualización (`functionDisplayPane`)
   - Etiqueta para mostrar errores (`functionErrorLabel`)
   - Botones de operaciones matemáticas para facilitar la entrada de funciones

2. **Proceso de Visualización**:
   - El usuario ingresa una función matemática en el campo de texto o utiliza los botones de operaciones matemáticas.
   - La función se convierte a formato LaTeX mediante el método `convertToLatex()`.
   - Se crea un objeto `TeXFormula` con la cadena LaTeX.
   - Se renderiza la fórmula en un objeto `BufferedImage`.
   - La imagen se convierte a un formato compatible con JavaFX.
   - La imagen se muestra centrada en el área de visualización.

3. **Conversión a LaTeX**:
   - El método `convertToLatex()` transforma la notación matemática estándar a sintaxis LaTeX.
   - Reemplaza funciones matemáticas comunes (sin, cos, log, etc.) con sus equivalentes en LaTeX.
   - Maneja fracciones (a/b → \frac{a}{b}).
   - Maneja exponentes (x^n → x^{n}).
   - Maneja raíces cuadradas, cúbicas y n-ésimas (sqrt(x) → \sqrt{x}, cbrt(x) → \sqrt[3]{x}, root(n,x) → \sqrt[n]{x}).
   - Maneja funciones exponenciales (e^x → \mathrm{e}^{x}).
   - Reemplaza operadores y símbolos especiales.

```java
// Ejemplo de conversión a LaTeX
String input = "sin(x^2) + 5/x";
String latex = "\\sin(x^{2}) + \\frac{5}{x}";

// Ejemplo de raíz n-ésima
String input = "root(3,x)";
String latex = "\\sqrt[3]{x}";

// Ejemplo de función exponencial
String input = "e^(x+1)";
String latex = "\\mathrm{e}^{x+1}";
```

4. **Botones de Operaciones Matemáticas**:
   - Primera fila: Potencia (x^n), Raíz cuadrada (√x), Raíz cúbica (∛x), Raíz n-ésima (ⁿ√x), sin, cos, tan
   - Segunda fila: Fracción (a/b), Pi (π), Paréntesis, log, ln, Exponencial (e^x)
   - Cada botón inserta la sintaxis correspondiente en el campo de texto en la posición del cursor

5. **Manejo de Raíces N-ésimas**:
   - La aplicación implementa un algoritmo sofisticado para manejar la sintaxis `root(n,x)` y convertirla a la notación LaTeX `\sqrt[n]{x}`.
   - El algoritmo analiza la expresión, extrae el valor de n y el contenido de la raíz, y construye la fórmula LaTeX correcta.
   - Maneja correctamente paréntesis anidados y expresiones complejas.

6. **Manejo de Errores**:
   - Validación de entrada vacía.
   - Captura de excepciones durante el proceso de renderizado.
   - Muestra de mensajes de error claros al usuario.

### Controlador Principal

El controlador principal (`MenuController.java`) maneja los eventos de la interfaz de usuario y conecta la vista con la lógica de negocio. A continuación, se muestran los métodos principales:

```java
/**
 * Controller class for the main menu of the Numerical Methods application.
 * Handles user interactions with the menu items and the function visualizer.
 */
public class MenuController {
    // UI Components
    @FXML
    private Label welcomeText;

    @FXML
    private TextField functionTextField;

    @FXML
    private Pane functionDisplayPane;

    @FXML
    private Label functionErrorLabel;

    /**
     * Initializes the controller.
     * Sets the initial state of UI components.
     */
    @FXML
    public void initialize() {
        functionErrorLabel.setVisible(false);
        welcomeText.setText("Bienvenido al Visualizador de Funciones");
    }

    /**
     * Maneja los clics en los elementos del menú.
     */
    @FXML
    public void handleMenuItemClick(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String menuText = menuItem.getText();
        welcomeText.setText("Selected: " + menuText);

        // Aquí se implementaría la lógica específica para cada método numérico
    }

    /**
     * Maneja el clic en el botón Gráfica.
     */
    @FXML
    protected void onGraficaButtonClick() {
        welcomeText.setText("Función de Gráfica seleccionada");
        // Aquí se implementaría la lógica para mostrar gráficas
    }

    /**
     * Maneja el clic en el botón Home.
     */
    @FXML
    protected void onHomeButtonClick() {
        welcomeText.setText("Página principal");
        // Aquí se implementaría la lógica para volver a la página principal
    }

    /**
     * Maneja el clic en el botón Exit.
     */
    @FXML
    protected void onExitButtonClick() {
        Platform.exit();
    }

    /**
     * Visualiza la función ingresada en el campo de texto.
     */
    private void visualizeFunction() {
        // Implementación detallada en el código fuente
    }

    /**
     * Convierte una función a formato LaTeX.
     */
    private String convertToLatex(String function) {
        // Implementación detallada en el código fuente
    }
}
```

## Extensibilidad

La aplicación está diseñada para ser fácilmente extensible. Para agregar un nuevo método numérico, se deben seguir estos pasos:

1. **Implementar el algoritmo**: Crear una nueva clase o método que implemente el algoritmo del método numérico.
2. **Actualizar la interfaz de usuario**: Agregar un nuevo `MenuItem` en el archivo FXML correspondiente.
3. **Actualizar el controlador**: Implementar la lógica específica para el nuevo método en el controlador.

## Consideraciones de Rendimiento

Para métodos numéricos que requieren cálculos intensivos, se recomienda:

- Utilizar hilos separados para los cálculos para evitar bloquear la interfaz de usuario.
- Implementar técnicas de memoización para evitar recalcular valores ya calculados.
- Considerar el uso de bibliotecas optimizadas para cálculos numéricos como Apache Commons Math.

## Pruebas

Se recomienda implementar pruebas unitarias para cada método numérico utilizando JUnit. Las pruebas deberían verificar:

- Casos de prueba con soluciones conocidas.
- Comportamiento en casos límite.
- Manejo adecuado de errores y excepciones.

## Futuras Mejoras

Algunas mejoras que podrían implementarse en futuras versiones:

- Soporte para entrada de funciones mediante una interfaz más amigable (por ejemplo, un editor de ecuaciones).
- Visualización más avanzada de gráficos utilizando bibliotecas como JFreeChart.
- Exportación de resultados a formatos como CSV, PDF o Excel.
- Implementación de más métodos numéricos avanzados.
- Soporte para cálculos en paralelo para mejorar el rendimiento en problemas grandes.
