package ghost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Builds a connected, randomized maze for each level after the first. */
public final class MazeGenerator {
    private static final int ROWS = MapGrid.HEIGHT;
    private static final int COLUMNS = MapGrid.WIDTH;
    private static final int FIRST_CELL_ROW = 4;
    private static final int FIRST_CELL_COLUMN = 1;
    private static final int CELL_ROWS = 14;
    private static final int CELL_COLUMNS = 13;

    private MazeGenerator() { }

    public static String[] generate(int levelNumber) {
        return generate(levelNumber, 0x51A7L + 7919L * levelNumber);
    }

    public static String[] generate(int levelNumber, long seed) {
        char[][] maze = new char[ROWS][COLUMNS];
        for (int row = 0; row < ROWS; row++) {
            java.util.Arrays.fill(maze[row], '#');
        }

        // Leave the same three-row header area used by the original screen.
        for (int row = 0; row < 3; row++) {
            java.util.Arrays.fill(maze[row], '0');
        }
        for (int row = 33; row < ROWS; row++) {
            java.util.Arrays.fill(maze[row], '0');
        }

        Random random = new Random(seed);
        boolean[][] visited = new boolean[CELL_ROWS][CELL_COLUMNS];
        carve(0, 0, visited, maze, random);

        List<int[]> openTiles = new ArrayList<>();
        for (int row = 4; row <= 31; row++) {
            for (int column = 1; column <= 26; column++) {
                if (maze[row][column] == '0') {
                    maze[row][column] = '.';
                    openTiles.add(new int[]{row, column});
                }
            }
        }

        // Keep the player at the entrance, place the ghosts around the center,
        // and put the power fruit in a distant part of the connected maze.
        maze[FIRST_CELL_ROW][FIRST_CELL_COLUMN] = 'p';
        placeGhostsNearCenter(maze, openTiles);
        int fruitIndex = Math.min(openTiles.size() - 1, openTiles.size() * 3 / 4);
        int[] fruit = openTiles.get(fruitIndex);
        maze[fruit[0]][fruit[1]] = '*';

        String[] result = new String[ROWS];
        for (int row = 0; row < ROWS; row++) result[row] = new String(maze[row]);
        return result;
    }

    private static void carve(int cellRow, int cellColumn, boolean[][] visited,
                              char[][] maze, Random random) {
        visited[cellRow][cellColumn] = true;
        int row = FIRST_CELL_ROW + 2 * cellRow;
        int column = FIRST_CELL_COLUMN + 2 * cellColumn;
        maze[row][column] = '0';

        List<int[]> directions = new ArrayList<>();
        directions.add(new int[]{-1, 0});
        directions.add(new int[]{1, 0});
        directions.add(new int[]{0, -1});
        directions.add(new int[]{0, 1});
        Collections.shuffle(directions, random);

        for (int[] direction : directions) {
            int nextRow = cellRow + direction[0];
            int nextColumn = cellColumn + direction[1];
            if (nextRow < 0 || nextRow >= CELL_ROWS
                    || nextColumn < 0 || nextColumn >= CELL_COLUMNS
                    || visited[nextRow][nextColumn]) continue;

            maze[row + direction[0]][column + direction[1]] = '0';
            carve(nextRow, nextColumn, visited, maze, random);
        }
    }

    private static void placeGhostsNearCenter(char[][] maze, List<int[]> openTiles) {
        String[] types = {"a", "c", "i", "w"};
        int centerRow = ROWS / 2;
        int centerColumn = COLUMNS / 2;
        openTiles.sort((left, right) -> {
            int leftDistance = Math.abs(left[0] - centerRow) + Math.abs(left[1] - centerColumn);
            int rightDistance = Math.abs(right[0] - centerRow) + Math.abs(right[1] - centerColumn);
            return Integer.compare(leftDistance, rightDistance);
        });

        int placed = 0;
        for (int[] tile : openTiles) {
            if (tile[0] == FIRST_CELL_ROW && tile[1] == FIRST_CELL_COLUMN) continue;
            maze[tile[0]][tile[1]] = types[placed++].charAt(0);
            if (placed == types.length) break;
        }
    }
}
