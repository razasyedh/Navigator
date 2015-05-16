/**
 * A linked grid that connects and manages nodes.
 */
public class LinkedGrid {
    /** A 2D array representing a grid of nodes. */
    private final Node[][] grid;
    /** The number of rows in the grid. */
    private int rows;
    /** The number of columns in the grid. */
    private int cols;
    /** The default value of an unfilled node. */
    public static final int UNFILLED = 0;
    /** The value of a node that has been blocked. */
    public static final int BLOCKED = 1;

    /**
     * Creates a linked grid of the specified size, linking each node to each
     * other.
     *
     * @param rows The number of rows.
     * @param cols The number of columns.
     */
    public LinkedGrid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        grid = new Node[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Node();
            }
        }

        linkNodes();
    }

    /**
     * Links nodes to each other based on their position in the grid.
     */
    private void linkNodes() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i != 0) {
                    grid[i][j].setWest(grid[i-1][j]);
                }

                if (i != rows-1) {
                    grid[i][j].setEast(grid[i+1][j]);
                }

                if (j != 0) {
                    grid[i][j].setSouth(grid[i][j-1]);
                }

                if (j != cols-1) {
                    grid[i][j].setNorth(grid[i][j+1]);
                }
            }
        }
    }

    /**
     * Resets every node except blocked nodes.
     */
    public void partialReset() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j].getValue() != BLOCKED) {
                    grid[i][j].setValue(UNFILLED);
                }
            }
        }
    }

    /**
     * Resets every node.
     */
    public void fullReset() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j].setValue(UNFILLED);
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
    public Node getNode(int x, int y) {
        return grid[x][y];
    }

    /**
     * Gets the node specified by the given point,
     *
     * @param p The 2D point.
     * @return The node located at the given point.
     */
    public Node getNode(Point2D p) {
        int x = p.getX();
        int y = p.getY();
        return getNode(x, y);
    }

    /**
     * Returns a string representation of the grid with numbered axes.
     * <p>
     * Note: The axes of the grid are switched, so the x coordinate would
     * correspond to the vertical axis and vice-versa.
     *
     * @return The formatted grid as a string.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("\\Y");

        // Column numbers
        for (int j = 0; j < cols; j++) {
            String colNum = String.format("%3s", j);
            result.append(" " + colNum);
        }
        result.append("\nX _|\n");

        for (int i = 0; i < rows; i++) {
            // Row numbers
            String row = String.format("%2s", i);
            result.append(row + " ");

            for (int j = 0; j < cols; j++) {
                String node = grid[i][j].toString();
                String valueString = String.format("%3s", node);

                result.append(valueString  + " ");
            }
            result.append("\n");
        }

        return result.toString();
    }

    public static void main(String[] args) {
        LinkedGrid test = new LinkedGrid(2, 5);
        test.getNode(0, 1).setValue(1);
        test.getNode(1, 0).setValue(2);
        System.out.println(test);

        Node north = test.getNode(0, 0).getNorth();
        Node east = test.getNode(0, 0).getEast();
        System.out.println("North of (0,0): " + north);
        System.out.println("East of (0,0): " + east);
    }
}
