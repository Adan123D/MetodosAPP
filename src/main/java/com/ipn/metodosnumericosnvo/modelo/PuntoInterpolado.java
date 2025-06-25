package com.ipn.metodosnumericosnvo.modelo;

import javafx.beans.property.SimpleDoubleProperty;

public class PuntoInterpolado {
    private final SimpleDoubleProperty x;
    private final SimpleDoubleProperty y;

    public PuntoInterpolado(double x, double y) {
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
    }

    public double getX() {
        return x.get();
    }

    public SimpleDoubleProperty xProperty() {
        return x;
    }

    public double getY() {
        return y.get();
    }

    public SimpleDoubleProperty yProperty() {
        return y;
    }
}
