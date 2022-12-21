module com.example.colorgenerator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.colorgenerator to javafx.fxml;
    exports com.example.colorgenerator;
}