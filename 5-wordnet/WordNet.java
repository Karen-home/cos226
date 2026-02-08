import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.Topological;

public class WordNet {

    public ST<Integer, String[]> id_nouns = new ST<Integer, String[]>();
    public ST<String, Queue<Integer>> noun_ids = new ST<String, Queue<Integer>>();
    private Digraph G;
    private boolean verbose = false;
    ShortestCommonAncestor s;


    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {

        int size = readSynsets(synsets);
        readHypernyms(hypernyms, size);
    }

    private void readHypernyms(String hypernyms, int size) {

        In hypin = new In(hypernyms);

        // use hypernym file to create digraph edges
        // 34,48504,49019

        G = new Digraph(size);
        while (!hypin.isEmpty()) {
            String s = hypin.readLine();
            String delimiter = ",";
            String[] parts = s.split(delimiter);
            Integer id = Integer.parseInt(parts[0]); // index of word
            for (int i = 1; i < parts.length; i++) {
                G.addEdge(id, Integer.parseInt(parts[i]));
            }
        }
        // Check that it's a DAG
        Topological t = new Topological(G);
        if (!t.hasOrder())
            throw new IllegalArgumentException("is not a DAG");

        s = new ShortestCommonAncestor(G);
    }

    private int readSynsets(String synsets) {

        In synin = new In(synsets);

        // Synset example: 21,A a,the 1st letter of the Roman alphabet

        int size = 0;
        Integer id;
        String[] parts;
        String s;
        String synonyms;
        String[] syns;

        while (!synin.isEmpty()) {
            s = synin.readLine();
            parts = s.split(",");
            id = Integer.parseInt(parts[0]); // 21
            synonyms = parts[1]; // "A a"
            syns = synonyms.split(" ");

            // Add to id -> syns[] mapping
            id_nouns.put(id, syns);

            // Add to syn -> id[] mapping
            for (int i = 0; i < syns.length; i++) {
                String syn = syns[i];

                Queue<Integer> ids = new Queue<Integer>();
                if (noun_ids.contains(syn))
                    ids = noun_ids.get(syn);
                ids.enqueue(id);
                noun_ids.put(syn, ids);
            }
            size++;
        }
        return size;
    }

    // the set of all WordNet nouns
    public Iterable<String> nouns() {
        return noun_ids.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return noun_ids.contains(word);
    }

    // a synset (second field of synsets.txt) that is a shortest common ancestor
    // of noun1 and noun2 (defined below)
    public String sca(String noun1, String noun2) {

        String ancestor = "";

        if (noun_ids.contains(noun1) && noun_ids.contains(noun2)) {
            Queue<Integer> id1 = noun_ids.get(noun1);
            Queue<Integer> id2 = noun_ids.get(noun2);

            // for (int i1 : id1)
            //     System.out.println("Noun1 " + noun1 + " mapped to id " + i1);

            // for (int i2 : id2)
            //    System.out.println("Noun2 " + noun2 + " mapped to id " + i2);


            int antid = s.ancestorSubset(id1, id2);
            if (verbose)
                System.out.println("Shortest ancestor is " + antid);
            ancestor = antid + " ";

            String[] ancestors = id_nouns.get(antid);
            // System.out.println("Number of nouns mapped to id " + antid + " is " + ancestors.length);
            for (int i = 0; i < ancestors.length; i++) {
                ancestor += ancestors[i];
                if (i != ancestors.length - 1) ancestor += ", ";
            }

        }
        return ancestor;
    }

    // distance between noun1 and noun2 (defined below)
    public int distance(String noun1, String noun2) {

        int d = -1;
        if (noun_ids.contains(noun1) && noun_ids.contains(noun2)) {
            Queue<Integer> id1 = noun_ids.get(noun1);
            Queue<Integer> id2 = noun_ids.get(noun2);

            if (verbose) {
                for (int i1 : id1)
                    System.out.println("Noun1 " + noun1 + " mapped to id " + i1);

                for (int i2 : id2)
                    System.out.println("Noun2 " + noun2 + " mapped to id " + i2);
            }

            return s.lengthSubset(id1, id2);
        }
        return -1;
    }

    // unit testing
    public static void main(String[] args) {

        WordNet w = new WordNet(args[0], args[1]);
        String noun1 = args[2];
        String noun2 = args[3];
        String ancestor = w.sca(noun1, noun2);

        System.out.println("Closest ancestor(s) of " + noun1 + " and " + noun2 + ": " + ancestor);
        int d = w.distance(noun1, noun2);

        System.out.println("Closest distance between " + noun1 + " and " + noun2 + " is " + d);
    }

}
