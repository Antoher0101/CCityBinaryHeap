module com.antoher.oop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.antoher.oop to javafx.fxml;
    exports com.antoher.oop;
    exports com.antoher.oop.controllers;
    exports com.antoher.oop.views;
    exports com.antoher.oop.models;
    opens com.antoher.oop.controllers to javafx.fxml;
}