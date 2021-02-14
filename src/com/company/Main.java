package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class Main {

    public static void main(String[] args) throws IOException {
        File dir = new File("data/Output/");
        try {
            Files.walk(dir.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (Exception ignored){}
        dir.mkdir();
        File[] workFiles = getWorkFiles();
        doOptimize(workFiles);
    }

    private static void doOptimize(File[] workFiles) throws IOException {
        for (int i = 0; i < workFiles.length; i++) {
            File file = workFiles[i];
            System.out.println(file.getPath() + " " + (i + 1) + " : " + workFiles.length);
            String format = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase(Locale.ROOT);
            if (format.equals(".jpg")) {
                createNewFile(file, ImageIO.read(file), file.getPath().replace("Image", "Output"));
            } else if (format.equals(".png")) {
                convertPNGToJPEG(file);
            } else {
                File outputFile = new File(file.getPath().replace("Image", "Output"));
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
        String path = file.getPath().replace("Image", "Output").substring(0, file.getPath().replace("Image", "Output").lastIndexOf(".")) + ".jpg";
        createNewFile(file, newBufferedImage, path);
    }

    private static void createNewFile(File inputFile, BufferedImage newBufferedImage, String path) throws IOException {
        File outputFile = new File(path);
        outputFile.createNewFile();
        ImageIO.write(newBufferedImage, "jpg", outputFile);
        setTime(inputFile, outputFile);
    }

    private static File[] getWorkFiles() {
        File workDirectory = new File("data/Image/");
        ArrayList<File> files = new ArrayList<>();
        for (File file : workDirectory.listFiles()) {
            files.addAll(getAllFile(file));
        }
        return files.toArray(new File[0]);
    }

    private static ArrayList<File> getAllFile(File file) {
        if (file.isFile()) {
            ArrayList<File> files = new ArrayList<>();
            files.add(file);
            return files;
        }
        if (file.isDirectory()) {
            new File(file.getPath().replace("Image", "Output")).mkdir();
            ArrayList<File> files = new ArrayList<>();
            for (File file1 : file.listFiles()) {
                files.addAll(getAllFile(file1));
            }
            return files;
        }

        return null;
    }
}
