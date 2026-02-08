import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdOut;

public class PointST<Value> {

    RedBlackBST<Point2D, Value> bst = new RedBlackBST<Point2D, Value>();

    // construct an empty symbol table of points
    public PointST() {

    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return (bst.size() == 0);
    }

    // number of points
    public int size() {
        return bst.size();
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        bst.put(p, val);
    }

    // value associated with point p
    public Value get(Point2D p) {
        return bst.get(p);
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        return bst.contains(p);
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {
        return bst.keys();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {

        Queue<Point2D> keys = new Queue<Point2D>();
        for (Point2D k : points()) {
            if (rect.contains(k)) {
                keys.enqueue(k);
            }
        }
        return keys;
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (bst.contains(p))
            return p;

        Point2D champion = null;
        double champion_dist = Double.POSITIVE_INFINITY;
        for (Point2D iterpt : bst.keys()) {
            double dist = p.distanceSquaredTo(iterpt);
            if (dist < champion_dist) {
                champion_dist = dist;
                champion = iterpt;
            }
        }
        return champion;
    }

    // unit testing
    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);

        PointST<Integer> kdtree = new PointST<Integer>();

        // create k-d tree
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            // validate(x, y);

            Point2D p = new Point2D(x, y);
            kdtree.put(p, i);
        }

        for (Point2D p : kdtree.points()) {
            StdOut.print(p.toString());
        }
        StdOut.print("\n");

        // Test range
        RectHV rect = new RectHV(0.1, 0.3, 0.8, 0.5);
        System.out.println("Finding points in rect " + rect.toString());
        Iterable<Point2D> keys = kdtree.range(rect);
        for (Point2D k : keys) {
            System.out.println(k.toString());
        }

    }

}
