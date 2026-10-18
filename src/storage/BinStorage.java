package storage;

import geometry2d.Figure;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BinStorage implements Storage {
    @Override
    public List<Figure> read(Path path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
            Object object = in.readObject();
            if (!(object instanceof List<?> list)) {
                throw new InvalidObjectException("В файле лежит не список, а " + describe(object));
            }
            List<Figure> figures = new ArrayList<>(list.size());
            for (Object element : list) {
                if (!(element instanceof Figure figure)) {
                    throw new InvalidObjectException("Элемент списка не является фигурой: " + describe(element));
                }
                figures.add(figure);
            }
            return figures;
        }
    }

    @Override
    public void write(Path path, List<Figure> figures) throws IOException {
        ArrayList<Figure> copy = new ArrayList<>(figures);
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))) {
            out.writeObject(copy);
        }
    }

    private static String describe(Object object) {
        return object == null ? "null" : object.getClass().getName();
    }
}
