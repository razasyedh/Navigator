/**
 * Methods a class should implement so that a {@link PathFinder} can navigate
 * nodes based on relative directions.
 */
interface Compass<E> {
    public E getNorth();
    public E getSouth();
    public E getEast();
    public E getWest();

    public void setNorth(E e);
    public void setSouth(E e);
    public void setEast(E e);
    public void setWest(E e);
}
