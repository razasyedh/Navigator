public class Node {
    // TODO: Make these final
    private Node north;
    private Node south;
    private Node west;
    private Node east;
    private int value;

    public void setNorth(Node north) {
        this.north = north;
    }

    public void setSouth(Node south) {
        this.south = south;
    }

    public void setWest(Node west) {
        this.west = west;
    }

    public void setEast(Node east) {
        this.east = east;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Node getNorth() {
        return north;
    }

    public Node getSouth() {
        return south;
    }

    public Node getWest() {
        return west;
    }

    public Node getEast() {
        return east;
    }

    public Node[] getNeighbors() {
        Node[] neighbors = {north, south, east, west};
        return neighbors;
    }

    public int getValue() {
        return value;
    }

    public int compareTo(Node other) {
        int compareResult;

        if (this.value > other.getValue()) {
            compareResult = 1;
        }
        else if (this.value < other.getValue()) {
            compareResult = -1;
        }
        else {
            compareResult = 0;
        }

        return compareResult;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    public static void main(String[] args) {
        Node test = new Node();
        System.out.println(test + " North:" + test.getNorth());
    }
}
