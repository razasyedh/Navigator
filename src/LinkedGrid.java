/**
 * A linked grid that connects and manages nodes.
 *
 * Note: Nodes will be filled in and managed by a {@link PathFinder}. You
 * should only manually modify the values of the nodes in order to
 * (un)block them, which will require updating the PathFinder.
 */
public class LinkedGrid extends Grid {
    /**
     * Creates a linked grid of the specified size, linking each node to each
     * other.
     *
     * @param rows The number of rows.
     * @param cols The number of columns.
     */
    public LinkedGrid(int rows, int cols) {
        super(rows, cols);
        linkNodes();
    }

    /**
     * Links nodes to each other based on their position in the grid.
     */
    private void linkNodes() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i != 0) {
                    grid[i][j].setWest(grid[i - 1][j]);
                }

                if (i != rows - 1) {
                    grid[i][j].setEast(grid[i + 1][j]);
                }

                if (j != 0) {
                    grid[i][j].setSouth(grid[i][j - 1]);
                }

                if (j != cols - 1) {
                    grid[i][j].setNorth(grid[i][j + 1]);
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
                if (grid[i][j].getValue() != PathFinder.BLOCKED) {
                    grid[i][j].setValue(PathFinder.UNFILLED);
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
                grid[i][j].setValue(PathFinder.UNFILLED);
            }
        }
    }
}
