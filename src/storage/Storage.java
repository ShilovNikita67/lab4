package storage;

import exceptions.FormatException;
import geometry2d.Figure;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Storage {
    List<Figure> read(Path path) throws IOException, FormatException, ClassNotFoundException;

    void write(Path path, List<Figure> figures) throws IOException;
}
