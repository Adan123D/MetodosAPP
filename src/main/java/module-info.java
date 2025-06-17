module com.ipn.metodosnumericosnvo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // Required for function visualization
    requires java.desktop;
    requires org.jfree.fxgraphics2d;
    requires jlatexmath;

    opens com.ipn.metodosnumericosnvo to javafx.fxml;
    exports com.ipn.metodosnumericosnvo;
}
