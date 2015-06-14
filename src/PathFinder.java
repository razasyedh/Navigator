import java.util.concurrent.LinkedBlockingQueue;
import java.util.Queue;
import java.util.Vector;

/**
 * An implementation of the Wavefront algorithm for finding the path between
 * two points on a grid of linked nodes.
 */
public class PathFinder implements Navigation {
    /** The linked grid. */
    private final Grid grid;
    /** The start point. */
    private Point2D start;
    /** The end point. */
    private Point2D end;
    /** The calculated path. */
    private final Vector<Point2D> path;

    /**
     * Creates a PathFinder object with the given grid and points.
     * <p>
     * Preconditions: No null links should be present in the given grid, the
     * path shouldn't be blocked, and start + end points have to be different
     * and within the grid range.
     *
     * @throws IllegalArgumentException If the start and end points are the
     *         same.
     */
    public PathFinder(Grid grid, Point2D start, Point2D end)
            throws UnreachablePointException {
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
    private void calculatePath() throws UnreachablePointException {
        if (start.equals(end)) {
            throw new IllegalArgumentException("End point is the same as the"
                                               + " start point.");
        }

        if (!pointInRange(start) || !pointInRange(end)) {
            throw new IllegalArgumentException("Start or end points out of"
                                               + " outside grid.");
        }

        path.clear();
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
        boolean isInRange;
        int x = p.getX();
        int y = p.getY();

        if (x >= 0 && x < grid.rows && y >= 0 && y < grid.cols) {
            isInRange = true;
        } else {
            isInRange = false;
        }

        return isInRange;
    }

    /**
     * Fills a grid with values corresponding to distance from the end point.
     * Points are filled in a wave pattern since neighboring nodes will be
     * equidistant.
     */
    private void fillGrid() {
        Queue<DNode> fillQueue = new LinkedBlockingQueue<>();
        DNode startNode = grid.getNode(start);
        DNode endNode = grid.getNode(end);
        int fillValue = LinkedGrid.BLOCKED + 1;

        // Fill the nodes breadth-first by continously queueing the neighbors
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

            if (nodeValue == LinkedGrid.UNFILLED &&
                !(queue.contains(neighbor))) {
                queue.add(neighbor);
            }
        }
    }

    /**
     * Naively creates a path from the start point to the end point based on
     * the node values.
     */
    private void traverseGrid() throws UnreachablePointException {
        Point2D coordinates = start;
        while (!coordinates.equals(end)) {
            path.add(coordinates);
            coordinates = getNextNode(coordinates);
        }
        path.add(end);
    }

    /**
     * Gets the next traversable node with a lower value.
     *
     * @param coordinates The coordinates representing the current node.
     * @return The next node.
     */
    private Point2D getNextNode(Point2D coordinates)
            throws UnreachablePointException {
        Point2D nextNode = new Point2D(coordinates);
        DNode current = grid.getNode(coordinates);

        DNode north = current.getNorth();
        DNode south = current.getSouth();
        DNode west = current.getWest();
        DNode east = current.getEast();
        if (isNextNode(current, north)) {
            nextNode.setY(nextNode.getY() + 1);
        } else if (isNextNode(current, south)) {
            nextNode.setY(nextNode.getY() - 1);
        } else if (isNextNode(current, west)) {
            nextNode.setX(nextNode.getX() - 1);
        } else if (isNextNode(current, east)) {
            nextNode.setX(nextNode.getX() + 1);
        } else {
            throw new UnreachablePointException("Next node unreachable from: "
                                                + nextNode);
        }

        return nextNode;
    }

    /**
     * Determines if a given node can be traversed from the first.
     * <p>
     * Precondition: The first node cannot be null.
     *
     * @param first The first node.
     * @param second The second node.
     * @return true if the second node is traversable from the first.
     * @throws NullPointerException If the first node is null since this
     *                              indicates a linked grid with a null link.
     */
    private boolean isNextNode(DNode first, DNode second) {
        if (first == null) {
            throw new NullPointerException("Linked grid has a null link");
        }

        if (second == null) {
            return false;
        }

        boolean isLess;
        int secondVal = second.getValue();
        if (secondVal == LinkedGrid.UNFILLED
            || secondVal == LinkedGrid.BLOCKED) {
            isLess = false;
        } else if (second.compareTo(first) < 0) {
            isLess = true;
        } else { // Higher or equal value
            isLess = false;
        }

        return isLess;
    }

    /**
     * Updates the start node. This does not require recalculating the path.
     *
     * @param start The new starting point.
     */
    public void setStart(Point2D start) throws UnreachablePointException {
        this.start = start;
        traverseGrid();
    }

    /**
     * Updates the end node. This recalculates the path.
     *
     * @param end The new ending point.
     */
    public void setEnd(Point2D end) throws UnreachablePointException {
        this.end = end;
        calculatePath();
    }

    /**
     * Updates the path to reflect changes in the grid. Must be called when
     * (un)blocking any nodes.
     */
    public void update() throws UnreachablePointException {
        calculatePath();
    }

    /**
     * Returns the path that was calculated, including the start and end
     * points.
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

    public static void main(String[] args) {
        LinkedGrid grid = new LinkedGrid(5, 8);
        grid.getNode(0, 2).setValue(LinkedGrid.BLOCKED);
        grid.getNode(0, 4).setValue(LinkedGrid.BLOCKED);
        grid.getNode(1, 4).setValue(LinkedGrid.BLOCKED);
        grid.getNode(2, 4).setValue(LinkedGrid.BLOCKED);

        System.out.println(grid);

        Point2D start = new Point2D(3, 2);
        Point2D end = new Point2D(0, 3);
        System.out.println("Start: " + start);
        System.out.println("End: " + end);

        System.out.println("Calculating...\n");
        PathFinder wave = null;

        try {
            wave = new PathFinder(grid, start, end);
        } catch (UnreachablePointException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println(grid);
        System.out.println(wave);

        Point2D[] path = wave.getPath();
        int actualDistance = path.length - 1;
        int predictedDistance = start.distance(end);

        System.out.println("\nDistance:\n\tActual: " + actualDistance
                           + "\n\tPredicted:" + predictedDistance);
    }
}
