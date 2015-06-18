package com.syedraza.WaveFront;

/**
 * A node that is aware of it's neighbors in 4 directions.
 */
public class DNode extends Node implements Compass<DNode> {
    /** The node to the north. */
    private DNode north;
    /** The node to the northwest. */
    private DNode northWest;
    /** The node to the west. */
    private DNode west;
    /** The node to the southwest. */
    private DNode southWest;
    /** The node to the south. */
    private DNode south;
    /** The node to the southeast. */
    private DNode southEast;
    /** The node to the east. */
    private DNode east;
    /** The node to the northeast. */
    private DNode northEast;

    @Override
    public void setNorth(DNode north) {
        this.north = north;
    }

    /**
     * Sets the node's northwestern neighbor.
     *
     * @param northWest The northwestern node.
     */
    public void setNorthWest(DNode northWest) {
        this.northWest = northWest;
    }

    @Override
    public void setWest(DNode west) {
        this.west = west;
    }

    /**
     * Sets the node's southwestern neighbor.
     *
     * @param southWest The southwestern node.
     */
    public void setSouthWest(DNode southWest) {
        this.southWest = southWest;
    }

    @Override
    public void setSouth(DNode south) {
        this.south = south;
    }

    /**
     * Sets the node's southeastern neighbor.
     *
     * @param southEast The southeastern node.
     */
    public void setSouthEast(DNode southEast) {
        this.southEast = southEast;
    }

    @Override
    public void setEast(DNode east) {
        this.east = east;
    }

    /**
     * Sets the node's northerneastern neighbor.
     *
     * @param northEast The northerneastern node.
     */
    public void setNorthEast(DNode northEast) {
        this.northEast = northEast;
    }

    @Override
    public DNode getNorth() {
        return north;
    }

    /**
     * Gets the node's northwestern neighbor.
     *
     * @return The northwestern node.
     */
    public DNode getNorthWest() {
        return northWest;
    }

    @Override
    public DNode getWest() {
        return west;
    }

    /**
     * Gets the node's southwestern neighbor.
     *
     * @return The southwestern node.
     */
    public DNode getSouthWest() {
        return southWest;
    }

    @Override
    public DNode getSouth() {
        return south;
    }

    /**
     * Gets the node's southeastern neighbor.
     *
     * @return The southeastern node.
     */
    public DNode getSouthEast() {
        return southEast;
    }

    @Override
    public DNode getEast() {
        return east;
    }

    /**
     * Gets the node's northerneastern neighbor.
     *
     * @return The northerneastern node.
     */
    public DNode getNorthEast() {
        return northEast;
    }

    /**
     * Returns all of the node's neighbors.
     *
     * @return The neighbors as an array.
     */
    public DNode[] getNeighbors() {
        return new DNode[]{north, northWest, west, southWest, south,
                             southEast, east, northEast};
    }
}
