module com.ipn.metodosnumericosnvo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.graphics;
    requires jdk.jsobject;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    // Commented out due to compatibility issues with Java 17
    // requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // Required for function visualization
    requires java.desktop;
    requires org.jfree.fxgraphics2d;
    requires jlatexmath;

    // Required for JavaScript function evaluation
    requires java.scripting;
    requires org.mozilla.rhino;

    // Required for optimized mathematical calculations
    requires commons.math3;

    // Required for mXparser function evaluation
    requires MathParser.org.mXparser;
    requires matheclipse.core;
    requires JMathAnim;
    //requires rings;

    opens com.ipn.metodosnumericosnvo.app to javafx.fxml;
    opens com.ipn.metodosnumericosnvo.controller to javafx.fxml;
    opens com.ipn.metodosnumericosnvo.math to javafx.fxml;
    // Commented out since the package doesn't exist
    // opens com.ipn.metodosnumericosnvo.visualization to javafx.fxml;
    opens com.ipn.metodosnumericosnvo.metodos_raices to javafx.base;
    opens com.ipn.metodosnumericosnvo.derivacion_controller to javafx.fxml;
    opens com.ipn.metodosnumericosnvo.metodo_derivacion to javafx.fxml;
    opens com.ipn.metodosnumericosnvo.modelo to javafx.base;
    opens com.ipn.metodosnumericosnvo.integracion_controller to javafx.fxml;
    opens com.ipn.metodosnumericosnvo.animation to javafx.fxml;

    exports com.ipn.metodosnumericosnvo.app;
    exports com.ipn.metodosnumericosnvo.controller;
    exports com.ipn.metodosnumericosnvo.math;
    //exports com.ipn.metodosnumericosnvo.visualization;
    exports com.ipn.metodosnumericosnvo.metodos_raices;
    exports com.ipn.metodosnumericosnvo.derivacion_controller;
    exports com.ipn.metodosnumericosnvo.metodo_derivacion;
    exports com.ipn.metodosnumericosnvo.modelo;
    exports com.ipn.metodosnumericosnvo.integracion_controller;
    exports com.ipn.metodosnumericosnvo.animation;
}
