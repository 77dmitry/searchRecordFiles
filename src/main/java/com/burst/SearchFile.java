package com.burst;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Scanner;

public class SearchFile {

    public static void main(String[] args) throws IOException {

        String parameter = null;
        if (args[0].equals("-disk")) {
            parameter = "disk";
        } else if (args[0].equals("-file")) {
            parameter = "file";
        } else if (args[0].equals("-read")) {
            parameter = "read";
        }

        Scanner scanner = new Scanner(System.in);

        switch (parameter) {
            case "disk":
                FileSystemView fileSystemView = FileSystemView.getFileSystemView();
                File[] drives = File.listRoots();
                if (drives != null && drives.length > 0) {
                    for (File aDrive : drives) {
                        System.out.println("Drive Letter: " + aDrive);
                        System.out.println("\tType: " + fileSystemView.getSystemTypeDescription(aDrive));
                        System.out.println("\tTotal space: " + aDrive.getTotalSpace());
                        System.out.println("\tFree space: " + aDrive.getFreeSpace());
                        System.out.println();
                    }
                }
                scanner.close();
                break;
            case "file":
                String pathString = scanner.nextLine();
                String firstFileName = scanner.nextLine();

                Files.walkFileTree(Paths.get(pathString), new HashSet<>(), 8, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (attrs.isRegularFile() && file.getFileName().toString().endsWith(".txt") && file.getFileName().toString().contains(firstFileName)) {
                            System.out.println("visitDirectory: " + file.getParent().toString());
                            System.out.println("visitFile: " + file.getFileName());
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
                scanner.close();
                break;
            case "read":
                String pathFail = scanner.nextLine();
                String myFile = scanner.nextLine();

                Files.walkFileTree(Paths.get(pathFail), new HashSet<>(), 4, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                        String fileName = file.getFileName().toString();

                        if (attrs.isRegularFile() && myFile.equals(fileName)) {
                            Files.lines(file).forEach(System.out::println);
                            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
                                writer.println(scanner.nextLine());
                                writer.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return FileVisitResult.TERMINATE;
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
                scanner.close();
                break;
            default:
                throw new IllegalArgumentException("Введите корректную команду");
        }
    }
}
