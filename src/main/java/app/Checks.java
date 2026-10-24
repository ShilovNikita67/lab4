package app;

import exceptions.FormatException;
import exceptions.ParamException;
import geometry2d.Circle;
import geometry2d.Figure;
import geometry2d.Rectangle;
import geometry3d.Cylinder;
import storage.BinStorage;
import storage.Storage;
import storage.TextStorage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Checks {
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static int passed;
    private static int failed;

    public static void main(String[] args) throws Exception {
        Path data = Path.of(args.length > 0 ? args[0] : "data");
        Path tmp = Files.createTempDirectory("lab3-checks-");
        try {
            geometry();
            validation();
            finallyDemo();
            textReading(data);
            storageErrors(data, tmp);
            roundTrip(data, tmp);
            binaryProblems(tmp);
        } finally {
            try (Stream<Path> walk = Files.walk(tmp)) {
                walk.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            }
        }
        System.out.println();
        System.out.println("Итого: успешно " + passed + ", с ошибкой " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void geometry() {
        section("Вычисления");
        Circle c = new Circle(2);
        check("круг: площадь", near(c.getArea(), 4 * Math.PI));
        check("круг: периметр", near(c.getPerimeter(), 4 * Math.PI));
        Rectangle r = new Rectangle(3, 4);
        check("прямоугольник: площадь", near(r.getArea(), 12));
        check("прямоугольник: периметр", near(r.getPerimeter(), 14));
        check("цилиндр с кругом: объём", near(new Cylinder(new Circle(1), 2).volume(), 2 * Math.PI));
        check("цилиндр с прямоугольником: объём", near(new Cylinder(new Rectangle(2, 3), 4).volume(), 24));
        check("toString круга", c.toString().equals("Circle radius=2.0"));
    }

    private static void validation() {
        section("Проверка параметров");
        double[] bad = {0, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (double v : bad) {
            expectThrows("Circle(" + v + ")", ParamException.class, () -> new Circle(v));
            expectThrows("Rectangle(" + v + ", 1)", ParamException.class, () -> new Rectangle(v, 1));
            expectThrows("Rectangle(1, " + v + ")", ParamException.class, () -> new Rectangle(1, v));
            expectThrows("Cylinder(base, " + v + ")", ParamException.class,
                    () -> new Cylinder(new Circle(1), v));
        }
        Throwable t = expectThrows("Cylinder(null, 1)", ParamException.class,
                () -> new Cylinder(null, 1));
        check("сообщение с параметром и причиной",
                t != null && t.getMessage().contains("base") && t.getMessage().contains("null"));
    }

    static class EmptyTextException extends Exception {
        private static final long serialVersionUID = 1L;

        EmptyTextException(String message) {
            super(message);
        }
    }

    private static String normalize(String text) throws EmptyTextException {
        if (text == null) {
            throw new IllegalArgumentException("Текст не должен быть null");
        }
        if (text.isBlank()) {
            throw new EmptyTextException("Текст не должен быть пустым");
        }
        return text.strip();
    }

    private static void finallyDemo() {
        section("finally");
        String[] examples = {"  Java  ", "   ", null};
        int finallyRuns = 0;
        for (String text : examples) {
            try {
                System.out.println("    " + normalize(text));
            } catch (EmptyTextException e) {
                System.out.println("    " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("    " + e.getMessage());
            } finally {
                System.out.println("    Проверка завершена");
                finallyRuns++;
            }
        }
        check("finally выполнен 3 раза", finallyRuns == 3);
    }

    private static void textReading(Path data) throws Exception {
        section("Текстовое чтение");
        Storage text = new TextStorage();
        List<Figure> figures = text.read(data.resolve("figures.txt"));
        check("figures.txt: 4 фигуры",
                figures.equals(List.of(new Circle(1.5), new Rectangle(3, 4), new Circle(2.25), new Rectangle(0.5, 10))));
        check("empty.txt: пустой список", text.read(data.resolve("empty.txt")).isEmpty());

        formatError(text, data, "unknown-type.txt", 2, null);
        formatError(text, data, "wrong-count.txt", 2, null);
        formatError(text, data, "bad-number.txt", 3, NumberFormatException.class);
        formatError(text, data, "negative.txt", 2, ParamException.class);
    }

    private static void formatError(Storage s, Path data, String file, int line, Class<?> cause) {
        Throwable t = expectThrows(file, FormatException.class, () -> s.read(data.resolve(file)));
        if (t instanceof FormatException e) {
            System.out.println("      " + e.getMessage());
            check(file + ": строка " + line, e.getLineNumber() == line);
            if (cause != null) {
                check(file + ": причина " + cause.getSimpleName(), cause.isInstance(e.getCause()));
            }
        }
    }

    private static void storageErrors(Path data, Path tmp) throws Exception {
        section("Файловые ошибки");
        for (Storage s : storages()) {
            String name = s.getClass().getSimpleName();
            expectThrows(name + ": нет файла", IOException.class, () -> s.read(tmp.resolve("missing.dat")));
            expectThrows(name + ": запись в каталог", IOException.class, () -> s.write(tmp, List.of()));
            Path empty = tmp.resolve(name + "-empty.dat");
            try {
                s.write(empty, List.of());
                check(name + ": пустой список", s.read(empty).isEmpty());
            } catch (Exception e) {
                check(name + ": пустой список (" + e + ")", false);
            }
        }
        Path bad = tmp.resolve("bad-utf8.txt");
        Files.write(bad, new byte[]{'C', 'I', 'R', 'C', 'L', 'E', ';', (byte) 0xFF, (byte) 0xFE, '\n'});
        expectThrows("текст: неверная UTF-8", MalformedInputException.class,
                () -> new TextStorage().read(bad));
    }

    private static void roundTrip(Path data, Path tmp) throws Exception {
        section("Запись и чтение");
        List<Figure> original = new TextStorage().read(data.resolve("figures.txt"));
        for (Storage s : storages()) {
            String name = s.getClass().getSimpleName();
            Path file = tmp.resolve(name + "-roundtrip.dat");
            s.write(file, original);
            List<Figure> restored = s.read(file);
            check(name + ": списки равны", original.equals(restored));
            boolean same = restored.size() == original.size();
            for (int i = 0; same && i < original.size(); i++) {
                same = original.get(i).getClass() == restored.get(i).getClass()
                        && Double.compare(original.get(i).getArea(), restored.get(i).getArea()) == 0
                        && Double.compare(original.get(i).getPerimeter(), restored.get(i).getPerimeter()) == 0;
            }
            check(name + ": расчёты совпадают", same);
        }
    }

    private static void binaryProblems(Path tmp) throws Exception {
        section("Бинарные файлы");
        Storage bin = new BinStorage();

        Path garbage = tmp.resolve("garbage.bin");
        Files.write(garbage, new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        expectThrows("мусор", IOException.class, () -> bin.read(garbage));

        Path valid = tmp.resolve("valid.bin");
        bin.write(valid, List.of(new Circle(1), new Rectangle(2, 3)));
        byte[] bytes = Files.readAllBytes(valid);
        Path truncated = tmp.resolve("truncated.bin");
        Files.write(truncated, java.util.Arrays.copyOf(bytes, bytes.length / 2));
        expectThrows("обрезанный файл", IOException.class, () -> bin.read(truncated));

        Path emptyFile = tmp.resolve("zero.bin");
        Files.write(emptyFile, new byte[0]);
        expectThrows("файл нулевой длины", IOException.class, () -> bin.read(emptyFile));

        Path wrongType = tmp.resolve("string.bin");
        writeObject(wrongType, "просто строка");
        expectThrows("String вместо списка", IOException.class, () -> bin.read(wrongType));

        Path wrongElements = tmp.resolve("wrong-elements.bin");
        ArrayList<Object> mixed = new ArrayList<>();
        mixed.add(new Circle(1));
        mixed.add(new Cylinder(new Circle(1), 1));
        writeObject(wrongElements, mixed);
        expectThrows("Cylinder в списке", IOException.class, () -> bin.read(wrongElements));
    }

    private static void writeObject(Path path, Object o) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(o);
        }
    }

    private static Storage[] storages() {
        return new Storage[]{new TextStorage(), new BinStorage()};
    }

    private static boolean near(double a, double b) {
        return Math.abs(a - b) < 1e-9;
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
        } else {
            failed++;
        }
        System.out.println("  " + (ok ? "OK    " : "FAIL  ") + name);
    }

    private static Throwable expectThrows(String name, Class<? extends Throwable> type, ThrowingRunnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            boolean ok = type.isInstance(t);
            check(name + ": " + type.getSimpleName() + (ok ? "" : " (получено " + t + ")"), ok);
            return ok ? t : null;
        }
        check(name + ": ожидалось " + type.getSimpleName() + ", исключения нет", false);
        return null;
    }
}
