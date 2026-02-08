import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class ShortestCommonAncestor {

    private static final boolean ANCESTOR = true;
    private static final boolean LENGTH = false;
    private boolean verbose = false;

    private Digraph G;
    public BreadthFirstDirectedPaths v_bfs = null;
    public BreadthFirstDirectedPaths w_bfs = null;
    private int leastdist = Integer.MAX_VALUE;

    // constructor takes a rooted DAG as argument
    public ShortestCommonAncestor(Digraph G) {
        this.G = G;
    }

    public int shortest_path(boolean ret, BreadthFirstDirectedPaths bfs_v,
                             BreadthFirstDirectedPaths bfs_w) {
        int minlength = Integer.MAX_VALUE;
        int sca = Integer.MAX_VALUE;

        for (int i = 0; i < G.V(); i++) {
            // System.out.println("Checking path to vertex " + i);
            if (bfs_v.hasPathTo(i) && bfs_w.hasPathTo(i)) {

                // if verbose
                int d1 = bfs_v.distTo(i);
                int d2 = bfs_w.distTo(i);

                if (verbose) {
                    System.out.println("Common ancestor found: " + i);
                    System.out.println("Distance from noun1 to common ancestor is " + d1);
                    System.out.println("Distance from noun2 to common ancestor is " + d2);
                }

                int m = bfs_v.distTo(i) + bfs_w.distTo(i);
                // System.out.println("Total distance is " + m);
                if (m < minlength) {
                    minlength = m;
                    sca = i;
                }
            }
        }
        // System.out.println("Closest ancestor: " + sca);
        // System.out.println("Shortest length: " + minlength);

        return (ret == ANCESTOR) ? sca : minlength;
    }

    // length of shortest ancestral path between v and w
    public int length(int v, int w) {

        BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(G, w);

        return shortest_path(LENGTH, bfs_v, bfs_w);
    }

    // a shortest common ancestor of vertices v and w
    public int ancestor(int v, int w) {
        BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(G, w);

        return shortest_path(ANCESTOR, bfs_v, bfs_w);
    }

    // length of shortest ancestral path of vertex subsets A and B
    public int lengthSubset(Iterable<Integer> subsetA, Iterable<Integer> subsetB) {
        BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(G, subsetA);
        BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(G, subsetB);

        return shortest_path(LENGTH, bfs_v, bfs_w);
    }

    // a shortest common ancestor of vertex subsets A and B
    public int ancestorSubset(Iterable<Integer> subsetA, Iterable<Integer> subsetB) {
        BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(G, subsetA);
        BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(G, subsetB);

        return shortest_path(ANCESTOR, bfs_v, bfs_w);
    }

    // unit testing (required)
    public static void main(String[] args) {


        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        ShortestCommonAncestor sca = new ShortestCommonAncestor(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sca.length(v, w);
            int ancestor = sca.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }


        /*
        int n = Integer.parseInt(args[0]);
        int m = Integer.parseInt(args[1]);

        Integer[] pathsA = new Integer[n];
        Integer[] pathsB = new Integer[n];
        int suma = 0;
        int sumb = 0;

        int mina = 100;
        int minb = 100;



        for (int i = 0; i < n; i++) {
            pathsA[i] = StdRandom.uniformInt(1, m);
            pathsB[i] = StdRandom.uniformInt(1, m);

            suma += pathsA[i];
            sumb += pathsB[i];

            if (pathsA[i] < mina)
                mina = pathsA[i];
            if (pathsB[i] < minb)
                minb = pathsB[i];
        }

        System.out.print("pathsA = {");
        for (int i = 0; i < n; i++) {
            System.out.print(pathsA[i]);
            if (i < n - 1) {
                System.out.print(",");
            }
        }
        System.out.println("}");

        System.out.print("pathsB = {");
        for (int i = 0; i < n; i++) {
            System.out.print(pathsB[i]);
            if (i < n - 1) {
                System.out.print(",");
            }
        }
        System.out.println("}");

        int vsum = suma + sumb + 1;
        Digraph G = new Digraph(vsum);

        Queue<Integer> qa = new Queue<Integer>();
        Queue<Integer> qb = new Queue<Integer>();

        int counter = 1;
        for (int i = 0; i < n; i++) {
            int l = pathsA[i];
            for (int j = 0; j < l; j++) {
                if (j == 0) {
                    System.out.println("enqueuing A " + counter);
                    qa.enqueue(counter);
                }
                int next = (j < l - 1) ? counter + 1 : 0;
                System.out.println("Adding edge from " + counter + " " + next);
                G.addEdge(counter, next);
                counter++;
            }
        }
        for (int i = 0; i < n; i++) {
            int l = pathsB[i];
            for (int j = 0; j < l; j++) {
                if (j == 0) {
                    qb.enqueue(counter);
                }
                int next = (j < l - 1) ? counter + 1 : 0;
                System.out.println("Adding edge from " + counter + " " + next);
                G.addEdge(counter, next);
                counter++;
            }
        }
        StdOut.println(G);

        ShortestCommonAncestor sca = new ShortestCommonAncestor(G);
        sca.ancestorSubset(qa, qb);
        int len = sca.lengthSubset(qa, qb);

        int summin = mina + minb;

        System.out.println("Length Subset is: " + len);
        System.out.println("Sum mins is: " + summin);

        System.out.println(len != summin ? "ERROR" : "SUCCESS");
*/
    }
}
