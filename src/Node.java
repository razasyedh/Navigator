/**
 * A node in a grid that holds a value.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
public class Node implements Comparable<Node> {
    /** The node's value. */
    private int value;

    /**
     * Sets the nodes value.
     *
     * @param value The integer value to set.
     */
    public void setValue(int value) {
        this.value = value;
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
        } else if (this.value < other.getValue()) {
            compareResult = -1;
        } else {
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
}
