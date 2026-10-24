# Waka Game

A Java and Processing maze game inspired by Pac-Man. Move through the maze, collect fruit, avoid ghosts, and try to finish all four levels.

## Requirements

- JDK 17 or newer
- Gradle installed on your system
- A desktop environment that can open Processing graphics windows

The project targets Java 17. Run Gradle from a JDK 17 environment for the most compatible setup. Gradle downloads Processing and JSON dependencies the first time you run the game, so an internet connection is needed for the initial build.

## Run

Open a terminal in the project root (the folder containing `build.gradle`) and run:

```bash
gradle run
```

Start the program from the project root so it can find `config.json`, `map.txt`, and the image resources. If Gradle reports that the command is not found, install Gradle or open the project in an IDE with Gradle support.

## Controls

| Key | Action |
| --- | --- |
| Arrow keys | Move |
| `P` | Pause or resume |
| `R` | Restart from level 1 |
| `N` | Switch between classic and random map modes; starts a new game |

## Gameplay

- Collect cherries to earn points. The score, high score, level, and remaining lives are shown on screen.
- Collect a superfruit to enter a temporary powered-up state. Its remaining time is displayed, and ghosts can be eaten for a growing combo bonus.
- A collected superfruit respawns after a short delay at a reachable location in the maze.
- Lightning grants a temporary speed boost. The ice pickup freezes ghosts briefly. Both award bonus points.
- Earn an extra life for every 5,000 points.
- The game has four levels. Collect 100 regular fruit in a level to move on; later levels increase the movement speed and use different maze and wall-color themes.
- Sound effects play for fruit, ghosts, lives, and level transitions.
- The best score is saved in `highscore.txt` in the project root.

In classic mode, level 1 uses `map.txt` and later levels use generated mazes. Random map mode creates a new maze layout for each level whenever a new game starts.

## Configuration

Edit `config.json` to change the starting map, lives, movement speed, and superfruit power duration. For example:

```json
{
  "map": "map.txt",
  "lives": 3,
  "speed": 1,
  "frightenedLength": 20,
  "modeLengths": [7, 20, 7, 20, 5, 20, 5, 1000]
}
```

The `modeLengths` array configures the ghost behavior timing used by the game. `highscore.txt`, Gradle's `.gradle/` directory, and generated build files are local runtime/build data and are excluded from Git.
