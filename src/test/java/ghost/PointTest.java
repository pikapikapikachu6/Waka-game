package ghost;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {
    Point point1;
    Point point2;
    @BeforeEach
    void setUp() {
        point1 = new Point(3,4);
        point2 = new Point(200,250);
    }

    @AfterEach
    void tearDown() {
        point2 = null;
        point1 = null;
    }

    @Test
    void testgetX() {
        assertEquals(point1.getX(),3);
        assertEquals(point2.getX(),200);
    }

    @Test
    void testgetY() {
        assertEquals(point1.getY(),4);
        assertEquals(point2.getY(),250);
    }

    @Test
    void testdistance() {
        assertEquals(point1.Distance(new Point(0,0),new Point(3,4)),5);
        assertEquals(point1.Distance(point1,point2),315.15869140625);
    }
}