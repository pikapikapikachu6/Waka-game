package ghost;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PImage;

import static org.junit.jupiter.api.Assertions.*;
class MapGridTest {

    MapGrid mapGrid;
    String map_address;
    PImage[] walls;
    PImage[] fruit;
    App app;

    @BeforeEach
    void setUp() {
        map_address = "map.txt";
        mapGrid = new MapGrid(map_address,walls,fruit);
    }

    @AfterEach
    void tearDown() {
        mapGrid = null;
    }

    @Test
    void testReadMap() {
        mapGrid.readMap("map.txt");
        assertFalse(mapGrid.getMap_grid() == null);
        mapGrid = new MapGrid(map_address,walls,fruit);
        mapGrid.readMap("map2.txt");
        assertFalse(mapGrid.getMap_grid() == null);
        mapGrid = new MapGrid(map_address,walls,fruit);
        mapGrid.readMap("map3.txt");
        assertFalse(mapGrid.getMap_grid() == null);

    }

    @Test
    void testDraw() {
        assertEquals(mapGrid.getMap_grid_char(0,0),'0');
    }

    @Test
    void testIsGeneral() {
        assertEquals(mapGrid.isGeneral(0,0), false);
        assertEquals(mapGrid.isGeneral(10,14), false);
        assertEquals(mapGrid.isGeneral(12,14), false);
        assertEquals(mapGrid.isGeneral(14,14), false);
        assertEquals(mapGrid.isGeneral(16,14), false);
        assertEquals(mapGrid.isGeneral(26,32), false);
        assertEquals(mapGrid.isGeneral(13,20), false);
        assertEquals(mapGrid.isGeneral(8,26), false);

    }

    @Test
    void testGetMap_grid() {
        assertEquals(mapGrid.getMap_grid()[0][0],'0');
        assertEquals(mapGrid.getMap_grid()[14][10], 'a');
        assertEquals(mapGrid.getMap_grid()[14][12], 'c');
        assertEquals(mapGrid.getMap_grid()[14][14], 'i');
        assertEquals(mapGrid.getMap_grid()[14][16], 'w');
        assertEquals(mapGrid.getMap_grid()[32][26], '8');
        assertEquals(mapGrid.getMap_grid()[20][13], 'p');
    }

    @Test
    void testGetMap_grid_char() {
        assertEquals(mapGrid.getMap_grid_char(0,0),'0');
        assertEquals(mapGrid.getMap_grid_char(14,10), 'a');
        assertEquals(mapGrid.getMap_grid_char(14,12), 'c');
        assertEquals(mapGrid.getMap_grid_char(14,14), 'i');
        assertEquals(mapGrid.getMap_grid_char(14,16), 'w');
        assertEquals(mapGrid.getMap_grid_char(32,26), '8');
        assertEquals(mapGrid.getMap_grid_char(20,13), 'p');

        assertEquals(mapGrid.getMap_grid_char(26,8), 'g');
    }

    @Test
    void testSetMap_grid_char() {
        mapGrid.setMap_grid_char(14,10);
        assertEquals(mapGrid.getMap_grid_char(14,10),'0');
    }
    @Test
    void testGetStart_waka() {
        assertEquals(mapGrid.getStart_waka().x,208);
        assertEquals(mapGrid.getStart_waka().y,320);
    }

    @Test
    void testGetStart_ghost_a() {
        assertEquals(mapGrid.getStart_ghost_a().size(),1);
        assertEquals(mapGrid.getStart_ghost_a().get(0).x,10*16);
        assertEquals(mapGrid.getStart_ghost_a().get(0).y,14*16);
    }

    @Test
    void testGetStart_ghost_c() {
        assertEquals(mapGrid.getStart_ghost_c().size(),1);
        assertEquals(mapGrid.getStart_ghost_c().get(0).x,12*16);
        assertEquals(mapGrid.getStart_ghost_c().get(0).y,14*16);
    }

    @Test
    void testGetStart_ghost_i() {
        assertEquals(mapGrid.getStart_ghost_i().size(),1);
        assertEquals(mapGrid.getStart_ghost_i().get(0).x,14*16);
        assertEquals(mapGrid.getStart_ghost_i().get(0).y,14*16);
    }

    @Test
    void testGetStart_ghost_w() {
        assertEquals(mapGrid.getStart_ghost_w().size(),1);
        assertEquals(mapGrid.getStart_ghost_w().get(0).x,16*16);
        assertEquals(mapGrid.getStart_ghost_w().get(0).y,14*16);
    }
}