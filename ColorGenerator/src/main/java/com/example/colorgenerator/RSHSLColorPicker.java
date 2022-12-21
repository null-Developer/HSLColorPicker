package com.example.colorgenerator;

import javafx.scene.Group;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class RSHSLColorPicker extends WritableImage {
    private final int width;
    private final int height;
    private final int hueWidth;
    private final int pCircleDiameter;
    private final int hCircleDiameter;
    private int currentHue;
    private int currentSaturation;
    private int currentLightness;
    private int pickedColor;
    private int lastColorX;
    private int lastColorY;
    private int lastHueY;
    private final int pCircleRadius;
    private final int hCircleRadius;
    private final PixelWriter writer;

    public RSHSLColorPicker(int width, int height, int hueWidth, int pickedCircleDiameter, int hueCircleDiameter) {
        super(width + hueWidth, height);
        writer = getPixelWriter();

        this.width = width;
        this.height = height;
        this.hueWidth = hueWidth;
        this.pCircleDiameter = pickedCircleDiameter;
        this.hCircleDiameter = hueCircleDiameter;
        this.pickedColor = -1;
        this.pCircleRadius = pCircleDiameter / 2;
        this.hCircleRadius = hCircleDiameter / 2;
    }

    private void drawHSLColorPicker(int x, int y, int w, int h, int hueWidth, int hue) {
        createSL(x, y, w, h, hue);
        createHue(x + w, y, hueWidth, h);
        //drawStroke(x, y, w + hueWidth, h, 0x00FF00, 1);
    }

    private void createSL(int x, int y, int pickerWidth, int pickerHeight, int hue) {
        for (int pX = x; pX < x + pickerWidth; pX++) {
            for (int pY = y; pY < y + pickerHeight; pY++) {
                int saturation = (int) MathUtils.map(pX, x, x + pickerWidth, 0, 7);
                int lightness = (int) (127 - MathUtils.map(pY, y, y + pickerHeight, 0, 127));
                int rgb = HSLPalette.getRgbForHsl(hue, saturation, lightness);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                writer.setColor(pX, pY, Color.rgb(r, g, b));
            }
        }
    }

    private void createHue(int x, int y, int hueWidth, int hueHeight) {
        for (int pX = x; pX < x + hueWidth; pX++) {
            for (int pY = y; pY < y + hueHeight; pY++) {
                int hue = (int) (63 - MathUtils.map(pY, y, y + hueHeight, 0, 63));
                int rgb = HSLPalette.getRgbForHsl(hue, 7, 80);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                writer.setColor(pX, pY, Color.rgb(r, g, b));
            }
        }
    }

    public void drawStroke(int xPos, int yPos, int width, int height, int color, int strokeWidth) {

        drawVerticalStrokeLine(xPos, yPos, height, color, strokeWidth);
        drawVerticalStrokeLine((xPos + width) - strokeWidth, yPos, height, color, strokeWidth);
        drawHorizontalStrokeLine(xPos, yPos, width, color, strokeWidth);
        drawHorizontalStrokeLine(xPos, (yPos + height) - strokeWidth, width, color, strokeWidth);
    }

    private void drawVerticalStrokeLine(int xPosition, int yPosition, int height, int hexColor,
                                        int strokeWidth) {
        int pixelIndex = xPosition + yPosition * width;
        for (int rowIndex = 0; rowIndex < height; rowIndex++) {
            for (int x = 0; x < strokeWidth; x++) {
                int r = (hexColor >> 16) & 0xFF;
                int g = (hexColor >> 8) & 0xFF;
                int b = hexColor & 0xFF;
                int index = pixelIndex + x + rowIndex * width;
                writer.setColor(index / height, index % height, Color.rgb(r, g, b));
            }
        }
    }

    private void drawHorizontalStrokeLine(int xPos, int yPos, int w, int hexColor, int strokeWidth) {
        int index = xPos + yPos * width;
        int leftWidth = width - w;
        for (int x = 0; x < strokeWidth; x++) {
            for (int y = 0; y < w; y++) {
                int r = (hexColor >> 16) & 0xFF;
                int g = (hexColor >> 8) & 0xFF;
                int b = hexColor & 0xFF;
                writer.setColor(index / height, index % height, Color.rgb(r, g, b));
            }
            index += leftWidth;
        }

    }

    public void render(int x, int y) {
        drawHSLColorPicker(x, y, width, height, hueWidth, currentHue);
        handleBounds();
        updateCircles(x, y);
    }

    private Circle pickCircle;
    private Circle hueCircle;

    public void renderCircle(int x, int y, Group root) {
        x += 300;
        y += 300;
        pickCircle = new Circle(pCircleDiameter);
        pickCircle.setFill(Color.TRANSPARENT);
        pickCircle.setStroke(Color.WHITE);
        pickCircle.setLayoutX(x + lastColorX - (pCircleDiameter / 2f));
        pickCircle.setLayoutY(y + lastColorY - (pCircleDiameter / 2f));


        hueCircle = new Circle(hCircleDiameter);
        hueCircle.setFill(Color.TRANSPARENT);
        hueCircle.setStroke(Color.WHITE);
        hueCircle.setLayoutX(x + width + (hueWidth / 2f) - hCircleRadius);
        hueCircle.setLayoutY(y + lastHueY - (hCircleDiameter / 2f));

        root.getChildren().addAll(pickCircle, hueCircle);
    }

    public void updateCircles(int x, int y) {
        if(pickCircle == null) {
            return;
        }
        x += 300;
        y += 300;
        pickCircle.setLayoutX(x + lastColorX - (pCircleDiameter / 2f));
        pickCircle.setLayoutY(y + lastColorY - (pCircleDiameter / 2f));

        hueCircle.setLayoutX(x + width + (hueWidth / 2f) - hCircleRadius);
        hueCircle.setLayoutY(y + lastHueY - (hCircleDiameter / 2f));
    }


    public void handlePick(int x, int y) {

        if (x <= width) {
            currentSaturation = (int) MathUtils.map(x, 0, width, 0, 7);
            currentLightness = (int) (127 - MathUtils.map(y, 0, height - 1, 0, 127));
            lastColorX = x;
            lastColorY = y;
        } else {
            currentHue = (int) (63 - MathUtils.map(y, 0, height - 1, 0, 63));
            lastHueY = y;
        }
        pickedColor = HSLPalette.getHsl(currentHue, currentSaturation, currentLightness);
        render(0, 0);
    }

    private void handleBounds() {
        // color picker(saturation, lightness)
        if (lastColorX >= width - pCircleRadius) {
            lastColorX = width - pCircleRadius - 1;
        }
        if (lastColorX <= pCircleRadius) {
            lastColorX = pCircleRadius + 1;
        }

        if (lastColorY >= height - pCircleRadius) {
            lastColorY = height - pCircleRadius - 1;
        }

        if (lastColorY <= pCircleRadius) {
            lastColorY = pCircleRadius + 1;
        }

        // hue picker

        if (lastHueY >= height - hCircleRadius) {
            lastHueY = height - hCircleRadius - 1;
        }
        if (lastHueY <= hCircleRadius) {
            lastHueY = hCircleRadius + 1;
        }
    }

    public int getPickedColor() {
        return pickedColor;
    }

    public void setPickedColor(int pickedColor) {
        this.pickedColor = pickedColor;
    }
}
