package ghost;

import processing.core.PImage;

import static ghost.Direction.*;

/**
 * This class is used to create a object waka.This class used to create ghost object. It includes function of creating
 * a waka, moving the ghost and drawing the waka.
 * @author Shutong Li
 * @version 1.1
 */
public class Waka{
    private static final int GRIDSIZE = 16;
    private static final int OFFSET = 8;

    private int speed;
    public int lives;
    private PImage[] wakas;
    private PImage close_waka;
    private MapGrid mapGrid;
    public Point current_position;
    public Point start_position;
    public Direction direction = UP;
    public int mark = 0;
    public int score = 0;
    public int total_fruit;
    public boolean friented;
    public int frightenedDurationFrames = 20 * 60;
    public int frightenedFramesRemaining = 0;
    public int ghostsEatenThisFrightened = 0;
    public int speedBoostFrames = 0;
    public int ghostFreezeFrames = 0;

    /**
     * This method is waka class constructor method.
     * @param speed int
     * @param lives int
     * @param wakas PImage[]
     * @param close_waka PImage
     */
    public Waka(int speed, int lives, PImage[] wakas, PImage close_waka) {
        this.speed = speed;
        this.lives = lives;
        this.wakas = wakas;
        this.close_waka = close_waka;
        this.mark = 0;
    }

    /**
     * This method is used to set the waka into original state.
     * @param mapGrid MapGrid
     */
    public void initialLoc(MapGrid mapGrid) {
        this.mapGrid = mapGrid;
        //x = 20 y = 13
        // x hang y lie
        this.start_position = new Point(mapGrid.getStart_waka().getX(),mapGrid.getStart_waka().getY());
        this.current_position = new Point(mapGrid.getStart_waka().getX(),mapGrid.getStart_waka().getY());
        this.total_fruit = mapGrid.total_fruit;
        this.friented = false;
        this.frightenedFramesRemaining = 0;
        this.ghostsEatenThisFrightened = 0;
        this.speedBoostFrames = 0;
        this.ghostFreezeFrames = 0;
    }

    /**
     * This method is used to change the waka dierction with LEFT.
     */
    public void setDirToLeft() {
        this.direction = LEFT;
        move();
    }

    /**
     * This method is used to change the waka dierction with RIGHT.
     */
    public void setDirToRight() {
        this.direction = RIGHT;
        move();
    }

    /**
     * This method is used to change the waka dierction with UP.
     */
    public void setDirToUp() {
        this.direction = UP;
        move();
    }

    /**
     * This method is used to change the waka dierction with DOWN.
     */
    public void setDirToDown() {
        this.direction = DOWN;
        move();
    }

    /**
     * This method is used to move the waka.
     */
    public void move() {
        if (canMove(this.current_position, this.speed, this.lives, this.direction, this.mapGrid)) {
            switch (this.direction) {
                case UP:
                    this.current_position.y -= this.speed;
                    if (eat(this.mapGrid, this.current_position) == 1) mark += 1;
                    break;
                case DOWN:
                    this.current_position.y += this.speed;
                    if (eat(this.mapGrid, this.current_position) == 1) mark += 1;
                    break;
                case LEFT:
                    this.current_position.x -= this.speed;
                    if (eat(this.mapGrid, this.current_position) == 1) mark += 1;
                    break;
                case RIGHT:
                    this.current_position.x += this.speed;
                    if (eat(this.mapGrid, this.current_position) == 1) mark += 1;
                    break;
            }
        }
    }

    /**
     * This method is used to check which direction the waka can move.
     * @param current_position Point
     * @param speed int
     * @param lives int
     * @param direction Direction
     * @param mapGrid MapGrid
     * @return true/false boolean
     */
    public boolean canMove(Point current_position, int speed, int lives, Direction direction, MapGrid mapGrid) {
        float next_x = current_position.getX();
        float next_y = current_position.getY();
        switch (direction) {
            case RIGHT:
                next_x = next_x + speed + OFFSET;
                if (current_position.getX() < App.WIDTH && next_x <= App.WIDTH) {
                    return !mapGrid.isGeneral((int) next_x / GRIDSIZE, (int) next_y / GRIDSIZE);
                }
                break;
            case LEFT:
                next_x = next_x - speed - OFFSET;
                if (current_position.getX() > 0 && next_x >= 0) {
                    return !mapGrid.isGeneral((int) next_x / GRIDSIZE, (int) next_y / GRIDSIZE);
                }
                break;
            case UP:
                next_y = next_y - speed - OFFSET;
                if (current_position.getY() > 0 && next_y >= 0) {
                    return !mapGrid.isGeneral((int) next_x / GRIDSIZE, (int) next_y / GRIDSIZE);
                }
                break;
            case DOWN:
                next_y = next_y + speed + OFFSET;
                if (current_position.getY() < App.HEIGHT && next_y <= App.HEIGHT) {
                    return !mapGrid.isGeneral((int) next_x / GRIDSIZE, (int) next_y / GRIDSIZE);
                }
                break;
        }
        return false;
    }

    /**
     * This method is used to test the waka whether eat a fruit in the current position.
     * @param mapGrid MapGrid
     * @param current_position Point
     * @return 0 if empty, 1 for a regular pellet, 2 for a power fruit
     */
    public int eat(MapGrid mapGrid, Point current_position) {
        if (mapGrid.getMap_grid_char((int)current_position.getY()/GRIDSIZE, (int)current_position.getX()/GRIDSIZE) == '.') {
            mapGrid.setMap_grid_char((int)current_position.getY()/GRIDSIZE, (int)current_position.getX()/GRIDSIZE);
            this.score += 10;
            return 1;
        }
        if (mapGrid.getMap_grid_char((int)current_position.getY()/GRIDSIZE, (int)current_position.getX()/GRIDSIZE) == '*') {
            mapGrid.setMap_grid_char((int)current_position.getY()/GRIDSIZE, (int)current_position.getX()/GRIDSIZE);
            this.friented = true;
            this.frightenedFramesRemaining = this.frightenedDurationFrames;
            this.ghostsEatenThisFrightened = 0;
            this.score += 50;
            return 2;
        }
        char tile = mapGrid.getMap_grid_char((int)current_position.getY()/GRIDSIZE, (int)current_position.getX()/GRIDSIZE);
        if (tile == '!') {
            mapGrid.setMap_grid_char((int)current_position.getY()/GRIDSIZE, (int)current_position.getX()/GRIDSIZE);
            speedBoostFrames = 8 * 60;
            speed = Math.min(speed + 2, 8);
            score += 100;
            mark++;
            return 1;
        }
        if (tile == '~') {
            mapGrid.setMap_grid_char((int)current_position.getY()/GRIDSIZE, (int)current_position.getX()/GRIDSIZE);
            score += 100;
            ghostFreezeFrames = 4 * 60;
            mark++;
            return 1;
        }
        return 0;
    }

    public void tickBonusTimers(int baseSpeed) {
        if (speedBoostFrames > 0 && --speedBoostFrames == 0) speed = baseSpeed;
    }

    public void tickFrightenedTimer() {
        if (!friented) return;
        if (frightenedFramesRemaining > 0) frightenedFramesRemaining--;
        if (frightenedFramesRemaining == 0) friented = false;
    }

    /**
     * Ater the waka died, this method is set the waka to the original state.
     */
    public void start_game() {
        if (this.lives > 0) {
            this.lives -= 1;
            this.current_position = new Point(this.start_position.getX(),this.start_position.getY());
        }
    }

    /**
     * This method is used to draw waka on the map and show.
     * @param app App
     * @return true/false boolean
     */
    public boolean draw(App app) {
        if (app.current_time % 16 < 8) {
            switch (this.direction) {
                case LEFT:
                    app.image(wakas[0], current_position.getX() - OFFSET,current_position.getY()- OFFSET);
                    break;
                case RIGHT:
                    app.image(wakas[1],current_position.getX() - OFFSET,current_position.getY() - OFFSET);
                    break;
                case UP:
                    app.image(wakas[2], current_position.getX() - OFFSET,current_position.getY()- OFFSET);
                    break;
                case DOWN:
                    app.image(wakas[3], current_position.getX() - OFFSET,current_position.getY() - OFFSET);
                    break;
            }
            return true;
        } else {
            switch (this.direction) {
                case LEFT:
                    app.image(close_waka, current_position.getX()- OFFSET,current_position.getY()- OFFSET);
                    break;
                case RIGHT:
                    app.image(close_waka, current_position.getX() - OFFSET,current_position.getY() - OFFSET);
                    break;
                case UP:
                    app.image(close_waka, current_position.getX()- OFFSET,current_position.getY()- OFFSET);
                    break;
                case DOWN:
                    app.image(close_waka, current_position.getX() - OFFSET,current_position.getY() - OFFSET);
                    break;
            }
            return true;
        }
    }
}
