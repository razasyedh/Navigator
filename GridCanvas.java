import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.Graphics;

class GridCanvas extends JPanel {
    private final int DIAMETER = 29;
    private final int PADDING = 5;
    private final int SPAN = DIAMETER + 2 * PADDING;
    private final Color COLOR_BLACK = new Color(30, 30, 30);
    private final Color COLOR_RED = new Color(245, 60, 20);
    private final float FONT_SIZE = 22.0F;
    private final int FONT_OFFSET_X = 5;
    private final int FONT_OFFSET_Y = 8;

    private int x, y;
    private LinkedGrid grid;
    private Point2D start, end;
    private Point2D[] path;
    private Ellipse2D[][] circles;

    public GridCanvas(int x, int y, LinkedGrid grid, Point2D start,
                      Point2D end) {
        this.x = x;
        this.y = y;
        this.grid = grid;
        this.start = Point2D.reverse(start);
        this.end = Point2D.reverse(end);

        circles = new Ellipse2D[x][y];
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // Make circles look rounder
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        for (int row = 0; row < x; row++) {
            for (int col = 0; col < y; col++) {
                // Determine node color
                Color fillColor;
                int nodeValue = grid.getNode(row, col).getValue();
                if (nodeValue == LinkedGrid.UNFILLED) {
                    fillColor = COLOR_BLACK;
                } else if (nodeValue == LinkedGrid.BLOCKED) {
                    fillColor = COLOR_RED;
                } else {
                    // Color the node based on it's fill value corresponding
                    // to it's distance from the start point
                    fillColor = new Color(
                        nodeValue * 2 % 256,
                        (255 - nodeValue) / 3,
                        nodeValue * 10 % 256);
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
        if (start != null) {
            drawIndicator(g2, start, "S");
        }

        if (end != null) {
            drawIndicator(g2, end, "E");
        }


        for (int row = 0; row < x; row++) {
            for (int col = 0; col < y; col++) {
                int nodeValue = grid.getNode(row, col).getValue();
                if (nodeValue == LinkedGrid.BLOCKED) {
                    drawIndicator(g2, new Point2D(col, row), "B");
                }
            }
        }

        if (path != null) {
            drawPath(g2);
        }
    }

    private void drawIndicator(Graphics2D g2, Point2D point, String ch) {
        g2.setPaint(Color.WHITE);
        Font defaultFont = g2.getFont();
        Font scaledFont = defaultFont.deriveFont(FONT_SIZE);
        g2.setFont(scaledFont);

        int pointX = point.getX();
        int pointY = point.getY();
        int centerX = (int) circles[pointY][pointX].getCenterX();
        int centerY = (int) circles[pointY][pointX].getCenterY();
        int circleX = centerX - FONT_OFFSET_X;
        int circleY = centerY + FONT_OFFSET_Y;

        g2.drawString(ch, circleX, circleY);
    }

    private void drawPath(Graphics2D g2) {
        for (int i = 0; i < path.length - 1; i++) {
            Point2D p1 = path[i];
            Point2D p2 = path[i+1];

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

    public void setStart(Point2D start) {
        this.start = Point2D.reverse(start);
    }

    public void setEnd(Point2D end) {
        this.end = Point2D.reverse(end);
    }

    public void setGrid(LinkedGrid grid) {
        this.grid = grid;
    }

    public void setPath(Point2D[] path) {
        this.path = path;
    }

    public Ellipse2D[][] getCircles() {
        return circles;
    }
}
