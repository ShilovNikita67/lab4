package app;

import exceptions.FormatException;
import exceptions.ParamException;
import geometry2d.Figure;
import geometry3d.Cylinder;
import storage.BinStorage;
import storage.Storage;
import storage.TextStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        int code;
        try (Scanner in = new Scanner(System.in, StandardCharsets.UTF_8)) {
            code = run(args, in);
        }
        if (code != 0) {
            System.exit(code);
        }
    }

    private static int run(String[] args, Scanner in) {
        Path input = Path.of(args.length > 0 ? args[0] : ask(in, "Входной текстовый файл: "));
        Path output = Path.of(args.length > 1 ? args[1] : ask(in, "Выходной бинарный файл: "));

        Storage textStorage = new TextStorage();
        Storage binaryStorage = new BinStorage();

        List<Figure> figures;
        try {
            figures = textStorage.read(input);
        } catch (FormatException e) {
            System.err.println("Ошибка в файле " + input + ": " + e.getMessage());
            return 1;
        } catch (IOException e) {
            System.err.println("Не удалось прочитать файл " + input + ": " + e);
            return 1;
        } catch (ClassNotFoundException e) {
            System.err.println("Класс не найден: " + e.getMessage());
            return 1;
        }

        System.out.println("Прочитано фигур: " + figures.size());
        for (int i = 0; i < figures.size(); i++) {
            Figure figure = figures.get(i);
            System.out.println(String.format(Locale.ROOT, "%d. %s, площадь = %.4f, периметр = %.4f",
                    i + 1, figure, figure.getArea(), figure.getPerimeter()));
        }

        if (figures.isEmpty()) {
            System.out.println("Список пуст: цилиндр не создаётся.");
        } else {
            Figure base = figures.get(ThreadLocalRandom.current().nextInt(figures.size()));
            String heightText = args.length > 2 ? args[2] : ask(in, "Высота цилиндра: ");
            try {
                Cylinder cylinder = new Cylinder(base, Double.parseDouble(heightText.strip()));
                System.out.println("Основание: " + cylinder.getBase());
                System.out.println("Высота: " + cylinder.getHeight());
                System.out.println(String.format(Locale.ROOT, "Объём цилиндра: %.4f", cylinder.volume()));
            } catch (NumberFormatException e) {
                System.err.println("Высота '" + heightText + "' не является числом, цилиндр не создан.");
            } catch (ParamException e) {
                System.err.println("Цилиндр не создан. " + e.getMessage());
            }
        }

        try {
            binaryStorage.write(output, figures);
            System.out.println("Список сохранён в " + output);
        } catch (IOException e) {
            System.err.println("Не удалось записать файл " + output + ": " + e);
            return 1;
        }
        return 0;
    }

    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().strip() : "";
    }
}
