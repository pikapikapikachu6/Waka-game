package ghost;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

import static ghost.M_TYPE.CHASE;
import static ghost.M_TYPE.SCATTER;
import static org.junit.jupiter.api.Assertions.*;

class GhostTest {
    Ghost ghost;
    Waka waka;
    MapGrid mapGrid;
    PImage[] ghosts;
    Ghost_type ghost_type;
    ConfigProcessing con_file;
    String map_address;
    PImage[] walls;
    PImage[] fruit;
    List<Ghost> ghostsList;
    PImage[] picture;
    PImage close_waka;
    App app;

    @BeforeEach
    void setUp() {
        ghost_type = Ghost_type.Amubusher;
        ghost = new Ghost(ghosts, ghost_type);
    }

    @AfterEach
    void tearDown() {
        ghost = null;
        ghost_type = null;
    }


    @Test
    void testInitialLoc() {
        map_address = "map.txt";
        con_file = new ConfigProcessing();
        con_file.readJsonFile();
        Point point_ghost = new Point(14,10);
        mapGrid = new MapGrid(map_address, walls, fruit);
        ghost.initialLoc(point_ghost, con_file, mapGrid);
        assertEquals(ghost.ghost_type,Ghost_type.Amubusher);
        assertEquals(ghost.current_position.x,14);
        assertEquals(ghost.current_position.y,10);
        assertEquals(ghost.start_position.x,14);
        assertEquals(ghost.start_position.y,10);
        assertEquals(ghost.speed,1);
        assertEquals(ghost.modes.size(),8);
        assertEquals(ghost.frient_time,20);
        assertEquals(ghost.left_frient,20);
    }

    @Test
    void testImportModes() {
        con_file = new ConfigProcessing();
        con_file.readJsonFile();
        ghost.importModes(con_file.getIterators());
        assertEquals(ghost.modes.size(),8);
    }

    @Test
    void testUpdateList() {
        map_address = "map.txt";
        con_file = new ConfigProcessing();
        con_file.readJsonFile();
        Point point_ghost = new Point(14,10);
        mapGrid = new MapGrid(map_address, walls, fruit);
        ghost.initialLoc(point_ghost, con_file, mapGrid);
        ghost.modes = ghost.updateList(ghost.modes);
        assertEquals(ghost.modes.get(0).current_type, SCATTER);
        assertEquals(ghost.modes.get(0).leftTime, 419);
        for (int i = 0; i < 420; i ++) {
            ghost.modes = ghost.updateList(ghost.modes);
        }
        assertEquals(ghost.modes.get(0).current_type, CHASE);
    }

    @Test
    void testIntersection() {
        map_address = "map.txt";
        con_file = new ConfigProcessing();
        con_file.readJsonFile();
        Point point_ghost = new Point(14,10);
        mapGrid = new MapGrid(map_address, walls, fruit);
        ghost.initialLoc(point_ghost, con_file, mapGrid);
        ghost.direction = Direction.UP;
        ghost.current_position.x = 160;
        ghost.current_position.y = 159;
        assertEquals(ghost.Intersection(Direction.UP, true, false, true, true), false);
        ghost.current_position.x = 159;
        ghost.current_position.y = 160;
        assertEquals(ghost.Intersection(Direction.UP, true, false, true, true), false);
        ghost.current_position.x = 160;
        ghost.current_position.y = 160;
        ghost.direction = Direction.LEFT;
        assertEquals(ghost.Intersection(Direction.LEFT, true, false, false, true), true);
        assertEquals(ghost.Intersection(Direction.LEFT, true, false, true, false), true);
        ghost.direction = Direction.RIGHT;
        assertEquals(ghost.Intersection(Direction.RIGHT, true, true, false, true), true);
        assertEquals(ghost.Intersection(Direction.RIGHT, true, true, true, false), true);
        ghost.direction = Direction.UP;
        assertEquals(ghost.Intersection(Direction.UP, false, true, true, true), true);
        assertEquals(ghost.Intersection(Direction.UP, true, false, true, true), true);
        ghost.direction = Direction.DOWN;
        assertEquals(ghost.Intersection(Direction.DOWN, false, true, true, true), true);
        assertEquals(ghost.Intersection(Direction.DOWN, true, false, true, true), true);
        assertEquals(ghost.Intersection(Direction.DOWN, false, false, true, true), false);
        ghost.direction = Direction.NODIR;
        assertEquals(ghost.Intersection(Direction.NODIR, false, true, true, true), false);
        assertEquals(ghost.Intersection(Direction.NODIR, true, false, true, true), false);
        assertEquals(ghost.Intersection(Direction.NODIR, false, false, true, true), false);

    }

    @Test
    void testMove() {
        ghostsList = new ArrayList<Ghost>();
        ghostsList.add(new Ghost(ghosts, Ghost_type.Amubusher));
        ghostsList.add(new Ghost(ghosts, Ghost_type.Whim));
        ghostsList.add(new Ghost(ghosts, Ghost_type.Ignorant));
        ghostsList.add(new Ghost(ghosts, Ghost_type.Chaser));
        map_address = "map.txt";
        con_file = new ConfigProcessing();
        con_file.readJsonFile();

        waka = new Waka (1, 3, picture, close_waka);
        Point point_ghost = new Point(14,10);
        mapGrid = new MapGrid(map_address, walls, fruit);
        ghost.initialLoc(point_ghost, con_file, mapGrid);
        ghost.current_position.x = 8 * 16;
        ghost.current_position.y = 1 * 16;
        ghost.direction = Direction.UP;
        waka.friented = false;
        ghost.move(waka, ghostsList);
        assertEquals(ghost.current_position.x,129);
        assertEquals(ghost.current_position.y,16);
        waka.friented = true;
        ghost.move(waka, ghostsList);
        assertEquals(ghost.current_position.x,130);
        assertEquals(ghost.current_position.y,16);

        ghost.ghost_type = Ghost_type.Die;
        ghost.move(waka, ghostsList);

        ghost.ghost_type = Ghost_type.Amubusher;
        ghost.left_frient = 20;
        waka.friented = true;
        ghost.current_position.x = 26 * 16;
        ghost.current_position.y = 4 * 16;
        ghost.direction = Direction.DOWN;
        ghost.move(waka, ghostsList);

        ghost.ghost_type = Ghost_type.Chaser;
        ghost.left_frient = 20;
        waka.friented = true;
        ghost.current_position.x = 4 * 16;
        ghost.current_position.y = 26 * 16;
        ghost.direction = Direction.DOWN;
        ghost.move(waka, ghostsList);

        ghost.ghost_type = Ghost_type.Ignorant;
        ghost.left_frient = 20;
        waka.friented = true;
        ghost.current_position.x = 4 * 16;
        ghost.current_position.y = 26 * 16;
        ghost.direction = Direction.RIGHT;
        ghost.move(waka, ghostsList);

        ghost.ghost_type = Ghost_type.Whim;
        ghost.left_frient = 20;
        waka.friented = false;
        ghost.current_position.x = 4 * 16;
        ghost.current_position.y = 26 * 16;
        ghost.direction = Direction.UP;
        ghost.move(waka, ghostsList);

        ghost.left_frient = 20;
        waka.friented = false;
        ghost.current_position.x = 4 * 16;
        ghost.current_position.y = 26 * 16;
        ghost.direction = Direction.DOWN;
        ghost.move(waka, ghostsList);

        ghost.left_frient = 20;
        waka.friented = false;
        ghost.current_position.x = 1 * 16;
        ghost.current_position.y = 7 * 16;
        ghost.direction = Direction.UP;
        ghost.move(waka, ghostsList);

        ghost.left_frient = 1;
        waka.friented = true;
        ghost.current_position.x = 4 * 16;
        ghost.current_position.y = 26 * 16;
        ghost.direction = Direction.DOWN;
        ghost.move(waka, ghostsList);

        ghost.left_frient = 2;
        waka.friented = true;
        ghost.current_position.x = 1 * 16;
        ghost.current_position.y = 7 * 16;
        ghost.direction = Direction.UP;
        ghost.move(waka, ghostsList);

        ghost.left_frient = 20;
        waka.friented = false;
        ghost.current_position.x = 1 * 16;
        ghost.current_position.y = 7 * 16;
        ghost.direction = Direction.DOWN;
        ghost.move(waka, ghostsList);

        waka.direction = Direction.NODIR;
        ghost.move(waka, ghostsList);

    }

    @Test
    void testDraw() {
        map_address = "map.txt";
        con_file = new ConfigProcessing();
        con_file.readJsonFile();
        Point point_ghost = new Point(14,10);
        mapGrid = new MapGrid(map_address, walls, fruit);
        ghost.initialLoc(point_ghost, con_file, mapGrid);
        waka = new Waka (1, 3, picture, close_waka);
        waka.initialLoc(mapGrid);
        waka.friented = true;
        app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup();
        waka.friented = false;
        app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup();


    }

    @Test
    void testEat_waka() {
        map_address = "map.txt";
        con_file = new ConfigProcessing();
        con_file.readJsonFile();
        Point point_ghost = new Point(14,10);
        mapGrid = new MapGrid(map_address, walls, fruit);
        ghost.initialLoc(point_ghost, con_file, mapGrid);
        waka = new Waka (1, 3, picture, close_waka);
        waka.initialLoc(mapGrid);
        waka.current_position = new Point(14*16,14*16);
        ghost.current_position = new Point(14*16,14*16);
        assertEquals(ghost.eat_waka(waka,ghost),true);


        waka.current_position = new Point(14*16,14*16);
        ghost.current_position = new Point(20*16,20*16);
        assertEquals(ghost.eat_waka(waka,ghost),false);

        waka.friented = true;
        waka.current_position = new Point(14*16,14*16);
        ghost.current_position = new Point(14*16,14*16);
        assertEquals(ghost.eat_waka(waka,ghost),false);
        assertEquals(ghost.ghost_type, Ghost_type.Die);
        assertEquals(ghost.eat_waka(waka,ghost),false);

        ghost.ghost_type = Ghost_type.Amubusher;
        waka.friented = false;
        assertEquals(ghost.eat_waka(waka,ghost),true);
    }

    @Test
    void testFindTarget() {
        map_address = "map.txt";
        con_file = new ConfigProcessing();
        con_file.readJsonFile();
        Point point_ghost = new Point(14,10);
        mapGrid = new MapGrid(map_address, walls, fruit);
        ghost.initialLoc(point_ghost, con_file, mapGrid);
        waka = new Waka (1, 3, picture, close_waka);
        waka.initialLoc(mapGrid);

        ghostsList = new ArrayList<Ghost>();
        ghostsList.add(new Ghost(ghosts, Ghost_type.Chaser));
        ghostsList.get(0).current_position = new Point(5*16,5*16);
        ghostsList.add(new Ghost(ghosts, Ghost_type.Whim));

        ghostsList.get(1).current_position = new Point(10*16,12*16);
        ghostsList.add(new Ghost(ghosts, Ghost_type.Ignorant));

        ghostsList.get(2).current_position = new Point(2*16,20*16);
        ghostsList.add(new Ghost(ghosts, Ghost_type.Chaser));

        ghostsList.get(3).current_position = new Point(27*16,20*16);

        Modes mode = new Modes(SCATTER,10);
        ghost.ghost_type = Ghost_type.Chaser;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,new Point(0,0).x);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,new Point(0,0).y);

        mode = new Modes(SCATTER,10);
        ghost.ghost_type = Ghost_type.Ignorant;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,new Point(0,576).x);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,new Point(0,576).y);

        mode = new Modes(SCATTER,10);
        ghost.ghost_type = Ghost_type.Whim;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,new Point(448,576).x);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,new Point(448,576).y);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Amubusher;
        waka.direction = Direction.UP;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,208);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,316);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Amubusher;
        waka.direction = Direction.DOWN;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,208);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,324);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Amubusher;
        waka.direction = Direction.LEFT;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,204);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,320);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Amubusher;
        waka.direction = Direction.RIGHT;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,212);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,320);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Chaser;
        waka.direction = Direction.UP;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,208);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,320);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Chaser;
        waka.direction = Direction.DOWN;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,208);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,320);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Chaser;
        waka.direction = Direction.LEFT;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,208);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,320);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Chaser;
        waka.direction = Direction.RIGHT;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,208);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,320);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Ignorant;
        waka.direction = Direction.RIGHT;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,208);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,320);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Whim;
        waka.direction = Direction.RIGHT;
        waka.current_position = new Point(14*16,14*16);
        ghost.current_position = new Point(20*16,20*16);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,20);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,128);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Ignorant;
        waka.direction = Direction.RIGHT;
        waka.current_position = new Point(0*16,0*16);
        ghost.current_position = new Point(0*16,0*16);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,0);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,576);

        mode = new Modes(SCATTER,10);
        ghost.ghost_type = Ghost_type.Whim;
        waka.current_position = new Point(14*16,14*16);
        ghost.current_position = new Point(20*16,20*16);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,448);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,576);

        mode = new Modes(SCATTER,10);
        ghost.ghost_type = Ghost_type.Die;
        waka.current_position = new Point(14*16,14*16);
        ghost.current_position = new Point(20*16,20*16);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,224);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,224);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Die;
        waka.current_position = new Point(14*16,14*16);
        ghost.current_position = new Point(20*16,20*16);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,224);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,224);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Whim;
        waka.direction = Direction.UP;
        waka.current_position = new Point(14*16,14*16);
        ghost.current_position = new Point(20*16,20*16);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,16);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,124);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Whim;
        waka.direction = Direction.DOWN;
        waka.current_position = new Point(14*16,14*16);
        ghost.current_position = new Point(20*16,20*16);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,16);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,132);

        mode = new Modes(CHASE,10);
        ghost.ghost_type = Ghost_type.Whim;
        waka.direction = Direction.LEFT;
        waka.current_position = new Point(14*16,14*16);
        ghost.current_position = new Point(20*16,20*16);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,12);
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).y,128);

        mode.current_type = CHASE;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,12);

        mode.current_type = SCATTER;
        assertEquals(ghost.FindTarget(waka,mode,ghostsList,ghost).x,448);


    }

    @Test
    void testNew_direction() {
        map_address = "map.txt";
        con_file = new ConfigProcessing();
        con_file.readJsonFile();
        Point point_ghost = new Point(14,10);
        mapGrid = new MapGrid(map_address, walls, fruit);
        ghost.initialLoc(point_ghost, con_file, mapGrid);

        ghost.current_position.x = 1*16;
        ghost.current_position.y = 6*16;
        ghost.target = new Point(1*16,5*16);
        ghost.direction = Direction.UP;
        ghost.new_direction(ghost.current_position,ghost.target,ghost.direction);
        assertEquals(ghost.direction, Direction.UP);

        ghost.current_position.x = 1*16;
        ghost.current_position.y = 5*16;
        ghost.target = new Point(1*16,6*16);
        ghost.direction = Direction.DOWN;
        ghost.new_direction(ghost.current_position,ghost.target,ghost.direction);
        assertEquals(ghost.direction, Direction.DOWN);

        ghost.current_position.x = 1*16;
        ghost.current_position.y = 4*16;
        ghost.target = new Point(2*16,4*16);
        ghost.direction = Direction.RIGHT;
        ghost.new_direction(ghost.current_position,ghost.target,ghost.direction);
        assertEquals(ghost.direction, Direction.RIGHT);

        ghost.current_position.x = 2*16;
        ghost.current_position.y = 4*16;
        ghost.target = new Point(1*16,4*16);
        ghost.direction = Direction.LEFT;
        ghost.new_direction(ghost.current_position,ghost.target,ghost.direction);
        assertEquals(ghost.direction, Direction.LEFT);
    }

    @Test
    void testCanMove() {
        map_address = "map.txt";
        con_file = new ConfigProcessing();
        con_file.readJsonFile();
        Point point_ghost = new Point(14,10);
        mapGrid = new MapGrid(map_address, walls, fruit);
        ghost.initialLoc(point_ghost, con_file, mapGrid);
        assertEquals(ghost.canMove(ghost.current_position, ghost.speed,  Direction.UP,  mapGrid),false);
        assertEquals(ghost.canMove(ghost.current_position, ghost.speed,  Direction.DOWN,  mapGrid),true);
        assertEquals(ghost.canMove(ghost.current_position, ghost.speed,  Direction.LEFT,  mapGrid),false);
        assertEquals(ghost.canMove(ghost.current_position, ghost.speed,  Direction.RIGHT,  mapGrid),true);
        ghost.current_position.x = 1*16;
        ghost.current_position.y = 5*16;
        assertEquals(ghost.canMove(ghost.current_position, ghost.speed,  Direction.UP,  mapGrid),true);
        ghost.current_position.x = 3*16;
        ghost.current_position.y = 4*16;
        assertEquals(ghost.canMove(ghost.current_position, ghost.speed,  Direction.LEFT,  mapGrid),true);
        ghost.current_position.x = 8*16;
        ghost.current_position.y = 10*16;
        assertEquals(ghost.canMove(ghost.current_position, ghost.speed,  Direction.NODIR,  mapGrid),false);
        ghost.current_position.x = 448;
        ghost.current_position.y = 576;
        assertEquals(ghost.canMove(ghost.current_position, ghost.speed,  Direction.RIGHT,  mapGrid),false);
        assertEquals(ghost.canMove(ghost.current_position, ghost.speed,  Direction.DOWN,  mapGrid),false);
        ghost.current_position.x = 448 - 16;
        ghost.current_position.y = 576 - 16;
        assertEquals(ghost.canMove(ghost.current_position, ghost.speed,  Direction.RIGHT,  mapGrid),false);
        assertEquals(ghost.canMove(ghost.current_position, ghost.speed,  Direction.DOWN,  mapGrid),false);

    }

}