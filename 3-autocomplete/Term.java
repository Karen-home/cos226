import java.util.Comparator;

public class Term implements Comparable<Term> {

    String query;
    long weight;

    // Initializes a term with the given query string and weight.
    public Term(String query, long weight) {
        this.query = query;
        this.weight = weight;
    }


    private static class ReverseTermComparator implements Comparator<Term> {
        public int compare(Term a, Term b) {
            if (a.weight < b.weight)
                return 1;
            else if (a.weight > b.weight)
                return -1;
            else return 0;
        }
    }

    // Compares the two terms in descending order by weight.
    public static Comparator<Term> byReverseWeightOrder() {
        return new ReverseTermComparator();

        // Usage:
        // Comparator<Term> comparator = Term.byReverseWeightOrder();
        // comparator.compare(term1, term2);
        // validate(0, comparator.compare(term1, term2));

    }

    private static class PrefixTermComparator implements Comparator<Term> {

        private int length = 0;

        // Initializes a term with the given query string and weight.
        public PrefixTermComparator(int len) {
            length = len;
        }

        public int compare(Term a, Term b) {
            // to do: improve perf by comparing indiv char
            // System.out.println("PrefixTermComparator::compare: " + a.query + " " + b.query);

            int letter_index = 0;
            Character ac = a.query.charAt(letter_index);
            Character bc = b.query.charAt(letter_index);
            int result = ac.compareTo(bc);

            while (result == 0 && letter_index < length - 1) {
                letter_index++;
                if (letter_index < a.query.length())
                    ac = a.query.charAt(letter_index);
                else
                    return -1;
                if (letter_index < b.query.length())
                    bc = b.query.charAt(letter_index);
                else
                    return 1;
                result = ac.compareTo(bc);
            }
            return result;

            // String as = a.query.substring(0, Math.min(length, a.query.length()));
            // String bs = b.query.substring(0, Math.min(length, b.query.length()));
            // return as.compareTo(bs);
        }
    }


    // Compares the two terms in lexicographic order,
    // but using only the first r characters of each query.
    public static Comparator<Term> byPrefixOrder(int r) {
        return new PrefixTermComparator(r);
    }

    // Compares the two terms in lexicographic order by query.
    public int compareTo(Term that) {
        // System.out.println("Term::compareTo " + query + " " + that.query);
        return (query.compareTo(that.query));
    }

    // Returns a string representation of this term in the following format:
    // the weight, followed by a tab, followed by the query.
    public String toString() {
        return Long.toString(weight) + "\t" + query;
    }

    // unit testing (required)
    public static void main(String[] args) {
        Term a = new Term(args[0], 2);
        Term b = new Term(args[1], 3);
        int l = Integer.valueOf(args[2]);

        int res = a.compareTo(b);
        System.out.println("Compare result for " + a.query + " and " + b.query + " :" + res);

        Comparator<Term> rc = Term.byReverseWeightOrder();
        res = rc.compare(a, b);
        System.out.println("Reverse compare result: " + a.weight + " and " + b.weight + " :" + res);

        Comparator<Term> pc = Term.byPrefixOrder(l);
        res = pc.compare(a, b);
        System.out.println("Prefix compare result for len " + l + " :" + res);

        String as = a.query.substring(0, Math.min(l, a.query.length()));
        String bs = b.query.substring(0, Math.min(l, b.query.length()));
        int subres = as.compareTo(bs);
        System.out.println("Substring compare result: " + subres);
        if (subres == res) {
            System.out.println("Test success: matching compares");
        }
        else {
            System.out.println("Test failed: compares did not match");
        }

        // validate(0, comparator.compare(term1, term2));

    }

}
