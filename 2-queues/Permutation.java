import edu.princeton.cs.algs4.StdIn;

public class Permutation {
    public static void main(String[] args) {

        int x = Integer.valueOf(args[0]);
        RandomizedQueue<String> q = new RandomizedQueue<String>();

        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            q.enqueue(item);
        }

        for (int i = 0; i < x; i++) {
            System.out.println(q.iterator().next());
        }

    }
}
