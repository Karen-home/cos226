import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    // initial capacity of underlying resizing array
    private static int INIT_CAPACITY = 8;
    private Item[] array; // array of items
    private int size = 0; // number of elements

    private int counter = 0;
    private boolean[] already_iterated;

    // construct an empty randomized queue
    public RandomizedQueue() {
        array = (Item[]) new Object[INIT_CAPACITY];
        already_iterated = new boolean[INIT_CAPACITY];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    private void resize(int capacity) {
        assert capacity >= size;

        // textbook implementation
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            copy[i] = array[i];
        }
        array = copy;

        // copy iterator
        boolean[] copy_iterator = new boolean[capacity];
        for (int i = 0; i < size; i++) {
            copy_iterator[i] = already_iterated[i];
        }
        already_iterated = copy_iterator;

        // alternative implementation
        // a = java.util.Arrays.copyOf(a, capacity);

    }

    // add the item
    public void enqueue(Item item) {
        if (size == array.length) resize(2 * array.length);
        array[size++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        Item item = array[size - 1];
        array[size - 1] = null;                              // to avoid loitering
        size--;
        // shrink size of array if necessary
        if (size > 0 && size == array.length / 4) resize(array.length / 2);
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        return iterator().next();
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {

        public RandomizedQueueIterator() {

        }

        public boolean hasNext() {
            return counter < size;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();

            int r = StdRandom.uniformInt(size);
            int x = 0;
            while (already_iterated[r] && x < size) {
                r = StdRandom.uniformInt(size);
                x++;
            }

            if (x == size) {
                // Start over
                for (int j = 0; j < size; j++) {
                    already_iterated[j] = false;
                }
            }

            already_iterated[r] = true;

            Item i = array[r];
            counter++;
            return i;
        }
    }

    // unit testing (optional)
    public static void main(String[] args) {

        RandomizedQueue<Integer> q = new RandomizedQueue<Integer>();
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        q.enqueue(5);

        int r = q.sample();
        System.out.println("Random sample " + r);

        int n = 5;
        RandomizedQueue<Integer> queue = new RandomizedQueue<Integer>();
        for (int i = 0; i < n; i++)
            queue.enqueue(i);
        for (int a : queue) {
            for (int b : queue)
                StdOut.print(a + "-" + b + " ");
            StdOut.println();
        }
    }

}
