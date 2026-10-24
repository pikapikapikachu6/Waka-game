package ghost;

/**
 * Used to represent the mode type
 */
enum M_TYPE {
    SCATTER,
    CHASE
}

/**
 * This class is used to create mode.
 * @author Shutong Li
 * @version 1.1
 */
public class Modes {
    public M_TYPE current_type;
    public int leftTime;
    public int totalTime;

    /**
     * This is the mode constructor method.
     * @param current_type M_TYPE
     * @param time int
     */
    public Modes(M_TYPE current_type, int time) {
        this.current_type = current_type;
        this.totalTime = time * 60;
        this.leftTime = this.totalTime;
    }
}
