import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.Graphics;

/**
 * Draws a visual representation of a grid with colored and labeled indicators
 * for the different types of nodes.
 */
class GridCanvas extends JPanel {
    public static final int DIAMETER = 29;
    public static final int PADDING = 5;
    public static final int SPAN = DIAMETER + 2 * PADDING;
    private final float FONT_SIZE = 22.0F;
    private final int FONT_OFFSET_X = 6;
    private final int FONT_OFFSET_Y = 8;

    private int x, y;
    private Grid grid;
    private Point2D start, end;
    private Point2D[] path;
    private Ellipse2D[][] circles;

    private int moveIndicatorX;
    private int moveIndicatorY;
    private String moveIndicatorLabel;

    private boolean debugOn;

    /**
     * Creates an empty canvas of the given size and properties.
     *
     * @param x The width of the canvas.
     * @param y The height of the canvas.
     * @param grid A linked grid to read values from.
     * @param start The start point.
     * @param end The end point.
     */
    public GridCanvas(int x, int y, Grid grid, Point2D start,
                      Point2D end) {
        this.x = x;
        this.y = y;
        this.grid = grid;
        this.start = Point2D.reverse(start);
        this.end = Point2D.reverse(end);
        this.moveIndicatorLabel = "";

        circles = new Ellipse2D[x][y];
    }

    /**
     * Draws the circles of the grid, as well as any indicators and the
     * calculated path.
     *
     * @param g The graphics object to draw on.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Make circles look rounder
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        // Scale font
        Font defaultFont = g2.getFont();
        Font scaledFont = defaultFont.deriveFont(FONT_SIZE);
        g2.setFont(scaledFont);

        // Calculate a color interval that's evenly distributed among values
        int minValue = PathFinder.BLOCKED + 1;
        int maxValue = grid.getMax();
        double interval = 255.0 / (maxValue - minValue);

        // Draw nodes
        for (int row = 0; row < x; row++) {
            for (int col = 0; col < y; col++) {
                // Determine circle color
                Color fillColor;
                int nodeValue = grid.getNode(row, col).getValue();
                if (nodeValue == PathFinder.UNFILLED) {
                    fillColor = Color.BLACK;
                } else if (nodeValue == PathFinder.BLOCKED) {
                    fillColor = Color.RED;
                } else {
                    // Color the circle based on it's fill value, which
                    // corresponds to it's distance from the start point
                    int nodeColor = (int) ((nodeValue - minValue) * interval);
                    int red = nodeColor / 3;
                    int green = 255 - nodeColor;
                    int blue = nodeColor;
                    fillColor = new Color(red, green, blue);
                }
                g2.setPaint(fillColor);

                circles[row][col] = new Ellipse2D.Double(
                    2 * PADDING + col * SPAN,
                    2 * PADDING + row * SPAN,
                    DIAMETER, DIAMETER
                );
                g2.fill(circles[row][col]);
            }
        }

        // Draw indicators
        g2.setPaint(Color.WHITE);

        if (start != null && !moveIndicatorLabel.equals("Start")) {
            drawIndicator(g2, start, "S");
        }

        if (end != null && !moveIndicatorLabel.equals("End")) {
            drawIndicator(g2, end, "E");
        }

        for (int row = 0; row < x; row++) {
            for (int col = 0; col < y; col++) {
                int nodeValue = grid.getNode(row, col).getValue();
                Point2D p = new Point2D(col, row);

                if (nodeValue == PathFinder.BLOCKED) {
                    drawIndicator(g2, p, "B");
                } else if (debugOn && !p.equals(start) && !p.equals(end)) {
                    // Smaller font needed because values can be 2 digits
                    g2.setFont(defaultFont);
                    drawIndicator(g2, p, Integer.toString(nodeValue));
                    g2.setFont(scaledFont);
                }
            }
        }

        if (path != null && moveIndicatorLabel.equals("")) {
            drawPath(g2);
        }

        if (!moveIndicatorLabel.equals("")) {
            g2.setColor(Color.BLACK); // For visiblity
            String ch = moveIndicatorLabel.substring(0, 1);
            g2.drawString(ch, moveIndicatorX, moveIndicatorY);
        }

        moveIndicatorLabel = "";

        // Print path distance in lower left corner
        if (debugOn) {
            g2.setColor(Color.RED);
            String dist = Integer.toString(path.length - 1);
            g2.drawString(dist, 1.0F, getHeight() - 1.0F);
        }
    }

    /**
     * Draws indicators on special nodes including: start, end, and blocked.
     *
     * @param g2 The graphics object to draw on.
     * @param point The coordinates of the circle to draw on.
     * @param ch The character to label the circle with.
     */
    private void drawIndicator(Graphics2D g2, Point2D point, String ch) {
        int pointX = point.getX();
        int pointY = point.getY();
        int centerX = (int) circles[pointY][pointX].getCenterX();
        int centerY = (int) circles[pointY][pointX].getCenterY();
        int circleX = centerX - FONT_OFFSET_X;
        int circleY = centerY + FONT_OFFSET_Y;

        g2.drawString(ch, circleX, circleY);
    }

    /**
     * Draws the path from the start point to the end point.
     *
     * @param g2 The graphics object to draw on.
     */
    private void drawPath(Graphics2D g2) {
        for (int i = 0; i < path.length - 1; i++) {
            Point2D p1 = path[i];
            Point2D p2 = path[i + 1];

            int p1X = p1.getX();
            int p1Y = p1.getY();
            int p2X = p2.getX();
            int p2Y = p2.getY();
            int p1CenterX = (int) circles[p1X][p1Y].getCenterX();
            int p1CenterY = (int) circles[p1X][p1Y].getCenterY();
            int p2CenterX = (int) circles[p2X][p2Y].getCenterX();
            int p2CenterY = (int) circles[p2X][p2Y].getCenterY();

            g2.drawLine(p1CenterX, p1CenterY, p2CenterX, p2CenterY);
        }
    }

    /**
     * Finds the circle that was clicked on.
     *
     * @return The coordinates of the circle or null if one wasn't clicked.
     */
    public Point2D findCircle(double x, double y) {
        for (int i = 0; i < circles.length; i++) {
            for (int j = 0; j < circles[i].length; j++) {
                Ellipse2D circle = circles[i][j];
                if (circle.contains(x, y)) {
                    return new Point2D(i, j);
                }
            }
        }

        return null;
    }

    public void setMoveIndicator(int x, int y, String label) {
        moveIndicatorX = x - FONT_OFFSET_X;
        moveIndicatorY = y + FONT_OFFSET_Y;
        moveIndicatorLabel = label;
    }

    /**
     * Sets the start point.
     *
     * @param start The start point.
     */
    public void setStart(Point2D start) {
        this.start = Point2D.reverse(start);
    }

    /**
     * Sets the end point.
     *
     * @param end The end point.
     */
    public void setEnd(Point2D end) {
        this.end = Point2D.reverse(end);
    }

    /**
     * Updates the grid to the given grid.
     *
     * @param grid The linked grid.
     */
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    /**
     * Sets the path to draw.
     *
     * @param path The path of 2D points.
     */
    public void setPath(Point2D[] path) {
        this.path = path;
    }

    public void toggleDebug() {
        debugOn = !debugOn;
    }
}
