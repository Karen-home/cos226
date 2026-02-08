import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    public WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {

        int maxdistance = -1;
        String pariah = "";

        for (String noun : nouns) {
            int totaldist = 0;
            for (String other : nouns) {
                if (noun != other)
                    totaldist += wordnet.distance(noun, other);
            }
            if (totaldist > maxdistance) {
                maxdistance = totaldist;
                pariah = noun;
            }
        }

        return pariah;
    }

    // test client (see below)
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
