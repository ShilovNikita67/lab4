package geometry3d;

import exceptions.ParamException;
import geometry2d.Circle;
import geometry2d.Figure;
import geometry2d.Rectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CylinderTest {
    private static final double EPS = 1e-9;

    @Test
    void volumeWithCircleBase() {
        assertEquals(37.69911184307752, new Cylinder(new Circle(2.0), 3.0).volume(), EPS);
        assertEquals(1.5707963267948966, new Cylinder(new Circle(1.0), 0.5).volume(), EPS);
    }

    @Test
    void volumeWithRectangleBase() {
        assertEquals(60.0, new Cylinder(new Rectangle(3.0, 4.0), 5.0).volume(), EPS);
        assertEquals(10.0, new Cylinder(new Rectangle(0.5, 10.0), 2.0).volume(), EPS);
    }

    @Test
    void validCylinderIsCreated() {
        assertDoesNotThrow(() -> new Cylinder(new Circle(1.0), 1.0));
    }

    @Test
    void gettersReturnConstructorValues() {
        Figure base = new Rectangle(3.0, 4.0);
        Cylinder c = new Cylinder(base, 5.0);
        assertSame(base, c.getBase());
        assertEquals(5.0, c.getHeight(), EPS);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, Double.NaN,
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void invalidHeightThrowsParamException(double bad) {
        ParamException e = assertThrows(ParamException.class,
                () -> new Cylinder(new Circle(1.0), bad));
        assertEquals("height", e.getParameter());
    }

    @Test
    void nullBaseThrowsParamException() {
        ParamException e = assertThrows(ParamException.class, () -> new Cylinder(null, 5.0));
        assertEquals("base", e.getParameter());
    }

    @Test
    void toStringContainsTypeHeightAndBase() {
        String s = new Cylinder(new Circle(1.5), 5.0).toString();
        assertTrue(s.contains("Cylinder"));
        assertTrue(s.contains("5.0"));
        assertTrue(s.contains("Circle"));
        assertTrue(s.contains("1.5"));
    }

    @Test
    void equalCylindersAreEqualAndHaveSameHashCode() {
        Cylinder a = new Cylinder(new Circle(2.0), 3.0);
        Cylinder b = new Cylinder(new Circle(2.0), 3.0);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void differentCylindersAreNotEqual() {
        Cylinder a = new Cylinder(new Circle(2.0), 3.0);
        assertNotEquals(a, new Cylinder(new Circle(2.0), 4.0));
        assertNotEquals(a, new Cylinder(new Circle(3.0), 3.0));
        assertNotEquals(a, new Cylinder(new Rectangle(2.0, 2.0), 3.0));
        assertFalse(a.equals(null));
    }
}