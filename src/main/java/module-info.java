module com.ipn.metodosnumericosnvo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.graphics;

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

    opens com.ipn.metodosnumericosnvo.app to javafx.fxml;
    opens com.ipn.metodosnumericosnvo.controller to javafx.fxml;
    opens com.ipn.metodosnumericosnvo.math to javafx.fxml;
    opens com.ipn.metodosnumericosnvo.visualization to javafx.fxml;

    exports com.ipn.metodosnumericosnvo.app;
    exports com.ipn.metodosnumericosnvo.controller;
    exports com.ipn.metodosnumericosnvo.math;
    exports com.ipn.metodosnumericosnvo.visualization;
}
