package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class Main {

    public static void main(String[] args) throws IOException {
        File[] workFiles = getWorkFiles();
        doOptimize(workFiles);
    }

    private static void doOptimize(File[] workFiles) throws IOException {
        if (!new File("data/Output/").exists()) {
            new File("data/Output/").mkdir();
        }
        for (int i = 0; i < workFiles.length; i++){
            File file = workFiles[i];
            System.out.println(file.getName() + " " + (i + 1) + " : " + workFiles.length);
            String format = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase(Locale.ROOT);
            if (format.equals(".jpg")) {
                BufferedImage inputImage = ImageIO.read(file);
                ImageIO.write(inputImage, "jpg", new File("data/Output/" + file.getName()));
            } else if (format.equals(".png")) {
                convertPNGToJPEG(file);
            }
        }
    }

    private static void convertPNGToJPEG(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file);
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
        String path = "data/Output/" + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".jpg";
        File outputFile = new File(path);
        ImageIO.write(newBufferedImage, "jpg", outputFile);
    }

    private static File[] getWorkFiles() {
        File workDirectory = new File("data/Image/");
        return workDirectory.listFiles();
    }
}
