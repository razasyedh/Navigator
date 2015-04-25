import java.lang.Math;

// Although AWT has a nearly identical class, it is reimplemented here
// because that implementation returns <code>int</code> coordinates as
// <code>double</code>s, which is undesirable since these coordinates are
// used as array indices.
class Point2D {
    private int x;
    private int y;

    Point2D() {
        x = 0;
        y = 0;
    }

    Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Point2D(Point2D p) {
        x = p.getX();
        y = p.getY();
    }

    public int getX() {return x;}

    public int getY() {return y;}

    // d = \sqrt((x_2 - x_1)^2 + (y_2 - y_1)^2)
    public double distance(Point2D pt) {
        int x2 = pt.getX();
        int y2 = pt.getY();

        int d1 = (x2 - x);
        int d2 = (y2 - y);

        double d1sq = Math.pow(d1, 2);
        double d2sq = Math.pow(d2, 2);

        return Math.sqrt(d1sq + d2sq);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean equals(Point2D other) {
        if (other.getX() == x && other.getY() == y) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
