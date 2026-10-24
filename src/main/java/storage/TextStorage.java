package storage;

import exceptions.FormatException;
import exceptions.ParamException;
import geometry2d.Circle;
import geometry2d.Figure;
import geometry2d.Rectangle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TextStorage implements Storage {
    private static final String SEPARATOR = ";";

    @Override
    public List<Figure> read(Path path) throws IOException, FormatException {
        List<Figure> figures = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String text = line.strip();
                if (text.isEmpty()) {
                    continue;
                }
                figures.add(parseLine(lineNumber, text));
            }
        }
        return figures;
    }

    @Override
    public void write(Path path, List<Figure> figures) throws IOException {
        List<String> lines = new ArrayList<>(figures.size());
        for (Figure figure : figures) {
            lines.add(format(figure));
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private static String format(Figure figure) {
        if (figure instanceof Circle c) {
            return "CIRCLE" + SEPARATOR + c.getRadius();
        }
        if (figure instanceof Rectangle r) {
            return "RECTANGLE" + SEPARATOR + r.getSideA() + SEPARATOR + r.getSideB();
        }
        throw new IllegalArgumentException("Неподдерживаемая фигура: " + figure);
    }

    private static Figure parseLine(int lineNumber, String text) throws FormatException {
        String[] parts = text.split(SEPARATOR, -1);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].strip();
        }
        String type = parts[0].toUpperCase(Locale.ROOT);
        try {
            switch (type) {
                case "CIRCLE":
                    requireCount(lineNumber, type, parts, 1);
                    return new Circle(parseNumber(lineNumber, "radius", parts[1]));
                case "RECTANGLE":
                    requireCount(lineNumber, type, parts, 2);
                    return new Rectangle(
                            parseNumber(lineNumber, "sideA", parts[1]),
                            parseNumber(lineNumber, "sideB", parts[2]));
                default:
                    throw new FormatException(lineNumber, "неизвестный тип фигуры '" + parts[0] + "'");
            }
        } catch (ParamException e) {
            throw new FormatException(lineNumber, e.getMessage(), e);
        }
    }

    private static void requireCount(int lineNumber, String type, String[] parts, int expected)
            throws FormatException {
        if (parts.length - 1 != expected) {
            throw new FormatException(lineNumber, "для " + type + " нужно параметров: " + expected
                    + ", найдено: " + (parts.length - 1));
        }
    }

    private static double parseNumber(int lineNumber, String name, String value) throws FormatException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new FormatException(lineNumber,
                    "параметр '" + name + "': '" + value + "' не является числом", e);
        }
    }
}
