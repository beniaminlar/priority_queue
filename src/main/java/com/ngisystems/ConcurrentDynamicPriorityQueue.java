package com.ngisystems;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A queue that returns elements in order of their priority, from highest to lowest
 * <p>Operations on this class make no guarantees about the ordering of elements with equal priority.
 *
 * @param <E> the type of elements held in the queue.(Must implement {@code Comparable<E>}
 */
public class ConcurrentDynamicPriorityQueue<E extends Comparable<E>> {

    private static final int INITIAL_CAPACITY = 10;
    private E[] queue;
    private int size;

    public ConcurrentDynamicPriorityQueue() {
        queue = (E[]) new Comparable[INITIAL_CAPACITY];
        size = 0;
    }

    /**
     * Inserts the specified element into this priority queue.
     *
     * @param e the element to add
     */
    public synchronized void add(E e) {
        if (size >= queue.length - 1) {
            queue = resize();
        }
        queue[++size] = e;
        bubbleUp(this.size);
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * @return the head of this queue, or null if this queue is empty
     */
    public synchronized E poll() {
        if (size < 1) return null;
        return removeFromIndex(1);
    }

    /**
     * Retrieves the head of this queue, or returns null if this queue is empty.
     * @return the head of this queue, or null if this queue is empty
     */
    public synchronized E peek() {
        if (size < 1) return null;
        return queue[1];
    }

    /**
     * Updates an existing element in the queue.
     * This method should be used to update the priority of the element.
     * @param existingElement the original element from the queue
     * @param newElement the updated element to be added
     * @throws NoSuchElementException if the {@code existingElement} is not found in the queue
     */
    public synchronized void update(E existingElement, E newElement)
            throws NoSuchElementException {
        remove(existingElement);
        add(newElement);
    }

    /**
     * Returns an iterator over the elements in this queue. The
     * iterator does not return the elements in any particular order.
     *
     * <p>The returned iterator is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException
     * ConcurrentModificationException}, and guarantees to traverse
     * elements as they existed upon construction of the iterator.
     *
     * @return an iterator over the elements in this queue
     */
    public synchronized Iterator<E> iterator() {
        return new Itr(Arrays.copyOf(queue, size + 1));
    }

    private E remove(E elementToRemove) throws NoSuchElementException {
        for (int i = 1; i <= size; i++) { //linear time O(n)
            if (queue[i].equals(elementToRemove)) {
                return removeFromIndex(i);
            }
        }
        throw new NoSuchElementException();
    }

    private E[] resize() {
        E[] largerQueue = (E[]) new Comparable[queue.length * 2];
        for (int i = 1; i <= size; i++) {
            largerQueue[i] = queue[i];
        }
        return largerQueue;
    }


    //     if ( value in replacement node < its parent node )
//    Filter the replacement node UP the binary tree
//         else
//    Filter the replacement node DOWN the binary tree

    private E removeFromIndex(int index) {
        E removedElement = queue[index];
        if(index == size) {
            queue[size--] = null;
            return removedElement;
        }
        queue[index] = queue[size];
        queue[size--] = null;
        if(index == 1 || queue[index].compareTo(queue[parentIndex(index)]) < 0) {
            bubbleDown(index);
        } else {
            bubbleUp(index);
        }
        return removedElement;
    }

    private class Itr implements Iterator<E> {
        private E[] items;
        private int cursor = 1;

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

    //Binary Heap methods
    private void bubbleDown(int index) {
        while (hasLeftChild(index)) {
            int largerChild = leftIndex(index);

            if (hasRightChild(index)
                    && queue[leftIndex(index)].compareTo(queue[rightIndex(index)]) < 0) {
                largerChild = rightIndex(index);
            }

            if (queue[index].compareTo(queue[largerChild]) < 0) {
                swap(index, largerChild);
            } else {
                break;
            }

            index = largerChild;
        }
    }

    private void bubbleUp(int index) {

        while (index > 1
                && (queue[parentIndex(index)].compareTo(queue[index]) < 0)) {
            // parent/child are out of order; swap them
            swap(index, parentIndex(index));
            index = parentIndex(index);
        }
    }

    private void swap(int index1, int index2) {
        E tmp = queue[index1];
        queue[index1] = queue[index2];
        queue[index2] = tmp;
    }

    private int leftIndex(int i) {
        return i * 2;
    }

    private int rightIndex(int i) {
        return i * 2 + 1;
    }

    private boolean hasLeftChild(int i) {
        return leftIndex(i) <= size;
    }

    private boolean hasRightChild(int i) {
        return rightIndex(i) <= size;
    }

    private int parentIndex(int i) {
        return i / 2;
    }
}