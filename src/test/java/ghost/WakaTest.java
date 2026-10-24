package ghost;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

import static org.junit.jupiter.api.Assertions.*;

class WakaTest {
    Waka waka;
    MapGrid mapGrid;
    PImage[] picture;
    PImage close_waka;
    PImage[] walls;
    PImage[] fruit;
    App app;
    @BeforeEach
    void setUp() {
        waka = new Waka (1, 3, picture, close_waka);
        mapGrid = new MapGrid("map.txt", walls, fruit);
        waka.initialLoc(mapGrid);
        waka.move();
    }

    @AfterEach
    void tearDown() {
        waka = null;
        mapGrid = null;
        picture = null;
        close_waka = null;
        walls = null;
        fruit = null;
    }

    @Test
    void testinitialLoc() {
        assertEquals(waka.current_position.x, 208.0);
        assertEquals(waka.current_position.y,320.0);
        assertEquals(waka.start_position.x, 208.0);
        assertEquals(waka.start_position.y,320.0);
        assertEquals(waka.total_fruit,301);
        assertEquals(waka.friented,false);
    }

    @Test
    void testsetDirToLeft() {
        waka.setDirToLeft();
        assertEquals(waka.direction, Direction.LEFT);
    }

    @Test
    void testsetDirToRight() {
        waka.setDirToRight();
        assertEquals(waka.direction, Direction.RIGHT);
    }

    @Test
    void testsetDirToUp() {
        waka.setDirToUp();
        assertEquals(waka.direction, Direction.UP);
    }

    @Test
    void testsetDirToDown() {
        waka.setDirToDown();
        assertEquals(waka.direction, Direction.DOWN);
    }

    @Test
    void testmove() {
        assertEquals(waka.canMove(new Point(14,1),1,3,Direction.UP,mapGrid), false);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.UP,mapGrid), false);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.DOWN,mapGrid), true);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.LEFT,mapGrid), true);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.RIGHT,mapGrid), true);
        assertEquals(waka.current_position.x,208);
        assertEquals(waka.current_position.y,320);
        assertEquals(waka.direction,Direction.UP);
        assertEquals(waka.mark,0);
        waka.direction = Direction.LEFT;
        waka.move();
        assertEquals(waka.mark,1);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.UP,mapGrid), false);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.DOWN,mapGrid), true);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.LEFT,mapGrid), true);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.RIGHT,mapGrid), true);
        assertEquals(waka.current_position.x,207);
        assertEquals(waka.current_position.y,320);
        assertEquals(waka.direction,Direction.LEFT);
        assertEquals(waka.mark,1);
        waka.current_position.x = 14;
        waka.current_position.y = 1;
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.UP,mapGrid), false);
        waka.direction = Direction.UP;
        waka.move();
        assertEquals(waka.mark,1);
        waka.direction = Direction.NODIR;
        waka.move();
        assertEquals(waka.mark,1);

        waka.current_position = new Point(14*16,14*16);
        waka.direction = Direction.UP;
        waka.move();
        assertEquals(waka.mark,1);
        waka.direction = Direction.DOWN;
        waka.move();
        assertEquals(waka.mark,1);
        waka.direction = Direction.LEFT;
        waka.move();
        assertEquals(waka.mark,2);
        waka.direction = Direction.RIGHT;
        waka.move();
        assertEquals(waka.mark,2);

        waka.current_position = new Point(14*16,14*16);
        waka.direction = Direction.UP;
        waka.move();
        assertEquals(waka.mark,2);
        waka.direction = Direction.DOWN;
        waka.move();
        assertEquals(waka.mark,2);
        waka.direction = Direction.LEFT;
        waka.move();
        assertEquals(waka.mark,2);
        waka.direction = Direction.RIGHT;
        waka.move();
        assertEquals(waka.mark,2);

        waka.current_position = new Point(12*16,14*16);
        waka.direction = Direction.UP;
        waka.move();
        assertEquals(waka.mark,3);

        waka.current_position = new Point(12*16,14*16);
        waka.direction = Direction.UP;
        waka.move();
        assertEquals(waka.mark,3);

        waka.current_position = new Point(1*16,4*16);
        waka.direction = Direction.DOWN;
        waka.move();
        assertEquals(waka.mark,4);

        waka.current_position = new Point(1*16,4*16);
        waka.direction = Direction.DOWN;
        waka.move();
        assertEquals(waka.mark,4);

        waka.current_position = new Point(1*16,4*16);
        waka.direction = Direction.RIGHT;
        waka.move();
        assertEquals(waka.mark,4);

        waka.current_position = new Point(1*16,32*16);
        waka.direction = Direction.RIGHT;
        waka.move();
        assertEquals(waka.mark,5);

    }

    @Test
    void testeat() {
        assertEquals(waka.eat(mapGrid,waka.current_position),0);
        waka.direction = Direction.LEFT;
        waka.move();
        assertEquals(mapGrid.getMap_grid_char(1,4),'0');
        assertEquals(mapGrid.getMap_grid_char(32,26),'8');
        assertEquals(waka.eat(mapGrid,waka.current_position),0);
        assertEquals(waka.eat(mapGrid,new Point(1*16,4*16)),1);
        assertEquals(waka.eat(mapGrid,new Point(3*16,32*16)),1);
        assertEquals(waka.eat(mapGrid,new Point(0*16,0*16)),0);
        assertEquals(waka.eat(mapGrid,new Point(3*16,32*16)),0);
        assertEquals(waka.eat(mapGrid,new Point(26*16,32*16)),1);
    }

    @Test
    void teststart_game() {
        assertEquals(waka.lives,3);
        waka.start_game();
        assertEquals(waka.lives,2);
        waka.start_game();
        assertEquals(waka.lives,1);
        waka.start_game();
        assertEquals(waka.lives,0);
        waka.start_game();
    }

    @Test
    void testdraw() {
        waka = new Waka (1, 3, picture, close_waka);
        mapGrid = new MapGrid("map.txt", walls, fruit);
        waka.initialLoc(mapGrid);

    }

    @Test
    void testcanMove() {
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.NODIR ,mapGrid), false);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.UP,mapGrid), false);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.DOWN,mapGrid), true);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.LEFT,mapGrid), true);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.RIGHT,mapGrid), true);
        assertEquals(waka.canMove(new Point(14,1),1,3,Direction.UP,mapGrid), false);
        waka = new Waka (1, 3, picture, close_waka);
        mapGrid = new MapGrid("map.txt", walls, fruit);
        waka.initialLoc(mapGrid);
        waka.current_position = new Point(0*16,3*16);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.DOWN,mapGrid), false);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.LEFT,mapGrid), false);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.RIGHT,mapGrid), false);
        waka.current_position = new Point(0,0);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.UP,mapGrid), false);
        waka.current_position = new Point(448,0);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.RIGHT,mapGrid), false);
        waka.current_position = new Point(0,576);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.LEFT,mapGrid), false);
        waka.current_position = new Point(0,576);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.DOWN,mapGrid), false);
        waka.current_position = new Point(447,0);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.RIGHT,mapGrid), false);
        waka.current_position = new Point(1,576);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.LEFT,mapGrid), false);
        waka.current_position = new Point(0,575);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.DOWN,mapGrid), false);
        waka.current_position = new Point(12*16,14*16);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.LEFT,mapGrid), true);
        waka.current_position = new Point(14*16,12*16);
        assertEquals(waka.canMove(waka.current_position,1,3,Direction.LEFT,mapGrid), false);
    }
}