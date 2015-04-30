import java.util.Vector;

public class PathFinder {
    private LinkedGrid grid;
    private Point2D start;
    private Point2D end;
    private Vector<Point2D> path;

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
        Node endNode = grid.getNode(end);
        int fillValue = LinkedGrid.BLOCKED + 1;

        endNode.setValue(fillValue);
        fillNeighbors(endNode, fillValue + 1);
    }

    // TODO: Build a list of nodes to fill and fill in order
    private void fillNeighbors(Node node, int fillValue) {
        Node[] neighbors = node.getNeighbors();
        Vector<Node> fillableNodes = new Vector<>(4);
        for (Node neighbor : neighbors) {
            int nodeValue;
            if (neighbor != null) {
                nodeValue = neighbor.getValue();
            } else {
                continue;
            }

            if (nodeValue == LinkedGrid.UNFILLED) {
                neighbor.setValue(fillValue);
                fillableNodes.add(neighbor);
            }
        }

        // System.out.println(grid);

        for (Node neighbor : fillableNodes) {
            fillNeighbors(neighbor, fillValue + 1);
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
        if (secondVal == LinkedGrid.BLOCKED) {
            isLess = false;
        } else if (secondVal == LinkedGrid.UNFILLED) {
            throw new RuntimeException("Unfilled node reachable");
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
        LinkedGrid grid = new LinkedGrid(5, 5);
        Point2D point = new Point2D(1, 0);
        // System.exit(1);
        // grid.getNode(0, 1).setValue(LinkedGrid.BLOCKED);
        // grid.getNode(1, 3).setValue(LinkedGrid.BLOCKED);
        System.out.println(grid);

        Point2D start = new Point2D(2, 3);
        Point2D end = new Point2D(0, 0);
        System.out.println("Start: " + start);
        System.out.println("End: " + end);

        System.out.println("Calculating...");
        PathFinder wave = null;

        try {
            wave = new PathFinder(grid, start, end);
        } catch (UnreachablePointException e) {
            e.printStackTrace();
        }

        System.out.println(grid);
        System.out.println(wave);
    }
}
