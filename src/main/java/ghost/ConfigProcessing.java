package ghost;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used to read "config.json" file.
 * @author Shutong Li
 * @version 1.1
 */

public class ConfigProcessing{
    private String map_address;
    private int lives;
    private int speed;
    private int frightenedLength;
    private Iterator<Long> iterator;
    private List<Integer> iterators = new ArrayList<Integer>();

    /**
     * This method used to open file and read object in it.
     */
    public void readJsonFile() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("config.json"));
            JSONObject jsonObject = (JSONObject) obj;
            this.map_address = (String) jsonObject.get("map");
            System.out.println(map_address);

            Long lives = (Long) jsonObject.get("lives");
            this.lives = Math.toIntExact(lives);

            Long speed = (Long) jsonObject.get("speed");
            this.speed = Math.toIntExact(speed);

            Long frightenedLength = (Long) jsonObject.get("frightenedLength");
            this.frightenedLength = Math.toIntExact(frightenedLength);

            JSONArray msg = (JSONArray) jsonObject.get("modeLengths");
            this.iterator = msg.iterator();
            while (this.iterator.hasNext()) {
                this.iterators.add(Math.toIntExact(this.iterator.next()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the map address stored in the file.
     * @return map_address String
     */
    public String getMap_address() {
        return map_address;
    }

    /**
     * Return lives number stores in the file.
     * @return lived int
     */
    public int getLives() {
        return lives;
    }

    /**
     * Return speed stores in the file
     * @return speed int
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Return "modelengths" stored in the file.
     * @return modelengths List
     */
    public Iterator<Long> getIterator() {
        return iterator;
    }

    /**
     * Return a list memory "modelengths" in the file
     * @return iterators List
     */
    public List<Integer> getIterators() {
        return iterators;
    }

    /**
     * Return "frightenedLength" stored in the file
     * @return frightenedLength int
     */
    public int getFrightenedLength() {
        return frightenedLength;
    }
}
