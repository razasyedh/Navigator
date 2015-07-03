package com.syedraza.Navigator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

import com.syedraza.WaveFront.Grid;
import com.syedraza.WaveFront.PathFinder;
import com.syedraza.WaveFront.Point2D;

/**
 * Draws a visual representation of a grid with colored and labeled indicators
 * for the different types of nodes.
 */
class GridCanvas extends JPanel {
    /** The diameter of a node. */
    public static final int DIAMETER = 29;
    /** The padding on one side of a node. */
    public static final int PADDING = 5;
    /** The diameter along with the padding on both sides of a node. */
    public static final int SPAN = DIAMETER + 2 * PADDING;
    /** The size of labels so they take up the full space of a node. */
    private final float FONT_SIZE = 22.0F;
    /** How much to shift a letter to the left to center it. */
    private final int FONT_OFFSET_X = 6;
    /** How much to shift a letter down from the baseline to center it. */
    private final int FONT_OFFSET_Y = 8;

    /** The height of the canvas. */
    private final int height;
    /** The width of the canvas. */
    private final int width;
    /** The grid to read blocked nodes from. */
    private final Grid grid;
    /** A start point to label. */
    private Point2D start;
    /** An end point to label. */
    private Point2D end;
    /** The calculated path to draw. */
    private Point2D[] path;
    /** The graphical nodes. */
    private final Ellipse2D[][] circles;

    /** The x coordinate of a floating indicator. */
    private int moveIndicatorX;
    /** The y coordinate of a floating indicator. */
    private int moveIndicatorY;
    /** The text the label for a floating indicator. */
    private String moveIndicatorLabel;

    /** Whether debug mode is enabled. */
    private boolean debugOn;

    /**
     * Creates a canvas of the given size and properties.
     *
     * @param height The height of the canvas.
     * @param width The width of the canvas.
     * @param grid A grid to read values from.
     * @param start The start point.
     * @param end The end point.
     */
    public GridCanvas(int height, int width, Grid grid, Point2D start,
                      Point2D end) {
        this.height = height;
        this.width = width;
        this.grid = grid;
        this.start = Point2D.reverse(start);
        this.end = Point2D.reverse(end);
        this.moveIndicatorLabel = "";

        circles = new Ellipse2D[height][width];
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
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // Determine circle color
                Color fillColor;
                int nodeValue = grid.getNode(row, col).getValue();
                if (nodeValue == PathFinder.UNFILLED) {
                    fillColor = Color.BLACK;
                } else if (nodeValue == PathFinder.BLOCKED) {
                    fillColor = Color.RED;
                } else {
                    final int RED_FACTOR = 3;
                    // Color the circle based on it's fill value, which
                    // corresponds to it's distance from the start point
                    int nodeColor = (int) ((nodeValue - minValue) * interval);
                    int red = nodeColor / RED_FACTOR;
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

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
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
            g2.setColor(Color.BLACK); // For visibility
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
     * Finds the grid coordinates of a circle that was clicked on.
     *
     * @param x The x coordinate of the click.
     * @param y The y coordinate of the click.
     * @return The coordinates of the circle or {@code null} if one wasn't
     *         clicked.
     */
    public Point2D findCircle(double x, double y) {
        for (int i = 0; i < circles.length; i++) {
            for (int j = 0; j < circles[i].length; j++) {
                Ellipse2D circle = circles[i][j];
                if (circle != null && circle.contains(x, y)) {
                    return new Point2D(i, j);
                }
            }
        }

        return null;
    }

    /**
     * Updates the location of a indicator that was moved.
     *
     * @param x The x coordinate of the indicator.
     * @param y The y coordinate of the indicator.
     * @param label The text of the label for the indicator. If it is an empty
     *              string, indicators will be drawn in their non-floating
     *              positions.
     */
    public void updateMoveIndicator(int x, int y, String label) {
        moveIndicatorX = x - FONT_OFFSET_X;
        moveIndicatorY = y + FONT_OFFSET_Y;
        moveIndicatorLabel = label;
    }

    /**
     * Updates the start point to draw.
     *
     * @param start The start point.
     */
    public void setStart(Point2D start) {
        this.start = Point2D.reverse(start);
    }

    /**
     * Updates the end point to draw.
     *
     * @param end The end point.
     */
    public void setEnd(Point2D end) {
        this.end = Point2D.reverse(end);
    }

    /**
     * Sets the path to draw.
     *
     * @param path The path of 2D points. If the path is {@code null}, no path
     *             will be drawn.
     */
    public void setPath(Point2D[] path) {
        this.path = path;
    }

    /**
     * Toggles debug mode. The default is off.
     */
    public void toggleDebug() {
        debugOn = !debugOn;
    }

    /**
     * Returns the start point used by the canvas.
     *
     * @return The start point.
     */
    public Point2D getStart() {
        return start;
    }

    /**
     * Returns the end point used by the canvas.
     *
     * @return The end point.
     */
    public Point2D getEnd() {
        return end;
    }

    /**
     * Returns the path used by the canvas.
     *
     * @return path.
     */
    public Point2D[] getPath() {
        return path;
    }
}
