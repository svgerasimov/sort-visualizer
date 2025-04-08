package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Приложение для визуализации и сравнения алгоритмов сортировки вставками
 * Сравнивает эффективность алгоритмов сортировки простыми вставками и бинарными вставками
 */
public class SortVisualizerApp extends JFrame {
    // Графические компоненты
    private JPanel contentPanel;
    private JTextArea resultTextArea;
    private JPanel chartPanel;

    // Данные
    private int[] currentArray;
    private JComboBox<String> sortTypeComboBox;

    // История результатов сортировки для построения графиков
    private List<SortExperimentResult> experimentResults = new ArrayList<>();

    /**
     * Конструктор приложения
     */
    public SortVisualizerApp() {
        // Настройка окна
        setTitle("Визуализатор сравнения алгоритмов сортировки вставками");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Создание компонентов GUI
        initComponents();

        // Отображение окна
        setVisible(true);
    }

    /**
     * Инициализация компонентов интерфейса
     */
    private void initComponents() {
        // Основная панель с BorderLayout
        contentPanel = new JPanel(new BorderLayout());
        setContentPane(contentPanel);

        // Верхняя панель управления
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contentPanel.add(controlPanel, BorderLayout.NORTH);

        // Кнопки
        JButton loadButton = new JButton("Загрузить из файла");
        JButton saveButton = new JButton("Сохранить файл");
        JButton generateButton = new JButton("Сгенерировать массив");
        JButton sortButton = new JButton("Сортировать");
        JButton compareButton = new JButton("Сравнить оба алгоритма");
        JButton clearButton = new JButton("Очистить всё");

        // Выпадающий список типов сортировки
        sortTypeComboBox = new JComboBox<>(new String[]{"Простые вставки", "Бинарные вставки"});

        // Создаем панель инструментов
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(loadButton);
        toolBar.add(saveButton);
        toolBar.add(new JToolBar.Separator());
        toolBar.add(generateButton);
        toolBar.add(new JToolBar.Separator());
        toolBar.add(sortTypeComboBox);
        toolBar.add(sortButton);
        toolBar.add(compareButton);

        // Добавляем пружину для отделения кнопки очистки от других элементов
        toolBar.add(Box.createHorizontalGlue());

        // Добавляем визуальный разделитель перед кнопкой очистки
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(2, 24));
        separator.setMaximumSize(new Dimension(2, 24));
        toolBar.add(Box.createHorizontalStrut(15)); // Отступ слева от разделителя
        toolBar.add(separator);
        toolBar.add(Box.createHorizontalStrut(15)); // Отступ справа от разделителя

        // Стилизуем кнопку очистки
        clearButton.setBackground(new Color(255, 200, 200));
        clearButton.setForeground(Color.RED);
        clearButton.setFocusPainted(false);
        clearButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 0, 0), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15))); // Увеличенные внутренние отступы
        clearButton.setFont(new Font(clearButton.getFont().getName(), Font.BOLD, clearButton.getFont().getSize()));

        toolBar.add(clearButton);
        toolBar.add(Box.createHorizontalStrut(10)); // Отступ справа от кнопки

        // Добавляем панель инструментов в верхнюю часть
        controlPanel.add(toolBar);

        // Панель с результатами
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        resultPanel.setBorder(BorderFactory.createTitledBorder("Результаты сортировки"));

        // Панель с графиком
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("График сравнения алгоритмов"));

        // Разделение центральной части на результаты и график
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultPanel, chartPanel);
        splitPane.setResizeWeight(0.4);
        contentPanel.add(splitPane, BorderLayout.CENTER);

        // Добавление обработчиков событий
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadArrayFromFile();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveResultsToFile();
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGenerateDialog();
            }
        });

        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortArray();
            }
        });

        compareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareBothAlgorithms();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResults();
            }
        });
    }

    /**
     * Загрузка массива из файла
     */
    private void loadArrayFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String line = reader.readLine();
                reader.close();

                String[] numbers = line.split("\\s+"); // Разделение по пробельным символам
                currentArray = new int[numbers.length];
                for (int i = 0; i < numbers.length; i++) {
                    if (!numbers[i].isEmpty()) {
                        currentArray[i] = Integer.parseInt(numbers[i].trim());
                    }
                }

                // Очистка предыдущих результатов при загрузке нового массива
                experimentResults.clear();

                resultTextArea.setText("Массив загружен из файла: " + selectedFile.getName() + "\n");
                resultTextArea.append("Количество элементов: " + currentArray.length + "\n");

                // Ограничиваем вывод массива, если он слишком большой
                if (currentArray.length <= 100) {
                    resultTextArea.append("Массив: " + Arrays.toString(currentArray) + "\n");
                } else {
                    // Показываем только первые и последние 10 элементов
                    StringBuilder sb = new StringBuilder("Массив: [");
                    for (int i = 0; i < 10; i++) {
                        sb.append(currentArray[i]).append(", ");
                    }
                    sb.append("... , ");
                    for (int i = currentArray.length - 10; i < currentArray.length; i++) {
                        sb.append(currentArray[i]);
                        if (i < currentArray.length - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append("]\n");
                    resultTextArea.append(sb.toString());
                }

                // Обновление графика (очистка старого)
                updateChart();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при чтении файла: " + ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Сохранение результатов в файл
     */
    private void saveResultsToFile() {
        if (experimentResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет данных для сохранения",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                PrintWriter writer = new PrintWriter(selectedFile);

                // Записываем заголовок
                writer.println("Размер массива, Алгоритм, Сравнения, Вставки, Время (мс)");

                // Записываем результаты экспериментов
                for (SortExperimentResult experiment : experimentResults) {
                    writer.println(String.format("%d, %s, %d, %d, %d",
                            experiment.arraySize,
                            experiment.algorithm,
                            experiment.comparisons,
                            experiment.swaps,
                            experiment.timeMs));
                }

                writer.close();
                JOptionPane.showMessageDialog(this, "Результаты сохранены в файл: " + selectedFile.getName(),
                        "Информация", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при сохранении файла: " + ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Показ диалогового окна для генерации случайного массива
     */
    private void showGenerateDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Размер массива:"));
        JTextField sizeField = new JTextField("1000");
        panel.add(sizeField);

        panel.add(new JLabel("Максимальное значение:"));
        JTextField maxValueField = new JTextField("10000");
        panel.add(maxValueField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Генерация массива", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int size = Integer.parseInt(sizeField.getText().trim());
                int maxValue = Integer.parseInt(maxValueField.getText().trim());

                if (size <= 0 || maxValue <= 0) {
                    JOptionPane.showMessageDialog(this, "Значения должны быть положительными",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                generateRandomArray(size, maxValue);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректные числа",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Генерация случайного массива заданного размера
     * @param size размер массива
     * @param maxValue максимальное значение элементов
     */
    private void generateRandomArray(int size, int maxValue) {
        Random rand = new Random();
        currentArray = new int[size];
        for (int i = 0; i < size; i++) {
            currentArray[i] = rand.nextInt(maxValue);
        }

        // Очистка предыдущих результатов при генерации нового массива
        experimentResults.clear();

        resultTextArea.setText("Сгенерирован случайный массив\n");
        resultTextArea.append("Количество элементов: " + NumberFormat.getNumberInstance().format(size) + "\n");
        resultTextArea.append("Максимальное значение: " + NumberFormat.getNumberInstance().format(maxValue) + "\n");

        // Ограничиваем вывод массива, если он слишком большой
        if (size <= 100) {
            resultTextArea.append("Массив: " + Arrays.toString(currentArray) + "\n");
        } else {
            // Показываем только первые и последние 10 элементов
            StringBuilder sb = new StringBuilder("Массив: [");
            for (int i = 0; i < 10; i++) {
                sb.append(currentArray[i]).append(", ");
            }
            sb.append("... , ");
            for (int i = size - 10; i < size; i++) {
                sb.append(currentArray[i]);
                if (i < size - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]\n");
            resultTextArea.append(sb.toString());
        }

        // Обновление графика (очистка старого)
        updateChart();
    }

    /**
     * Сортировка текущего массива выбранным алгоритмом
     */
    private void sortArray() {
        if (currentArray == null || currentArray.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Нет данных для сортировки. Сначала загрузите или сгенерируйте массив.",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int[] arrayCopy = Arrays.copyOf(currentArray, currentArray.length);
        SortResult result;
        String sortType = (String) sortTypeComboBox.getSelectedItem();

        long startTime = System.nanoTime();
        if ("Простые вставки".equals(sortType)) {
            result = insertionSort(arrayCopy);
        } else {
            result = binaryInsertionSort(arrayCopy);
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;

        resultTextArea.append("\n===== Результаты сортировки =====\n");
        resultTextArea.append("Метод сортировки: " + sortType + "\n");
        resultTextArea.append("Размер массива: " + NumberFormat.getNumberInstance().format(currentArray.length) + "\n");
        resultTextArea.append("Время выполнения: " + NumberFormat.getNumberInstance().format(duration) + " мс\n");
        resultTextArea.append("Количество сравнений: " + NumberFormat.getNumberInstance().format(result.comparisons) + "\n");
        resultTextArea.append("Количество вставок: " + NumberFormat.getNumberInstance().format(result.swaps) + "\n");

        // Проверяем, отсортирован ли массив
        boolean isSorted = true;
        for (int i = 1; i < arrayCopy.length; i++) {
            if (arrayCopy[i - 1] > arrayCopy[i]) {
                isSorted = false;
                break;
            }
        }
        resultTextArea.append("Массив отсортирован: " + (isSorted ? "Да" : "Нет") + "\n");

        // Сохраняем результаты эксперимента для графика
        experimentResults.add(new SortExperimentResult(
                currentArray.length,
                sortType,
                result.comparisons,
                result.swaps,
                duration
        ));

        // Обновляем график
        updateChart();
    }

    /**
     * Сравнение обоих алгоритмов сортировки на текущем массиве
     */
    private void compareBothAlgorithms() {
        if (currentArray == null || currentArray.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Нет данных для сортировки. Сначала загрузите или сгенерируйте массив.",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }

        resultTextArea.append("\n===== Сравнение алгоритмов сортировки =====\n");
        resultTextArea.append("Размер массива: " + NumberFormat.getNumberInstance().format(currentArray.length) + "\n\n");

        // Сортировка простыми вставками
        int[] arrayCopy1 = Arrays.copyOf(currentArray, currentArray.length);
        long startTime1 = System.nanoTime();
        SortResult result1 = insertionSort(arrayCopy1);
        long endTime1 = System.nanoTime();
        long duration1 = (endTime1 - startTime1) / 1000000;

        // Сортировка бинарными вставками
        int[] arrayCopy2 = Arrays.copyOf(currentArray, currentArray.length);
        long startTime2 = System.nanoTime();
        SortResult result2 = binaryInsertionSort(arrayCopy2);
        long endTime2 = System.nanoTime();
        long duration2 = (endTime2 - startTime2) / 1000000; // в миллисекундах

        // Вывод результатов
        resultTextArea.append("Простые вставки:\n");
        resultTextArea.append("  Время выполнения: " + NumberFormat.getNumberInstance().format(duration1) + " мс\n");
        resultTextArea.append("  Количество сравнений: " + NumberFormat.getNumberInstance().format(result1.comparisons) + "\n");
        resultTextArea.append("  Количество вставок: " + NumberFormat.getNumberInstance().format(result1.swaps) + "\n\n");

        resultTextArea.append("Бинарные вставки:\n");
        resultTextArea.append("  Время выполнения: " + NumberFormat.getNumberInstance().format(duration2) + " мс\n");
        resultTextArea.append("  Количество сравнений: " + NumberFormat.getNumberInstance().format(result2.comparisons) + "\n");
        resultTextArea.append("  Количество вставок: " + NumberFormat.getNumberInstance().format(result2.swaps) + "\n\n");

        // Добавляем выделенную секцию выводов с Unicode-символами
        resultTextArea.append("\n■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■\n");
        resultTextArea.append("                     ВЫВОДЫ                     \n");
        resultTextArea.append("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■\n");

        if (result1.comparisons > result2.comparisons) {
            resultTextArea.append("✓ Бинарные вставки требуют меньше сравнений (на " +
                    NumberFormat.getNumberInstance().format(result1.comparisons - result2.comparisons) + ")\n");
        } else if (result1.comparisons < result2.comparisons) {
            resultTextArea.append("✓ Простые вставки требуют меньше сравнений (на " +
                    NumberFormat.getNumberInstance().format(result2.comparisons - result1.comparisons) + ")\n");
        } else {
            resultTextArea.append("✓ Оба алгоритма выполнили одинаковое количество сравнений\n");
        }

        if (result1.swaps > result2.swaps) {
            resultTextArea.append("✓ Бинарные вставки требуют меньше перестановок (на " +
                    NumberFormat.getNumberInstance().format(result1.swaps - result2.swaps) + ")\n");
        } else if (result1.swaps < result2.swaps) {
            resultTextArea.append("✓ Простые вставки требуют меньше перестановок (на " +
                    NumberFormat.getNumberInstance().format(result2.swaps - result1.swaps) + ")\n");
        } else {
            resultTextArea.append("✓ Оба алгоритма выполнили одинаковое количество перестановок\n");
        }

        if (duration1 > duration2) {
            resultTextArea.append("✓ Бинарные вставки быстрее (на " +
                    NumberFormat.getNumberInstance().format(duration1 - duration2) + " мс)\n");
        } else if (duration1 < duration2) {
            resultTextArea.append("✓ Простые вставки быстрее (на " +
                    NumberFormat.getNumberInstance().format(duration2 - duration1) + " мс)\n");
        } else {
            resultTextArea.append("✓ Оба алгоритма выполнились за одинаковое время\n");
        }

        // Сохраняем результаты экспериментов для графика
        experimentResults.add(new SortExperimentResult(
                currentArray.length,
                "Простые вставки",
                result1.comparisons,
                result1.swaps,
                duration1
        ));

        experimentResults.add(new SortExperimentResult(
                currentArray.length,
                "Бинарные вставки",
                result2.comparisons,
                result2.swaps,
                duration2
        ));

        // Обновляем график
        updateChart();
    }

    /**
     * Очистка всех данных приложения и графика
     */
    private void clearResults() {
        // Очищаем текущий массив
        currentArray = null;

        // Очищаем текстовую область результатов
        resultTextArea.setText("Все данные очищены. Вы можете сгенерировать или загрузить новый массив.");

        // Очищаем историю экспериментов
        experimentResults.clear();

        // Очищаем график
        chartPanel.removeAll();
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    /**
     * Обновление графика сравнения алгоритмов
     */
    private void updateChart() {
        // Очищаем панель графика если нет данных
        if (experimentResults.isEmpty()) {
            chartPanel.removeAll();
            chartPanel.revalidate();
            chartPanel.repaint();
            return;
        }

        // Создаем три набора данных для разных характеристик
        DefaultCategoryDataset comparisonDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset swapsDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset timeDataset = new DefaultCategoryDataset();

        // Заполняем наборы данных
        for (SortExperimentResult result : experimentResults) {
            String label = result.algorithm + " (n=" + NumberFormat.getNumberInstance().format(result.arraySize) + ")";
            comparisonDataset.addValue(result.comparisons, "Сравнения", label);
            swapsDataset.addValue(result.swaps, "Вставки", label);
            timeDataset.addValue(result.timeMs, "Время (мс)", label);
        }

        // Создаем три графика
        JFreeChart comparisonChart = createBarChart(comparisonDataset, "Количество сравнений");
        JFreeChart swapsChart = createBarChart(swapsDataset, "Количество вставок");
        JFreeChart timeChart = createBarChart(timeDataset, "Время выполнения (мс)");

        // Создаем панели для графиков
        ChartPanel comparisonChartPanel = new ChartPanel(comparisonChart);
        ChartPanel swapsChartPanel = new ChartPanel(swapsChart);
        ChartPanel timeChartPanel = new ChartPanel(timeChart);

        // Создаем панель с табами для графиков
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Сравнения", comparisonChartPanel);
        tabbedPane.addTab("Вставки", swapsChartPanel);
        tabbedPane.addTab("Время", timeChartPanel);

        // Обновляем панель с графиком
        chartPanel.removeAll();
        chartPanel.add(tabbedPane, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    /**
     * Создание столбчатой диаграммы
     * @param dataset набор данных
     * @param yAxisLabel подпись оси Y
     * @return объект диаграммы
     */
    private JFreeChart createBarChart(DefaultCategoryDataset dataset, String yAxisLabel) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Сравнение алгоритмов сортировки",
                "Алгоритм",
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Настраиваем внешний вид графика
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(true);
        renderer.setMaximumBarWidth(0.1);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.2);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // Добавляем форматирование чисел с разделителями
        rangeAxis.setNumberFormatOverride(NumberFormat.getNumberInstance());

        return chart;
    }

    /**
     * Класс для хранения результатов эксперимента сортировки
     */
    static class SortExperimentResult {
        int arraySize;
        String algorithm;
        long comparisons;
        long swaps;
        long timeMs;
        String experimentId; // Уникальный идентификатор эксперимента

        public SortExperimentResult(int arraySize, String algorithm, long comparisons, long swaps, long timeMs) {
            this.arraySize = arraySize;
            this.algorithm = algorithm;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.timeMs = timeMs;
            // Создаем идентификатор для эксперимента
            this.experimentId = "Exp_" + System.currentTimeMillis() % 10000;
        }
    }

    // Методы сортировки (на основе кода из InsertionSortComparison.java)

    /**
     * Сортировка простыми вставками
     * @param arr массив для сортировки
     * @return результат сортировки (количество сравнений и перестановок)
     */
    public static SortResult insertionSort(int[] arr) {
        long comparisons = 0;
        long swaps = 0;

        for (int i = 1; i < arr.length; i++) {
            int current = arr[i];
            int j = i - 1;

            // Сдвигаем все элементы вправо которые больше current
            while (j >= 0 && arr[j] > current) {
                comparisons++;
                arr[j + 1] = arr[j];
                swaps++;
                j--;
            }

            if (j >= 0) {
                comparisons++;
            }

            arr[j + 1] = current;
        }

        return new SortResult(comparisons, swaps);
    }

    /**
     * Сортировка бинарными вставками
     * @param arr массив для сортировки
     * @return результат сортировки (количество сравнений и перестановок)
     */
    public static SortResult binaryInsertionSort(int[] arr) {
        SortResult result = new SortResult(0, 0);
        for (int i = 1; i < arr.length; i++) {
            int current = arr[i];

            // Определяем позицию для вставки в подмассиве [0..i-1] используя метод бинарного поиска
            int position = binarySearchPosition(arr, current, 0, i-1, result);

            int j = i - 1;
            while (j >= position) {
                arr[j + 1] = arr[j];
                result.swaps++;
                j--;
            }

            arr[position] = current;
        }

        return result;
    }

    /**
     * Бинарный поиск позиции для вставки элемента
     * @param arr массив
     * @param value значение для вставки
     * @param left левая граница поиска
     * @param right правая граница поиска
     * @param result объект для хранения статистики
     * @return позиция для вставки
     */
    private static int binarySearchPosition(int[] arr, int value, int left, int right, SortResult result) {
        while (left <= right) {
            int mid = (left + right) / 2;
            result.comparisons++;

            if (value < arr[mid]) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    /**
     * Класс для хранения результатов сортировки
     */
    static class SortResult {
        long comparisons; // количество сравнений
        long swaps;       // количество перестановок/вставок

        public SortResult(long comparisons, long swaps) {
            this.comparisons = comparisons;
            this.swaps = swaps;
        }
    }

    /**
     * Главный метод приложения
     */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Ошибка при установке Look and Feel: " + e.getMessage());
        }

        // Запуск приложения в потоке обработки событий Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SortVisualizerApp();
            }
        });
    }
}