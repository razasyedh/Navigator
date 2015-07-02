/**
 * A node that is aware of it's neighbors in 4 directions.
 */
public class DNode extends Node implements Compass<DNode>
{
    /** The node to the north. */
    private DNode north;
    /** The node to the south. */
    private DNode south;
    /** The node to the west. */
    private DNode west;
    /** The node to the east. */
    private DNode east;

    /**
     * Sets the node's northern neighbor.
     *
     * @param north The northern node.
     */
    public void setNorth(DNode north) {
        this.north = north;
    }

    /**
     * Sets the node's southern neighbor.
     *
     * @param south The souther node.
     */
    public void setSouth(DNode south) {
        this.south = south;
    }

    /**
     * Sets the node's western neighbor.
     *
     * @param west The western node.
     */
    public void setWest(DNode west) {
        this.west = west;
    }

    /**
     * Sets the node's eastern neighbor.
     *
     * @param east The eastern node.
     */
    public void setEast(DNode east) {
        this.east = east;
    }

    /**
     * Gets the node's northern neighbor.
     *
     * @return The norther node.
     */
    public DNode getNorth() {
        return north;
    }

    /**
     * Gets the node's southern neighbor.
     *
     * @return The southern node.
     */
    public DNode getSouth() {
        return south;
    }

    /**
     * Gets the node's western neighbor.
     *
     * @return The western node.
     */
    public DNode getWest() {
        return west;
    }

    /**
     * Gets the node's eastern neighbor.
     *
     * @return The eastern node.
     */
    public DNode getEast() {
        return east;
    }

    /**
     * Returns all of the node's neighbors.
     *
     * @return The neighbors as an array.
     */
    public DNode[] getNeighbors() {
        DNode[] neighbors = {north, south, east, west};
        return neighbors;
    }
}
