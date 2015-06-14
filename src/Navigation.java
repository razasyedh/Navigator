/**
 * A class that navigates between points must return a path consisting of
 * {@link Point2D} objects.
 */
interface Navigation {
    /**
     * Returns the points in the path determined by the pathfinding algorithm.
     *
     * @return The calculated path.
     */
    public Point2D[] getPath();
}
