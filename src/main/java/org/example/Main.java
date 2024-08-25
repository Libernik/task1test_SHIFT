package org.example;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

//   java D:\task1test\src\main\java\org\example\Main.java -s text1.txt

public class Main {
    static void writeToFile(List<String> lines, String filePath, String name, boolean toExistingFiles) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + name, toExistingFiles))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка при записи в файл: " + e.getMessage());
        }
    }

    static boolean isInteger(String input) {
        try {
            new BigInteger(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {

        Boolean fullStatistics = null;
        Boolean addToExistingFiles = null;
        String outputPath = System.getProperty("user.dir");
        String namePrefix = "";
        List<String> inputFiles = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-s":
                    if (fullStatistics == null) {
                        fullStatistics = false;
                    } else {
                        System.out.println("Задайте только 1 параметр для статистики!");
                        return;
                    }
                    break;
                case "-f":
                    if (fullStatistics == null) {
                        fullStatistics = true;
                    } else {
                        System.out.println("Задайте только 1 параметр для статистики!");
                        return;
                    }
                    break;
                case "-a":
                    if (addToExistingFiles == null) {
                        addToExistingFiles = true;
                    } else {
                        System.out.println("Режим добавления в файлы уже активирован!");
                        return;
                    }
                    break;
                case "-o":
                    if (outputPath.equals(System.getProperty("user.dir"))) {
                        if (args[i + 1] != null) {
                            outputPath = args[i + 1];
                            i++;
                        } else {
                            System.out.println("Указан аргумент -o, но не указан путь!");
                            return;
                        }
                    } else {
                        System.out.println("Укажите только один путь вывода!");
                        return;
                    }
                    break;
                case "-p":
                    if (namePrefix.isEmpty()) {
                        if (args[i + 1] != null) {
                            namePrefix = args[i + 1];
                            i++;
                        } else {
                            System.out.println("Указан аргумент -p, но не указан префикс файлов!");
                            return;
                        }
                    } else {
                        System.out.println("Укажите только один префикс файлов!");
                        return;
                    }
                    break;
                default:
                    if (args[i].endsWith(".txt")) {
                        if (inputFiles == null) {
                            inputFiles = new ArrayList<>();
                        }
                        inputFiles.add(args[i]);
                    } else {
                        System.out.println("Указан неверный файл в качестве входного файла!");
                        return;
                    }
            }
        }

        if (fullStatistics == null) {
            fullStatistics = false;
        }

        if (inputFiles == null) {
            System.out.println("Не указаны входные файлы!");
            return;
        }

        List<String> strings = new ArrayList<>();
        List<String> floats = new ArrayList<>();
        List<String> integers = new ArrayList<>();

        int numOfInt = 0, numOfFloat = 0, numOfString = 0; //переменные для краткой статистики

        BigInteger maxInt = new BigInteger(String.valueOf(Integer.MIN_VALUE));
        BigInteger minInt = new BigInteger(String.valueOf(Integer.MAX_VALUE));
        BigInteger sumInt = BigInteger.ZERO;
        BigInteger medianInt = BigInteger.ZERO;
        double maxFloat = Double.NEGATIVE_INFINITY, minFloat = Double.POSITIVE_INFINITY, sumFloat = 0.0, medianFloat = 0.0;
        int shortestStringSize = Integer.MAX_VALUE, longestStringSize = 0;

        for (String file : inputFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (isInteger(line)) {
                        integers.add(line);
                        numOfInt++;
                        if (fullStatistics) {
                            BigInteger integerValue = new BigInteger(line);
                            sumInt = sumInt.add(integerValue);
                            maxInt = maxInt.max(integerValue);
                            minInt = minInt.min(integerValue);
                        }
                    } else if (isDouble(line)) {
                        floats.add(line);
                        numOfFloat++;
                        if (fullStatistics) {
                            double floatValue = Double.parseDouble(line);
                            sumFloat += floatValue;
                            maxFloat = Math.max(maxFloat, floatValue);
                            minFloat = Math.min(minFloat, floatValue);
                        }
                    } else {
                        strings.add(line);
                        numOfString++;
                        if (fullStatistics) {
                            int length = line.length();
                            shortestStringSize = Math.min(shortestStringSize, length);
                            longestStringSize = Math.max(longestStringSize, length);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Произошла ошибка при чтении файла: " + e.getMessage());
            }
        }

        if (fullStatistics) {
            if (numOfInt != 0) {
                medianInt = sumInt.divide(BigInteger.valueOf(numOfInt)); // Вычисляем медиану как среднее значение
            }
            medianFloat = numOfFloat > 0 ? sumFloat / numOfFloat : 0;
        }

        if (addToExistingFiles != null) {
            if (!floats.isEmpty())
                writeToFile(floats, outputPath + "/", namePrefix + "floats.txt", true);
            if (!integers.isEmpty())
                writeToFile(integers, outputPath + "/", namePrefix + "integers.txt", true);
            if (!strings.isEmpty())
                writeToFile(strings, outputPath + "/", namePrefix + "strings.txt", true);
        } else {
            if (!floats.isEmpty())
                writeToFile(floats, outputPath + "/", namePrefix + "floats.txt", false);
            if (!integers.isEmpty())
                writeToFile(integers, outputPath + "/", namePrefix + "integers.txt", false);
            if (!strings.isEmpty())
                writeToFile(strings, outputPath + "/", namePrefix + "strings.txt", false);
        }

        System.out.println("\t Statistics:");
        System.out.printf("Num of integers: %s \n", numOfInt);
        System.out.printf("Num of floats: %s \n", numOfFloat);
        System.out.printf("Num of strings: %s \n", numOfString);

        if (fullStatistics) {
            if (!floats.isEmpty())
            {
                System.out.printf("Max float: %s \n", maxFloat);
                System.out.printf("Min floats: %s \n", minFloat);
                System.out.printf("Sum of floats: %s \n", sumFloat);
                System.out.printf("Median of floats: %s \n", medianFloat);
            }
            if (!integers.isEmpty())
            {
                System.out.printf("Max integer: %s \n", maxInt);
                System.out.printf("Min integer: %s \n", minInt);
                System.out.printf("Sum of integers: %s \n", sumInt);
                System.out.printf("Median of integers: %s \n", medianInt);
            }
            if (!strings.isEmpty())
            {
                System.out.printf("Shortest string's size: %s \n", shortestStringSize);
                System.out.printf("Longest string's size: %s \n", longestStringSize);
            }

        }
    }
}