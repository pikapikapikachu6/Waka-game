package ghost;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConfigProcessingTest {
    ConfigProcessing configProcessing;

    @Test
    void readJsonFile() {
        configProcessing.readJsonFile();
    }

    @Test
    void getMap_address() {
        configProcessing.readJsonFile();
        String address = configProcessing.getMap_address();
    }

    @Test
    void getLives() {
        configProcessing.readJsonFile();
        int lives = configProcessing.getLives();
    }

    @Test
    void getSpeed() {
        configProcessing.readJsonFile();
        int speed = configProcessing.getSpeed();
    }

    @Test
    void getIterator() {
        configProcessing.readJsonFile();
        Iterator<Long> longIterator = configProcessing.getIterator();
    }

    @Test
    void getIterators() {
        configProcessing.readJsonFile();
        List<Integer> longIterators = configProcessing.getIterators();
    }

    @Test
    void getFrightenedLength() {
        configProcessing.readJsonFile();
        int length = configProcessing.getFrightenedLength();
    }

    @BeforeEach
    /**
     * Create a new configProcessing.
     */
    void setUp() {
        configProcessing = new ConfigProcessing();
    }

    @AfterEach
    /**
     * Release the configProcessing.
     */
    void tearDown() {
        configProcessing = null;
    }

    @Test
    /**
     * Test the method "readJsonFile". Test it can run readJsonFile().
     */
    void testReadJsonFile() {
        configProcessing.readJsonFile();
    }

    @Test
    /**
     * Test the method "getMap_address". Check the return value of map address is equal with file value.
     */
    void testGetMap_address() {
        configProcessing.readJsonFile();
        assertEquals(configProcessing.getMap_address(),"map.txt");
    }

    @Test
    /**
     * Test the method "getLives". Check the return value of lives is equal with file value.
     */
    void testGetLives() {
        configProcessing.readJsonFile();
        assertEquals(configProcessing.getLives(),3);
    }

    @Test
    /**
     * Test the method "getSpeed". Check the return value of speed is equal with file value.
     */
    void testGetSpeed() {
        configProcessing.readJsonFile();
        assertEquals(configProcessing.getSpeed(),1);
    }

    @Test
    /**
     * Test the method "getIterator". Check it will return things.
     */
    void testGetIterator() {
        configProcessing.readJsonFile();
        assertFalse(configProcessing.getIterator() == null);
    }

    @Test
    /**
     * Test the method "getIterators". Check everyone in the list is equal with the file value.
     */
    void testGetIterators() {
        configProcessing.readJsonFile();
        assertEquals(configProcessing.getIterators().size(),8);
        assertEquals(configProcessing.getIterators().get(0),7);
        assertEquals(configProcessing.getIterators().get(1),20);
        assertEquals(configProcessing.getIterators().get(2),7);
        assertEquals(configProcessing.getIterators().get(3),20);
        assertEquals(configProcessing.getIterators().get(4),5);
        assertEquals(configProcessing.getIterators().get(5),20);
        assertEquals(configProcessing.getIterators().get(6),5);
        assertEquals(configProcessing.getIterators().get(7),1000);
    }

    @Test
    /**
     * Test the method "getFrightenedLength". Check the number of frientenedlength is correct.
     */
    void testGetFrightenedLength() {
        configProcessing.readJsonFile();
        assertEquals(configProcessing.getFrightenedLength(),20);
    }
}