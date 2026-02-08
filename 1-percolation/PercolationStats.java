import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {

    private PercolationStats PercolationStats;
    double[] tries;
    int size;
    double time;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        size = n;
        tries = new double[trials];
        Stopwatch stopwatch = new Stopwatch();
        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(size);
            boolean p = percolation.percolates();
            int t = 1;

            while (!p) {
                int row = StdRandom.uniformInt(size);
                int col = StdRandom.uniformInt(size);
                if (!percolation.isOpen(row, col)) {
                    percolation.open(row, col);
                    p = percolation.percolates();
                    t++;
                }
            }
            double threshold = 0.00;
            threshold = (double) t / (size * size);
            String s = "trial " + i + ": open sites " + t + " threshold: " + threshold;
            // System.out.println(s);
            tries[i] = threshold;
        }
        time = stopwatch.elapsedTime();

    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(tries);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(tries);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLow() {
        double x = 0;
        return 0;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHigh() {
        double x = 0;
        return 0;
    }

    // test client (see below)
    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);
        int tries = Integer.parseInt(args[1]);

        PercolationStats PercolationStats = new PercolationStats(size, tries);
        double mean = PercolationStats.mean();
        double stddev = PercolationStats.stddev();
        double lowconf = mean - 1.96 * stddev / Math.sqrt(tries);
        double hiconf = mean + 1.96 * stddev / Math.sqrt(tries);
        
        String s;
        s = "mean()           = " + String.format("%.6f", mean);
        System.out.println(s);
        s = "stddev()         = " + String.format("%.6f", stddev);
        System.out.println(s);
        s = "confidenceLow()  = " + String.format("%.6f", lowconf);
        System.out.println(s);
        s = "confidenceHigh() = " + String.format("%.6f", hiconf);
        System.out.println(s);
        s = "elapsed time()   = " + String.format("%.6f", PercolationStats.time);
        System.out.println(s);
    }
}
