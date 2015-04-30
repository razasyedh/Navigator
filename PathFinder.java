import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Vector;

public class PathFinder {
    private final LinkedGrid grid;
    private final Point2D start;
    private final Point2D end;
    private final Vector<Point2D> path;

    // Preconditions: No null links, and a non-blocked path
    public PathFinder(LinkedGrid grid, Point2D start, Point2D end)
            throws UnreachablePointException {
        this.grid = grid;
        this.start = start;
        this.end = end;

        path = new Vector<>();
        calculatePath();
    }

    private void calculatePath() throws UnreachablePointException {
        fillGrid();
        traverseGrid();
    }

    private void fillGrid() {
        ConcurrentLinkedQueue<Node> fillQueue = new ConcurrentLinkedQueue<>();
        Node startNode = grid.getNode(start);
        Node endNode = grid.getNode(end);
        int fillValue = LinkedGrid.BLOCKED + 1;

        fillQueue.add(endNode);
        while (fillQueue.size() != 0) {
            int nodesToFill = fillQueue.size();

            while (nodesToFill != 0) {
                Node fillNode = fillQueue.poll();
                fillNode.setValue(fillValue);

                queueNeighbors(fillQueue, fillNode);
                nodesToFill--;
            }

            fillValue++;
        }
    }

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

    private void traverseGrid() throws UnreachablePointException {
        Point2D coordinates = start;
        while (!coordinates.equals(end)) {
            path.add(coordinates);
            coordinates = getNextNode(coordinates);
        }
        path.add(end);
    }

    // Find first node with a lower value
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

    // Precondition: first can't be null
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

    public Point2D[] getPath() {
        Point2D[] emptyArray = new Point2D[path.size()];
        return path.toArray(emptyArray);
    }

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
        // grid.getNode(0, 1).setValue(LinkedGrid.BLOCKED);
        // grid.getNode(1, 1).setValue(LinkedGrid.BLOCKED);
        // grid.getNode(2, 1).setValue(LinkedGrid.BLOCKED);
        // // grid.getNode(3, 1).setValue(LinkedGrid.BLOCKED);
        // grid.getNode(4, 1).setValue(LinkedGrid.BLOCKED);
        // // grid.getNode(5, 3).setValue(LinkedGrid.BLOCKED);
        // grid.getNode(4, 3).setValue(LinkedGrid.BLOCKED);
        // grid.getNode(3, 3).setValue(LinkedGrid.BLOCKED);
        // // grid.getNode(2, 3).setValue(LinkedGrid.BLOCKED);
        // grid.getNode(1, 3).setValue(LinkedGrid.BLOCKED);

        System.out.println(grid);

        Point2D start = new Point2D(2, 2);
        Point2D end = new Point2D(5, 11);
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
