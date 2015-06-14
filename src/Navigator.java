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
    private String cursorMode;
    private ArrayList<JButton> toggleButtons;
    private GridCanvas gridCanvas;
    private JButton navigateButton, resetButton;

    private LinkedGrid grid;
    private Point2D start, end;
    private Point2D[] path;

    /**
     * Sets up everything necessary for the GUI to function.
     */
    public DrawFrame() {
        grid = new LinkedGrid(GRID_HEIGHT, GRID_WIDTH);
        toggleButtons = new ArrayList<>(4);
        setDefaults();

        applySettings();
        addComponents();
        setListeners();
    }

    /**
     * Sets defaults so users can see an example on startup.
     */
    private void setDefaults() {
        start = new Point2D(0, 0);
        end = new Point2D(0, 3);
        grid.getNode(0, 2).setValue(LinkedGrid.BLOCKED);
    }

    /**
     * Applies seetings for the main application window.
     */
    private void applySettings() {
        setSize(595, 500);
        setLocation(100, 100);
        setTitle("Navigator");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
    }

    /**
     * Adds the components of the interface.
     */
    private void addComponents() {
        gridCanvas = new GridCanvas(GRID_HEIGHT, GRID_WIDTH, grid, start, end);
        add(gridCanvas, BorderLayout.CENTER);

        JPanel options = new JPanel();

        JPanel toggles = new JPanel();
        JButton startButton = new JButton("Start");
        startButton.setToolTipText(UIStrings.startButtonTip);
        toggles.add(startButton);
        toggleButtons.add(startButton);
        JButton endButton = new JButton("End");
        endButton.setToolTipText(UIStrings.endButtonTip);
        toggles.add(endButton);
        toggleButtons.add(endButton);
        JButton blockButton = new JButton("Block");
        blockButton.setToolTipText(UIStrings.blockButtonTip);
        toggles.add(blockButton);
        toggleButtons.add(blockButton);
        JButton noneButton = new JButton("None");
        noneButton.setToolTipText(UIStrings.noneButtonTip);
        toggles.add(noneButton);
        toggleButtons.add(noneButton);

        toggles.setBorder(new TitledBorder("Toggles"));
        options.add(toggles);

        JPanel actions = new JPanel();
        navigateButton = new JButton("Navigate");
        navigateButton.setToolTipText(UIStrings.navigateButtonTip);
        actions.add(navigateButton);
        getRootPane().setDefaultButton(navigateButton);
        resetButton = new JButton("Clear");
        resetButton.setToolTipText(UIStrings.resetButtonTip);

        actions.add(resetButton);
        actions.setBorder(new TitledBorder("Actions"));
        options.add(actions);

        add(options, BorderLayout.SOUTH);
    }

    /**
     * Adds listeners for various events.
     */
    private void setListeners() {
        for (JButton button : toggleButtons) {
            button.addActionListener(new ToggleListener(button.getText()));
            button.addKeyListener(new EscapeListener());
        }

        gridCanvas.addMouseListener(new ToggleClickListener());
        navigateButton.addActionListener(new NavigateClickListener());

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                resetCursor();
                fullReset();
                gridCanvas.repaint();
            }
        });
    }

    /**
     * Resets a point specified by name.
     */
    private void resetPoint(String name) {
        if (name.equals("start")) {
            start = null;
            gridCanvas.setStart(null);
        } else {
            end = null;
            gridCanvas.setEnd(null);
        }

        resetNavigation();
    }

    /**
     * Resets the path.
     */
    public void resetPath() {
        path = null;
        gridCanvas.setPath(null);
    }

    /**
     * Resets everything that has to do with navigation which is the path and
     * the grid.
     */
    private void resetNavigation() {
        resetPath();
        grid.partialReset();
    }

    /**
     * Resets everything including the grid, points, and the path.
     */
    private void fullReset() {
        resetPath();
        resetPoint("start");
        resetPoint("end");
        grid.fullReset();
    }

    /**
     * Resets the cursor to the default.
     */
    public void resetCursor() {
        setCursor(
            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
        );
        cursorMode = null;
    }

    public void navigate(){
            // Calculate the path between the start and end points
            PathFinder pathFinder;
            try {
                pathFinder = new PathFinder(grid, start, end);
            } catch (UnreachablePointException exception) {
                JOptionPane.showMessageDialog(
                    null, UIStrings.unreachableEndpoint, "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                grid.partialReset();
                return;
            }

            System.err.println(grid);
            path = pathFinder.getPath();
            gridCanvas.setPath(path);
            gridCanvas.repaint();
        }

    /**
     * A listener that changes the cursor when a toggle button is clicked.
     */
    class ToggleListener implements ActionListener {
        private String mode;

        /**
         * Creates a listener with the given mode.
         */
        public ToggleListener(String mode) {
            this.mode = mode;
        }

        /**
         * Changes the cursor and mode when a button is clicked.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            cursorMode = mode;
            setCursor(
                Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
            );
        }
    }

    /**
     * A listener that checks for the escape key to reset the cursor
     * after a toggle button was clicked.
     */
    class EscapeListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                resetCursor();
            }
        }
    }

    /**
     * A listener that locates the circle that was clicked and toggles it.
     */
    class ToggleClickListener extends MouseAdapter {
        private Ellipse2D[][] circles;
        private double clickX, clickY;

        /**
         * Finds a circle and toggles it, resetting the cursor.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (cursorMode == null) {
                return;
            }

            if (e.getButton() == MouseEvent.BUTTON1) {
                clickX = (double) e.getX();
                clickY = (double) e.getY();
                circles = gridCanvas.getCircles();

                Point2D p = findCircle();
                if (p == null) {
                    return;
                }

                toggleCircle(p);
                resetCursor();
            }
        }

        /**
         * Finds the circle that was clicked on.
         *
         * @return The coordinates of the circle or null if one wasn't clicked.
         */
        private Point2D findCircle() {
            for (int i = 0; i < circles.length; i++) {
                for (int j = 0; j < circles[i].length; j++) {
                    Ellipse2D circle = circles[i][j];
                    if (circle.contains(clickX, clickY)) {
                        return new Point2D(i, j);
                    }
                }
            }

            return null;
        }

        /**
         * Toggles the state of the circle clicked on.
         *
         * @param p The coordinates of the circle.
         */
        private void toggleCircle(Point2D p) {
            if (cursorMode.equals("Start")) {
                if (end != null && p.equals(end)) {
                    displayStartEqualEndError();
                    return;
                }

                if (path != null) {
                    resetPoint("start");
                }

                if (grid.getNode(p).getValue() == LinkedGrid.BLOCKED) {
                    displayStartEndBlockError();
                    return;
                }

                start = p;
                gridCanvas.setStart(p);
            } else if (cursorMode.equals("End")) {
                if (start != null && p.equals(start)) {
                    displayStartEqualEndError();
                    return;
                }

                if (grid.getNode(p).getValue() == LinkedGrid.BLOCKED) {
                    displayStartEndBlockError();
                    return;
                }

                if (path != null) {
                    resetPoint("end");
                }

                end = p;
                gridCanvas.setEnd(p);
            }

            if (cursorMode.equals("Block")) {
                if (path != null) {
                    resetNavigation();
                }

                if ((start != null && p.equals(start))
                    || (end != null && p.equals(end))) {
                    displayStartEndBlockError();
                    return;
                }

                grid.getNode(p).setValue(LinkedGrid.BLOCKED);
            } else if (cursorMode.equals("None")) {
                if (start != null && p.equals(start)) {
                    resetPoint("start");
                }

                if (end != null && p.equals(end)) {
                    resetPoint("end");
                }

                if (path != null) {
                    resetNavigation();
                }

                grid.getNode(p).setValue(LinkedGrid.UNFILLED);
            }

            gridCanvas.repaint();
        }

        /**
         * Displays an error when attempting to block a start or end point.
         */
        private void displayStartEndBlockError() {
            JOptionPane.showMessageDialog(
                null, UIStrings.blockedStartEndPoint, "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        /**
         * Displays an error when trying to overlap the start and end points.
         */
        private void displayStartEqualEndError() {
            JOptionPane.showMessageDialog(
                null, UIStrings.sameStartEndPoints, "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Listens for the Navigate button to be clicked and Calculates the path.
     */
    class NavigateClickListener implements ActionListener {
        /**
         * Attempts to calculate and display the path, showing corresponding
         * errors.
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            resetCursor();
            // If the path was already determined, don't do anything
            if (path != null) {
                return;
            }

            if (start == null || end == null) {
                JOptionPane.showMessageDialog(
                    null, UIStrings.missingStartEndPoint, "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            navigate();
        }
    }
}
