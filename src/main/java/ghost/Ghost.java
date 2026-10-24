package ghost;

import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

import static ghost.Ghost_type.Die;

/**
 * Stored all the ghost types
 */
enum Ghost_type {
    Amubusher,
    Chaser,
    Ignorant,
    Whim,
    Die
}

/**
 * This class used to create ghost object. It includes function of creating a ghost, moving the ghost and drawing the ghost.
 * @author Shutong Li
 * @version 1.1
 */

public class Ghost {
    private static final int OFFSET = 8;
    private static final int GRIDSIZE = 16;
    public static final int WIDTH = 448;
    public static final int HEIGHT = 576;
    private static final Point TOPRIGHT = new Point(448,0);
    private static final Point TOPLEFT = new Point(0,0);
    private static final Point BOTTOMRIGHT = new Point(448,576);
    private static final Point BOTTOMLEFT = new Point(0,576);
    private PImage[] ghosts;
    public Point start_position;
    public Point current_position;
    public Point target;
    public int speed;
    public Direction direction;
    public List<Modes> modes = new ArrayList<Modes>();
    public Ghost_type ghost_type;
    private MapGrid mapGrid;

    /**
     * Class Ghost constructor method. Used to create a Ghost class.
     * @param ghosts PImage[]
     * @param ghost_type Ghost_type
     */
    public Ghost(PImage[] ghosts, Ghost_type ghost_type) {
        this.ghost_type = ghost_type;
        this.ghosts = ghosts;
    }

    /**
     * This method used to initialize the ghost class and update the original state.
     * @param point_ghost Poing
     * @param con_file ConfigProcessing
     * @param mapGrid MapGrid
     */
    public void initialLoc(Point point_ghost, ConfigProcessing con_file, MapGrid mapGrid) {
        this.mapGrid = mapGrid;
        this.start_position = point_ghost;
        this.current_position = point_ghost;
        this.speed = con_file.getSpeed();
        this.modes = importModes(con_file.getIterators());
    }

    /**
     * This method used to import Mode list into ghost class.
     * @param iterators List
     * @return this.modes List
     */
    public List<Modes> importModes(List<Integer> iterators) {
        for (int i = 0; i < iterators.size(); i ++) {
            int now = iterators.get(i);
            M_TYPE current_type;
            if (i % 2 == 0) {
                current_type = M_TYPE.SCATTER;
            } else {
                current_type = M_TYPE.CHASE;
            }
            Modes new_mode = new Modes(current_type, now);
            this.modes.add(new_mode);
        }
        return this.modes;
    }

    /**
     * This method used to updata the mode list.
     * @param modes List
     * @return this.modes List
     */
    public List<Modes> updateList(List<Modes> modes) {
        Modes current = modes.get(0);
        current.leftTime -= 1;
        int length = modes.size();
        if (current.leftTime <= 0) {
            for (int i = 0; i < length - 1; i ++) {
                modes.set(i, modes.get(i + 1));
            }
            modes.set(length - 1, current);
        }
        return this.modes;
    }

    /**
     * This method used to check in the given direction, the ghost whethercan move or not.
     * @param current_position Point
     * @param speed int
     * @param direction Direction
     * @param mapGrid MapGrid
     * @return true/false boolean
     */
    public boolean canMove(Point current_position, int speed, Direction direction, MapGrid mapGrid) {
        float next_x = current_position.getX(); // lie
        float next_y = current_position.getY(); // hang

        switch (direction) {
            case RIGHT:
                next_x = next_x + 16;
                if (next_x > 448) return false;
                    else return next_x < 448 && !mapGrid.isGeneral((int)next_x/GRIDSIZE,(int)next_y/GRIDSIZE);
            case LEFT:
                next_x = next_x - 16 ;
                if (next_x < 0) return false;
                    else return next_x > 0 && !mapGrid.isGeneral((int)next_x/GRIDSIZE,(int)next_y/GRIDSIZE);
            case UP:
                next_y = next_y - 16 ;
                if (next_y < 0) return false;
                    else return next_y > 0 && !mapGrid.isGeneral((int)next_x/GRIDSIZE,(int)next_y/GRIDSIZE);
            case DOWN:
                next_y = next_y + 16;
                if (next_y > 576) return false;
                    else return next_y < 576 && !mapGrid.isGeneral((int)next_x/GRIDSIZE,(int)next_y/GRIDSIZE);
        }
        return false;
    }

    /**
     * This method is used to check the current position whether is a intersection or not.
     * @param direction Direction
     * @param LeftTurn boolean
     * @param RightTurn boolean
     * @param UpTurn boolean
     * @param DownTurn boolean
     * @return true/false boolean
     */
    public boolean Intersection(Direction direction, boolean LeftTurn, boolean RightTurn, boolean UpTurn, boolean DownTurn) {
        if (this.current_position.getX() % 16 != 0) return false;
        if (this.current_position.getY() % 16 != 0) return false;
        switch (direction) {
            case DOWN:
                if (LeftTurn) return true;
                if (RightTurn) return true;
                break;
            case UP:
                if (LeftTurn) return true;
                if (RightTurn) return true;
                break;
            case LEFT:
                if (UpTurn) return true;
                if (DownTurn) return true;
                break;
            case RIGHT:
                if (UpTurn) return true;
                if (DownTurn) return true;
                break;
        }
        return false;
    }

    /**
     * This method is used to find current target of this.ghost.
     * @param waka Waka
     * @param modes Node
     * @param ghosts List
     * @param cur_ghost Ghost
     * @return this.target Point
     */
    public Point FindTarget(Waka waka, Modes modes, List<Ghost>ghosts, Ghost cur_ghost) {
            Point result = waka.current_position;
            Ghost_type cur_type = cur_ghost.ghost_type;
            switch (modes.current_type) {
                case SCATTER:
                    switch (cur_type) {
                        case Amubusher:
                            result = TOPRIGHT;
                            break;
                        case Chaser:
                            result = TOPLEFT;
                            break;
                        case Ignorant:
                            result = BOTTOMLEFT;
                            break;
                        case Whim:
                            result = BOTTOMRIGHT;
                            break;
                    }
                    break;
                case CHASE:
                    switch (cur_type) {
                        case Amubusher:
                            Direction waka_dir = waka.direction;
                            Point waka_poi = waka.current_position;
                            if (waka_dir == Direction.UP) result = new Point(waka_poi.getX(), waka_poi.getY() - 4);
                            if (waka_dir == Direction.DOWN) result = new Point(waka_poi.getX(), waka_poi.getY() + 4);
                            if (waka_dir == Direction.LEFT) result = new Point(waka_poi.getX() - 4, waka_poi.getY());
                            if (waka_dir == Direction.RIGHT) result = new Point(waka_poi.getX() + 4, waka_poi.getY());
                            break;
                        case Chaser:
                            result = waka.current_position;
                            break;
                        case Ignorant:
                            float cur_dis = current_position.Distance(cur_ghost.current_position, waka.current_position);
                            if (cur_dis > 8) result = waka.current_position;
                            else result = BOTTOMLEFT;
                            break;
                        case Whim:
                            Direction w_dir = waka.direction;
                            Point w_poi = waka.current_position;
                            Point chaser_cur = new Point(0, 0);
                            for (int i = 0; i < ghosts.size(); i++) {
                                Ghost cur = ghosts.get(i);
                                Ghost_type ty = cur.ghost_type;
                                if (ty == Ghost_type.Chaser) {
                                    chaser_cur = cur.current_position;
                                }
                            }
                            if (w_dir == Direction.UP) w_poi = new Point(2 * w_poi.getX(), 2 * (w_poi.getY() - 2));
                            if (w_dir == Direction.DOWN) w_poi = new Point(2 * w_poi.getX(), 2 * (w_poi.getY() + 2));
                            if (w_dir == Direction.LEFT) w_poi = new Point(2 * (w_poi.getX() - 2), 2 * w_poi.getY());
                            if (w_dir == Direction.RIGHT) w_poi = new Point(2 * (w_poi.getX() + 2), 2 * w_poi.getY());
                            result = new Point(w_poi.getX() - chaser_cur.getX(), w_poi.getY() - chaser_cur.getY());
                            break;
                    }
                    break;
            }
            return result;
        }

    /**
     * This method used to find the newest direction of this ghost.
     * @param ghost Pont
     * @param target Point
     * @param direction Direction
     * @return this.direction Direction
     */
    public Direction new_direction(Point ghost, Point target, Direction direction) {
            // up down left right
            Direction result = direction;
            double distance[] = new double[4];

            distance[0] = ghost.Distance(new Point(ghost.getX(),ghost.getY()-speed),target);
            distance[1] = ghost.Distance(new Point(ghost.getX(),ghost.getY()+speed),target);
            distance[2] = ghost.Distance(new Point(ghost.getX()-speed,ghost.getY()),target);
            distance[3] = ghost.Distance(new Point(ghost.getX()+speed,ghost.getY()),target);

            if (direction == Direction.UP) distance[1] = Integer.MAX_VALUE;
            if (direction == Direction.DOWN) distance[0] = Integer.MAX_VALUE;
            if (direction == Direction.LEFT) distance[3] = Integer.MAX_VALUE;
            if (direction == Direction.RIGHT) distance[2] = Integer.MAX_VALUE;
            if (canMove(ghost,this.speed,Direction.UP,this.mapGrid) == false) distance[0] = Integer.MAX_VALUE;
            if (canMove(ghost,this.speed,Direction.DOWN,this.mapGrid) == false) distance[1] = Integer.MAX_VALUE;
            if (canMove(ghost,this.speed,Direction.LEFT,this.mapGrid) == false) distance[2] = Integer.MAX_VALUE;
            if (canMove(ghost,this.speed,Direction.RIGHT,this.mapGrid) == false) distance[3] = Integer.MAX_VALUE;
            double min_value = Integer.MAX_VALUE;

            for (int i = 0; i < 4; i ++) { ;
                if (distance[i] < min_value) {
                    min_value = distance[i];
                    switch (i) {
                        case 0:
                            result = Direction.UP;
                            break;
                        case 1:
                            result = Direction.DOWN;
                            break;
                        case 2:
                            result = Direction.LEFT;
                            break;
                        case 3:
                            result = Direction.RIGHT;
                            break;
                    }
                }
            }
            this.direction = result;
            return result;
        }

    /**
     * This method used to move ghost.
     * @param waka Waka
     * @param ghosts List
     */
    public void move(Waka waka, List<Ghost> ghosts) {
        boolean turn_UP = canMove(this.current_position, this.speed, Direction.UP,  this.mapGrid);
        boolean turn_DOWN = canMove(this.current_position, this.speed, Direction.DOWN,  this.mapGrid);
        boolean turn_LEFT = canMove(this.current_position, this.speed, Direction.LEFT,  this.mapGrid);
        boolean turn_RIGHT = canMove(this.current_position, this.speed, Direction.RIGHT,  this.mapGrid);

        boolean is_inter;
        if (this.direction == null) is_inter = true;
            else  is_inter = Intersection(this.direction, turn_LEFT, turn_RIGHT, turn_UP, turn_DOWN);

        if (is_inter == true) {

            if (waka.friented == true) {
                final long l = System.currentTimeMillis();
                final int i = (int)( l % 4 );
                switch (i) {
                    case 0:
                        if (this.direction != Direction.DOWN && turn_UP == true) this.direction = Direction.UP;
                            else if (turn_DOWN) this.direction = Direction.DOWN;
                                else if (turn_LEFT) this.direction = Direction.LEFT;
                                    else if (turn_RIGHT) this.direction = Direction.RIGHT;
                                        else this.direction = Direction.UP;
                        break;
                    case 1:
                        if (this.direction != Direction.UP && turn_DOWN == true) this.direction = Direction.DOWN;
                            else if (turn_UP) this.direction = Direction.UP;
                                else if (turn_LEFT) this.direction = Direction.LEFT;
                                    else if (turn_RIGHT) this.direction = Direction.RIGHT;
                                        else this.direction = Direction.DOWN;
                        break;
                    case 2:
                        if (this.direction != Direction.RIGHT && turn_LEFT == true) this.direction = Direction.LEFT;
                            else if (turn_RIGHT) this.direction = Direction.RIGHT;
                                else if (turn_UP) this.direction = Direction.UP;
                                    else if (turn_DOWN) this.direction = Direction.DOWN;
                                        else this.direction = Direction.LEFT;
                        break;
                    case 3:
                        if (this.direction != Direction.LEFT && turn_RIGHT == true) this.direction = Direction.RIGHT;
                            else if (turn_LEFT) this.direction = Direction.LEFT;
                                else if (turn_UP) this.direction = Direction.UP;
                                    else if (turn_DOWN) this.direction = Direction.DOWN;
                                        else this.direction = Direction.RIGHT;
                        break;
                }
            } else {
                Point cur_target = FindTarget(waka, this.modes.get(0), ghosts, this);
                this.direction = new_direction(this.current_position, cur_target, this.direction);
                this.target = cur_target;
            }
        }
        switch (this.direction) {
            case RIGHT:
                this.current_position = new Point(current_position.getX()+this.speed,current_position.getY());
                break;
            case LEFT:
                this.current_position = new Point(current_position.getX()-this.speed,current_position.getY());
                break;
            case UP:
                this.current_position = new Point(current_position.getX(),current_position.getY()-this.speed);
                break;
            case DOWN:
                this.current_position = new Point(current_position.getX(),current_position.getY()+this.speed);
                break;
        }
    }

    /**
     * This method is used to draw ghost on the map.
     * @param app App
     * @param waka Waka
     */
    public void draw(App app, Waka waka) {
        draw(app, waka, true);
    }

    public void draw(App app, Waka waka, boolean advanceModes) {
        if (waka.friented == true) {
            if (this.ghost_type != Die) {
                app.image(ghosts[4], this.current_position.getX() - OFFSET, current_position.getY() - OFFSET);
            }
        } else {
            switch (this.ghost_type) {
                case Whim:
                    app.image(ghosts[3], this.current_position.getX() - OFFSET, current_position.getY() - OFFSET);
                    break;
                case Ignorant:
                    app.image(ghosts[2], this.current_position.getX() - OFFSET, current_position.getY() - OFFSET);
                    break;
                case Chaser:
                    app.image(ghosts[1], this.current_position.getX() - OFFSET, current_position.getY() - OFFSET);
                    break;
                case Amubusher:
                    app.image(ghosts[0], this.current_position.getX() - OFFSET, current_position.getY() - OFFSET);
                    break;
            }
        }
        if (advanceModes) updateList(this.modes);
    }

    /**
     * This method used to check whether the ghost eat the waka.
     * @param waka Waka
     * @param ghost Ghpst
     * @return true/false boolean
     */
    public boolean eat_waka(Waka waka, Ghost ghost) {
        if (ghost.ghost_type == Die) return false;
        if ((int) waka.current_position.getX() / 16 == (int) ghost.current_position.getX() / 16) {
            if ((int) waka.current_position.getY() / 16 == (int) ghost.current_position.getY() / 16) {
                if (waka.friented == false) {
                    waka.start_game();
                    return true;
                } else {
                    this.ghost_type = Die;
                    int comboMultiplier = Math.min(waka.ghostsEatenThisFrightened, 3);
                    waka.score += 200 * (1 << comboMultiplier);
                    waka.ghostsEatenThisFrightened++;
                }
            }
        }
        return false;
    }

}
