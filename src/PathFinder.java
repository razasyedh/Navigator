import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An implementation of the Wavefront algorithm for finding the path between
 * two points on a grid of linked nodes.
 * <p>
 * Passing {@code null} parameters will throw a {@code NullPointerException}.
 */
public class PathFinder implements Navigation {
    /** The linked grid. */
    private final LinkedGrid grid;
    /** The start point. */
    private Point2D start;
    /** The end point. */
    private Point2D end;
    /** The calculated path. */
    private final Vector<Point2D> path;
    /** The default value of an unfilled node. */
    public static final int UNFILLED = 0;
    /** The value of a node that has been blocked. */
    public static final int BLOCKED = 1;

    /**
     * Creates a PathFinder object with the given grid and points.
     *
     * @param grid The pre-initialized linked grid containing no null links.
     * @param start The start point inside the grid different from the end.
     * @param end The end point inside the grid different from the start.
     * @throws IllegalArgumentException If the start and end points are the
     *         same
     * @throws IndexOutOfBoundsException If the start and end points are
     *         outside of the grid.
     */
    public PathFinder(LinkedGrid grid, Point2D start, Point2D end) {
        if (grid == null) {
            throw new NullPointerException("The passed in grid is null.");
        }

        this.grid = grid;
        this.start = start;
        this.end = end;
        path = new Vector<>();

        calculatePath();
    }

    /**
     * Calculates the path from the start point to the end by filling each
     * node and traversing the result.
     */
    private void calculatePath() {
        // Run some checks
        if (start == null || end == null) {
            throw new NullPointerException("The start or end point is null.");
        }

        if (start.equals(end)) {
            throw new IllegalArgumentException("End point is the same as the"
                                               + " start point.");
        }

        if (!pointInRange(start) || !pointInRange(end)) {
            throw new IndexOutOfBoundsException("Start or end points outside"
                                                + " of grid range.");
        }

        fillGrid();
        traverseGrid();
    }

    /**
     * Determines if a 2D point is in the range of the grid.
     *
     * @param p The point.
     * @return true if the coordinates of the point are in the range.
     */
    private boolean pointInRange(Point2D p) {
        int x = p.getX();
        int y = p.getY();

        return x >= 0 && x < grid.rows && y >= 0 && y < grid.cols;
    }

    /**
     * Fills a grid with values corresponding to distance from the end point.
     * Points are filled in a wave pattern since neighboring nodes will be
     * equidistant.
     */
    private void fillGrid() {
        Queue<DNode> fillQueue = new LinkedBlockingQueue<>();
        DNode endNode = grid.getNode(end);
        int fillValue = BLOCKED + 1;

        // Fill the nodes breadth-first by continuously queueing the neighbors
        fillQueue.add(endNode);
        while (fillQueue.size() != 0) {
            int nodesToFill = fillQueue.size();

            while (nodesToFill != 0) {
                DNode fillNode = fillQueue.poll();
                fillNode.setValue(fillValue);

                queueNeighbors(fillQueue, fillNode);
                nodesToFill--;
            }

            fillValue++;
        }
    }

    /**
     * Adds fillable neighbors to the queue.
     *
     * @param queue The queue to add to.
     * @param node The node who's neighbors are to be added.
     */
    private void queueNeighbors(Queue<DNode> queue, DNode node) {
        DNode[] neighbors = node.getNeighbors();
        for (DNode neighbor : neighbors) {
            int nodeValue;
            if (neighbor != null) {
                nodeValue = neighbor.getValue();
            } else {
                continue;
            }

            if (nodeValue == UNFILLED && !(queue.contains(neighbor))) {
                queue.add(neighbor);
            }
        }
    }

    /**
     * Naively creates a path from the start point to the end point based on
     * the node values.
     */
    private void traverseGrid() {
        path.clear();
        Point2D coordinates = start;
        while (!coordinates.equals(end)) {
            path.add(coordinates);
            coordinates = getNextNode(coordinates);

            if (coordinates == null) {
                return;
            }
        }
        path.add(end);
    }

    /**
     * Gets the next traversable node with a lower value.
     *
     * @param coordinates The coordinates representing the current node.
     * @return The next node or {@code null} if none could be found.
     */
    private Point2D getNextNode(Point2D coordinates) {
        Point2D nextNode = new Point2D(coordinates);
        DNode current = grid.getNode(coordinates);

        DNode north = current.getNorth();
        DNode south = current.getSouth();
        DNode west = current.getWest();
        DNode east = current.getEast();
        if (isNextNode(current, north)) {
            nextNode.translate(0, 1);
        } else if (isNextNode(current, south)) {
            nextNode.translate(0, -1);
        } else if (isNextNode(current, west)) {
            nextNode.translate(-1, 0);
        } else if (isNextNode(current, east)) {
            nextNode.translate(1, 0);
        } else {
            return null;
        }

        return nextNode;
    }

    /**
     * Determines if a given node can be traversed from the first.
     *
     * @param first The first node.
     * @param second The second node.
     * @return true if the second node is traversable from the first.
     */
    private boolean isNextNode(DNode first, DNode second) {
        if (second == null) {
            return false;
        }

        boolean isLess;
        int secondVal = second.getValue();
        int comparison = second.compareTo(first);
        if (secondVal == UNFILLED || secondVal == BLOCKED) {
            isLess = false;
        } else if (comparison < 0) {
            isLess = true;
        } else if (comparison == 0) {
            // Make sure the grid isn't in an invalid state
            throw new IllegalStateException("Adjacent nodes have the same"
                                            + " value.");
        } else { // Higher value
            isLess = false;
        }

        return isLess;
    }

    /**
     * Updates the start node. This does not require recalculating the path.
     *
     * @param start The new starting point.
     * @throws IllegalArgumentException If the start point is the same as the
     *         end.
     * @throws IndexOutOfBoundsException If the start point is outside of the
     *         grid.
     */
    public void setStart(Point2D start) {
        this.start = start;
        traverseGrid();
    }

    /**
     * Updates the end node. This recalculates the path.
     *
     * @param end The new ending point.
     * @throws IllegalArgumentException If the end point is the same as the
     *         start.
     * @throws IndexOutOfBoundsException If the end point is outside of the
     *         grid.
     */
    public void setEnd(Point2D end) {
        this.end = end;
        grid.partialReset();
        calculatePath();
    }

    /**
     * Updates the path to reflect changes in the grid. Must be called when
     * (un)blocking any nodes.
     */
    public void update() {
        grid.partialReset();
        calculatePath();
    }

    /**
     * Resets all blocked nodes and recalculates the path.
     */
    public void reset() {
        grid.fullReset();
        calculatePath();
    }

    /**
     * Returns the path that was calculated from start to end. If a suitable
     * path couldn't be found, only the start point will be included.
     *
     * @return The path of 2D points.
     */
    public Point2D[] getPath() {
        Point2D[] emptyArray = new Point2D[path.size()];
        return path.toArray(emptyArray);
    }

    /**
     * Returns a string representation of the path that was determined.
     *
     * @return The comma-delimited points of the path.
     */
    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < path.size(); i++) {
            result += path.get(i) + ", ";
        }

        return result;
    }
}
