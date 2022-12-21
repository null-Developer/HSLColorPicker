package com.example.colorgenerator;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        Group group = new Group();
        IntegerProperty colorProperty = new SimpleIntegerProperty();
        group.getChildren().add(new RSHSLColorPickerPane(colorProperty));
        primaryStage.setScene(new Scene(group));
        primaryStage.show();
    }
}