import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import javax.swing.border.*;
import javax.swing.JOptionPane;
import java.util.ArrayList;

/**
 * Instantiates the navigator GUI for pathfinding.
 */
public class Navigator {
    public static void main(String[] args) {
        DrawFrame frame = new DrawFrame();
        frame.setVisible(true);
    }
}

/**
 * A GUI for creating and manipulating a fixed-size grid for pathfinding.
 */
class DrawFrame extends JFrame {
    private static final int GRID_WIDTH = 15;
    private static final int GRID_HEIGHT = 10;
    private final boolean isMacOSX;
    private String cursorMode;
    private GridCanvas gridCanvas;

    private final LinkedGrid grid;
    private final PathFinder pathFinder;
    private Point2D start, end;
    private Point2D[] path;

    /**
     * Sets up everything necessary for the GUI to function.
     */
    public DrawFrame() {
        isMacOSX = System.getProperty("os.name").startsWith("Mac OS X");
        grid = new LinkedGrid(GRID_HEIGHT, GRID_WIDTH);
        setDefaults();

        applySettings();
        addComponents();
        setListeners();

        pathFinder = new PathFinder(grid, start, end);
        navigate();
    }

    /**
     * Sets defaults so users can see an example on startup.
     */
    private void setDefaults() {
        start = new Point2D(0, 0);
        end = new Point2D(0, 3);
        grid.getNode(0, 2).setValue(PathFinder.BLOCKED);
    }

    /**
     * Applies seetings for the main application window.
     */
    private void applySettings() {
        setSize(595, 425);
        setLocationRelativeTo(null);
        setTitle("Navigator");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
    }

    /**
     * Adds the components of the interface.
     */
    private void addComponents() {
        gridCanvas = new GridCanvas(GRID_HEIGHT, GRID_WIDTH, grid, start, end);
        gridCanvas.setFocusable(true);
        add(gridCanvas);
    }

    /**
     * Adds listeners for various events.
     */
    private void setListeners() {
        gridCanvas.addMouseListener(new ToggleClickListener());
        gridCanvas.addMouseMotionListener(new MouseMoveListener());
        gridCanvas.addKeyListener(new EscapeListener());
    }

    /**
     * Resets the path.
     */
    public void resetPath() {
        path = null;
        gridCanvas.setPath(null);
    }

    /**
     * Resets all blocked nodes in the grid.
     */
    public void resetGrid() {
        resetPath();
        pathFinder.reset();
        navigate();
    }

    /**
     * Obtains and shows the path between the start and end point.
     */
    public void navigate(){
        path = pathFinder.getPath();
        gridCanvas.setPath(path);
        gridCanvas.repaint();
    }

    /**
     * A listener that checks for the escape key to reset the cursor
     * after a toggle button was clicked.
     */
    class EscapeListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                resetGrid();
            }
        }
    }

    /**
     * Listens for mouse movement to change the cursor and to float labels
     * when dragged.
     */
    class MouseMoveListener extends MouseMotionAdapter {
        private double clickX, clickY;

        @Override
        public void mouseMoved(MouseEvent e) {
            clickX = (double) e.getX();
            clickY = (double) e.getY();
            Point2D p = gridCanvas.findCircle(clickX, clickY);

            if (p == null) {
                setCursor(
                    Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                );
            } else if (p.equals(start) || p.equals(end)){
                if (isMacOSX) {
                    setCursor(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    );
                } else { // Doesn't show up on OS X
                    setCursor(
                        Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
                    );
                }
            } else {
                setCursor(
                    Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
                );
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (cursorMode.equals("Start") || cursorMode.equals("End")) {
                gridCanvas.setMoveIndicator(e.getX(), e.getY(), cursorMode);
                gridCanvas.repaint();
            }
        }
    }

    /**
     * A listener that locates the circle that was clicked and toggles it.
     */
    class ToggleClickListener extends MouseAdapter {
        private double clickX, clickY;
        private Point2D p1, p2;

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                clickX = (double) e.getX();
                clickY = (double) e.getY();
                p1 = gridCanvas.findCircle(clickX, clickY);

                if (p1 == null) {
                    return;
                } else if (p1.equals(start)){
                    cursorMode = "Start";
                } else if (p1.equals(end)){
                    cursorMode = "End";
                } else {
                    cursorMode = "Block";
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                clickX = (double) e.getX();
                clickY = (double) e.getY();
                p2 = gridCanvas.findCircle(clickX, clickY);

                // Reset
                if (p2 == null) {
                    gridCanvas.setMoveIndicator(0, 0, "");
                    gridCanvas.repaint();
                    return;
                }

                // If we dragged while trying to block, ignore
                if (cursorMode.equals("Block") && !p1.equals(p2)) {
                    return;
                }

                toggleCircle(p2);
            }
        }

        /**
         * Toggles the state of the circle clicked on.
         *
         * @param p The coordinates of the circle.
         */
        private void toggleCircle(Point2D p) {
            if (cursorMode.equals("Start")) {
                if (p.equals(end)) {
                    displayStartEqualEndError();
                    gridCanvas.requestFocusInWindow();
                    return;
                }

                if (grid.getNode(p).getValue() == PathFinder.BLOCKED) {
                    grid.getNode(p).setValue(PathFinder.UNFILLED);
                    pathFinder.update();
                }

                start = p;
                gridCanvas.setStart(p);
                pathFinder.setStart(p);
            } else if (cursorMode.equals("End")) {
                if (p.equals(start)) {
                    displayStartEqualEndError();
                    gridCanvas.requestFocusInWindow();
                    return;
                }

                end = p;
                gridCanvas.setEnd(p);
                pathFinder.setEnd(p);
            }

            if (cursorMode.equals("Block")) {
                Node n = grid.getNode(p);
                // Toggle whether the node is blocked
                if (n.getValue() == PathFinder.BLOCKED) {
                    n.setValue(PathFinder.UNFILLED);
                } else {
                    n.setValue(PathFinder.BLOCKED);
                }

                pathFinder.update();
            }

            resetPath();
            navigate();
        }

        /**
         * Displays an error when trying to overlap the start and end points.
         */
        private void displayStartEqualEndError() {
            JOptionPane.showMessageDialog(
                null, "The end point cannot be the same as the start point",
                "Error", JOptionPane.ERROR_MESSAGE
            );
            gridCanvas.setMoveIndicator(0, 0, "");
            gridCanvas.repaint();
        }
    }
}
