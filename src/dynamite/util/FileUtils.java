package dynamite.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility functions about File IO.
 */
public class FileUtils {

    /**
     * Read all lines from a file.
     *
     * @param filepath path to the file
     * @return a list of string where each string is a line in the file
     */
    public static List<String> readLinesFromFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File reading error");
        }
    }

    /**
     * Read a file as a string.
     *
     * @param filepath path to the file
     * @return file content as a string
     */
    public static String readFromFile(String filepath) {
        List<String> lines = readLinesFromFile(filepath);
        return lines.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Write a list of strings to a file.
     *
     * @param filepath path to the file
     * @param lines    a list of strings
     */
    public static void writeLinesToFile(String filepath, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File writing error");
        }
    }

    /**
     * Write a string to a file.
     *
     * @param filepath path to the file
     * @param content  the string
     */
    public static void writeStringToFile(String filepath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File writing error");
        }
    }

    /**
     * Create a directory and its parent directories.
     *
     * @param path the path to the directory
     */
    public static void createDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Cannot create dir: %s", path.toAbsolutePath()));
        }
    }

    /**
     * Delete a directory and all files inside.
     *
     * @param path the path to the directory
     */
    public static void deleteDirAndFiles(Path path) {
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Deletion error for %s", path.toAbsolutePath()));
        }
    }

    /**
     * Get the file name without extension.
     *
     * @param fileName the provided file name
     * @return the file name without extension
     */
    public static String removeExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");
        if (pos > 0 && pos < (fileName.length() - 1)) { // If '.' is not the first or last character.
            fileName = fileName.substring(0, pos);
        }
        return fileName;
    }

    /**
     * Get the extension of a file.
     *
     * @param filepath path to the provided file
     * @return the file extension
     */
    public static String getFileExtension(String filepath) {
        int pos = filepath.lastIndexOf('.');
        if (pos < 0) {
            throw new IllegalArgumentException("Cannot find extension for file: " + filepath);
        }
        return filepath.substring(pos + 1);
    }

    public static String getRelationName(String filepath) {
        File f = new File(filepath);
        return removeExtension(f.getName());
    }
}
