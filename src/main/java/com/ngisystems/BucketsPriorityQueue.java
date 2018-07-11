package com.ngisystems;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  A queue that returns elements in order of their priority, from highest to lowest.
 *  Elements must implement the Prioritizable interface and the priority must be an Integer.
 *  The maximum allowed priority must be supplied when the queue is created and the minimum priority is 1;
 *
 *  <p>This is best suited for a small nu</p>
 *
 *  <p>Operations on this class make no guarantees about the ordering of elements with equal priority.
 */
public class BucketsPriorityQueue<E extends Prioritizable> {

    private static final int BUCKET_INITIAL_CAPACITY = 5;

    private static class Bucket<E> {
        E[] elements;
        int size;

        public Bucket() {
            elements = (E[]) new Prioritizable[BUCKET_INITIAL_CAPACITY];
        }

        void add(E element) {
            if (size >= elements.length - 1) {
                elements = Arrays.copyOf(elements, elements.length * 2);
            }
            elements[size++] = element;
        }

        E take() {
            if(size == 0) return null;
            E e = elements[size - 1];
            elements[--size] = null;
            return e;
        }

        E peek() {
            if(size == 0) return null;
            return elements[size - 1];
        }
    }

    private Bucket<E>[] buckets;
    private final int maxPriority;
    private int top;
    private int count;

    /**
     * Constructs a priority queue that will hold {@code Prioritizable} elements with the maximum specified priority.
     *
     * <p>This runs in O(maxPriority) time because it needs to initialize all the buckets.</p>
     * @param maxPriority the maximum priority of elements allowed in the queue
     */
    public BucketsPriorityQueue(int maxPriority) {
        buckets = new Bucket[maxPriority];
        for(int i = 0; i < buckets.length; i++) {
            buckets[i] = new Bucket();
        }
        this.maxPriority = maxPriority;
        this.top = - 1;
    }

    /**
     * Adds the element to the queue.
     *
     * <p>This operation runs in amortized constant time. A simple insertion would run in constant time but the
     * underlying array might need to be increased in size.
     *
     * @param element the element to add to the queue
     * @throws IllegalStateException if the priority of the element is greater than the maximum queue priority
     */
    public synchronized void add(E element) throws IllegalStateException {
        if(element.getPriority() > maxPriority || element.getPriority() < 1) {
            throw new IllegalStateException("Priority must be between 1 and " + maxPriority);
        }

        int index = element.getPriority() - 1;
        buckets[index].add(element);
        count++;
        top = top < index ? index : top;
    }

    /**
     * Retrieves and removes the top priority element from the queue.
     *
     * <p>This operation runs in O(maxPriority) in the worst case
     * because it needs to update the reference to the top element.
     *
     * @return the top priority element or null if the queue is empty
     */
    public synchronized E poll() {
        E result = top >= 0 ? buckets[top].take() : null;
        count--;
        while (top >= 0 && buckets[top].size == 0) {
            top--;
        }

        return result;
    }

    /**
     * Retrieves the top priority element from the queue.
     *
     * <p>This operation runs in O(1)
     *
     * @return the top priority element or null if the queue is empty
     */
    public synchronized E peek() {
        return top >= 0 ? buckets[top].peek() : null;
    }

    /**
     * Updates the priority of the element that is found first in the queue.
     * If more equal elements exist only the first one will be updated.
     *
     * <p>This algorithm performs a sequential search in the list of elements with the same priority so this is
     * done in O(n) where n is the number of elements with the same priority.
     *
     * The update of top and the insertion is done in constant time.
     *
     * @param element the element to update
     * @param newPriority the new priority
     * @throws NoSuchElementException if the element is not found in the queue
     */
    public synchronized void update(E element, int newPriority) throws NoSuchElementException {
        Bucket bucket = buckets[element.getPriority() - 1];
        boolean removed = false;
        for(int i = 0; i < bucket.size; i++) {
            if(bucket.elements[i].equals(element)) {
                //delete the element
                for(int j = i; j < bucket.size - 1; j++) {
                    bucket.elements[j] = bucket.elements[j + 1];
                }
                bucket.elements[--bucket.size] = null;
                removed = true;
                count--;

                //update top if necessary
                if(element.getPriority() - 1 == top && bucket.size == 0) {
                    while (top >= 0 && buckets[top].size == 0) {
                        top--;
                    }
                }
                break;
            }
        }

        if(removed) {
            element.setPriority(newPriority);
            add(element);
        } else {
            throw new NoSuchElementException();
        }
    }


    /**
     * Returns an iterator over the elements in this queue. The
     * iterator returns the elements in order of their priority.
     *
     * <p>The returned iterator is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException
     * ConcurrentModificationException}, and guarantees to traverse
     * elements as they existed upon construction of the iterator.
     *
     * <p>The iterator uses a copy of the current queue and this is constructed in O(n)
     * @return an iterator over the elements in this queue
     */
    public synchronized Itr iterator() {
        int c = 0;
        E[] items = (E[]) new Prioritizable[count];

        for(int i = top; i >= 0; i--) {
            for(int j = 0; j < buckets[i].size; j++) {
                items[c++] = buckets[i].elements[j];
            }
        }
        return new Itr(items);
    }

    private class Itr implements Iterator<E> {
        private E[] items;
        private int cursor;

        Itr(E[] items) {
           this.items = items;
        }

        public boolean hasNext() {
            return cursor < items.length;
        }

        public E next() {
            if (cursor >= items.length)
                throw new NoSuchElementException();
            return items[cursor++];
        }
    }
}
