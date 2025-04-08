package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Утилитарный класс для генерации файлов с тестовыми массивами
 */
public class ArrayFileGenerator {

    /**
     * Создает файл с случайным массивом заданного размера
     * @param filename имя файла
     * @param size размер массива
     * @param maxValue максимальное значение элементов
     */
    public static void generateRandomArrayFile(String filename, int size, int maxValue) throws IOException {
        Random rand = new Random();
        PrintWriter writer = new PrintWriter(new FileWriter(filename));

        for (int i = 0; i < size; i++) {
            writer.print(rand.nextInt(maxValue));
            if (i < size - 1) {
                writer.print(" ");
            }
        }

        writer.close();
        System.out.println("Файл " + filename + " успешно создан");
    }

    /**
     * Создает файл с отсортированным массивом заданного размера
     * @param filename имя файла
     * @param size размер массива
     */
    public static void generateSortedArrayFile(String filename, int size) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));

        for (int i = 0; i < size; i++) {
            writer.print(i);
            if (i < size - 1) {
                writer.print(" ");
            }
        }

        writer.close();
        System.out.println("Файл " + filename + " успешно создан");
    }

    /**
     * Создает файл с обратно отсортированным массивом заданного размера
     * @param filename имя файла
     * @param size размер массива
     */
    public static void generateReversedArrayFile(String filename, int size) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));

        for (int i = size - 1; i >= 0; i--) {
            writer.print(i);
            if (i > 0) {
                writer.print(" ");
            }
        }

        writer.close();
        System.out.println("Файл " + filename + " успешно создан");
    }

    /**
     * Основной метод для генерации тестовых файлов
     */
    public static void main(String[] args) {
        try {
            // Генерация случайных массивов разных размеров
            generateRandomArrayFile("random_500.txt", 500, 1000);
            generateRandomArrayFile("random_1000.txt", 1000, 1000);
            generateRandomArrayFile("random_5000.txt", 5000, 1000);

            // Генерация отсортированных массивов
            generateSortedArrayFile("sorted_1000.txt", 1000);

            // Генерация обратно отсортированных массивов (худший случай)
            generateReversedArrayFile("reversed_1000.txt", 1000);

        } catch (IOException e) {
            System.err.println("Ошибка при создании файла: " + e.getMessage());
        }
    }
}