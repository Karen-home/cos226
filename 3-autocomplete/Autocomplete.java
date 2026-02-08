import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class Autocomplete {

    Term[] terms;
    int num_matches = 0;

    // Initializes the data structure from the given array of terms.
    public Autocomplete(Term[] terms) {
        this.terms = terms;
    }

    // Returns all terms that start with the given prefix,
    // in descending order of weight.
    public Term[] allMatches(String prefix) {
        Comparator<Term> pc = Term.byPrefixOrder(prefix.length());
        Term p = new Term(prefix, prefix.length());
        int firstIndex = BinarySearchDeluxe.firstIndexOf(terms, p, pc);
        int lastIndex = BinarySearchDeluxe.lastIndexOf(terms, p, pc);

        if (firstIndex != -1)
            num_matches = lastIndex - firstIndex + 1;
        else
            num_matches = 0;

        // System.out.println("allMatches: There are " + num_matches + " matches");
        Term[] matches = new Term[num_matches];
        if (num_matches > 0) {
            for (int i = 0; i < num_matches; i++) {
                matches[i] = terms[firstIndex + i];
            }
        }

        // Sort by reverse weight
        Comparator<Term> rc = Term.byReverseWeightOrder();
        Arrays.sort(matches, rc);

        return matches;
    }

    // Returns the number of terms that start with the given prefix.
    public int numberOfMatches(String prefix) {

        // TO DO: simplify this
        /*
        if (num_matches ) {
            Comparator<Term> pc = Term.byPrefixOrder(prefix.length());
            Term p = new Term(prefix, prefix.length());
            int firstIndex = BinarySearchDeluxe.firstIndexOf(terms, p, pc);
            int lastIndex = BinarySearchDeluxe.lastIndexOf(terms, p, pc);
            num_matches = lastIndex - firstIndex + 1;
        }

         */

        return num_matches;
    }

    // unit testing
    public static void main(String[] args) {
        // read in the terms from a file
        String filename = args[0];
        In in = new In(filename);
        int n = in.readInt();
        Term[] terms = new Term[n];
        for (int i = 0; i < n; i++) {

            long weight = in.readLong();           // read the next weight
            in.readChar();                         // scan past the tab
            String query = in.readLine();          // read the next query
            // System.out.println("Autocomplete::main reading " + query);
            terms[i] = new Term(query, weight);    // construct the term
        }

        // Arrays.sort(terms, Collections.reverseOrder());
        Arrays.sort(terms);
        // System.out.println("Sorted array:");
        // for (int a = 0; a < terms.length; a++) {
        //    System.out.println(terms[a]);
        //}


        // read in queries from standard input and print the top k matching terms
        int k = Integer.parseInt(args[1]);
        Autocomplete autocomplete = new Autocomplete(terms);
        while (StdIn.hasNextLine()) {
            String prefix = StdIn.readLine();
            Term[] results = autocomplete.allMatches(prefix);
            StdOut.printf("%d matches\n", autocomplete.numberOfMatches(prefix));
            for (int i = 0; i < Math.min(k, results.length); i++)
                StdOut.println(results[i]);
        }

    }

}
