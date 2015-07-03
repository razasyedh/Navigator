package com.syedraza.WaveFront;

/**
 * A node that is aware of it's neighbors in 4 directions.
 */
public class DNode extends Node implements Compass<DNode> {
    /** The node to the north. */
    private DNode north;
    /** The node to the south. */
    private DNode south;
    /** The node to the west. */
    private DNode west;
    /** The node to the east. */
    private DNode east;

    @Override
    public void setNorth(DNode north) {
        this.north = north;
    }

    @Override
    public void setSouth(DNode south) {
        this.south = south;
    }

    @Override
    public void setWest(DNode west) {
        this.west = west;
    }

    @Override
    public void setEast(DNode east) {
        this.east = east;
    }

    @Override
    public DNode getNorth() {
        return north;
    }

    @Override
    public DNode getSouth() {
        return south;
    }

    @Override
    public DNode getWest() {
        return west;
    }

    @Override
    public DNode getEast() {
        return east;
    }

    /**
     * Returns all of the node's neighbors.
     *
     * @return The neighbors as an array.
     */
    public DNode[] getNeighbors() {
        return new DNode[]{north, south, east, west};
    }
}
