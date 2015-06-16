import java.lang.Math;

/**
 * A 2D coordinate pair representing a point on a grid.
 * <p>
 * Although AWT has a nearly identical class, it is reimplemented here
 * because that implementation returns {@code int} coordinates as
 * {@code double}s, which is undesirable since these coordinates are used as
 * array indices.
 */
public class Point2D {
    /** The X coordinate */
    private int x;
    /** The Y coordinate */
    private int y;

    /**
     * The default constructor which sets the point to (0, 0).
     */
    Point2D() {
        x = 0;
        y = 0;
    }

    /**
     * The overloaded constructor which takes x and y coordinates.
     *
     * @param x The x coordinate.
     * @param x The y coordinate.
     */
    Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The overloaded copy constructor which takes another Point2D object.
     *
     * @param p The Point2D to whose coordinates to copy.
     */
    Point2D(Point2D p) {
        x = p.getX();
        y = p.getY();
    }

    /**
     * Reverses the x and y coordinates of a Point2D object.
     *
     * @param p The Point2D to reverse.
     * @return The reversed Point2D.
     */
    public static Point2D reverse(Point2D p) {
        if (p == null) {
            return null;
        }

        int x = p.getX();
        int y = p.getY();

        return new Point2D(y, x);
    }

    /**
     * Gets the x coordinate of the Point2D.
     *
     * @return The x coordinate.
     */
    public int getX() {return x;}

    /**
     * Gets the y coordinate of the Point2D.
     *
     * @return The y coordinate.
     */
    public int getY() {return y;}

    /**
     * Calculates the integer distance between two points.
     *
     * @param pt The second point.
     * @return The vertical and horizontal distance between the points.
     */
    public int distance(Point2D pt) {
        int x2 = pt.getX();
        int y2 = pt.getY();

        int d1 = Math.abs(x2 - x);
        int d2 = Math.abs(y2 - y);

        return d1 + d2;
    }

    /**
     * Sets the x coordinate of the point.
     *
     * @param x The x coordinate to set.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y coordinate of the point.
     *
     * @param y The y coordinate to set.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Determines if the point has the same coordinates as another point.
     *
     * @param obj The object to compare to.
     * @return true if the points have the same x and y coordinates.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Point2D)) {
            return false;
        }

        Point2D other = (Point2D) obj;
        return other.getX() == x && other.getY() == y;
    }

    @Override
    public int hashCode() {
        return 2 * x + y;
    }

    /**
     * Returns a string representation of the point.
     *
     * @return The point in '(x, y)' format.
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
