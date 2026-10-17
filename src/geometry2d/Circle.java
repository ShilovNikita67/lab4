package geometry2d;

import exceptions.ParamException;

import java.util.Objects;

public class Circle implements Figure {
    private static final long serialVersionUID = 1L;

    private final double radius;

    public Circle(double radius) {
        this.radius = ParamException.requirePositiveFinite("radius", radius);
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    @Override
    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Circle other && Double.compare(radius, other.radius) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(radius);
    }

    @Override
    public String toString() {
        return "Circle radius=" + radius;
    }
}
