public class Point {
    public int x; 
    public int y; 

    public Point(int x, int y) { this.x = x; this.y = y; }

    public boolean equals(Point p) { return this.x == p.x && this.y == p.y; }

    public Point copy() { return new Point(x, y); }

    @Override
    public String toString() { return "(" + x + "," + y + ")"; }
}