import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    private static final boolean X = true;
    private static final boolean Y = false;
    private static int RED = 16;
    private static int GREEN = 8;
    private static int BLUE = 0;

    private static int FULL = 3;
    private static int PARTIAL = 2;
    private static int MINIMAL = 1;

    private int verbose = 0;
    private Picture picture;
    private int width;
    private int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("Picture argument is null\n");
        this.picture = new Picture(picture);
        this.width = picture.width();
        this.height = picture.height();
    }

    // current picture
    public Picture picture() {
        return picture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {

        validate(x, 0, width - 1);
        validate(y, 0, height - 1);

        int prev_x = getPrev(x, X);
        int next_x = getNext(x, X);

        int prev_y = getPrev(y, Y);
        int next_y = getNext(y, Y);

        int grad_x = gradient(x, y, prev_x, next_x, prev_y, next_y, X);
        int grad_y = gradient(x, y, prev_x, next_x, prev_y, next_y, Y);

        double grt = Math.sqrt(grad_x + grad_y);
        return (grt);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // Transpose picture
        picture = transpose(picture());
        int[] seam = findVerticalSeam();
        picture = transpose(picture());
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {

        Double[][] energy = new Double[picture.width()][picture.height()];
        Double[][] distTo = new Double[picture.width()][picture.height()];
        int[][] edgeTo = new int[picture.width()][picture.height()];

        // Calculate energies
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++)
                energy[col][row] = energy(col, row);
        }

        // Initialize distTo
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++) {
                distTo[col][row] = (row == 0) ? energy[col][0] : Double.POSITIVE_INFINITY;
            }
        }

        // calculate or relax (col-1, row+1), (col, row+1), and (col+1, row+1);
        for (int row = 0; row < height() - 1; row++) {
            for (int col = 0; col < width(); col++) {
                double new_center_dist = distTo[col][row] + energy[col][row + 1];
                double curr_center_dist = distTo[col][row + 1];
                if (new_center_dist < curr_center_dist) {
                    distTo[col][row + 1] = new_center_dist;
                    edgeTo[col][row + 1] = col;
                }

                if (col > 0) { // if not left edge
                    double new_left_dist = distTo[col][row] + energy[col - 1][row + 1];
                    double curr_left_dist = distTo[col - 1][row + 1];
                    if (new_left_dist < curr_left_dist) {
                        distTo[col - 1][row + 1] = new_left_dist;
                        edgeTo[col - 1][row + 1] = col;
                    }
                }

                if (col < width() - 1) { // if not right edge
                    double new_right_dist = distTo[col][row] + energy[col + 1][row + 1];
                    double curr_right_dist = distTo[col + 1][row + 1];
                    if (new_right_dist < curr_right_dist) {
                        distTo[col + 1][row + 1] = new_right_dist;
                        edgeTo[col + 1][row + 1] = col;
                    }
                }
            }
        }

        if (verbose >= PARTIAL) {
            printDistTo(distTo);
            printEdgeTo(edgeTo);
        }

        // now traverse back up from lowest energy in last row to find min seam
        int row = height() - 1;
        int col = findLowestCell(distTo, row); // lowest cell in last row

        int[] seam = new int[height()];
        seam[row] = col; // this is the column of the lowest energy pixel in the last row

        if (verbose >= FULL)
            System.out.println("FIND   Seam [" + row + "] = " + col + " (Last Row)");

        while (row > 0) {
            col = edgeTo[col][row];
            row--;
            seam[row] = col;
        }

        if (verbose >= MINIMAL) {
            printSeam(seam);
        }

        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        picture = transpose(picture());
        removeVerticalSeam(seam);
        picture = transpose(picture());
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {

        validateSeam(seam, X);
        if (verbose >= PARTIAL)
            printSeam(seam);

        Picture newpic = new Picture(picture.width() - 1, picture.height());

        int newcol;
        for (int row = 0; row < picture.height(); row++) {
            newcol = 0;
            for (int col = 0; col < picture.width(); col++) {
                if (col != seam[row]) { // skip if part of the vert seam
                    newpic.setRGB(newcol, row, picture.getRGB(col, row));
                    newcol++;
                }
            }
        }
        picture = new Picture(newpic);
        width = picture().width();
        height = picture().height();
    }

    // ----------------------------
    // Helper methods
    // ----------------------------

    private void validate(int x, int min, int max) {
        if (x < min)
            throw new IllegalArgumentException("Index " + x + " is below min " + min + "\n");
        else if (x > max)
            throw new IllegalArgumentException("Index " + x + " is above max " + max + "\n");
    }

    private int getPrev(int x, boolean dir) {
        int len = (dir == X) ? width() : height();
        return (x > 0) ? (x - 1) : len - 1;
    }

    private int getNext(int x, boolean dir) {
        int len = (dir == X) ? width() : height();
        return (x == (len - 1)) ? 0 : x + 1;
    }

    private int getColor(int rgb, int color) {
        return (rgb >> color) & 0xFF;
    }

    private int gradient(int x, int y, int prev_x, int next_x, int prev_y, int next_y, boolean X) {

        // RGB for previous pixel
        int prgb = picture().getRGB((X ? prev_x : x), (X ? y : prev_y));
        // RGB for next pixel
        int nrgb = picture().getRGB((X ? next_x : x), (X ? y : next_y));

        // Find differential
        int xr = getColor(nrgb, RED) - getColor(prgb, RED);
        int xg = getColor(nrgb, GREEN) - getColor(prgb, GREEN);
        int xb = getColor(nrgb, BLUE) - getColor(prgb, BLUE);

        // Return square of differentials
        return (xr * xr + xg * xg + xb * xb);
    }


    private Picture transpose(Picture picture) {

        // Create pic with transposed dimensions;
        Picture tpicture = new Picture(picture.height(), picture.width());
        for (int row = 0; row < picture.height(); row++) {
            for (int col = 0; col < picture.width(); col++)
                tpicture.set(row, col, picture.get(col, row));
        }
        height = tpicture.height();
        width = tpicture.width();
        return tpicture;
    }

    private void printDistTo(Double[][] distTo) {
        System.out.println("DIST   Printing total distTo matrix:");
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++)
                StdOut.printf("%7.2f ", distTo[col][row]);
            StdOut.println();
        }
    }

    private void printEdgeTo(int[][] edgeTo) {
        System.out.println("EDGE   Printing total edgeTo matrix: ");
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++)
                StdOut.printf("%d ", edgeTo[col][row]);
            StdOut.println();
        }
    }

    private int findLowestCell(Double[][] distTo, int row) {
        // find lowest energy pixel in row and return column
        double min_dist = Double.POSITIVE_INFINITY;
        int min_col = 0;
        for (int col = 0; col < width(); col++) {
            double dist = distTo[col][row];
            if (dist < min_dist) {
                min_dist = dist;
                min_col = col;
            }
        }
        return min_col;
    }

    private void printSeam(int[] seam) {
        System.out.print("SEAM   Printing seam: [ ");
        for (int i = 0; i < seam.length; i++)
            StdOut.printf("%d ", seam[i]);
        System.out.print("]\n");
        // StdOut.println();
    }

    private void validateSeam(int[] seam, boolean direction) {

        // null seam
        if (seam == null)
            throw new IllegalArgumentException(
                    "Null seam passed to remove method\n");

        int max_length = (direction == X) ? height : width;
        int range = (direction == X) ? width : height;

        // picture is 1 pixel wide or high
        if (range == 1)
            throw new IllegalArgumentException(
                    "Picture size too small to remove seam\n");

        // Seam is incorrect size
        if (seam.length != max_length)
            throw new IllegalArgumentException(
                    "Seam length is incorrect\n");

        // Seam entries are invalid
        for (int i = 0; i < seam.length; i++) {
            int s = seam[i];
            if (s < 0 || s >= range)
                throw new IllegalArgumentException(
                        "Seam entry " + s + " is invalid\n");
            if (i != seam.length - 1) { // check the diff between this and next s isn't more than 1
                if (Math.abs(s - seam[i + 1]) > 1)
                    throw new IllegalArgumentException(
                            "Seam entry " + s + " (index + " + i + ") is more than 1 away from "
                                    + seam[i + 1] + "\n");
            }
        }

    }

    //  unit testing
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        StdOut.printf("%d-by-%d image\n", picture.width(), picture.height());

        int removeRows = Integer.parseInt(args[1]);
        int removeCols = Integer.parseInt(args[2]);

        SeamCarver sc = new SeamCarver(picture);

        if (args.length > 3)
            sc.verbose = Integer.parseInt(args[3]);

        for (int i = 0; i < removeRows; i++) {
            int[] seam = sc.findVerticalSeam();
            sc.removeVerticalSeam(seam);
            StdOut.printf("MAIN   New image is %d-by-%d\n", sc.picture.width(),
                          sc.picture.height());
        }
        for (int i = 0; i < removeCols; i++) {
            int[] seam = sc.findHorizontalSeam();
            sc.removeHorizontalSeam(seam);
            StdOut.printf("MAIN   New image is %d-by-%d\n", sc.picture.width(),
                          sc.picture.height());
        }
    }


}
