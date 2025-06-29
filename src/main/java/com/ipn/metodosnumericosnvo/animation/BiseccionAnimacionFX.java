package com.ipn.metodosnumericosnvo.animation;

import com.ipn.metodosnumericosnvo.metodos_raices.Biseccion;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.KeyValue;
import javafx.scene.control.ToggleButton;

/**
 * Clase para animar el método de bisección utilizando JavaFX.
 */
@SuppressWarnings("unchecked")
public class BiseccionAnimacionFX {
    
    private final String funcion;
    private final double x0;
    private final double x1;
    private final double tolerancia;
    private final List<Biseccion.Iteracion> iteraciones;
    private final Color COLOR_FUNCION = Color.BLUE;
    private final Color COLOR_X0 = Color.RED;
    private final Color COLOR_X1 = Color.GREEN;
    private final Color COLOR_X2 = Color.PURPLE;
    private final Color COLOR_EJE_X = Color.BLACK;
    private final Color COLOR_RAIZ = Color.ORANGE;
    private Timeline timeline;
    private IntegerProperty iteracionActual;
    private boolean zoomAutomaticoActivado = true; // Zoom activado por defecto
    private double limiteXOriginalMin, limiteXOriginalMax;
    private double limiteYOriginalMin, limiteYOriginalMax;

    /**
     * Constructor para la animación del método de bisección.
     * 
     * @param funcion La función a evaluar
     * @param x0 El límite inferior del intervalo inicial
     * @param x1 El límite superior del intervalo inicial
     * @param tolerancia La tolerancia para el criterio de parada
     * @param iteraciones Las iteraciones del método de bisección
     */
    public BiseccionAnimacionFX(String funcion, double x0, double x1, double tolerancia, List<Biseccion.Iteracion> iteraciones) {
        this.funcion = funcion;
        this.x0 = x0;
        this.x1 = x1;
        this.tolerancia = tolerancia;
        this.iteraciones = iteraciones;
        this.iteracionActual = new SimpleIntegerProperty(0);
    }
    
    /**
     * Evalúa la función en un punto dado.
     * 
     * @param x El punto donde evaluar la función
     * @return El valor de la función en el punto x
     */
    private double evaluar(double x) {
        Argument arg = new Argument("x", x);
        Expression e = new Expression(funcion, arg);
        return e.calculate();
    }
    
    /**
     * Muestra la animación del método de bisección con una interfaz mejorada y más interactiva.
     */
    public void mostrarAnimacion() {
        // Verificar si el archivo CSS existe
        boolean cssExists = getClass().getResource("/styles/biseccion.css") != null;
        if (!cssExists) {
            System.err.println("Advertencia: El archivo CSS no se encuentra en la ruta esperada. Se usarán estilos predeterminados.");
        }

        // Crear los ejes con estilo mejorado
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("x");
        yAxis.setLabel("f(x)");
        xAxis.setAnimated(true);
        yAxis.setAnimated(true);

        // Calcular los límites del gráfico con mayor margen para mejor visualización
        double minX = Math.min(x0, x1) - 1.0;
        double maxX = Math.max(x0, x1) + 1.0;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        // Calcular los límites Y basados en los valores de la función
        double step = (maxX - minX) / 200; // Más puntos para una curva más suave
        for (double x = minX; x <= maxX; x += step) {
            double y = evaluar(x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        // Añadir margen vertical
        double yMargin = (maxY - minY) * 0.2;
        minY -= yMargin;
        maxY += yMargin;

        // Establecer los límites en los ejes
        xAxis.setLowerBound(minX);
        xAxis.setUpperBound(maxX);
        yAxis.setLowerBound(minY);
        yAxis.setUpperBound(maxY);

        // Guardar los límites originales para poder restaurarlos
        limiteXOriginalMin = minX;
        limiteXOriginalMax = maxX;
        limiteYOriginalMin = minY;
        limiteYOriginalMax = maxY;

        // Crear el gráfico con estilo mejorado
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Método de Bisección: " + funcion);
        lineChart.setCreateSymbols(false); // Sin símbolos en la función principal para una línea más limpia
        lineChart.setAnimated(false);
        lineChart.setLegendVisible(true);

        // Aplicar CSS para mejorar la apariencia
        lineChart.getStyleClass().add("biseccion-chart");

        // Series para diferentes elementos de la visualización
        XYChart.Series<Number, Number> functionSeries = new XYChart.Series<Number, Number>();
        XYChart.Series<Number, Number> x0Series = new XYChart.Series<Number, Number>();
        XYChart.Series<Number, Number> x1Series = new XYChart.Series<Number, Number>();
        XYChart.Series<Number, Number> x2Series = new XYChart.Series<Number, Number>();
        XYChart.Series<Number, Number> xAxisSeries = new XYChart.Series<Number, Number>();
        XYChart.Series<Number, Number> rootSeries = new XYChart.Series<Number, Number>();

        // Configurar nombres para la leyenda
        functionSeries.setName("f(x) = " + funcion);
        x0Series.setName("x0 (límite inferior)");
        x1Series.setName("x1 (límite superior)");
        x2Series.setName("x2 (punto medio)");
        rootSeries.setName("Raíz aproximada");

        // Agregar puntos a la función principal con alta resolución
        for (double x = minX; x <= maxX; x += step) {
            double y = evaluar(x);
            functionSeries.getData().add(new XYChart.Data<>(x, y));
        }

        // Agregar punto para el eje X (y=0)
        xAxisSeries.getData().add(new XYChart.Data<>(minX, 0));
        xAxisSeries.getData().add(new XYChart.Data<>(maxX, 0));

        // Agregar las series al gráfico
        lineChart.getData().addAll(functionSeries, xAxisSeries, x0Series, x1Series, x2Series);

        // Aplicar estilos a las series
        styleChartSeries(lineChart);

        // Panel de información detallada
        VBox infoPanel = createInfoPanel();

        // Panel de control con botones y slider para la animación
        HBox controlPanel = createControlPanel(lineChart);

        // Crear layout principal
        BorderPane root = new BorderPane();
        root.setCenter(lineChart);
        root.setRight(infoPanel);
        root.setBottom(controlPanel);
        root.setPadding(new Insets(10));
        BorderPane.setMargin(infoPanel, new Insets(0, 0, 0, 10));
        BorderPane.setMargin(controlPanel, new Insets(10, 0, 0, 0));

        // Escena con estilo
        Scene scene = new Scene(root, 1024, 768);
        try {
            String cssPath = "/styles/biseccion.css";
            if (getClass().getResource(cssPath) != null) {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            } else {
                System.err.println("No se pudo cargar el archivo CSS: " + cssPath);
                // Aplicar estilos básicos en línea como respaldo
                root.setStyle("-fx-background-color: white;");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el archivo CSS: " + e.getMessage());
        }

        // Ventana
        Stage stage = new Stage();
        stage.setTitle("Animación Interactiva del Método de Bisección");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        // Crear la timeline para la animación
        configurarAnimacion(lineChart, infoPanel);

        // Mostrar ventana e iniciar animación con manejo de errores
        try {
            stage.show();
            if (timeline.getKeyFrames().isEmpty()) {
                throw new RuntimeException("No se pudieron crear los keyframes de la animación");
            }
            timeline.playFromStart();
        } catch (Exception e) {
            System.err.println("Error al mostrar la animación principal: " + e.getMessage());
            e.printStackTrace();
            // Cerrar la ventana actual si está abierta
            stage.close();
            // Mostrar la versión simplificada como alternativa
            mostrarAnimacionSimplificada();
        }
    }

    /**
     * Configura la animación del método de bisección.
     */
    private void configurarAnimacion(LineChart<Number, Number> chart, VBox infoPanel) {
        timeline = new Timeline();

        // Declarar las etiquetas finales que usaremos en los keyframes
        final Label iterLabelFinal;
        final Label x0LabelFinal;
        final Label x1LabelFinal;
        final Label x2LabelFinal;
        final Label fx0LabelFinal;
        final Label fx1LabelFinal;
        final Label fx2LabelFinal;
        final Label errorLabelFinal;

        // Etiquetas para la información detallada - con verificación de nulos
        Label iterLabel = (Label) infoPanel.lookup("#iterLabel");
        Label x0Label = (Label) infoPanel.lookup("#x0Label");
        Label x1Label = (Label) infoPanel.lookup("#x1Label");
        Label x2Label = (Label) infoPanel.lookup("#x2Label");
        Label fx0Label = (Label) infoPanel.lookup("#fx0Label");
        Label fx1Label = (Label) infoPanel.lookup("#fx1Label");
        Label fx2Label = (Label) infoPanel.lookup("#fx2Label");
        Label errorLabel = (Label) infoPanel.lookup("#errorLabel");

        // Verificar que todas las etiquetas se encontraron correctamente
        boolean panelCorrecto = iterLabel != null && x0Label != null && x1Label != null && 
                               x2Label != null && fx0Label != null && fx1Label != null && 
                               fx2Label != null && errorLabel != null;

        if (!panelCorrecto) {
            System.err.println("No se pudieron encontrar todas las etiquetas en el panel de información.");
            // Recrear las etiquetas si no se encuentran
            infoPanel.getChildren().clear();
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            // Crear nuevas etiquetas
            iterLabel = new Label("0");
            iterLabel.setId("iterLabel");
            x0Label = new Label("0");
            x0Label.setId("x0Label");
            x1Label = new Label("0");
            x1Label.setId("x1Label");
            x2Label = new Label("0");
            x2Label.setId("x2Label");
            fx0Label = new Label("0");
            fx0Label.setId("fx0Label");
            fx1Label = new Label("0");
            fx1Label.setId("fx1Label");
            fx2Label = new Label("0");
            fx2Label.setId("fx2Label");
            errorLabel = new Label("0");
            errorLabel.setId("errorLabel");

            // Recrear el grid con las etiquetas
            grid.add(new Label("Iteración:"), 0, 0);
            grid.add(iterLabel, 1, 0);
            grid.add(new Label("x0:"), 0, 1);
            grid.add(x0Label, 1, 1);
            grid.add(new Label("x1:"), 0, 2);
            grid.add(x1Label, 1, 2);
            grid.add(new Label("x2:"), 0, 3);
            grid.add(x2Label, 1, 3);
            grid.add(new Label("f(x0):"), 0, 4);
            grid.add(fx0Label, 1, 4);
            grid.add(new Label("f(x1):"), 0, 5);
            grid.add(fx1Label, 1, 5);
            grid.add(new Label("f(x2):"), 0, 6);
            grid.add(fx2Label, 1, 6);
            grid.add(new Label("Error:"), 0, 7);
            grid.add(errorLabel, 1, 7);

            // Agregar el grid al panel
            infoPanel.getChildren().add(grid);
        }

        // Asignar las referencias finales a las etiquetas que usaremos en las lambdas
        iterLabelFinal = iterLabel;
        x0LabelFinal = x0Label;
        x1LabelFinal = x1Label;
        x2LabelFinal = x2Label;
        fx0LabelFinal = fx0Label;
        fx1LabelFinal = fx1Label;
        fx2LabelFinal = fx2Label;
        errorLabelFinal = errorLabel;

        // Para cada iteración creamos un keyframe
        double duracionPorIteracion = 1.5; // Duración en segundos para cada iteración

        for (int i = 0; i < iteraciones.size(); i++) {
            final int index = i;

            // KeyFrame para la animación de cada iteración
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i * duracionPorIteracion), event -> {
                // Actualizar la iteración actual
                iteracionActual.set(index);

                // Obtener la iteración actual del método
                Biseccion.Iteracion iteracion = iteraciones.get(index);

                // Actualizar la información en el panel
                iterLabelFinal.setText(String.format("%d", iteracion.iteracion));
                x0LabelFinal.setText(String.format("%.8f", iteracion.x0));
                x1LabelFinal.setText(String.format("%.8f", iteracion.x1));
                x2LabelFinal.setText(String.format("%.8f", iteracion.x2));
                fx0LabelFinal.setText(String.format("%.8f", iteracion.fx0));
                fx1LabelFinal.setText(String.format("%.8f", iteracion.fx1));
                fx2LabelFinal.setText(String.format("%.8f", iteracion.fx2));
                errorLabelFinal.setText(String.format("%.10f", iteracion.error));

                // Actualizar los puntos en el gráfico
                actualizarPuntosEnGrafico(chart, iteracion);

                // Realizar zoom progresivo hacia la raíz
                realizarZoomEnRaiz(chart, iteracion, index, iteraciones.size());
            });

            timeline.getKeyFrames().add(keyFrame);
        }

        // Destacar la iteración actual en la tabla de iteraciones
        // La tabla se actualizaría aquí si la hubiéramos implementado

        // Configurar las propiedades de la timeline
        timeline.setAutoReverse(false);
        timeline.setCycleCount(1); // Reproducir una vez por defecto

        // Asegurarse de que hay keyframes antes de iniciar
        if (!timeline.getKeyFrames().isEmpty()) {
            // Iniciar automáticamente la animación
            timeline.playFromStart();
        } else {
            System.err.println("No se pudieron crear keyframes para la animación");
        }
    }

    /**
     * Actualiza los puntos en el gráfico para una iteración específica.
     */
    private void actualizarPuntosEnGrafico(LineChart<Number, Number> chart, Biseccion.Iteracion iteracion) {
        try {
            // Verificar que el gráfico tenga suficientes series
            if (chart.getData().size() < 5) {
                System.err.println("Error: No hay suficientes series en el gráfico.");
                return;
            }

            // Obtener las series del gráfico
            XYChart.Series<Number, Number> x0Series = chart.getData().get(2); // x0
            XYChart.Series<Number, Number> x1Series = chart.getData().get(3); // x1
            XYChart.Series<Number, Number> x2Series = chart.getData().get(4); // x2

        // Limpiar las series anteriores
        x0Series.getData().clear();
        x1Series.getData().clear();
        x2Series.getData().clear();

        // Agregar los nuevos puntos de la iteración
        x0Series.getData().add(new XYChart.Data<>(iteracion.x0, 0)); // Punto en el eje X
        x0Series.getData().add(new XYChart.Data<>(iteracion.x0, iteracion.fx0)); // Punto en la función

        x1Series.getData().add(new XYChart.Data<>(iteracion.x1, 0)); // Punto en el eje X
        x1Series.getData().add(new XYChart.Data<>(iteracion.x1, iteracion.fx1)); // Punto en la función

        x2Series.getData().add(new XYChart.Data<>(iteracion.x2, 0)); // Punto en el eje X
        x2Series.getData().add(new XYChart.Data<>(iteracion.x2, iteracion.fx2)); // Punto en la función

        // Añadir efectos visuales a los puntos
        aplicarEstiloPuntos(x0Series, COLOR_X0);
        aplicarEstiloPuntos(x1Series, COLOR_X1);
        aplicarEstiloPuntos(x2Series, COLOR_X2);
        } catch (Exception e) {
            System.err.println("Error al actualizar los puntos en el gráfico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Aplica estilo a los puntos de las series.
     */
    private void aplicarEstiloPuntos(XYChart.Series<Number, Number> series, Color color) {
        try {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                // Crear un círculo para el punto con estilo simplificado para mayor confiabilidad
                Rectangle punto = new Rectangle(8, 8);
                punto.setFill(color);
                punto.setStroke(Color.GRAY);
                punto.setStrokeWidth(1);

                // Asignar el nodo al punto de datos
                data.setNode(punto);
            }
        } catch (Exception e) {
            System.err.println("Error al aplicar estilo a los puntos: " + e.getMessage());
        }
    }

    /**
     * Crea el panel de información detallada sobre la iteración actual.
     */
    private VBox createInfoPanel() {
        VBox panel = new VBox(5);
        panel.setPadding(new Insets(5));
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        panel.setMinWidth(180);
        panel.setMaxWidth(180);
        panel.setAlignment(Pos.TOP_CENTER);

        // Título del panel
        Text titulo = new Text("Detalles de Iteración");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Crear etiquetas para mostrar la información
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5, 0, 5, 0));

        // Fila 0: Iteración
        grid.add(new Label("Iteración:"), 0, 0);
        Label iterLabel = new Label("0");
        iterLabel.setId("iterLabel");
        grid.add(iterLabel, 1, 0);

        // Fila 1: x0
        grid.add(new Label("x0:"), 0, 1);
        Label x0Label = new Label("0");
        x0Label.setId("x0Label");
        grid.add(x0Label, 1, 1);

        // Fila 2: x1
        grid.add(new Label("x1:"), 0, 2);
        Label x1Label = new Label("0");
        x1Label.setId("x1Label");
        grid.add(x1Label, 1, 2);

        // Fila 3: x2
        grid.add(new Label("x2:"), 0, 3);
        Label x2Label = new Label("0");
        x2Label.setId("x2Label");
        grid.add(x2Label, 1, 3);

        // Fila 4: f(x0)
        grid.add(new Label("f(x0):"), 0, 4);
        Label fx0Label = new Label("0");
        fx0Label.setId("fx0Label");
        grid.add(fx0Label, 1, 4);

        // Fila 5: f(x1)
        grid.add(new Label("f(x1):"), 0, 5);
        Label fx1Label = new Label("0");
        fx1Label.setId("fx1Label");
        grid.add(fx1Label, 1, 5);

        // Fila 6: f(x2)
        grid.add(new Label("f(x2):"), 0, 6);
        Label fx2Label = new Label("0");
        fx2Label.setId("fx2Label");
        grid.add(fx2Label, 1, 6);

        // Fila 7: Error
        grid.add(new Label("Error:"), 0, 7);
        Label errorLabel = new Label("0");
        errorLabel.setId("errorLabel");
        grid.add(errorLabel, 1, 7);

        // Agregar todos los componentes al panel
        panel.getChildren().addAll(titulo, grid);

        return panel;
    }

    /**
     * Crea el panel de control para la animación.
     * 
     * @param lineChart El gráfico que se usará para el zoom
     * @return Panel de control con botones y controles para la animación
     */
    private HBox createControlPanel(LineChart<Number, Number> lineChart) {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        // Botón para iniciar/reanudar la animación
        Button playButton = new Button("▶ Iniciar");
        playButton.getStyleClass().add("control-button");
        playButton.setOnAction(e -> {
            timeline.play();
        });

        // Botón para pausar la animación
        Button pauseButton = new Button("⏸ Pausar");
        pauseButton.getStyleClass().add("control-button");
        pauseButton.setOnAction(e -> {
            timeline.pause();
        });

        // Botón para detener y reiniciar la animación
        Button stopButton = new Button("⏹ Reiniciar");
        stopButton.getStyleClass().add("control-button");
        stopButton.setOnAction(e -> {
            timeline.stop();
            iteracionActual.set(0);

            // Restablecer el zoom a los valores originales
            NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
            NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();

            // Restaurar los límites originales con animación
            Timeline resetZoomTimeline = new Timeline();

            KeyFrame kf = new KeyFrame(Duration.millis(500), 
                new KeyValue(xAxis.lowerBoundProperty(), limiteXOriginalMin),
                new KeyValue(xAxis.upperBoundProperty(), limiteXOriginalMax),
                new KeyValue(yAxis.lowerBoundProperty(), limiteYOriginalMin),
                new KeyValue(yAxis.upperBoundProperty(), limiteYOriginalMax)
            );

            resetZoomTimeline.getKeyFrames().add(kf);
            resetZoomTimeline.play();

            // Esperar un momento para que el reseteo del zoom termine antes de iniciar la animación
            resetZoomTimeline.setOnFinished(event -> timeline.playFromStart());
        });

        // Botón para activar/desactivar el zoom automático
        ToggleButton zoomButton = new ToggleButton("🔍 Zoom Automático");
        zoomButton.getStyleClass().add("control-button");
        zoomButton.setSelected(true); // Activado por defecto
        zoomButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            // Guardar la preferencia de zoom en una propiedad de la clase
            zoomAutomaticoActivado = newVal;

            // Si se desactiva el zoom, restaurar la vista completa
            if (!newVal) {
                // Obtener los ejes
                NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
                NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();

                // Restaurar los límites originales con animación
                Timeline resetZoomTimeline = new Timeline();

                KeyFrame kf = new KeyFrame(Duration.millis(500), 
                    new KeyValue(xAxis.lowerBoundProperty(), limiteXOriginalMin),
                    new KeyValue(xAxis.upperBoundProperty(), limiteXOriginalMax),
                    new KeyValue(yAxis.lowerBoundProperty(), limiteYOriginalMin),
                    new KeyValue(yAxis.upperBoundProperty(), limiteYOriginalMax)
                );

                resetZoomTimeline.getKeyFrames().add(kf);
                resetZoomTimeline.play();
            }
        });

        // Slider para controlar la velocidad de la animación
        Label velocidadLabel = new Label("Velocidad:");
        Slider velocidadSlider = new Slider(0.5, 3.0, 1.0);
        velocidadSlider.setShowTickLabels(true);
        velocidadSlider.setShowTickMarks(true);
        velocidadSlider.setMajorTickUnit(0.5);
        velocidadSlider.setBlockIncrement(0.1);
        velocidadSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            timeline.setRate(newVal.doubleValue());
        });

        // Agregar todos los controles al panel
        panel.getChildren().addAll(playButton, pauseButton, stopButton, zoomButton, velocidadLabel, velocidadSlider);

        return panel;
    }

    /**
     * Aplica estilos a las series del gráfico.
     */
    private void styleChartSeries(LineChart<Number, Number> chart) {
        // Estilo para la serie de la función
        chart.getData().get(0).getNode().setStyle(
            "-fx-stroke: " + toRGBCode(COLOR_FUNCION) + "; " +
            "-fx-stroke-width: 3px;"
        );

        // Estilo para el eje X
        chart.getData().get(1).getNode().setStyle(
            "-fx-stroke: " + toRGBCode(COLOR_EJE_X) + "; " +
            "-fx-stroke-width: 1px; " +
            "-fx-stroke-dash-array: 5 5;"
        );

        // Estilo para x0, x1 y x2 se aplica dinámicamente en actualizarPuntosEnGrafico
    }

    /**
     * Convierte un Color a su representación RGB en formato de cadena.
     */
    private String toRGBCode(Color color) {
        return String.format("rgba(%d, %d, %d, %.2f)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                color.getOpacity());
    }

    /**
     * Método alternativo para mostrar una animación simplificada en caso de fallo
     * de la animación principal.
     */
    private void mostrarAnimacionSimplificada() {
        try {
            // Crear ventana y escena básicas
            Stage stage = new Stage();
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 800, 600);

            // Crear gráfica básica
            NumberAxis xAxis = new NumberAxis();
            NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("x");
            yAxis.setLabel("f(x)");

            LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle("Método de Bisección (Vista simplificada)");

            // Crear las series
            XYChart.Series<Number, Number> functionSeries = new XYChart.Series<Number, Number>();
            functionSeries.setName("f(x)");

            // Calcular rango
            double minX = Math.min(x0, x1) - 0.5;
            double maxX = Math.max(x0, x1) + 0.5;
            double step = (maxX - minX) / 100;

            // Agregar puntos a la función
            for (double x = minX; x <= maxX; x += step) {
                double y = evaluar(x);
                functionSeries.getData().add(new XYChart.Data<>(x, y));
            }

            // Agregar la serie al gráfico
            chart.getData().add(functionSeries);

            // Agregar puntos de las iteraciones
            if (!iteraciones.isEmpty()) {
                // Mostrar última iteración
                Biseccion.Iteracion ultima = iteraciones.get(iteraciones.size() - 1);

                XYChart.Series<Number, Number> pointsSeries = new XYChart.Series<Number, Number>();
                pointsSeries.setName("Puntos clave");

                // Agregar puntos
                pointsSeries.getData().add(new XYChart.Data<>(ultima.x0, ultima.fx0));
                pointsSeries.getData().add(new XYChart.Data<>(ultima.x1, ultima.fx1));
                pointsSeries.getData().add(new XYChart.Data<>(ultima.x2, ultima.fx2));

                chart.getData().add(pointsSeries);

                // Colorear los puntos
                for (XYChart.Data<Number, Number> data : pointsSeries.getData()) {
                    Rectangle rect = new Rectangle(8, 8);
                    rect.setFill(COLOR_X2);
                    data.setNode(rect);
                }
            }

            // Información de la solución
            Label infoLabel = new Label();
            if (!iteraciones.isEmpty()) {
                Biseccion.Iteracion ultima = iteraciones.get(iteraciones.size() - 1);
                infoLabel.setText(String.format(
                    "Raíz aproximada: %.8f, Error: %.10f",
                    ultima.x2, ultima.error
                ));
            } else {
                infoLabel.setText("No hay iteraciones para mostrar");
            }

            // Estructurar la interfaz
            root.setCenter(chart);
            root.setBottom(infoLabel);
            BorderPane.setMargin(infoLabel, new Insets(10));

            // Configurar y mostrar la ventana
            stage.setTitle("Animación del Método de Bisección (Simplificada)");
            stage.setScene(scene);
            stage.show();

            // Mensaje para el usuario
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modo Alternativo");
            alert.setHeaderText("Visualización Simplificada");
            alert.setContentText("Se está mostrando una versión simplificada de la animación debido a problemas con la visualización completa.");
            alert.show();

        } catch (Exception e) {
            System.err.println("Error en la animación simplificada: " + e.getMessage());
            mostrarErrorAnimacion(e);
        }
    }

    /**
     * Realiza un zoom progresivo hacia la raíz durante la animación.
     * 
     * @param chart El gráfico donde se realiza el zoom
     * @param iteracion La iteración actual del método
     * @param indiceIteracion El índice de la iteración actual
     * @param totalIteraciones El número total de iteraciones
     */
    private void realizarZoomEnRaiz(LineChart<Number, Number> chart, Biseccion.Iteracion iteracion, int indiceIteracion, int totalIteraciones) {
        // Verificar si el zoom automático está activado
        if (!zoomAutomaticoActivado) return;

        // Sólo realizar zoom después de las primeras iteraciones
        if (indiceIteracion < 2) return;

        // Obtener los ejes
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        // Calcular el factor de zoom (aumenta gradualmente con cada iteración)
        double progresoIteracion = (double) indiceIteracion / totalIteraciones;
        double factorZoom = 0.85 - (0.5 * progresoIteracion); // Reduce gradualmente el área visible

        // Obtener los límites actuales
        double xMin = xAxis.getLowerBound();
        double xMax = xAxis.getUpperBound();
        double yMin = yAxis.getLowerBound();
        double yMax = yAxis.getUpperBound();

        // Calcular nuevos límites centrados en la aproximación actual de la raíz (x2)
        double xCentro = iteracion.x2;
        double anchuraX = (xMax - xMin) * factorZoom;
        double nuevoXMin = xCentro - (anchuraX / 2);
        double nuevoXMax = xCentro + (anchuraX / 2);

        // Calcular nuevos límites para el eje Y, manteniendo visible la función
        double yEnXMin = evaluar(nuevoXMin);
        double yEnXMax = evaluar(nuevoXMax);
        double yEnX2 = iteracion.fx2;
        double yMinNuevo = Math.min(Math.min(yEnXMin, yEnXMax), yEnX2);
        double yMaxNuevo = Math.max(Math.max(yEnXMin, yEnXMax), yEnX2);

        // Añadir margen para que la visualización sea más agradable
        double margenY = (yMaxNuevo - yMinNuevo) * 0.2;
        yMinNuevo -= margenY;
        yMaxNuevo += margenY;

        // Asegurarse de que el eje Y siempre incluya el cero
        if (yMinNuevo > 0) yMinNuevo = -margenY;
        if (yMaxNuevo < 0) yMaxNuevo = margenY;

        // Establecer los nuevos límites con una animación suave
        // Solo animamos si hay cambios significativos para evitar parpadeos
        if (Math.abs(xMin - nuevoXMin) > 0.001 || Math.abs(xMax - nuevoXMax) > 0.001) {
            xAxis.setAutoRanging(false);
            yAxis.setAutoRanging(false);

            // Animar el cambio de límites para una transición suave
            Timeline zoomTimeline = new Timeline();

            KeyFrame kf = new KeyFrame(Duration.millis(500), 
                new KeyValue(xAxis.lowerBoundProperty(), nuevoXMin),
                new KeyValue(xAxis.upperBoundProperty(), nuevoXMax),
                new KeyValue(yAxis.lowerBoundProperty(), yMinNuevo),
                new KeyValue(yAxis.upperBoundProperty(), yMaxNuevo)
            );

            zoomTimeline.getKeyFrames().add(kf);
            zoomTimeline.play();
        }
    }

    /**
     * Muestra un diálogo de error con detalles sobre el problema de la animación.
     * 
     * @param e La excepción que causó el error
     */
    private void mostrarErrorAnimacion(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error en la Animación");
        alert.setHeaderText("No se pudo mostrar la animación");
        alert.setContentText("Se produjo un error al intentar mostrar la animación: " + e.getMessage());

        // Detalles expandibles del error
        TextArea textArea = new TextArea();
        textArea.setText(e.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setMaxWidth(Double.MAX_VALUE);

        alert.getDialogPane().setExpandableContent(textArea);
        alert.showAndWait();
    }
    }
