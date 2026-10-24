package ghost;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This file is the main file of this project about game Waka. In this file, the project will read the map.
 * load the "confij.json" file and using other classes to draw the game for every frame.
 * @author Shutong Li
 * @version 1.1
 *
*/
public class App extends PApplet {
    public static final int WIDTH = 448, HEIGHT = 576;
    public Waka waka;
    public ArrayList<Ghost> ghostList = new ArrayList<>();
    public MapGrid mapGrid;
    private ConfigProcessing con_file = new ConfigProcessing();
    private static final int LEVEL_COUNT = 4;
    private static final int PELLETS_PER_LEVEL = 100;
    private final Path highScorePath = Path.of("highscore.txt");
    private SoundEffects soundEffects;
    private int highScore = 0;
    private int nextExtraLifeScore = 5000;
    private int levelIndex = 0;
    private int levelClearFrames = 0;
    private boolean gameOver = false;
    private boolean gameWon = false;
    public PImage[] walls, wakas, fruit, ghosts;
    public PImage close_waka;
    public ArrayList<Point> ghosts_a = new ArrayList<Point>(), ghosts_c  = new ArrayList<Point>() ,ghosts_i  = new ArrayList<Point>(), ghosts_w = new ArrayList<Point>();
    public int ghost_number= 0,load_time1 = 0, load_time = 0, current_time = 0, dirc;
    private boolean paused = false;
    private boolean randomMapMode = false;
    private long randomMapSeed;

    /**
     * This method is class App constructor, which used to set objects.
     */
    public App() {
        con_file.readJsonFile();
    }

    /**
     *  The setup() function is called once when the program starts. It's used to define initial enviroment properties
     *  such as screen size and background color and to load media such as images and fonts as the program starts.
     *  There can only be one setup() function for each program and it shouldn't be called again after its initial execution.
     *  Note: Variables declared within setup() are not accessible within other functions, including draw(). (end auto-generated)
     */
    public void setup() {
        frameRate(60);
        soundEffects = new SoundEffects();
        loadHighScore();
        restart();
    }

    /**
     * A method created to set the game with the original state. Call when the game first time or finished the last time game.
     *
     */
    public void restart() {
        paused = false;
        dirc = 0;
        current_time = load_time = load_time1 = 0;
        levelIndex = 0;
        levelClearFrames = 0;
        gameOver = false;
        gameWon = false;
        randomMapSeed = ThreadLocalRandom.current().nextLong();
        nextExtraLifeScore = 5000;
        startLevel(levelIndex, con_file.getLives(), 0);
    }

    private void startLevel(int nextLevel, int lives, int score) {
        ghost_number = 0;
        ghostList = new ArrayList<Ghost>();
        ghosts_a = ghosts_c = ghosts_i = ghosts_w = new ArrayList<Point>();
        this.walls = new PImage[]{  this.loadImage("src/main/resources/horizontal.png"), this.loadImage("src/main/resources/vertical.png"), this.loadImage("src/main/resources/upLeft.png"), this.loadImage("src/main/resources/upRight.png"), this.loadImage("src/main/resources/downLeft.png"), this.loadImage("src/main/resources/downRight.png")};
        this.wakas = new PImage[]{  this.loadImage("src/main/resources/playerLeft.png"), this.loadImage("src/main/resources/playerRight.png"), this.loadImage("src/main/resources/playerUp.png"), this.loadImage("src/main/resources/playerDown.png")};
        this.ghosts = new PImage[]{  this.loadImage("src/main/resources/ambusher.png"), this.loadImage("src/main/resources/chaser.png"), this.loadImage("src/main/resources/ignorant.png"), this.loadImage("src/main/resources/whim.png"), this.loadImage("src/main/resources/frightened.png")};
        this.close_waka = this.loadImage("playerClosed.png");
        this.fruit = new PImage[]{ this.loadImage("src/main/resources/fruit.png"), this.loadImage("src/main/resources/superfruit.png") };
        if (randomMapMode) {
            this.mapGrid = new MapGrid(MazeGenerator.generate(nextLevel, randomMapSeed + nextLevel * 7919L), this.walls, this.fruit);
        } else if (nextLevel == 0) {
            this.mapGrid = new MapGrid(con_file.getMap_address(), this.walls, this.fruit);
        } else {
            this.mapGrid = new MapGrid(MazeGenerator.generate(nextLevel), this.walls, this.fruit);
        }
        mapGrid.setThemeIndex(nextLevel);
        int levelSpeed = con_file.getSpeed() + nextLevel / 2;
        this.waka = new Waka(levelSpeed, lives, this.wakas, this.close_waka);
        this.waka.score = score;
        this.waka.frightenedDurationFrames = con_file.getFrightenedLength() * 60;
        this.ghosts_a = mapGrid.getStart_ghost_a();
        this.ghosts_c = mapGrid.getStart_ghost_c();
        this.ghosts_i = mapGrid.getStart_ghost_i();
        this.ghosts_w = mapGrid.getStart_ghost_w();
        for (int i = 0; i < this.ghosts_a.size(); i ++, this.ghost_number ++) {
            ghostList.add(new Ghost(ghosts,Ghost_type.Amubusher));
            ghostList.get(this.ghost_number).initialLoc(this.ghosts_a.get(i), this.con_file, this.mapGrid);
        }
        for (int i = 0; i < this.ghosts_c.size(); i ++, this.ghost_number ++) {
            ghostList.add(new Ghost(ghosts,Ghost_type.Chaser));
            ghostList.get(this.ghost_number).initialLoc(this.ghosts_c.get(i), this.con_file,this.mapGrid);
        }
        for (int i = 0; i < this.ghosts_i.size(); i ++, this.ghost_number ++) {
            ghostList.add(new Ghost(ghosts,Ghost_type.Ignorant));
            ghostList.get(this.ghost_number).initialLoc(this.ghosts_i.get(i), this.con_file,this.mapGrid);
        }
        for (int i = 0; i < this.ghosts_w.size(); i ++, this.ghost_number ++) {
            ghostList.add(new Ghost(ghosts,Ghost_type.Whim));
            ghostList.get(this.ghost_number).initialLoc(this.ghosts_w.get(i), this.con_file,this.mapGrid);
        }
        for (Ghost ghost : ghostList) ghost.speed += nextLevel / 2;
        this.waka.initialLoc(this.mapGrid);
    }

    /**
     * A method extends from PApplet. Description to come... ( end auto-generated ) Override this method to call size()
     * when not using the PDE.
     */
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * A method extends from PApplet. Called directly after setup() and continuously executes the lines of code contained
     * inside its block until the program is stopped or noLoop() is called. The draw() function is called automatically
     * and should never be called explicitly. It should always be controlled with noLoop(), redraw() and loop(). After
     * noLoop() stops the code in draw() from executing, redraw() causes the code inside draw() to execute once and loop()
     * will causes the code inside draw() to execute continuously again. The number of times draw() executes in each second
     * may be controlled with frameRate() function. There can only be one draw() function for each sketch and draw() must
     * exist if you want the code to run continuously or to process events such as mousePressed(). Sometimes, you might
     * have an empty call to draw() in your program as shown in the above example. ( end auto-generated )
     */
    public void draw() {
        line(0,0,250,200);
        background(0, 0, 0);
        if (paused) {
            drawWorld(false);
            drawHud();
            fill(0, 0, 0, 185);
            rect(135, 250, 180, 58);
            fill(255);
            textAlign(CENTER, CENTER);
            textSize(18);
            text("PAUSED", WIDTH / 2, 268);
            textSize(9);
            text("P: RESUME   R: RESTART", WIDTH / 2, 291);
            return;
        }
        drawHud();
        if (gameOver) {
            drawWorld(false);
            drawOverlay("GAME OVER", "R: RESTART");
            return;
        }
        if (gameWon) {
            drawWorld(false);
            drawOverlay("YOU WIN!", "R: PLAY AGAIN");
            return;
        }
        if (levelClearFrames > 0) {
            drawWorld(false);
            drawOverlay("LEVEL " + (levelIndex + 1) + " CLEAR", "NEXT LEVEL...");
            if (--levelClearFrames == 0) {
                if (levelIndex + 1 < LEVEL_COUNT) {
                    levelIndex++;
                    startLevel(levelIndex, waka.lives, waka.score);
                } else {
                    gameWon = true;
                    soundEffects.playLevelClear();
                }
            }
            return;
        }

        int pelletsNeeded = Math.min(PELLETS_PER_LEVEL, mapGrid.total_fruit);
        if (waka.mark >= pelletsNeeded) {
            levelClearFrames = 120;
            soundEffects.playLevelClear();
            return;
        }

        mapGrid.updatePowerFruitRespawn(waka);
        int scoreBeforeFrame = waka.score;
        int markBeforeFrame = waka.mark;
        int ghostsBeforeFrame = waka.ghostsEatenThisFrightened;
        int livesBeforeFrame = waka.lives;
        int eaten = 0;
        boolean ghostsFrozen = waka.ghostFreezeFrames > 0;
        for (int i = 0; i < ghostList.size(); i ++, stroke(153)) {
            if (!ghostsFrozen) ghostList.get(i).move(this.waka,ghostList);
            if (dirc == 32) line(this.ghostList.get(i).current_position.getX(),this.ghostList.get(i).current_position.getY(),this.ghostList.get(i).target.getX(),this.ghostList.get(i).target.getY());
            if (!ghostsFrozen && ghostList.get(i).eat_waka(this.waka,this.ghostList.get(i))) eaten = 1;
        }
        if (eaten != 1) {
            if (dirc == UP) this.waka.setDirToUp();
            if (dirc == DOWN) this.waka.setDirToDown();
            if (dirc == LEFT) this.waka.setDirToLeft();
            if (dirc == RIGHT) this.waka.setDirToRight();
        }
        waka.tickFrightenedTimer();
        waka.tickBonusTimers(con_file.getSpeed() + levelIndex / 2);
        if (waka.ghostFreezeFrames > 0) waka.ghostFreezeFrames--;
        if (waka.lives < livesBeforeFrame) soundEffects.playLifeLost();
        else if (waka.ghostsEatenThisFrightened > ghostsBeforeFrame) soundEffects.playGhost();
        else if (waka.score > scoreBeforeFrame && waka.mark == markBeforeFrame) soundEffects.playPowerFruit();
        else if (waka.mark > markBeforeFrame) soundEffects.playPellet();

        while (waka.score >= nextExtraLifeScore) {
            waka.lives++;
            nextExtraLifeScore += 5000;
            soundEffects.playExtraLife();
        }
        updateHighScore();
        drawWorld(true);
        if (waka.lives <= 0) gameOver = true;
        current_time += 1;
    }

    private void drawWorld(boolean advanceGhostModes) {
        for (int i = 0; i < waka.lives; i++) image(wakas[1], 25 * i, 540);
        mapGrid.draw(this);
        waka.draw(this);
        for (Ghost ghost : ghostList) ghost.draw(this, waka, advanceGhostModes);
    }

    private void drawOverlay(String title, String subtitle) {
        fill(0, 0, 0, 190);
        rect(105, 245, 238, 78);
        fill(255, 230, 120);
        textAlign(CENTER, CENTER);
        textSize(16);
        text(title, WIDTH / 2, 270);
        fill(255);
        textSize(10);
        text(subtitle, WIDTH / 2, 300);
    }

    private void loadHighScore() {
        try {
            if (Files.exists(highScorePath)) highScore = Integer.parseInt(Files.readString(highScorePath).trim());
        } catch (IOException | NumberFormatException ignored) {
            highScore = 0;
        }
    }

    private void updateHighScore() {
        if (waka.score <= highScore) return;
        highScore = waka.score;
        try {
            Files.writeString(highScorePath, Integer.toString(highScore));
        } catch (IOException ignored) {
            // Keep the current game running even if the score file cannot be written.
        }
    }

    private void drawHud() {
        pushStyle();
        fill(255, 230, 120);
        textSize(12);
        textAlign(LEFT, BASELINE);
        text("SCORE " + waka.score, 8, 13);
        text("BEST " + highScore, 145, 13);
        text("LEVEL " + (levelIndex + 1), 330, 13);
        fill(170, 210, 255);
        textSize(8);
        text(randomMapMode ? "RANDOM MAP" : "CLASSIC MAP", 280, 29);
        if (waka.friented) {
            int seconds = (waka.frightenedFramesRemaining + 59) / 60;
            fill(115, 190, 255);
            text("POWER " + seconds + "s", 8, 29);
            fill(255);
            text("NEXT GHOST " + nextGhostScore(), 175, 29);
        }
        popStyle();
    }

    private int nextGhostScore() {
        int tier = Math.min(waka.ghostsEatenThisFrightened, 3);
        return 200 * (1 << tier);
    }

    /**
     * This is a method extends from PApplet. The boolean system variable keyPressed is true if any key is pressed and
     * false if no keys are pressed. ( end auto-generated )
     */
    public void keyPressed() {
        if (key == 'p' || key == 'P') {
            paused = !paused;
            return;
        }
        if (key == 'r' || key == 'R') {
            restart();
            return;
        }
        if (key == 'n' || key == 'N') {
            randomMapMode = !randomMapMode;
            restart();
            return;
        }
        if (paused) return;
        this.dirc = keyCode;
        if (key == ' ') paused = true;
    }

    /**
     * The main method. This project run start from this method.
     * @param args String[]
     */
    public static void main(String[] args) {
        PApplet.main("ghost.App");
    }
}
