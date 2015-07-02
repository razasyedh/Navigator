import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/**
 * Instantiates the navigator GUI for pathfinding.
 */
public class Navigator {
    /**
     * Instantiates the GUI for the navigator.
     */
    public static void main(String[] args) {
        DrawFrame frame = new DrawFrame();
        frame.setVisible(true);
    }
}

/**
 * A GUI for creating and manipulating a fixed-size grid for pathfinding.
 */
class DrawFrame extends JFrame {
    /** The width of the nodes in the created grid. */
    private static final int GRID_WIDTH = 15;
    /** The height of the nodes in the created grid. */
    private static final int GRID_HEIGHT = 10;

    /** The cursor used to indicate movability depending on the platform. */
    private Cursor platformMoveCursor;
    /** The calculated window width based on the grid width. */
    private int windowWidth;
    /** The calculated window height based on the grid height. */
    private int windowHeight;

    /** The mode of the cursor depending on the type of node it is over. */
    private String cursorMode;
    /** The canvas that draws a grid as well as it's points and path. */
    private GridCanvas gridCanvas;

    private final LinkedGrid grid;
    private final PathFinder pathFinder;
    private Point2D start, end;
    private Point2D[] path;

    /**
     * Sets up everything necessary for the GUI to function.
     */
    public DrawFrame() {
        grid = new LinkedGrid(GRID_HEIGHT, GRID_WIDTH);
        setDefaults();
        pathFinder = new PathFinder(grid, start, end);

        setPlatformProperties();
        applySettings();
        addComponents();
        setListeners();

        navigate();
    }

    /**
     * Sets defaults.
     */
    private void setDefaults() {
        final int WINDOW_BAR_HEIGHT = 22;
        windowWidth = GRID_WIDTH * GridCanvas.SPAN + 2 * GridCanvas.PADDING;
        windowHeight = GRID_HEIGHT * GridCanvas.SPAN + 2 * GridCanvas.PADDING
                       +  WINDOW_BAR_HEIGHT;

        start = new Point2D(0, 0);
        end = new Point2D(0, 3);
        grid.getNode(0, 2).setValue(PathFinder.BLOCKED);
    }

    /**
     * Sets GUI properties depending on whether the platform is OS X.
     */
    public void setPlatformProperties() {
        final int MENU_BAR_HEIGHT = 20;
        boolean isMacOSX = System.getProperty("os.name").startsWith("Mac OS X");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        if (isMacOSX) {
            platformMoveCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        } else {
            platformMoveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
            windowHeight += MENU_BAR_HEIGHT;
        }
    }

    /**
     * Applies settings for the main application window.
     */
    private void applySettings() {
        setSize(windowWidth, windowHeight);
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

        JMenuBar menuBar = new JMenuBar();
        JMenu editMenu = new JMenu("Edit");
        JMenuItem clear = new JMenuItem("Clear Blocked Nodes");
        clear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGrid();
            }
        });
        editMenu.add(clear);

        JMenu viewMenu = new JMenu("View");
        JMenuItem debug = new JMenuItem("Toggle Debug Mode");
        debug.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0));
        debug.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gridCanvas.toggleDebug();
                gridCanvas.repaint();
            }
        });
        viewMenu.add(debug);

        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Adds listeners for various events.
     */
    private void setListeners() {
        MouseMoveListener ml = new MouseMoveListener();
        gridCanvas.addMouseListener(ml);
        gridCanvas.addMouseMotionListener(ml);
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
    public void navigate() {
        path = pathFinder.getPath();
        gridCanvas.setPath(path);
        gridCanvas.repaint();
    }

    /**
     * A listener that responds to mouse movement and clicks to update the
     * interface.
     */
    class MouseMoveListener extends MouseAdapter {
        /** The x coordinate of the current click. */
        private double clickX;
        /** The y coordinate of the current click. */
        private double clickY;
        /** If the click landed on a circle, it's grid coordinates. */
        private Point2D p;

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                updateClickLocation(e);

                if (p == null) {
                    cursorMode = "";
                } else if (p.equals(start)) {
                    cursorMode = "Start";
                } else if (p.equals(end)) {
                    cursorMode = "End";
                } else {
                    cursorMode = "Block";
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                updateClickLocation(e);

                // Reset
                if (p == null) {
                    gridCanvas.setMoveIndicator(0, 0, "");
                    gridCanvas.repaint();
                    return;
                }

                toggleCircle(p);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            updateClickLocation(e);

            if (p == null) {
                setCursor(
                    Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                );
            } else if (p.equals(start) || p.equals(end)) {
                setCursor(
                    platformMoveCursor
                );
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

        /**
         * Use the x and y coordinates of the current click and locate if the
         * click coincided with a circle.
         */
        private void updateClickLocation(MouseEvent e) {
            clickX = (double) e.getX();
            clickY = (double) e.getY();
            p = gridCanvas.findCircle(clickX, clickY);
        }

        /**
         * Toggles the state of the circle clicked on.
         *
         * @param p The coordinates of the circle.
         */
        private void toggleCircle(Point2D p) {
            if (cursorMode.equals("Start")) {
                // Let the start point replace a blocked node
                if (grid.getNode(p).getValue() == PathFinder.BLOCKED) {
                    grid.getNode(p).setValue(PathFinder.UNFILLED);
                    pathFinder.update();
                }

                gridCanvas.setStart(p);
                pathFinder.setStart(p);

                // Switch start and end points.
                // Have to setStart() first because it bypasses calculatePath()
                // in PathFinder so it allows us to temporarily have the start
                // and end be the same.
                if (p.equals(end)) {
                    end = start;
                    gridCanvas.setEnd(start);
                    pathFinder.setEnd(start);
                }

                start = p;
            } else if (cursorMode.equals("End")) {
                // Switch end and start points
                if (p.equals(start)) {
                    start = end;
                    gridCanvas.setStart(end);
                    pathFinder.setStart(end);
                }

                gridCanvas.setEnd(p);
                pathFinder.setEnd(p);
                end = p;
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
    }
}
