import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Vector;

/**
 * An implementation of the Wavefront algorithm for finding the path between
 * two points on a grid of linked nodes.
 */
public class PathFinder {
    /** The linked grid. */
    private final LinkedGrid grid;
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
     * path shouldn't be blocked, and start + end points have to be different.
     *
     * @throws IllegalArgumentException If the start and end points are the
     *         same.
     */
    public PathFinder(LinkedGrid grid, Point2D start, Point2D end)
            throws UnreachablePointException {
        this.grid = grid;
        this.start = start;
        this.end = end;

        if (start.equals(end)) {
            throw new IllegalArgumentException("End point is the same as the"
                                               + "start point.");
        }

        path = new Vector<>();
        calculatePath();
    }

    /**
     * Calculates the path from the start point to the end by filling each
     * node and traversing the result.
     */
    private void calculatePath() throws UnreachablePointException {
        fillGrid();
        traverseGrid();
    }

    /**
     * Fills a grid with values corresponding to distance from the end point.
     * Points are filled in a wave pattern since neighboring nodes will be
     * equidistant.
     */
    private void fillGrid() {
        ConcurrentLinkedQueue<Node> fillQueue = new ConcurrentLinkedQueue<>();
        Node startNode = grid.getNode(start);
        Node endNode = grid.getNode(end);
        int fillValue = LinkedGrid.BLOCKED + 1;

        // Fill the nodes breadth-first by continously queueing the neighbors
        fillQueue.add(endNode);
        while (fillQueue.size() != 0) {
            int nodesToFill = fillQueue.size();

            while (nodesToFill != 0) {
                Node fillNode = fillQueue.poll();
                fillNode.setValue(fillValue);

                if (fillNode == startNode) {
                    fillQueue.clear();
                    break;
                }

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
    private void queueNeighbors(ConcurrentLinkedQueue<Node> queue, Node node) {
        Node[] neighbors = node.getNeighbors();
        for (Node neighbor : neighbors) {
            int nodeValue;
            if (neighbor != null) {
                nodeValue = neighbor.getValue();
            } else {
                continue;
            }

            if (nodeValue == LinkedGrid.UNFILLED) {
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
        Node current = grid.getNode(coordinates);

        Node north = current.getNorth();
        Node south = current.getSouth();
        Node west = current.getWest();
        Node east = current.getEast();
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
     * @return True if the second node is traversable from the first.
     * @throws NullPointerException If the first Node is null since this
     *                              indicates a linked grid with a null link.
     */
    private boolean isNextNode(Node first, Node second) {
        if (first == null) {
            throw new NullPointerException("Linked grid has a null link");
        }

        if (second == null) {
            return false;
        }

        boolean isLess;
        int secondVal = second.getValue();
        int comparison = second.compareTo(first);
        if (secondVal == LinkedGrid.UNFILLED
            || secondVal == LinkedGrid.BLOCKED) {
            isLess = false;
        } else if (comparison == -1) {
            isLess = true;
        } else { // Higher or equal value
            isLess = false;
        }

        return isLess;
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
        LinkedGrid grid = new LinkedGrid(6, 12);
        grid.getNode(0, 1).setValue(LinkedGrid.BLOCKED);
        grid.getNode(1, 1).setValue(LinkedGrid.BLOCKED);
        grid.getNode(2, 1).setValue(LinkedGrid.BLOCKED);
        grid.getNode(3, 1).setValue(LinkedGrid.BLOCKED);
        grid.getNode(4, 1).setValue(LinkedGrid.BLOCKED);
        grid.getNode(5, 3).setValue(LinkedGrid.BLOCKED);
        grid.getNode(4, 3).setValue(LinkedGrid.BLOCKED);
        grid.getNode(3, 3).setValue(LinkedGrid.BLOCKED);
        grid.getNode(2, 3).setValue(LinkedGrid.BLOCKED);
        grid.getNode(1, 3).setValue(LinkedGrid.BLOCKED);

        System.out.println(grid);

        Point2D start = new Point2D(4, 8);
        Point2D end = new Point2D(2, 2);
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
