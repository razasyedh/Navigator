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

    public int distance(Point2D pt) {
        int x2 = pt.getX();
        int y2 = pt.getY();

        int d1 = Math.abs(x2 - x);
        int d2 = Math.abs(y2 - y);

        return d1 + d2;
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
