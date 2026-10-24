package geometry3d;

import exceptions.ParamException;
import geometry2d.Figure;

import java.io.Serializable;
import java.util.Objects;

public class Cylinder implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Figure base;
    private final double height;

    public Cylinder(Figure base, double height) {
        if (base == null) {
            throw new ParamException("base", "основание не должно быть null");
        }
        this.base = base;
        this.height = ParamException.requirePositiveFinite("height", height);
    }

    public Figure getBase() {
        return base;
    }

    public double getHeight() {
        return height;
    }

    public double volume() {
        return base.getArea() * height;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Cylinder other
                && base.equals(other.base)
                && Double.compare(height, other.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, height);
    }

    @Override
    public String toString() {
        return "Cylinder height=" + height + " base=" + base;
    }
}
