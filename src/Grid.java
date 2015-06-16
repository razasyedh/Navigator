/**
 * A simple grid of nodes.
 */
public class Grid {
    /** A 2D array representing a grid of nodes. */
    protected final DNode[][] grid;
    /** The number of rows in the grid. */
    public final int rows;
    /** The number of columns in the grid. */
    public final int cols;

    /**
     * Creates a grid of the specified size.
     *
     * @param rows The number of rows.
     * @param cols The number of columns.
     */
    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        grid = new DNode[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new DNode();
            }
        }
    }

    /**
     * Gets the node specified by the given coordinates,
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The node located at the given point.
     */
    public DNode getNode(int x, int y) {
        return grid[x][y];
    }

    /**
     * Gets the node specified by the given point,
     *
     * @param p The 2D point.
     * @return The node located at the given point.
     */
    public DNode getNode(Point2D p) {
        int x = p.getX();
        int y = p.getY();
        return getNode(x, y);
    }

    /**
     * Returns the value of the largest node.
     *
     * @return the value.
     */
    public int getMax() {
        int max = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int val = grid[i][j].getValue();
                if (val > max) {
                    max = val;
                }
            }
        }

        return max;
    }
}
