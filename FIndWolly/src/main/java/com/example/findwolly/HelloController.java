package com.example.findwolly;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

public class HelloController {

    @FXML
    private ImageView originalImageView;
    @FXML
    private ImageView processedImageView;
    @FXML
    private Button processButton;
    @FXML
    public void onLoadAndProcessImage() {
        try {
            Image originalImage = getImage();
            originalImageView.setImage(originalImage);
            Image wollyTemplate = getWollyTemplate();
            WritableImage processedImage = findAndHighlightWolly(originalImage, wollyTemplate);
            processedImageView.setImage(processedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Image getImage() throws URISyntaxException {
        File imageFile = new File(Objects.requireNonNull(HelloController.class.getResource("/9.jpg")).toURI());
        return new Image(imageFile.toURI().toString());
    }
    // Метод для загрузки шаблона Уолли
    private static Image getWollyTemplate() throws URISyntaxException {
        File templateFile = new File(Objects.requireNonNull(HelloController.class.getResource("/11222.png")).toURI());
        return new Image(templateFile.toURI().toString());
    }
    private WritableImage findAndHighlightWolly(Image originalImage, Image template) {
        int originalWidth = (int) originalImage.getWidth();
        int originalHeight = (int) originalImage.getHeight();
        int templateWidth = (int) template.getWidth();
        int templateHeight = (int) template.getHeight();
        PixelReader originalReader = originalImage.getPixelReader();
        PixelReader templateReader = template.getPixelReader();
        WritableImage processedImage = new WritableImage(originalWidth, originalHeight);
        PixelWriter writer = processedImage.getPixelWriter();
        for (int y = 0; y < originalHeight; y++) {
            for (int x = 0; x < originalWidth; x++) {
                Color color = originalReader.getColor(x, y);
                double grayValue = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
                writer.setColor(x, y, Color.gray(grayValue));
            }
        }
        double minDifference = Double.MAX_VALUE;
        int bestX = -1;
        int bestY = -1;
        for (int y = 0; y <= originalHeight - templateHeight; y++) {
            for (int x = 0; x <= originalWidth - templateWidth; x++) {
                double difference = calculateTemplateMatch(originalReader, templateReader, x, y, templateWidth, templateHeight);
                if (difference < minDifference) {
                    minDifference = difference;
                    bestX = x;
                    bestY = y;
                }
            }
        }
        if (bestX != -1 && bestY != -1) {
            addRectangleBorder(writer, bestX, bestY, templateWidth, templateHeight);
        }
        return processedImage;
    }
    private double calculateTemplateMatch(PixelReader originalReader, PixelReader templateReader, int startX, int startY, int templateWidth, int templateHeight) {
        double totalDifference = 0.0;
        for (int y = 0; y < templateHeight; y++) {
            for (int x = 0; x < templateWidth; x++) {
                Color originalColor = originalReader.getColor(startX + x, startY + y);
                Color templateColor = templateReader.getColor(x, y);
                double rDiff = originalColor.getRed() - templateColor.getRed();
                double gDiff = originalColor.getGreen() - templateColor.getGreen();
                double bDiff = originalColor.getBlue() - templateColor.getBlue();
                totalDifference += Math.abs(rDiff) + Math.abs(gDiff) + Math.abs(bDiff);
            }
        }
        return totalDifference;
    }

    private void addRectangleBorder(PixelWriter writer, int startX, int startY, int width, int height) {
        Color borderColor = Color.RED;
        // Верхняя граница
        for (int x = startX; x < startX + width; x++) {
            writer.setColor(x, startY, borderColor);
        }
        // Нижняя граница
        for (int x = startX; x < startX + width; x++) {
            writer.setColor(x, startY + height - 1, borderColor);
        }
        // Левая граница
        for (int y = startY; y < startY + height; y++) {
            writer.setColor(startX, y, borderColor);
        }
        // Правая граница
        for (int y = startY; y < startY + height; y++) {
            writer.setColor(startX + width - 1, y, borderColor);
        }
    }
}
