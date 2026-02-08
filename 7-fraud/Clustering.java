import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.IndexMaxPQ;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdRandom;

public class Clustering {

    private EdgeWeightedGraph ewt;
    private int clusters;
    private CC cc;

    // run the clustering algorithm and create the clusters
    public Clustering(Point2D[] locations, int k) {

        if (locations == null || k == 0 || k > locations.length)
            throw new IllegalArgumentException("Clustering constructor: null argument\n");

        clusters = k;

        // Create an undirected graph
        ewt = new EdgeWeightedGraph(locations.length);
        for (int i = 0; i < locations.length; i++) {
            for (int j = 0; j < locations.length; j++) {
                if (i != j) {
                    Point2D v = locations[i];
                    Point2D w = locations[j];
                    double dist = v.distanceTo(w);
                    Edge e = new Edge(i, j, dist);
                    ewt.addEdge(e);
                }
            }
        }

        // Create minimum spanning tree
        KruskalMST kmst = new KruskalMST(ewt);
        IndexMaxPQ<Edge> cluster_pq = new IndexMaxPQ<Edge>(locations.length);
        int i = 0;
        for (Edge e : kmst.edges()) {
            cluster_pq.insert(i, e);
            i++;
        }

        // remove top (clusters-1) edges
        int l = clusters - 1;
        for (i = 0; i < l; i++) {
            Edge key = cluster_pq.maxKey();
            int max = cluster_pq.delMax();
        }

        // Create new ewt without top (clusters-1) edges
        EdgeWeightedGraph cluster_ewt = new EdgeWeightedGraph(locations.length);
        for (int x : cluster_pq) {
            Edge e = cluster_pq.keyOf(x);
            cluster_ewt.addEdge(e);
        }

        // Clusters
        cc = new CC(cluster_ewt);
    }

    // return the cluster of the ith location
    public int clusterOf(int i) {
        return cc.id(i);
    }

    // use the clusters to reduce the dimensions of an input
    public int[] reduceDimensions(int[] input) {

        if (input == null || input.length != ewt.V())
            throw new IllegalArgumentException("input argument does not equal # of locations\n");

        int[] reduced = new int[clusters];
        for (int i = 0; i < input.length; i++) {
            int cluster = cc.id(i);
            int value = input[i];
            reduced[cluster] += value;
        }
        return reduced;
    }

    // unit testing (required)
    public static void main(String[] args) {
        
        if (args.length < 2)
            throw new IllegalArgumentException("Not enough arguments passed in.");

        int c = Integer.parseInt(args[0]);
        int p = Integer.parseInt(args[1]);

        Point2D[] centers = new Point2D[c];

        int x = StdRandom.uniformInt(0, 1000);
        int y = StdRandom.uniformInt(0, 1000);

        Point2D xy = new Point2D(x, y);
        centers[0] = xy;

        int counter = 1;
        while (counter < c) {
            int cx = StdRandom.uniformInt(0, 1000);
            int cy = StdRandom.uniformInt(0, 1000);
            Point2D cxy = new Point2D(cx, cy);
            if (xy.distanceTo(cxy) >= 4) {
                centers[counter] = cxy;
                xy = cxy;
                counter++;
            }
        }

        for (int i = 0; i < centers.length; i++) {
            System.out.println(centers[i].toString());
        }

        Point2D[] locations = new Point2D[c * p];
        for (int i = 0; i < centers.length; i++) {
            Point2D ctr = centers[i];
            counter = 0;
            while (counter < p) {
                double px = StdRandom.uniformDouble(ctr.x() - 1, ctr.x() + 1);
                double py = StdRandom.uniformDouble(ctr.y() - 1, ctr.y() + 1);
                Point2D pt = new Point2D(px, py);
                if (pt.distanceTo(ctr) <= 1) {
                    int index = (i * p) + counter;
                    locations[index] = pt;
                    counter++;
                }
            }
        }

        Clustering cluster = new Clustering(locations, c);
        System.out.print("Clusters: ");
        int total = c * p;
        for (int i = 0; i < total; i++) {
            System.out.printf("%d ", cluster.clusterOf(i));
        }
        System.out.println();

        /*
        if (args.length == 0)
            throw new IllegalArgumentException("No filename passed in.");

        String filename = args[0];
        In in = new In(filename);
        int size = in.readInt();

        Point2D[] locations = new Point2D[size];
        // create clustering object
        // todo validate size
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            // validate(x, y);
            Point2D p = new Point2D(x, y);
            locations[i] = p;
        }

        int csize = 0;
        if (args.length == 1)
            System.out.println("No cluster size passed in. Using 5 as default");
        else
            csize = Integer.parseInt(args[1]);

        Clustering c = new Clustering(locations, csize);
        // int[] values = new int[locations.length];
        int[] values = { 5, 6, 7, 0, 6, 7, 5, 6, 7, 0, 6, 7, 0, 6, 7, 0, 6, 7, 0, 6, 7 };
        int[] reduced = c.reduceDimensions(values);
        */
    }
}
