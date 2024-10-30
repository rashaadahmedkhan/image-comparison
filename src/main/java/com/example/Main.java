package com.example;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.formats.png.PngImageParser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String imagePath1 = "images/old.png"; // Replace with your image paths
        String imagePath2 = "images/new.png";
        String superimposedImagePath = "images/superimposed.png"; // Path to save the superimposed image
        String differenceImagePath = "images/difference.png"; // Path to save the difference image
        int tolerance = 50; // Adjust tolerance level as needed

        try {
            compareImages(imagePath1, imagePath2, tolerance, superimposedImagePath, differenceImagePath);
        } catch (IOException | ImagingException e) {
            e.printStackTrace();
        }
    }

    private static void compareImages(String imagePath1, String imagePath2, int tolerance, String superimposedImagePath, String differenceImagePath)
            throws IOException, ImagingException {
        BufferedImage img1 = Imaging.getBufferedImage(new File(imagePath1));
        BufferedImage img2 = Imaging.getBufferedImage(new File(imagePath2));

        // Check if dimensions are the same
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            System.out.println("Images must be of the same dimensions.");
            return;
        }

        int width = img1.getWidth();
        int height = img1.getHeight();
        int totalPixels = width * height;
        int differingPixels = 0;

        // Create a superimposed image
        BufferedImage superimposedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = superimposedImage.createGraphics();
        g.drawImage(img1, 0, 0, null);
        g.setComposite(AlphaComposite.SrcOver.derive(0.5f)); // Adjust transparency
        g.drawImage(img2, 0, 0, null);
        g.dispose();

        // Save the superimposed image (remove null)
        Imaging.writeImage(superimposedImage, new File(superimposedImagePath), org.apache.commons.imaging.ImageFormats.PNG);
        System.out.println("Superimposed image saved to: " + superimposedImagePath);

        // Create a difference image
        BufferedImage differenceImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Compare pixel values
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                int red1 = (rgb1 >> 16) & 0xFF;
                int green1 = (rgb1 >> 8) & 0xFF;
                int blue1 = rgb1 & 0xFF;

                int red2 = (rgb2 >> 16) & 0xFF;
                int green2 = (rgb2 >> 8) & 0xFF;
                int blue2 = rgb2 & 0xFF;

                // Calculate the color difference
                int redDiff = Math.abs(red1 - red2);
                int greenDiff = Math.abs(green1 - green2);
                int blueDiff = Math.abs(blue1 - blue2);

                // Check if the difference is within the tolerance
                if (redDiff > tolerance || greenDiff > tolerance || blueDiff > tolerance) {
                    differingPixels++;
                    differenceImage.setRGB(x, y, Color.RED.getRGB()); // Highlight in red
                    System.out.printf("Difference at (%d, %d): R(%d vs %d), G(%d vs %d), B(%d vs %d)%n",
                            x, y, red1, red2, green1, green2, blue1, blue2);
                } else {
                    differenceImage.setRGB(x, y, rgb1); // Keep the original color
                }
            }
        }

        // Save the difference image (remove null)
        Imaging.writeImage(differenceImage, new File(differenceImagePath), org.apache.commons.imaging.ImageFormats.PNG);
        System.out.println("Difference image saved to: " + differenceImagePath);

        // Calculate percentage match/mismatch
        double mismatchPercentage = (double) differingPixels / totalPixels * 100;
        double matchPercentage = 100 - mismatchPercentage;

        System.out.printf("Total differing pixels: %d%n", differingPixels);
        System.out.printf("Match Percentage: %.2f%%%n", matchPercentage);
        System.out.printf("Mismatch Percentage: %.2f%%%n", mismatchPercentage);
    }
}
