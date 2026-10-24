package ghost;

/**
 * Used to represent the direction: LEFT, RIGHT, UP, DOWN
 */
enum Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN,
    NODIR
}

/**
 * This class used to create Point object and can test the distance between two points.
 * @author Shutong Li
 * @version 1.1
 */
public class Point {
    float x;
    float y;

    /**
     * This is class Point constructor method.
     * @param x float
     * @param y float
     */
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * This method is used to get point x-axis value.
     * @return x float
     */
    public float getX() {
        return x;
    }

    /**
     * This method is used to get point y-axis value.
     * @return y float
     */
    public float getY() {
        return y;
    }

    /**
     * This method is used to test the distance between two point.
     * @param x float
     * @param y float
     * @return distance float
     */
    public float Distance(Point x, Point y) {
        float x_x = x.getX();
        float x_y = x.getY();
        float y_x = y.getX();
        float y_y = y.getY();
        double result = Math.sqrt(Math.pow(x_x - y_x, 2) + Math.pow(x_y - y_y, 2));
        return (float)result;
    }
}
