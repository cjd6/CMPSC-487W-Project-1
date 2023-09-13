module com.example.desktop_database_application {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.desktop_database_application to javafx.fxml;
    exports com.example.desktop_database_application;
}