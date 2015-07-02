/**
 * Methods a class should implement so that a {@link PathFinder} can navigate
 * nodes based on relative directions.
 */
interface Compass<E> {
    E getNorth();
    E getSouth();
    E getEast();
    E getWest();
    void setNorth(E e);
    void setSouth(E e);
    void setEast(E e);
    void setWest(E e);
}
