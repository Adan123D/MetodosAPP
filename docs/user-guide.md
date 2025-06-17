# Guía de Usuario - Métodos Numéricos

Esta guía proporciona información detallada sobre cómo instalar, configurar y utilizar la aplicación Métodos Numéricos.

## Instalación

### Requisitos Previos

Antes de instalar la aplicación, asegúrese de tener instalado:

- Java Development Kit (JDK) 17 o superior
- Maven (para compilar desde el código fuente)

### Pasos de Instalación

1. **Descargar la Aplicación**
   - Opción 1: Descargar el archivo JAR ejecutable desde [enlace de descarga]
   - Opción 2: Clonar el repositorio y compilar el código fuente

2. **Compilar desde el Código Fuente (opcional)**
   ```bash
   git clone https://github.com/usuario/MetodosNumericosNvo.git
   cd MetodosNumericosNvo
   mvn clean package
   ```

3. **Ejecutar la Aplicación**
   - Desde el archivo JAR:
     ```bash
     java -jar MetodosNumericosNvo-1.0-SNAPSHOT.jar
     ```
   - Desde Maven:
     ```bash
     mvn javafx:run
     ```

## Interfaz de Usuario

La aplicación Métodos Numéricos tiene una interfaz gráfica intuitiva que consta de los siguientes elementos:

### Barra de Menú

La barra de menú en la parte superior de la aplicación contiene las siguientes categorías:

1. **Raíces de Ecuaciones**
   - Método de Bisección
   - Método de Muller
   - Método de la Secante
   - Método de Newton
   - Método de Punto Fijo
   - Método de Falsa Posición
   - Método de Steffensen
   - Método de Bisección-Aitken
   - Método de la Secante-Aitken
   - Deflación

2. **Derivación**
   - Interpolación
   - Polinomio Interpolante de Lagrange
   - Derivación Numérica
   - Interpolación de Richardson
   - Derivación para puntos desigualmente espaciados

3. **Integración**
   - Integración Numérica
   - Integración Compuesta
     - Trapecio Compuesto
     - Simpson 1/3 Compuesto
     - Simpson 3/8 Compuesto
   - Integración Múltiple
     - Método del Trapecio
     - Método de Simpson 1/3
   - Interpolación de Romberg
   - Método de Cuadratura Adaptiva
     - Simpson Compuesto
     - Trapecio Simple

4. **Ecuaciones Diferenciales**
   - Método de Euler
   - Método de Taylor
   - Método de Runge-Kutta
   - Método de Heun
   - SEDO

5. **Sistema de Ecuaciones**
   - Eliminación Gaussiana

### Botones de Acción

En la parte superior derecha de la aplicación, encontrará los siguientes botones:

- **Gráfica**: Permite visualizar gráficamente los resultados de los métodos numéricos.
- **Home**: Regresa a la pantalla principal de la aplicación.
- **Exit**: Cierra la aplicación.

### Área de Trabajo

El área central de la aplicación es donde se mostrarán los formularios para ingresar los parámetros de los métodos numéricos y donde se visualizarán los resultados.

### Visualizador de Funciones

La aplicación incluye un visualizador de funciones matemáticas que permite ver la representación gráfica de las funciones ingresadas. Para utilizar esta función:

1. Localice el área del visualizador de funciones en la pantalla principal.
2. Ingrese la función matemática en el campo de texto (por ejemplo, "sin(x^2) + 5/x") o utilice los botones de operaciones matemáticas disponibles debajo del área de visualización.
3. Haga clic en el botón "Visualizar" o presione Enter.
4. La función se mostrará renderizada en notación matemática en el área de visualización.

#### Botones de Operaciones Matemáticas

Para facilitar la entrada de funciones matemáticas, el visualizador incluye dos filas de botones:

**Primera fila:**
- **x^n**: Inserta el símbolo de potencia (^)
- **√x**: Inserta la función de raíz cuadrada (sqrt)
- **∛x**: Inserta la función de raíz cúbica (cbrt)
- **ⁿ√x**: Inserta la función de raíz n-ésima (root(n,x))
- **sin**: Inserta la función seno (sin)
- **cos**: Inserta la función coseno (cos)
- **tan**: Inserta la función tangente (tan)

**Segunda fila:**
- **a/b**: Inserta la estructura para fracciones (()/())
- **π**: Inserta la constante pi (pi)
- **(**: Inserta un paréntesis izquierdo
- **)**: Inserta un paréntesis derecho
- **log**: Inserta la función logaritmo (log)
- **ln**: Inserta la función logaritmo natural (ln)
- **e^x**: Inserta la función exponencial (e^())

Para usar estos botones:
1. Coloque el cursor en la posición donde desea insertar la función o símbolo.
2. Haga clic en el botón correspondiente.
3. Complete la expresión según sea necesario (por ejemplo, reemplace "n" en root(n,x) con el índice deseado).

#### Notaciones Soportadas

El visualizador admite diversas notaciones matemáticas:
- Funciones trigonométricas: sin, cos, tan, cot, sec, csc
- Funciones trigonométricas inversas: arcsin, arccos, arctan
- Funciones hiperbólicas: sinh, cosh, tanh
- Funciones logarítmicas: log, ln
- Raíces: 
  - Raíz cuadrada: sqrt(x)
  - Raíz cúbica: cbrt(x)
  - Raíz n-ésima: root(n,x) donde n es el índice de la raíz
- Función exponencial: e^(x)
- Fracciones: utilizando el símbolo / (por ejemplo, "1/x")
- Exponentes: utilizando el símbolo ^ (por ejemplo, "x^2")
- Constantes: pi, inf (infinito)
- Operadores: +, -, *, /, ^

#### Ejemplos de Uso

- Raíz cuadrada: `sqrt(x^2 + 1)`
- Raíz cúbica: `cbrt(x)`
- Raíz n-ésima: `root(4,x)` (raíz cuarta de x)
- Función exponencial: `e^(x*sin(x))`
- Fracción compleja: `(x^2 + 1)/(x - 1)`

Si ocurre algún error al visualizar la función, se mostrará un mensaje de error debajo del área de visualización.

## Uso de los Métodos Numéricos

A continuación, se describe cómo utilizar cada categoría de métodos numéricos disponibles en la aplicación.

### Raíces de Ecuaciones

Los métodos de raíces de ecuaciones se utilizan para encontrar los valores de x para los cuales f(x) = 0, donde f(x) es una función no lineal.

#### Método de Bisección

1. Seleccione "Raíces de Ecuaciones" > "Método de Bisección" en la barra de menú.
2. Ingrese la función f(x) en el campo correspondiente.
3. Especifique el intervalo [a, b] donde se buscará la raíz.
4. Establezca la tolerancia y el número máximo de iteraciones.
5. Haga clic en "Calcular" para ejecutar el método.
6. Los resultados se mostrarán en la tabla de iteraciones y, opcionalmente, en una gráfica.

#### Método de Newton

1. Seleccione "Raíces de Ecuaciones" > "Método de Newton" en la barra de menú.
2. Ingrese la función f(x) y su derivada f'(x) en los campos correspondientes.
3. Especifique el valor inicial x0.
4. Establezca la tolerancia y el número máximo de iteraciones.
5. Haga clic en "Calcular" para ejecutar el método.
6. Los resultados se mostrarán en la tabla de iteraciones y, opcionalmente, en una gráfica.

### Derivación

Los métodos de derivación numérica se utilizan para aproximar la derivada de una función en un punto dado.

#### Derivación Numérica

1. Seleccione "Derivación" > "Derivación Numérica" en la barra de menú.
2. Ingrese la función f(x) en el campo correspondiente.
3. Especifique el punto x donde se calculará la derivada.
4. Seleccione el método de aproximación (diferencias finitas hacia adelante, hacia atrás o centradas).
5. Establezca el tamaño del paso h.
6. Haga clic en "Calcular" para ejecutar el método.
7. Los resultados se mostrarán en el área de resultados.

### Integración

Los métodos de integración numérica se utilizan para aproximar el valor de una integral definida.

#### Método del Trapecio Compuesto

1. Seleccione "Integración" > "Integración Compuesta" > "Trapecio Compuesto" en la barra de menú.
2. Ingrese la función f(x) en el campo correspondiente.
3. Especifique los límites de integración [a, b].
4. Ingrese el número de subintervalos n.
5. Haga clic en "Calcular" para ejecutar el método.
6. Los resultados se mostrarán en el área de resultados.

### Ecuaciones Diferenciales

Los métodos para ecuaciones diferenciales se utilizan para aproximar la solución de ecuaciones diferenciales ordinarias.

#### Método de Runge-Kutta

1. Seleccione "Ecuaciones Diferenciales" > "Método de Runge-Kutta" en la barra de menú.
2. Ingrese la ecuación diferencial en la forma y' = f(x, y).
3. Especifique el valor inicial (x0, y0).
4. Ingrese el tamaño del paso h y el número de pasos.
5. Haga clic en "Calcular" para ejecutar el método.
6. Los resultados se mostrarán en la tabla de valores y, opcionalmente, en una gráfica.

### Sistema de Ecuaciones

Los métodos para sistemas de ecuaciones se utilizan para resolver sistemas de ecuaciones lineales.

#### Eliminación Gaussiana

1. Seleccione "Sistema de Ecuaciones" > "Eliminación Gaussiana" en la barra de menú.
2. Ingrese el número de ecuaciones y variables.
3. Complete la matriz de coeficientes y el vector de términos independientes.
4. Haga clic en "Resolver" para ejecutar el método.
5. La solución del sistema se mostrará en el área de resultados.

## Visualización de Gráficas

Para visualizar gráficamente los resultados de un método numérico:

1. Ejecute el método numérico deseado.
2. Haga clic en el botón "Gráfica" en la barra superior.
3. Se abrirá una ventana con la representación gráfica de los resultados.
4. Puede ajustar la escala, hacer zoom y guardar la gráfica utilizando las herramientas disponibles.

## Solución de Problemas Comunes

### La aplicación no inicia

- Verifique que tiene instalada la versión correcta de Java (JDK 17 o superior).
- Asegúrese de que el archivo JAR no está corrupto.
- Intente ejecutar la aplicación desde la línea de comandos para ver mensajes de error detallados.

### Error al calcular un método numérico

- Verifique que la función ingresada tiene la sintaxis correcta.
- Asegúrese de que los parámetros ingresados son válidos (por ejemplo, que el intervalo [a, b] contiene una raíz).
- Revise los mensajes de error mostrados en la aplicación para obtener más información.

## Contacto y Soporte

Si encuentra algún problema o tiene alguna pregunta sobre la aplicación, puede contactarnos a través de:

- Correo electrónico: [correo@ejemplo.com](mailto:correo@ejemplo.com)
- Sitio web: [www.ejemplo.com](http://www.ejemplo.com)
