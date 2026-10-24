package geometry2d;

import exceptions.ParamException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CircleTest {
    private static final double EPS = 1e-9;

    @ParameterizedTest
    @CsvSource({
            "1.0, 3.141592653589793",
            "2.0, 12.566370614359172",
            "0.5, 0.7853981633974483"
    })
    void areaForGivenRadius(double radius, double expected) {
        assertEquals(expected, new Circle(radius).getArea(), EPS);
    }

    @ParameterizedTest
    @CsvSource({
            "1.0, 6.283185307179586",
            "2.0, 12.566370614359172",
            "0.5, 3.141592653589793"
    })
    void perimeterForGivenRadius(double radius, double expected) {
        assertEquals(expected, new Circle(radius).getPerimeter(), EPS);
    }

    @Test
    void validRadiusIsStoredAndReturnedByGetter() {
        assertEquals(1.5, new Circle(1.5).getRadius(), EPS);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, Double.NaN,
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void invalidRadiusThrowsParamException(double radius) {
        ParamException e = assertThrows(ParamException.class, () -> new Circle(radius));
        assertEquals("radius", e.getParameter());
    }

    @Test
    void toStringContainsTypeAndRadius() {
        String s = new Circle(1.5).toString();
        assertTrue(s.contains("Circle"));
        assertTrue(s.contains("1.5"));
    }

    @Test
    void equalCirclesAreEqualAndHaveSameHashCode() {
        Circle a = new Circle(2.0);
        Circle b = new Circle(2.0);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void differentCirclesAreNotEqual() {
        Circle a = new Circle(2.0);
        assertNotEquals(a, new Circle(3.0));
        assertFalse(a.equals(null));
        assertFalse(a.equals("Circle radius=2.0"));
    }
}