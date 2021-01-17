public class Coordinates {
    public int x, y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isCoordinates(int x, int y) {
        return ((x == this.x) && (y == this.y));
    }

    public boolean isCoordinates(Coordinates coords) {
        return ((coords.x == this.x) && (coords.y == this.y));
    }

    public String toString() {
        return x + " " + y;
    }
}
