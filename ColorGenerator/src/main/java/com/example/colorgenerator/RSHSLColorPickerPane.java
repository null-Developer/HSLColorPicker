package com.example.colorgenerator;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RSHSLColorPickerPane extends HBox {
    public RSHSLColorPickerPane(IntegerProperty color) {
        setWidth(256);
        setHeight(256);
        setSpacing(5);


        RSHSLColorPicker picker = new RSHSLColorPicker(300, 300, 60, 20, 20);
        ImageView view = new ImageView(picker);
        view.setFitWidth(360);
        view.setFitHeight(300);
        picker.render(0, 0);
        view.setOnMouseClicked(event -> {
            int x = (int) event.getX();
            int y = (int) event.getY();
            picker.handlePick(x, y);
            color.set(picker.getPickedColor());
        });

        view.setOnMouseDragged(event -> {
            int x = (int) event.getX();
            int y = (int) event.getY();
            picker.handlePick(x, y);
            color.set(picker.getPickedColor());
        });

        VBox colorInfoBox = new VBox(10);
        Rectangle colorRectangle = new Rectangle(128, 128);
        ObjectBinding<Color> colorBinding = Bindings.createObjectBinding(() -> {
            int pickedRgb = HSLPalette.table[color.get()];
            int r = (pickedRgb >> 16) & 0xFF;
            int g = (pickedRgb >> 8) & 0xFF;
            int b = pickedRgb & 0xFF;
            return Color.rgb(r, g, b);
        }, color);
        colorRectangle.fillProperty().bind(colorBinding);

        Label colorLabel = new Label("HSL: 0");
        colorLabel.textProperty().bind(color.asString("HSL: %d"));
        colorInfoBox.getChildren().addAll(colorRectangle, colorLabel);
        getChildren().addAll(view, colorInfoBox);
    }
}
