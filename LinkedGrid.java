public class LinkedGrid {
    private Node[][] grid;
    private int rows;
    private int cols;
    public static final int UNFILLED = 0;
    public static final int BLOCKED = 1;

    public LinkedGrid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        grid = new Node[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Node();
            }
        }

        linkNodes();
    }

    private void linkNodes() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i != 0) {
                    grid[i][j].setWest(grid[i-1][j]);
                }

                if (i != rows-1) {
                    grid[i][j].setEast(grid[i+1][j]);
                }

                if (j != 0) {
                    grid[i][j].setSouth(grid[i][j-1]);
                }

                if (j != cols-1) {
                    grid[i][j].setNorth(grid[i][j+1]);
                }
            }
        }
    }

    public Node getNode(int x, int y) {
        return grid[x][y];
    }

    public Node getNode(Point2D p) {
        int x = p.getX();
        int y = p.getY();
        return getNode(x, y);
    }

    // public Point2D getNorthernPoint() {

    // }

    // Note: The axes of the grid are switched, so the x coordinate would
    //       correspond to the vertical axis and vice-versa.
    @Override
    public String toString() {
        String result = "\\Y";

        // Column numbers
        for (int j = 0; j < cols; j++) {
            String colNum = String.format("%3s", j);
            result += " " + colNum;
        }
        result += "\nX |‾\n";

        for (int i = 0; i < rows; i++) {
            // Row numbers
            String row = String.format("%2s", i);
            result += row + " ";

            for (int j = 0; j < cols; j++) {
                String node = grid[i][j].toString();
                String valueString = String.format("%3s", node);

                result += valueString  + " ";
            }
            result += "\n";
        }

        return result;
    }

    public static void main(String[] args) {
        LinkedGrid test = new LinkedGrid(2, 5);
        test.getNode(0, 1).setValue(1);
        test.getNode(1, 0).setValue(2);
        System.out.println(test);

        Node north = test.getNode(0, 0).getNorth();
        Node east = test.getNode(0, 0).getEast();
        System.out.println("North of (0,0): " + north);
        System.out.println("East of (0,0): " + east);
    }
}
