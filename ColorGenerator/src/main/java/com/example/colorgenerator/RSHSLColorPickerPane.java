package com.example.colorgenerator;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.example.colorgenerator.HSLPalette.*;

public class RSHSLColorPickerPane extends HBox {
    public RSHSLColorPickerPane(IntegerProperty color) {
        setWidth(256);
        setHeight(256);
        setSpacing(5);

        Label hexColorLabel = new Label("HEX: #000000");

        RSHSLColorPicker picker = new RSHSLColorPicker(300, 300, 60, 20, 20);
        ImageView view = new ImageView(picker);
        view.setFitWidth(360);
        view.setFitHeight(300);
        picker.render(0, 0);
        view.setOnMouseClicked(event -> {
            int x = (int) event.getX();
            int y = (int) event.getY();
            picker.handlePick(x, y);
            if(picker.getPickedColor() >=0) {
                color.set(picker.getPickedColor());
                addHexLabel(hexColorLabel, Integer.toHexString(getRgbForHsl(picker.getPickedColor())).toUpperCase());
            }else {
                picker.setPickedColor(0);
                color.set(picker.getPickedColor());
                addHexLabel(hexColorLabel, Integer.toHexString(getRgbForHsl(picker.getPickedColor())).toUpperCase());
            }
        });

        view.setOnMouseDragged(event -> {
            int x = (int) event.getX();
            int y = (int) event.getY();
            picker.handlePick(x, y);
            if(picker.getPickedColor() >=0) {
                color.set(picker.getPickedColor());
                addHexLabel(hexColorLabel, Integer.toHexString(getRgbForHsl(picker.getPickedColor())).toUpperCase());
            }else {
                picker.setPickedColor(0);
                color.set(picker.getPickedColor());
                addHexLabel(hexColorLabel, Integer.toHexString(getRgbForHsl(picker.getPickedColor())).toUpperCase());
            }
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

        Label hslColorLabel = new Label("HSL: 0");
        hslColorLabel.textProperty().bind(color.asString("HSL: %d"));

        Label hexLabel = new Label("\nFind HEX:");
        TextField txtHex = new TextField();
        addTextLimiter(txtHex, 7);
        txtHex.setMaxWidth(125);
        setOnKeyTyped(event -> {
            if (event.getCharacter().equals("#")) {
                // prevent hashtag from being entered
                event.consume();
            }
        });
        txtHex.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("a-zA-Z0-9*")) {
                txtHex.setText(newValue.replaceAll("[^a-zA-Z0-9]", ""));
            }
        });
        txtHex.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)){
                int hsl;
                try {
                    hsl = HSLPalette.findClosestHSL(Integer.parseInt(txtHex.getText(), 16), table);
                    Integer.parseInt(txtHex.getText(), 16);
                }catch (Exception e){
                    hsl = 0;
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setHeaderText("Input error");
                    a.setContentText("This is not a valid hexadecimal color code.");
                    a.show();
                }

                color.set(hsl);
                txtHex.setText("");
                addHexLabel(hexColorLabel, Integer.toHexString(getRgbForHsl(color.getValue())).toUpperCase());
            }
        });

        Label hslLabel = new Label("Find HSL:");
        TextField txtHsl = new TextField();
        addTextLimiter(txtHsl, 5);
        txtHsl.setMaxWidth(125);
        txtHsl.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtHsl.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        txtHsl.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)){
                int hsl = Integer.parseInt(txtHsl.getText());
                if(hsl >= 65535){
                    hsl = 65535;
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setHeaderText("Input error");
                    a.setContentText("HSL only goes up to 65535.");
                    a.show();
                }
                color.set(hsl);
                txtHsl.setText("");
                addHexLabel(hexColorLabel, Integer.toHexString(getRgbForHsl(color.getValue())).toUpperCase());
            }
        });
        colorInfoBox.setSpacing(2.0);
        colorInfoBox.getChildren().addAll(colorRectangle, hslColorLabel, hexColorLabel, hexLabel, txtHex, hslLabel, txtHsl);
        getChildren().addAll(view, colorInfoBox);
    }
    private static void addHexLabel(Label hexColorLabel, String hex){
        hexColorLabel.textProperty().set("HEX: #"+hex);
    }
    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength) {
                String s = tf.getText().substring(0, maxLength);
                tf.setText(s);
            }
        });
    }
}