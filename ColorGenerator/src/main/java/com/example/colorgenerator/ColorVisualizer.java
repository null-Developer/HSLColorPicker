package com.example.colorgenerator;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ColorVisualizer extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        Group group = new Group();
        IntegerProperty colorProperty = new SimpleIntegerProperty();
        group.getChildren().add(new RSHSLColorPickerPane(colorProperty));
        primaryStage.setTitle("HSL Color Visualizer");
        primaryStage.resizableProperty().set(false);
        primaryStage.setScene(new Scene(group));
        primaryStage.show();
    }
}