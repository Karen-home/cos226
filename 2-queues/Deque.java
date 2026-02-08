import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node first = null;
    private Node last = null;
    private Node current = null;
    private int current_direction = 1;
    private int size = 0;

    private class Node {
        private Item item;
        private Node next;
        private Node previous;
    }

    // construct an empty deque
    public Deque() {

    }

    // is the deque empty?
    public boolean isEmpty() {
        if (size == 0)
            return true;
        else return false;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {

        if (item == null) throw new IllegalArgumentException();

        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;
        first.previous = null;
        size++;
        if (size == 1) {
            last = first;
        }
        else {
            first.next.previous = first;
        }

        current = first;
    }

    // add the item to the back
    public void addLast(Item item) {

        if (item == null) throw new IllegalArgumentException();

        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.previous = oldLast;
        last.next = null;
        size++;
        if (size == 1) {
            current = last;
            first = last;
        }
        else {
            oldLast.next = last;
        }
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (size == 0) throw new IllegalArgumentException();

        Item item = first.item;

        size--;
        if (size != 0) {
            first = first.next;
            first.previous = null;
        }
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (size == 0) throw new IllegalArgumentException();

        Item item = last.item;
        last = last.previous;
        size--;
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private int i = 0;

        public boolean hasNext() {
            return (current != null);
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            if (current_direction == 1)
                current = current.next;
            else
                current = current.previous;
            return item;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {

        /* Test #1 -
        Create a deque of integers and add the numbers 1 through 10
        to the deque using addFirst(), and then remove them all using
        removeLast(). Verify that the output of the removals is the numbers
        1 through 10 (in this order), e.g., by printing "Error" to the
        standard output if you find a discrepancy.*/

        System.out.println("=== Test 1 ===");

        Deque<Integer> intdeque = new Deque<Integer>();
        for (int i = 1; i <= 10; i++) {
            intdeque.addFirst(i);
        }
        int s = intdeque.size();
        String res = "";
        for (int j = 0; j < s; j++) {
            int lastint = intdeque.removeLast();
            res += lastint + " ";
        }
        String correct1 = "1 2 3 4 5 6 7 8 9 10 ";
        if (res.equals(correct1)) {
            System.out.println("Success " + correct1);
        }
        else {
            System.out.println("Error" + res);
        }

        /* Test #2 -
        Create a deque of integers and add the numbers 1 through 10
        to the deque using addLast(), and then remove them all using
        removeFirst(). Verify that the output of the removals is the numbers
        1 through 10 (in this order), e.g., by printing "Error" to the
        standard output if you find a discrepancy.*/

        System.out.println("=== Test 2 ===");

        intdeque = new Deque<Integer>();
        for (int i = 1; i <= 10; i++) {
            intdeque.addLast(i);
        }

        s = intdeque.size();
        String res2 = "";
        for (int j = 0; j < s; j++) {
            int lastint = intdeque.removeFirst();
            res2 += lastint + " ";
        }
        String correct2 = "1 2 3 4 5 6 7 8 9 10 ";
        if (res2.equals(correct2)) {
            System.out.println("Success " + correct2);
        }
        else {
            System.out.println("Error" + res2);
        }

        // String s = deque.removeFirst();

        // for (Integer t : intdeque) {
        //    System.out.println(t);
        //}
    }

}
