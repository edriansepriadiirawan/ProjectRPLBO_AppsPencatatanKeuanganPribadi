module gui.appspencatatankeuanganpribadi {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires org.xerial.sqlitejdbc;

    opens gui.appspencatatankeuanganpribadi to javafx.fxml;
    exports gui.appspencatatankeuanganpribadi;
}