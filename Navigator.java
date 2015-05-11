import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import javax.swing.border.*;
import javax.swing.JOptionPane;
import java.util.ArrayList;

public class Navigator {
    public static void main(String[] args) {
        DrawFrame frame = new DrawFrame();
        frame.setVisible(true);
    }
}

class DrawFrame extends JFrame {
    private static final int GRID_WIDTH = 15;
    private static final int GRID_HEIGHT = 10;
    protected String cursorMode;
    private ArrayList<JButton> toggleButtons;
    private GridCanvas gridCanvas;
    private JButton navigateButton, resetButton;

    private LinkedGrid grid;
    private Point2D start, end;
    private Point2D[] path;
    private PathFinder pathFinder;

    public DrawFrame() {
        grid = new LinkedGrid(GRID_HEIGHT, GRID_WIDTH);
        toggleButtons = new ArrayList<>(4);
        setDefaults();

        applySettings();
        addComponents();
        setListeners();
    }

    private void setDefaults() {
        start = new Point2D(0, 0);
        end = new Point2D(0, 3);
        grid.getNode(0, 2).setValue(LinkedGrid.BLOCKED);
    }

    private void applySettings() {
        setSize(595, 500);
        setLocation(100, 100);
        setTitle("Navigator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
    }

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
                fullReset();
                gridCanvas.repaint();
            }
        });
    }

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

    public void resetPath() {
        path = null;
        gridCanvas.setPath(null);
    }

    private void resetNavigation() {
        resetPath();
        grid.partialReset();
    }

    private void fullReset() {
        resetPath();
        resetPoint("start");
        resetPoint("end");
        grid.fullReset();
    }

    // When a toggle button is clicked, change the mode and cursor
    class ToggleListener implements ActionListener {
        private String mode;

        public ToggleListener(String mode) {
            super();
            this.mode = mode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            cursorMode = mode;
            setCursor(
                Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
            );
        }
    }

    // When a toggle button is clicked, listen for the escape key to reset
    // the cursor
    class EscapeListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                setCursor(
                    Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                );
            }
        }
    }

    // Determine the circle that was clicked and toggle it
    class ToggleClickListener extends MouseAdapter {
        private Ellipse2D[][] circles;
        private double clickX, clickY;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (cursorMode == null) {
                return;
            }

            if (e.getButton() == MouseEvent.BUTTON1) {
                clickX = (double) e.getX();
                clickY = (double) e.getY();
                circles = gridCanvas.getCircles();

                boolean found = findCircle();
                if (!found) {
                    return;
                }

                // Reset cursor
                cursorMode = null;
                setCursor(
                    Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                );
            }
        }

        private boolean findCircle() {
            boolean found = false;
            for (int i = 0; i < circles.length && !found; i++) {
                for (int j = 0; j < circles[i].length && !found; j++) {
                    Ellipse2D circle = circles[i][j];
                    if (circle.contains(clickX, clickY)) {
                        found = true;
                        Point2D p = new Point2D(i, j);

                        if (cursorMode.equals("Start")) {
                            if (end != null && p.equals(end)) {
                                displayStartEqualEndError();
                                return found;
                            }

                            if (path != null) {
                                resetPoint("start");
                            }

                            if (grid.getNode(p).getValue()
                                == LinkedGrid.BLOCKED) {
                                displayStartEndBlockError();
                                return found;
                            }

                            start = p;
                            gridCanvas.setStart(p);
                        } else if (cursorMode.equals("End")) {
                            if (start != null && p.equals(start)) {
                                displayStartEqualEndError();
                                return found;
                            }

                            if (grid.getNode(p).getValue()
                                == LinkedGrid.BLOCKED) {
                                displayStartEndBlockError();
                                return found;
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
                                return found;
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
                }
            }

            return found;
        }

        private void displayStartEndBlockError() {
            JOptionPane.showMessageDialog(
                null, UIStrings.blockedStartEndPoint, "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        private void displayStartEqualEndError() {
            JOptionPane.showMessageDialog(
                null, UIStrings.sameStartEndPoints, "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    class NavigateClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
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

            // Calculate the path between the start and end points
            try {
                pathFinder = new PathFinder(grid, start, end);
            } catch (UnreachablePointException exception) {
                JOptionPane.showMessageDialog(
                    null, UIStrings.unreachableEndpoint, "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            gridCanvas.setGrid(grid);
            path = pathFinder.getPath();
            gridCanvas.setPath(path);
            gridCanvas.repaint();
        }
    }
}
