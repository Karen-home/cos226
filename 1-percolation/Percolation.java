import edu.princeton.cs.algs4.QuickFindUF;

public class Percolation {

    int rows; /* #rows */
    int cols; /* #cols */
    int opencount; /* #opens */
    //    public WeightedQuickUnionUF grid;
    QuickFindUF grid; /* main grid */
    int[] opens; /* track opens */
    int[] toprow; /* top row of grid */
    int[] bottomrow; /* bottom row of grid */

    /* Constructor */
    public Percolation(int n) {
        this.rows = n;
        this.cols = n;
        // grid = new WeightedQuickUnionUF(n * n);
        grid = new QuickFindUF(n * n);
        opens = new int[n * n];
        for (int i = 0; i < n * n; i++)
            opens[i] = 0;

        opencount = 0;
        toprow = new int[n];
        for (int i = 0; i < n; i++)
            toprow[i] = i;

        bottomrow = new int[n];
        int j = 0;
        for (int i = (rows - 1) * cols; i < rows * cols; i++) {
            bottomrow[j] = i;
            j++;
        }
    }

    private int gridtoindex(int row, int col) {
        return row * cols + col;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        int index = gridtoindex(row, col);
        if (!isOpen(row, col)) {

            if (row + 1 <= rows - 1 && isOpen(row + 1, col))
                grid.union(index, gridtoindex(row + 1, col));
            if (row - 1 >= 0 && isOpen(row - 1, col))
                grid.union(index, gridtoindex(row - 1, col));
            if (col + 1 <= cols - 1 && isOpen(row, col + 1))
                grid.union(index, gridtoindex(row, col + 1));
            if (col - 1 >= 0 && isOpen(row, col - 1))
                grid.union(index, gridtoindex(row, col - 1));

            opens[index] = 1;
            opencount++;
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {

        // TODO: throw exception

        if (opens[gridtoindex(row, col)] == 1)
            return true;
        else
            return false;
    }

    private boolean findinarray(int[] borderarray, int index) {
        for (int i = 0; i < borderarray.length; i++) {
            if (grid.find(borderarray[i]) == grid.find(index))
                return true;
        }
        return false;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        int index = gridtoindex(row, col);
        if (isOpen(row, col)) {
            if (findinarray(toprow, index) /*&& findinarray(bottomrow, index)*/)
                return true;
        }
        return false;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return opencount;
    }

    // does the system percolate?
    public boolean percolates() {
        int row = rows - 1;
        for (int i = 0; i < cols; i++) {
            int index = gridtoindex(row, i);
            if (isOpen(row, i)) {
                if (findinarray(toprow, index) && findinarray(bottomrow, index))
                    return true;
            }
        }
        return false;
    }

    // unit testing

    public static void main(String[] args) {

        int size = Integer.parseInt(args[0]);

        Percolation percolation = new Percolation(size);

        percolation.open(0, 0);
        percolation.open(1, 0);
        percolation.open(1, 1);
        percolation.open(2, 1);

        boolean r = percolation.isFull(1, 1);
        r = percolation.percolates();
    }


}
