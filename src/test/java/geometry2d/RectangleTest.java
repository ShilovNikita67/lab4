package geometry2d;

import exceptions.ParamException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class RectangleTest {
    private static final double EPS = 1e-9;

    @Test
    void areaOfThreeByFourRectangleIsTwelve() {
        assertEquals(12.0, new Rectangle(3.0, 4.0).getArea(), EPS);
    }

    @Test
    void perimeterOfThreeByFourRectangleIsFourteen() {
        assertEquals(14.0, new Rectangle(3.0, 4.0).getPerimeter(), EPS);
    }

    @ParameterizedTest
    @CsvSource({
            "3.0, 3.0, 9.0",
            "0.5, 10.0, 5.0",
            "10.0, 3.0, 30.0"
    })
    void areaForGivenSides(double a, double b, double expected) {
        assertEquals(expected, new Rectangle(a, b).getArea(), EPS);
    }

    @ParameterizedTest
    @CsvSource({
            "3.0, 3.0, 12.0",
            "0.5, 10.0, 21.0",
            "10.0, 3.0, 26.0"
    })
    void perimeterForGivenSides(double a, double b, double expected) {
        assertEquals(expected, new Rectangle(a, b).getPerimeter(), EPS);
    }

    @Test
    void gettersReturnConstructorValues() {
        Rectangle r = new Rectangle(3.0, 4.5);
        assertEquals(3.0, r.getSideA(), EPS);
        assertEquals(4.5, r.getSideB(), EPS);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, Double.NaN,
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void invalidSideAThrowsParamException(double bad) {
        ParamException e = assertThrows(ParamException.class, () -> new Rectangle(bad, 4.0));
        assertEquals("sideA", e.getParameter());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, Double.NaN,
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void invalidSideBThrowsParamException(double bad) {
        ParamException e = assertThrows(ParamException.class, () -> new Rectangle(3.0, bad));
        assertEquals("sideB", e.getParameter());
    }

    @Test
    void toStringContainsTypeAndSides() {
        String s = new Rectangle(3.0, 4.0).toString();
        assertTrue(s.contains("Rectangle"));
        assertTrue(s.contains("3.0"));
        assertTrue(s.contains("4.0"));
    }

    @Test
    void equalRectanglesAreEqualAndHaveSameHashCode() {
        Rectangle a = new Rectangle(3.0, 4.0);
        Rectangle b = new Rectangle(3.0, 4.0);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void differentRectanglesAreNotEqual() {
        Rectangle a = new Rectangle(3.0, 4.0);
        assertNotEquals(a, new Rectangle(4.0, 3.0));
        assertNotEquals(a, new Rectangle(3.0, 5.0));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Circle(3.0)));
    }
}