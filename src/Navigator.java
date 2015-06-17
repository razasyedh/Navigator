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
    private JButton resetButton;

    private LinkedGrid grid;
    private PathFinder pathFinder;
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
        setSize(595, 500);
        setLocationRelativeTo(null);
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
        JButton startButton = new JButton(UIStrings.startButtonLabel);
        startButton.setMnemonic(KeyEvent.VK_S);
        startButton.setToolTipText(UIStrings.startButtonTip);
        toggles.add(startButton);
        toggleButtons.add(startButton);
        JButton endButton = new JButton(UIStrings.endButtonLabel);
        endButton.setMnemonic(KeyEvent.VK_E);
        endButton.setToolTipText(UIStrings.endButtonTip);
        toggles.add(endButton);
        toggleButtons.add(endButton);
        JButton blockButton = new JButton(UIStrings.blockButtonLabel);
        blockButton.setMnemonic(KeyEvent.VK_B);
        blockButton.setToolTipText(UIStrings.blockButtonTip);
        toggles.add(blockButton);
        toggleButtons.add(blockButton);

        toggles.setBorder(new TitledBorder("Toggles"));
        options.add(toggles);

        JPanel actions = new JPanel();
        resetButton = new JButton(UIStrings.resetButtonLabel);
        resetButton.setMnemonic(KeyEvent.VK_C);
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

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                resetCursor();
                resetPath();
                pathFinder.reset();
                navigate();
            }
        });
    }

    /**
     * Resets the path.
     */
    public void resetPath() {
        path = null;
        gridCanvas.setPath(null);
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
        // Show the path between the start and end points
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
                if (p.equals(end)) {
                    displayStartEqualEndError();
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
                    return;
                }

                end = p;
                gridCanvas.setEnd(p);
                pathFinder.setEnd(p);
            }

            if (cursorMode.equals("Block")) {
                if (p.equals(start) || p.equals(end)) {
                    JOptionPane.showMessageDialog(
                        null, UIStrings.blockedStartEndPoint, "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

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
                null, UIStrings.sameStartEndPoints, "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
