package geometry2d;

import exceptions.ParamException;

import java.util.Objects;

public class Rectangle implements Figure {
    private static final long serialVersionUID = 1L;

    private final double sideA;
    private final double sideB;

    public Rectangle(double sideA, double sideB) {
        this.sideA = ParamException.requirePositiveFinite("sideA", sideA);
        this.sideB = ParamException.requirePositiveFinite("sideB", sideB);
    }

    public double getSideA() {
        return sideA;
    }

    public double getSideB() {
        return sideB;
    }

    @Override
    public double getArea() {
        return sideA * sideB;
    }

    @Override
    public double getPerimeter() {
        return 2 * (sideA + sideB);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Rectangle other
                && Double.compare(sideA, other.sideA) == 0
                && Double.compare(sideB, other.sideB) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sideA, sideB);
    }

    @Override
    public String toString() {
        return "Rectangle sideA=" + sideA + " sideB=" + sideB;
    }
}
