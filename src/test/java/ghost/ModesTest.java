package ghost;

import org.checkerframework.common.value.qual.StaticallyExecutable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModesTest {
    Modes mode1;
    Modes mode2;
    @BeforeEach
    void setUp() {
        mode1 = new Modes(M_TYPE.SCATTER, 7);
        mode2 = new Modes(M_TYPE.CHASE, 20);
    }

    @AfterEach
    void tearDown() {
        mode1 = null;
        mode2 = null;
    }

    @Test
    void testType() {
        assertEquals(mode1.current_type,M_TYPE.SCATTER);
        assertEquals(mode2.current_type,M_TYPE.CHASE);
    }

    @Test
    void testTime() {
        assertEquals(mode1.totalTime,420);
        assertEquals(mode2.totalTime,1200);
        assertEquals(mode1.leftTime,420);
        assertEquals(mode2.leftTime,1200);
    }
}