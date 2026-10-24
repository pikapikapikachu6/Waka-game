package ghost;

import processing.core.PImage;
import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

/**
 * This class is used to read map file. It includes the function of set the corresponding word into map and draw the map
 * things.
 * @author Shutong Li
 * @version 1.1
 */
public class MapGrid{
    private static final char PELLET = '.';
    private static final char POWER_FRUIT = '*';
    private static final int POWER_FRUIT_RESPAWN_FRAMES = 60 * 12;
    public static final int WIDTH = 28;
    public static final int HEIGHT = 36;
    public static final int GRIDSIZE = 16;
    private static final int[][] WALL_THEME_COLORS = {
            {8, 22, 68, 22, 76, 238}, {46, 18, 12, 240, 113, 45},
            {8, 42, 45, 30, 205, 183}, {38, 20, 58, 170, 90, 235}
    };

    private PImage[] walls;
    private PImage[] fruit;
    private char[][] map_grid = new char[HEIGHT][WIDTH];
    private Point start_waka;
    private ArrayList<Point> start_ghost_a = new ArrayList<Point>();
    private ArrayList<Point> start_ghost_c = new ArrayList<Point>();
    private ArrayList<Point> start_ghost_i = new ArrayList<Point>();
    private ArrayList<Point> start_ghost_w = new ArrayList<Point>();
    public int total_fruit = 0;
    private int powerFruitCount = 0;
    private int powerFruitRespawnTimer = -1;
    private int themeIndex;

    /**
     * This method is used as the class constructor method.
     * @param map_address String
     * @param walls PImage[]
     * @param fruit PImage[]
     */
    public MapGrid(String map_address, PImage[] walls, PImage[] fruit) {
        this.walls = walls;
        this.fruit = fruit;
        this.total_fruit = 0;
        readMap(map_address);
        placeBonusItems(new Random());
    }

    public MapGrid(String[] map_rows, PImage[] walls, PImage[] fruit) {
        this.walls = walls;
        this.fruit = fruit;
        readMap(map_rows);
        placeBonusItems(new Random());
    }

    public void setThemeIndex(int themeIndex) { this.themeIndex = Math.floorMod(themeIndex, 4); }

    private void placeBonusItems(Random random) {
        ArrayList<Integer> candidates = new ArrayList<>();
        for (int row = 1; row < HEIGHT - 1; row++) {
            for (int column = 1; column < WIDTH - 1; column++) {
                if (map_grid[row][column] == '.') candidates.add(row * WIDTH + column);
            }
        }
        java.util.Collections.shuffle(candidates, random);
        // Replace pellets so bonus items do not change the level's pellet goal.
        int placed = 0;
        for (int cell : candidates) {
            int row = cell / WIDTH, column = cell % WIDTH;
            if (placed == 0) map_grid[row][column] = '!'; // speed boost
            else if (placed == 1) map_grid[row][column] = '~'; // freeze ghosts
            else break;
            placed++;
        }
    }

    /**
     * This method is used to read the map file and store the contents into a char array.
     * @param address String
     */
    public void readMap(String address) {
        try {
            String pathname = address;
            File filename = new File(pathname);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader in = new BufferedReader(reader);
            String str;
            ArrayList<String> rows = new ArrayList<>();
            while ((str = in.readLine()) != null) {
                rows.add(str);
            }
            readMap(rows.toArray(new String[0]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMap(String[] rows) {
        for (int row = 0; row < Math.min(rows.length, HEIGHT); row++) {
            String line = rows[row];
            for (int column = 0; column < Math.min(line.length(), WIDTH); column++) {
                char current = line.charAt(column);
                if (current == 'p') this.start_waka = new Point(column * GRIDSIZE, row * GRIDSIZE);
                if (current == 'a') this.start_ghost_a.add(new Point(column * GRIDSIZE, row * GRIDSIZE));
                if (current == 'c') this.start_ghost_c.add(new Point(column * GRIDSIZE, row * GRIDSIZE));
                if (current == 'i') this.start_ghost_i.add(new Point(column * GRIDSIZE, row * GRIDSIZE));
                if (current == 'w') this.start_ghost_w.add(new Point(column * GRIDSIZE, row * GRIDSIZE));
                if (current == PELLET) this.total_fruit++;
                if (current == POWER_FRUIT) this.powerFruitCount++;
                map_grid[row][column] = current;
            }
        }
    }

    /** Spawns a replacement power fruit 12 seconds after the last one is eaten. */
    public void updatePowerFruitRespawn(Waka waka) {
        if (powerFruitCount > 0 || powerFruitRespawnTimer < 0) return;
        if (--powerFruitRespawnTimer > 0) return;

        int wakaColumn = (int) waka.current_position.getX() / GRIDSIZE;
        int wakaRow = (int) waka.current_position.getY() / GRIDSIZE;
        int[][] pathDistance = findReachableCells(wakaRow, wakaColumn);
        ArrayList<Point> candidates = new ArrayList<>();
        // Select only empty tiles connected to Waka through non-wall tiles.
        for (int row = 1; row < HEIGHT - 1; row++) {
            for (int column = 1; column < WIDTH - 1; column++) {
                if (map_grid[row][column] == '0' && pathDistance[row][column] >= 8) {
                    candidates.add(new Point(column, row));
                }
            }
        }
        if (candidates.isEmpty()) {
            powerFruitRespawnTimer = 60;
            return;
        }

        Point location = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        map_grid[(int) location.getY()][(int) location.getX()] = POWER_FRUIT;
        powerFruitCount = 1;
        powerFruitRespawnTimer = -1;
    }

    private int[][] findReachableCells(int startRow, int startColumn) {
        int[][] distance = new int[HEIGHT][WIDTH];
        for (int row = 0; row < HEIGHT; row++) {
            java.util.Arrays.fill(distance[row], -1);
        }

        if (!isInsideMap(startRow, startColumn)) return distance;

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        distance[startRow][startColumn] = 0;
        queue.add(startRow * WIDTH + startColumn);
        int[] rowStep = {-1, 1, 0, 0};
        int[] columnStep = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            int cell = queue.removeFirst();
            int row = cell / WIDTH;
            int column = cell % WIDTH;
            for (int direction = 0; direction < rowStep.length; direction++) {
                int nextRow = row + rowStep[direction];
                int nextColumn = column + columnStep[direction];
                if (isInsideMap(nextRow, nextColumn)
                        && distance[nextRow][nextColumn] < 0
                        && isWalkableTile(map_grid[nextRow][nextColumn])) {
                    distance[nextRow][nextColumn] = distance[row][column] + 1;
                    queue.add(nextRow * WIDTH + nextColumn);
                }
            }
        }
        return distance;
    }

    private boolean isInsideMap(int row, int column) {
        return row >= 0 && row < HEIGHT && column >= 0 && column < WIDTH;
    }

    private boolean isWalkableTile(char tile) {
        return tile == '0' || tile == PELLET || tile == POWER_FRUIT
                || tile == '!' || tile == '~'
                || tile == 'p' || tile == 'a' || tile == 'c' || tile == 'i'
                || tile == 'w' || tile == 'g';
    }

    /**
     * This method is used to print the map.
     * @param app App
     */
    public void draw(App app) {
        for (int j = 0; j < WIDTH; j ++) {
            for (int i = 0; i < HEIGHT; i ++) {
                char ch = map_grid[i][j];
                switch (ch) {
                    case '#':
                        app.pushStyle();
                        int[] colors = WALL_THEME_COLORS[themeIndex];
                        app.fill(colors[0], colors[1], colors[2]);
                        app.stroke(colors[3], colors[4], colors[5]);
                        app.strokeWeight(1.5f);
                        app.rect(j * GRIDSIZE + 1, i * GRIDSIZE + 1, GRIDSIZE - 2, GRIDSIZE - 2);
                        app.popStyle();
                        break;
                    case '1':
                        app.image(this.walls[0], j * GRIDSIZE, i * GRIDSIZE);
                        break;
                    case '2':
                        app.image(this.walls[1], j * GRIDSIZE, i * GRIDSIZE);
                        break;
                    case '3':
                        app.image(this.walls[2], j * GRIDSIZE, i * GRIDSIZE);
                        break;
                    case '4':
                        app.image(this.walls[3], j * GRIDSIZE, i * GRIDSIZE);
                        break;
                    case '5':
                        app.image(this.walls[4], j * GRIDSIZE, i * GRIDSIZE);
                        break;
                    case '6':
                        app.image(this.walls[5], j * GRIDSIZE, i * GRIDSIZE);
                        break;
                    case PELLET:
                        drawCherry(app, j * GRIDSIZE, i * GRIDSIZE);
                        break;
                    case POWER_FRUIT:
                        drawPowerFruit(app, j * GRIDSIZE, i * GRIDSIZE);
                        break;
                    case '!':
                        app.pushStyle(); app.noStroke(); app.fill(255, 220, 40);
                        app.beginShape();
                        app.vertex(j * GRIDSIZE + 9, i * GRIDSIZE + 2);
                        app.vertex(j * GRIDSIZE + 4, i * GRIDSIZE + 9);
                        app.vertex(j * GRIDSIZE + 8, i * GRIDSIZE + 9);
                        app.vertex(j * GRIDSIZE + 6, i * GRIDSIZE + 14);
                        app.vertex(j * GRIDSIZE + 13, i * GRIDSIZE + 7);
                        app.vertex(j * GRIDSIZE + 9, i * GRIDSIZE + 7);
                        app.endShape(app.CLOSE); app.popStyle();
                        break;
                    case '~':
                        app.pushStyle(); app.noStroke(); app.fill(100, 220, 255);
                        app.ellipse(j * GRIDSIZE + 8, i * GRIDSIZE + 8, 9, 9);
                        app.fill(220); app.ellipse(j * GRIDSIZE + 8, i * GRIDSIZE + 8, 3, 3); app.popStyle();
                        break;
                }
            }
        }
    }

    private void drawCherry(App app, float x, float y) {
        app.pushStyle();
        app.stroke(53, 30, 38);
        app.strokeWeight(1.5f);
        app.noFill();
        app.bezier(x + 7, y + 8, x + 6, y + 3, x + 9, y + 2, x + 10, y + 4);
        app.bezier(x + 10, y + 5, x + 12, y + 2, x + 14, y + 3, x + 13, y + 6);
        app.noStroke();
        app.fill(44, 150, 62);
        app.ellipse(x + 12, y + 4, 5, 3);
        app.fill(211, 34, 53);
        app.ellipse(x + 6, y + 10, 6, 6);
        app.ellipse(x + 11, y + 10, 6, 6);
        app.fill(255, 150, 145);
        app.ellipse(x + 5, y + 9, 2, 2);
        app.ellipse(x + 10, y + 9, 2, 2);
        app.popStyle();
    }

    private void drawPowerFruit(App app, float x, float y) {
        app.pushStyle();
        app.noStroke();
        app.fill(49, 145, 57);
        app.ellipse(x + 7, y + 5, 5, 3);
        app.ellipse(x + 10, y + 5, 5, 3);
        app.fill(232, 128, 25);
        app.ellipse(x + 8, y + 10, 10, 9);
        app.fill(255, 192, 58);
        app.ellipse(x + 6, y + 8, 3, 3);
        app.stroke(255, 238, 152);
        app.strokeWeight(1);
        app.line(x + 13, y + 3, x + 15, y + 1);
        app.line(x + 14, y + 5, x + 16, y + 5);
        app.popStyle();
    }

    /**
     * This method is used to check if the point on the map can move which means it isn't a wall.
     * @param i int
     * @param j int
     * @return true/false boolean
     */
    public boolean isGeneral(int i, int j) {
        if (this.getMap_grid_char(j,i) == '0') {
            return false;
        }
        if (this.getMap_grid_char(j,i) == PELLET) {
            return false;
        }
        if (this.getMap_grid_char(j,i) == 'p') {
            return false;
        }
        if (this.getMap_grid_char(j,i) == 'a') {
            return false;
        }
        if (this.getMap_grid_char(j,i) == 'c') {
            return false;
        }
        if (this.getMap_grid_char(j,i) == 'i') {
            return false;
        }
        if (this.getMap_grid_char(j,i) == 'w') {
            return false;
        }
        if (this.getMap_grid_char(j,i) == 'g') {
            return false;
        }
        if (this.getMap_grid_char(j,i) == POWER_FRUIT) {
            return false;
        }
        if (this.getMap_grid_char(j,i) == '!' || this.getMap_grid_char(j,i) == '~') return false;
        return true;
    }

    /**
     * This method is used to get the map array.
     * @return map_grid Array
     */
    public char[][] getMap_grid() {
        return map_grid;
    }

    /**
     * This method is used to get the content on the map point.
     * @param i int
     * @param j int
     * @return letter char
     */
    public char getMap_grid_char(int i, int j) {
        return getMap_grid()[i][j];
    }

    /**
     * This method is used to set the letter into the map corresponding point.
     * @param i int
     * @param j int
     */
    public void setMap_grid_char(int i, int j) {
        if (map_grid[i][j] == POWER_FRUIT) {
            powerFruitCount--;
            if (powerFruitCount == 0) powerFruitRespawnTimer = POWER_FRUIT_RESPAWN_FRAMES;
        }
        this.map_grid[i][j] = '0';
    }

    public int getThemeIndex() { return themeIndex; }

    /**
     * This method is used to get the waka start position.
     * @return start_waka Point
     */
    public Point getStart_waka() {
        return start_waka;
    }

    /**
     * This method is used to get the Amubusher ghost start positions.
     * @return list ArrayList
     */
    public ArrayList<Point> getStart_ghost_a() {
        return start_ghost_a;
    }

    /**
     * This method is used to get the Chaser ghost start positions.
     * @return list ArrayList
     */
    public ArrayList<Point> getStart_ghost_c() {
        return start_ghost_c;
    }

    /**
     *
     * This method is used to get the Ignorant ghost start positions.
     * @return list ArrayList
     */
    public ArrayList<Point> getStart_ghost_i() {
        return start_ghost_i;
    }

    /**
     * This method is used to get the Whim ghost start positions.
     * @return list ArrayList
     */
    public ArrayList<Point> getStart_ghost_w() {
        return start_ghost_w;
    }
}
