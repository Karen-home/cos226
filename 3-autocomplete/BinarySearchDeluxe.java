import java.util.Comparator;

public class BinarySearchDeluxe {


    // Returns the index of the first key in the sorted array a[]
    // that is equal to the search key, or -1 if no such key.
    public static <Key> int firstIndexOf(Key[] a, Key key, Comparator<Key> comparator) {
        int lo = 0;
        int hi = a.length - 1;

        // A A A B B B C C C
        // lo = 0, hi = 8

        int index = -1;
        while (lo <= hi) {
            // System.out.println("firstIndexOf lo: " + lo + " hi: " + hi);

            int mid = lo + (hi - lo) / 2; // 4
            int compare = comparator.compare(a[mid], key);

            if (compare >= 0) {
                hi = mid - 1;
                if (compare == 0)
                    index = mid;
            }
            else
                lo = mid + 1;

        }
        return index;
    }
    /*
    public static <Key> int firstIndexOf(Key[] a, Key key, Comparator<Key> comparator) {

            int lo = 0;
        int hi = a.length - 1;

        // find first index of c in a b c c c d e

        // System.out.println("BinarySearchDeluxe::firstIndexOf " + key);

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int compare = comparator.compare(a[mid], key);

            if (compare > 0) hi = mid - 1;
            else if (compare < 0) lo = mid + 1;
            else {
                int nextcompare = 0;
                while (nextcompare == 0 && mid > 0) {
                    nextcompare = comparator.compare(a[mid - 1], key);
                    if (nextcompare == 0) {
                        mid--;
                    }
                }
                return mid;
            }
        }

        return -1;
    }
     */

    // Returns the index of the last key in the sorted array a[]
    // that is equal to the search key, or -1 if no such key.
    public static <Key> int lastIndexOf(Key[] a, Key key, Comparator<Key> comparator) {
        int lo = 0;
        int hi = a.length - 1;

        int index = -1;
        int compare_ct = 1;
        while (lo <= hi) {
            // System.out.println("Compare ct " + compare_ct + " lo " + lo + " hi " + hi);

            int mid = lo + (hi - lo) / 2; // 4
            int compare = comparator.compare(a[mid], key);

            if (compare <= 0) {
                lo = mid + 1;
                if (compare == 0) {
                    index = mid;
                    // System.out.println("Key found at index " + index);
                }
            }
            else
                hi = mid - 1;
            compare_ct++;
        }
        // System.out.println("Last Index took " + compare_ct + " compares");
        return index;
    }
    /*
    public static <Key> int lastIndexOf(Key[] a, Key key, Comparator<Key> comparator) {

        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int compare = comparator.compare(a[mid], key);
            if (compare > 0) hi = mid - 1;
            else if (compare < 0) lo = mid + 1;
            else {
                int nextcompare = 0;
                while (nextcompare == 0 && mid < (a.length - 1)) {
                    nextcompare = comparator.compare(a[mid + 1], key);
                    if (nextcompare == 0) {
                        mid++;
                    }
                }
                return mid;
            }
        }
        return -1;
    }
     */

    // unit testing (required)
    public static void main(String[] args) {

        int size = Integer.valueOf(args[0]);
        int key = Integer.valueOf(args[1]);

        Integer[] testArray = new Integer[size * size];
        // 1 1 1 2 2 2 3 3 3
        // 0 1 2 3 4 5 6 7 8
        /*
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                testArray[j + i * size] = i + 1;
            }
        }
         */
        for (int i = 0; i < size * size; i++) {
            testArray[i] = size;
        }

        String s = "";
        for (int x = 0; x < testArray.length; x++) {
            s += testArray[x] + " ";
        }
        System.out.println("Array: " + s);

        int first = firstIndexOf(testArray, key, Integer::compare);
        int last = lastIndexOf(testArray, key, Integer::compare);

        System.out.println("First index: " + first + " Last index: " + last);
    }
}
