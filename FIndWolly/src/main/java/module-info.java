module com.example.findwolly {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;

    opens com.example.findwolly to javafx.fxml;
    exports com.example.findwolly;
}