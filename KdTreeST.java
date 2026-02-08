import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class KdTreeST<Value> {

    private static final boolean VERT = true;
    private static final boolean HOR = false;

    private int size = 0;

    private class Node {
        private Point2D p;     // the point
        private Value val;     // the symbol table maps the point to this value
        private RectHV rect;   // the axis-aligned rectangle corresponding to this node
        private Node left;       // the left/bottom subtree
        private Node right;       // the right/top subtree
        private boolean direction;

        public Node(Point2D key, Value val, RectHV rect, Node lb, Node rt, Boolean d) {
            this.p = key;
            this.val = val;
            this.rect = rect;
            this.left = lb;
            this.right = rt;
            this.direction = d;
        }

    }

    private Node root;

    // construct an empty symbol table of points
    public KdTreeST() {

    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return (size() == 0);
    }

    // number of points
    public int size() {
        return size;
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        RectHV rect = new RectHV(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                                 Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        root = put(root, p, val, rect, VERT);
    }

    // insert the key-value pair in the subtree rooted at h
    private Node put(Node parent, Point2D key, Value val, RectHV rect, Boolean dir) {
        if (parent == null) {
            size++;
            return new Node(key, val, rect, null, null, dir);
        }

        // if parent node is vertical, compare x, if horizontal, compare y
        double cmp = (parent.direction == VERT) ? key.x() - parent.p.x() : key.y() - parent.p.y();
        Boolean newdir = (parent.direction == VERT) ? HOR : VERT;
        RectHV r = getRect(newdir, parent, cmp);
        if (cmp < 0) parent.left = put(parent.left, key, val, r, newdir);
        else parent.right = put(parent.right, key, val, r, newdir);

        return parent;
    }

    private RectHV getRect(Boolean newdir, Node parent, double cmp) {

        RectHV prect = parent.rect;
        RectHV r;
        if (cmp < 0 && newdir == HOR) { // left/bottom and hor
            r = new RectHV(prect.xmin(), prect.ymin(), parent.p.x(), prect.ymax());
        }
        else if (cmp < 0 && newdir == VERT) { // left/bottom and ver
            r = new RectHV(prect.xmin(), prect.ymin(), prect.xmax(), parent.p.y());
        }
        else if (cmp >= 0 && newdir == HOR) { // right/top and hor
            r = new RectHV(parent.p.x(), prect.ymin(), prect.xmax(), prect.ymax());
        }
        else { // right/top and ver
            r = new RectHV(prect.xmin(), parent.p.y(), prect.xmax(), prect.ymax());
        }
        return r;
    }

    /*
    private Boolean orientation(String s) {
        if (s == "hor")
            return "vert";
        else
            return "hor";
    }
     */


    // value associated with point p
    public Value get(Point2D p) {
        return get(root, p);
    }

    private Value get(Node x, Point2D key) {
        if (key == null) throw new IllegalArgumentException("calls get() with a null key");
        if (x == null) return null;

        // swap directions -- optimize later
        // String newdir = orientation(x.direction);
        // double cmp = (newdir == "hor") ? key.x() - x.p.x() : key.y() - x.p.y();

        double cmp = (x.direction == VERT) ? key.x() - x.p.x() : key.y() - x.p.y();
        if (cmp < 0) return get(x.left, key);
        else return get(x.right, key);
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {

        Queue<Point2D> keys = new Queue<Point2D>();
        Queue<Node> queue = new Queue<Node>();
        queue.enqueue(root);
        while (!queue.isEmpty()) {
            Node x = queue.dequeue();
            if (x == null) continue;
            keys.enqueue(x.p);
            queue.enqueue(x.left);
            queue.enqueue(x.right);
        }
        return keys;
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> keys = new Queue<Point2D>();
        range(root, keys, rect);
        return keys;
    }

    private void range(Node parent, Queue<Point2D> keys, RectHV rect) {

        if (parent == null) return;

        // add parent if in rect
        if (rect.contains(parent.p))
            keys.enqueue(parent.p);

        // Check subtrees. 3 outcomes --
        // (1) rect is entirely to left/top of parent: call range on lt subtree, drop rb subtree
        // (2) rect straddles parent: call range on both lt and rb subtrees
        // (3) rect is entirely to right/bottom of parent: call range on rb subtree, drop lt subtree

        if (parent.direction == VERT) {
            // Outcome #1 - entirely to left
            if (rect.xmax() < parent.p.x() && parent.left != null)
                range(parent.left, keys, rect);
                // Outcome #2
            else if (rect.xmin() < parent.p.x() && rect.xmax() >= parent.p.x()) {
                if (parent.left != null)
                    range(parent.left, keys,
                          new RectHV(rect.xmin(), rect.ymin(), parent.p.x(), rect.ymax()));
                if (parent.right != null)
                    range(parent.right, keys,
                          new RectHV(parent.p.x(), rect.ymin(), rect.xmax(), rect.ymax()));
            }
            // Outcome #3 - entirely to right
            else if (rect.xmin() >= parent.p.x() && parent.right != null)
                range(parent.right, keys, rect);
        }
        else { // parent.direction = HOR
            if (rect.ymin() < parent.p.y() && parent.left != null) {
                RectHV newrect = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(),
                                            Math.min(parent.p.y(), rect.ymax()));
                range(parent.left, keys, newrect);
            }
            if (rect.ymax() >= parent.p.y() && parent.right != null) {
                RectHV newrect = new RectHV(rect.xmin(), Math.max(parent.p.y(), rect.ymin()),
                                            rect.xmax(), rect.ymax());
                range(parent.right, keys, newrect);
            }
        }
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (p == null || root == null)
            throw new IllegalArgumentException("argument to nearest() is null");
        return nearest(root, p, root.p, Double.POSITIVE_INFINITY);
    }

    private Point2D nearest(Node parent, Point2D p, Point2D champion, double champion_dist) {

        double distance = parent.p.distanceSquaredTo(p);

        if (distance == 0) {
            champion = parent.p;
            return champion;
        }
        else if (distance < champion_dist) {
            // new winner!
            champion_dist = distance;
            champion = parent.p;
        }

        // Check left and right subtrees
        double first = Double.POSITIVE_INFINITY;
        double second = Double.POSITIVE_INFINITY;

        Point2D first_champion = null;
        Point2D second_champion = null;

        double cmp = (parent.direction == VERT) ? parent.p.x() - p.x() :
                     parent.p.y() - parent.p.y();
        // if positive, pivot point is on left/bottom, if neg, on right/top

        Node first_tree = (cmp > 0) ? parent.left : parent.right;
        Node second_tree = (cmp > 0) ? parent.right : parent.left;

        if (first_tree != null) {
            first_champion = nearest(first_tree, p, champion, champion_dist);
            first = p.distanceSquaredTo(first_champion);
        }
        if (second_tree != null) {
            // only check right tree if shortest distance to parent.right.rect is smaller than champion
            double distance_to_second = second_tree.rect.distanceSquaredTo(p);
            if (distance_to_second < champion_dist) {
                second_champion = nearest(second_tree, p, champion, champion_dist);
                second = p.distanceSquaredTo(second_champion);
            }
        }
        // compare left, right, and orig champions
        if (first < champion_dist && first < second) champion = first_champion;
        else if (second < champion_dist && second < first) champion = second_champion;

        return champion;
    }

    // unit testing
    public static void main(String[] args) {

        String filename = args[0];
        In in = new In(filename);

        int calls = Integer.parseInt(args[1]);

        KdTreeST<Integer> kdtree = new KdTreeST<Integer>();

        // create k-d tree
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            // validate(x, y);

            Point2D p = new Point2D(x, y);
            kdtree.put(p, i);
        }

        Stopwatch stopwatch = new Stopwatch();

        for (int i = 0; i < calls; i++) {
            double x = StdRandom.uniformDouble(0.0, 1.0);
            double y = StdRandom.uniformDouble(0.0, 1.0);
            Point2D n = kdtree.nearest(new Point2D(x, y));
        }
        double elapsed_time = stopwatch.elapsedTime();
        double calls_per_sec = calls / elapsed_time;
        System.out.println("Calls per sec for " + calls + " calls = " + calls_per_sec);

    }
}
