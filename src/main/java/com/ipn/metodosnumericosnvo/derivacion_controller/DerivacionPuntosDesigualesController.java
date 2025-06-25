package com.ipn.metodosnumericosnvo.derivacion_controller;

import com.ipn.metodosnumericosnvo.metodo_derivacion.DerivacionPuntosDesiguales;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;


public class DerivacionPuntosDesigualesController {

    @FXML private TableView<PuntoTabla> tablaPuntos;
    @FXML private TableColumn<PuntoTabla, Double> columnaX;
    @FXML private TableColumn<PuntoTabla, Double> columnaY;
    @FXML private TextField campoPuntoEvaluacion;
    @FXML private ComboBox<String> comboMetodo;
    @FXML private ComboBox<String> comboOrden;
    @FXML private Button botonCalcular;
    @FXML private Button botonAgregarPunto;
    @FXML private Button botonEliminarPunto;
    @FXML private Button botonLimpiar;
    @FXML private TextArea areaResultados;
    @FXML private Label etiquetaError;

    private ObservableList<PuntoTabla> puntos;

    /**
     * Inicializa los componentes de la interfaz.
     */
    @FXML
    private void initialize() {
        inicializarTabla();
        inicializarComboBoxes();
        inicializarEventos();
        puntos = FXCollections.observableArrayList();
        tablaPuntos.setItems(puntos);
        
        // Agregar algunos puntos de ejemplo
        agregarPuntosEjemplo();
    }

    /**
     * Configura la tabla de puntos.
     */
    private void inicializarTabla() {
        // Configurar columnas
        columnaX.setCellValueFactory(new PropertyValueFactory<>("x"));
        columnaY.setCellValueFactory(new PropertyValueFactory<>("y"));
        
        // Hacer las celdas editables
        columnaX.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        columnaY.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        
        // Permitir edición en la tabla
        tablaPuntos.setEditable(true);
        
        // Configurar eventos de edición
        columnaX.setOnEditCommit(event -> {
            PuntoTabla punto = event.getRowValue();
            punto.setX(event.getNewValue());
            ordenarPuntos();
        });
        
        columnaY.setOnEditCommit(event -> {
            PuntoTabla punto = event.getRowValue();
            punto.setY(event.getNewValue());
        });
    }

    /**
     * Inicializa los ComboBox con sus opciones.
     */
    private void inicializarComboBoxes() {
        // Métodos disponibles
        comboMetodo.setItems(FXCollections.observableArrayList(
            "Diferencias Divididas",
            "Diferencia Hacia Adelante",
            "Diferencia Hacia Atrás",
            "Diferencia Centrada"
        ));
        comboMetodo.setValue("Diferencias Divididas");
        
        // Orden de derivada
        comboOrden.setItems(FXCollections.observableArrayList(
            "Primera Derivada",
            "Segunda Derivada"
        ));
        comboOrden.setValue("Primera Derivada");
    }

    /**
     * Configura los eventos de los controles.
     */
    private void inicializarEventos() {
        // Evento para habilitar/deshabilitar segundo orden según el método
        comboMetodo.setOnAction(event -> {
            String metodo = comboMetodo.getValue();
            if ("Diferencias Divididas".equals(metodo)) {
                comboOrden.setDisable(false);
            } else {
                comboOrden.setValue("Primera Derivada");
                comboOrden.setDisable(true);
            }
        });
    }

    /**
     * Agrega puntos de ejemplo para demostración.
     */
    private void agregarPuntosEjemplo() {
        puntos.addAll(
            new PuntoTabla(0.0, 1.0),
            new PuntoTabla(0.5, 1.649),
            new PuntoTabla(1.0, 2.718),
            new PuntoTabla(1.5, 4.482),
            new PuntoTabla(2.0, 7.389)
        );
        campoPuntoEvaluacion.setText("1.0");
    }

    /**
     * Maneja el evento de cálculo de la derivada.
     */
    @FXML
    private void calcularDerivada() {
        try {
            limpiarError();
            
            // Validar entrada
            if (puntos.size() < 2) {
                mostrarError("Se necesitan al menos 2 puntos para calcular la derivada.");
                return;
            }
            
            double puntoEvaluacion = Double.parseDouble(campoPuntoEvaluacion.getText());
            String metodo = comboMetodo.getValue();
            String orden = comboOrden.getValue();
            
            // Convertir puntos a arrays
            double[] x = puntos.stream().mapToDouble(PuntoTabla::getX).toArray();
            double[] y = puntos.stream().mapToDouble(PuntoTabla::getY).toArray();
            
            // Validar datos
            DerivacionPuntosDesiguales.validarDatos(x, y);
            
            // Calcular derivada según el método seleccionado
            double resultado = 0.0;
            StringBuilder detalles = new StringBuilder();
            
            switch (metodo) {
                case "Diferencias Divididas":
                    if ("Primera Derivada".equals(orden)) {
                        resultado = DerivacionPuntosDesiguales.primeraDerivadaDiferenciaDividida(x, y, puntoEvaluacion);
                        detalles.append("Método: Diferencias Divididas de Newton (Primera Derivada)\n");
                    } else {
                        if (puntos.size() < 3) {
                            mostrarError("Se necesitan al menos 3 puntos para la segunda derivada.");
                            return;
                        }
                        resultado = DerivacionPuntosDesiguales.segundaDerivadaDiferenciaDividida(x, y, puntoEvaluacion);
                        detalles.append("Método: Diferencias Divididas de Newton (Segunda Derivada)\n");
                    }
                    
                    // Mostrar tabla de diferencias divididas
                    double[][] tabla = DerivacionPuntosDesiguales.calcularTablaDiferenciasDivididas(x, y);
                    detalles.append("\nTabla de Diferencias Divididas:\n");
                    detalles.append(formatearTablaDiferenciasDivididas(x, tabla));
                    break;
                    
                case "Diferencia Hacia Adelante":
                    int indiceAdelante = encontrarIndice(x, puntoEvaluacion);
                    if (indiceAdelante >= x.length - 1) {
                        mostrarError("No se puede calcular diferencia hacia adelante en el último punto.");
                        return;
                    }
                    resultado = DerivacionPuntosDesiguales.derivadaHaciaAdelante(x, y, indiceAdelante);
                    detalles.append("Método: Diferencia Finita Hacia Adelante\n");
                    detalles.append(String.format("Usando puntos: x[%d] = %.4f, x[%d] = %.4f\n", 
                        indiceAdelante, x[indiceAdelante], indiceAdelante + 1, x[indiceAdelante + 1]));
                    break;
                    
                case "Diferencia Hacia Atrás":
                    int indiceAtras = encontrarIndice(x, puntoEvaluacion);
                    if (indiceAtras <= 0) {
                        mostrarError("No se puede calcular diferencia hacia atrás en el primer punto.");
                        return;
                    }
                    resultado = DerivacionPuntosDesiguales.derivadaHaciaAtras(x, y, indiceAtras);
                    detalles.append("Método: Diferencia Finita Hacia Atrás\n");
                    detalles.append(String.format("Usando puntos: x[%d] = %.4f, x[%d] = %.4f\n", 
                        indiceAtras - 1, x[indiceAtras - 1], indiceAtras, x[indiceAtras]));
                    break;
                    
                case "Diferencia Centrada":
                    int indiceCentro = encontrarIndice(x, puntoEvaluacion);
                    if (indiceCentro <= 0 || indiceCentro >= x.length - 1) {
                        mostrarError("No se puede calcular diferencia centrada en los puntos extremos.");
                        return;
                    }
                    resultado = DerivacionPuntosDesiguales.derivadaCentrada(x, y, indiceCentro);
                    detalles.append("Método: Diferencia Finita Centrada\n");
                    detalles.append(String.format("Usando puntos: x[%d] = %.4f, x[%d] = %.4f, x[%d] = %.4f\n", 
                        indiceCentro - 1, x[indiceCentro - 1], indiceCentro, x[indiceCentro], 
                        indiceCentro + 1, x[indiceCentro + 1]));
                    break;
            }
            
            // Mostrar resultados
            detalles.append(String.format("\nPunto de evaluación: x = %.4f\n", puntoEvaluacion));
            detalles.append(String.format("Resultado: f'(%.4f) = %.8f\n", puntoEvaluacion, resultado));
            
            areaResultados.setText(detalles.toString());
            
        } catch (NumberFormatException e) {
            mostrarError("Error en el formato numérico: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarError("Error en los datos: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Agrega un nuevo punto a la tabla.
     */
    @FXML
    private void agregarPunto() {
        puntos.add(new PuntoTabla(0.0, 0.0));
        int ultimoIndice = puntos.size() - 1;
        tablaPuntos.getSelectionModel().select(ultimoIndice);
        tablaPuntos.scrollTo(ultimoIndice);
    }

    /**
     * Elimina el punto seleccionado de la tabla.
     */
    @FXML
    private void eliminarPunto() {
        int indiceSeleccionado = tablaPuntos.getSelectionModel().getSelectedIndex();
        if (indiceSeleccionado >= 0) {
            puntos.remove(indiceSeleccionado);
        }
    }

    /**
     * Limpia todos los puntos de la tabla.
     */
    @FXML
    private void limpiarTabla() {
        puntos.clear();
        areaResultados.clear();
        limpiarError();
    }

    /**
     * Ordena los puntos por valor x.
     */
    private void ordenarPuntos() {
        puntos.sort((p1, p2) -> Double.compare(p1.getX(), p2.getX()));
        tablaPuntos.refresh();
    }

    /**
     * Encuentra el índice del punto más cercano al valor dado.
     */
    private int encontrarIndice(double[] x, double valor) {
        int indice = 0;
        double distanciaMinima = Math.abs(x[0] - valor);
        
        for (int i = 1; i < x.length; i++) {
            double distancia = Math.abs(x[i] - valor);
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                indice = i;
            }
        }
        
        return indice;
    }

    /**
     * Formatea la tabla de diferencias divididas para mostrar.
     */
    private String formatearTablaDiferenciasDivididas(double[] x, double[][] tabla) {
        StringBuilder sb = new StringBuilder();
        int n = x.length;
        
        // Encabezados
        sb.append(String.format("%-8s", "x"));
        for (int j = 0; j < n; j++) {
            sb.append(String.format("%-12s", "f[" + generarIndices(j) + "]"));
        }
        sb.append("\n");
        
        // Datos
        for (int i = 0; i < n; i++) {
            sb.append(String.format("%-8.4f", x[i]));
            for (int j = 0; j < n - i; j++) {
                sb.append(String.format("%-12.6f", tabla[i][j]));
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }

    /**
     * Genera los índices para la tabla de diferencias divididas.
     */
    private String generarIndices(int orden) {
        if (orden == 0) return "x";
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= orden; i++) {
            if (i > 0) sb.append(",");
            sb.append("x").append(i);
        }
        return sb.toString();
    }

    /**
     * Muestra un mensaje de error.
     */
    private void mostrarError(String mensaje) {
        etiquetaError.setText(mensaje);
        etiquetaError.setVisible(true);
    }

    /**
     * Limpia el mensaje de error.
     */
    private void limpiarError() {
        etiquetaError.setText("");
        etiquetaError.setVisible(false);
    }

    /**
     * Clase auxiliar para representar un punto en la tabla.
     */
    public static class PuntoTabla {
        private double x;
        private double y;

        public PuntoTabla(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
    }
}