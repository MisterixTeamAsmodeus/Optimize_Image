package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
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
                createNewFile(file,ImageIO.read(file), "data/Output/" + file.getName());
            } else if (format.equals(".png")) {
                convertPNGToJPEG(file);
            } else {
                File outputFile = new File("data/Output/" + file.getName());
                Files.copy(file.toPath(), outputFile.toPath());
                setTime(file, outputFile);
            }
        }
    }


    private static void setTime(File file, File outputFile) throws IOException {
        BasicFileAttributeView inputAtr = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
        BasicFileAttributeView newAtr = Files.getFileAttributeView(outputFile.toPath(), BasicFileAttributeView.class);
        if (inputAtr.readAttributes().lastModifiedTime().toMillis() < inputAtr.readAttributes().creationTime().toMillis())
            newAtr.setTimes(inputAtr.readAttributes().lastModifiedTime(), inputAtr.readAttributes().lastAccessTime(), inputAtr.readAttributes().lastModifiedTime());
        else
            newAtr.setTimes(inputAtr.readAttributes().lastModifiedTime(), inputAtr.readAttributes().lastAccessTime(), inputAtr.readAttributes().creationTime());
    }

    private static void convertPNGToJPEG(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file);
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
        String path = "data/Output/" + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".jpg";
        createNewFile(file, newBufferedImage, path);
    }

    private static void createNewFile(File inputFile, BufferedImage newBufferedImage, String path) throws IOException {
        File outputFile = new File(path);
        ImageIO.write(newBufferedImage, "jpg", outputFile);
        setTime(inputFile, outputFile);
    }

    private static File[] getWorkFiles() {
        File workDirectory = new File("data/Image/");
        return workDirectory.listFiles();
    }
}
