package com.syedraza.WaveFront;

/**
 * Methods a class should implement so that a {@link PathFinder} can navigate
 * nodes based on relative directions.
 *
 * @param <E> the type of the node.
 */
public interface Compass<E> {
    /**
     * Returns the node to the north relative to the current node.
     *
     * @return The northern node.
     */
    E getNorth();

    /**
     * Returns the node to the south relative to the current node.
     *
     * @return The southern node.
     */
    E getSouth();

    /**
     * Returns the node to the east relative to the current node.
     *
     * @return The eastern node.
     */
    E getEast();

    /**
     * Returns the node to the west relative to the current node.
     *
     * @return The western node.
     */
    E getWest();

    /**
     * Sets the node's northern neighbor.
     *
     * @param e The northern node.
     */
    void setNorth(E e);

    /**
     * Sets the node's southern neighbor.
     *
     * @param e The southern node.
     */
    void setSouth(E e);

    /**
     * Sets the node's eastern neighbor.
     *
     * @param e The eastern node.
     */
    void setEast(E e);

    /**
     * Sets the node's western neighbor.
     *
     * @param e The western node.
     */
    void setWest(E e);
}
