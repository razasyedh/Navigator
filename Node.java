/**
 * A node that is aware of it's neighbors in 4 directions. The node holds a
 * single value.
 */
public class Node implements Comparable<Node> {
    /** The node to the north. */
    private Node north;
    /** The node to the south. */
    private Node south;
    /** The node to the west. */
    private Node west;
    /** The node to the east. */
    private Node east;
    /** The node's value. */
    private int value;

    /**
     * Sets the node's northern neighbor.
     *
     * @param north The northern node.
     */
    public void setNorth(Node north) {
        this.north = north;
    }

    /**
     * Sets the node's southern neighbor.
     *
     * @param south The souther node.
     */
    public void setSouth(Node south) {
        this.south = south;
    }

    /**
     * Sets the node's western neighbor.
     *
     * @param west The western node.
     */
    public void setWest(Node west) {
        this.west = west;
    }

    /**
     * Sets the node's eastern neighbor.
     *
     * @param east The eastern node.
     */
    public void setEast(Node east) {
        this.east = east;
    }

    /**
     * Sets the nodes value.
     *
     * @param value The integer value to set.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Gets the node's northern neighbor.
     *
     * @return The norther node.
     */
    public Node getNorth() {
        return north;
    }

    /**
     * Gets the node's southern neighbor.
     *
     * @return The southern node.
     */
    public Node getSouth() {
        return south;
    }

    /**
     * Gets the node's western neighbor.
     *
     * @return The western node.
     */
    public Node getWest() {
        return west;
    }

    /**
     * Gets the node's eastern neighbor.
     *
     * @return The eastern node.
     */
    public Node getEast() {
        return east;
    }

    /**
     * Returns all of the node's neighbors.
     *å
     * @return The neighbors as an array.
     */
    public Node[] getNeighbors() {
        Node[] neighbors = {north, south, east, west};
        return neighbors;
    }

    /**
     * Returns the value of the node.
     *
     * @return The value.
     */
    public int getValue() {
        return value;
    }

    /**
     * Compares the value of the node to another node.
     *
     * @return 0 if the nodes have equal values, a positive integer if the
     *         node is greater, and a negative integer otherwise.
     */
    @Override
    public int compareTo(Node other) {
        int compareResult;

        if (this.value > other.getValue()) {
            compareResult = 1;
        }
        else if (this.value < other.getValue()) {
            compareResult = -1;
        }
        else {
            compareResult = 0;
        }

        return compareResult;
    }

    /**
     * Returns a string representation of the node.
     *
     * @return The nodes value.
     */
    @Override
    public String toString() {
        return Integer.toString(value);
    }

    public static void main(String[] args) {
        Node test = new Node();
        System.out.println("Value: " + test + "\n" + "North: "
                           + test.getNorth());
    }
}
