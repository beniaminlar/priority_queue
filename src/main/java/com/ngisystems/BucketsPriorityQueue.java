package com.ngisystems;

import java.util.Arrays;
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
public class BucketsPriorityQueue {

    private static final int BUCKET_INITIAL_CAPACITY = 5;

    private static class Bucket {
        Prioritizable[] elements;
        int size;

        public Bucket() {
            elements = new Prioritizable[BUCKET_INITIAL_CAPACITY];
        }

        void add(Prioritizable element) {
            if (size >= elements.length - 1) {
                elements = Arrays.copyOf(elements, elements.length * 2);
            }
            elements[size++] = element;
        }

        Prioritizable take() {
            if(size == 0) return null;
            Prioritizable e = elements[size - 1];
            elements[--size] = null;
            return e;
        }

        Prioritizable peek() {
            if(size == 0) return null;
            return elements[size - 1];
        }
    }

    private Bucket[] buckets;
    private final int maxPriority;
    private int top;

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
    public synchronized void add(Prioritizable element) throws IllegalStateException {
        if(element.getPriority() > maxPriority || element.getPriority() < 1) {
            throw new IllegalStateException("Priority must be between 1 and " + maxPriority);
        }

        int index = element.getPriority() - 1;
        buckets[index].add(element);
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
    public synchronized Prioritizable poll() {
        Prioritizable result = top >= 0 ? buckets[top].take() : null;

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
    public synchronized Prioritizable peek() {
        return top >= 0 ? buckets[top].peek() : null;
    }

    /**
     * Updates the priority of the element.
     *
     * @param element the element to update
     * @param newPriority the new priority
     * @throws NoSuchElementException if the element is not found in the queue
     */
    public synchronized void update(Prioritizable element, int newPriority) throws NoSuchElementException {
        Bucket bucket = buckets[element.getPriority() - 1];
        boolean removed = false;
        for(int i = 0; i < bucket.size; i++) {
            if(bucket.elements[i].equals(element)) {
                for(int j = i; j < bucket.size - 1; j++) {
                    bucket.elements[j] = bucket.elements[j + 1];
                }
                bucket.elements[--bucket.size] = null;
                removed = true;

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
}
