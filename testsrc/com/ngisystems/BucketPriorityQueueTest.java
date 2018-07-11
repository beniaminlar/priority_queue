package com.ngisystems;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BucketPriorityQueueTest {

    @Test
    void pollShouldReturnNullIfQueueEmpty() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);
        assertNull(q.poll(), "Poll should return null if the queue is empty.");
    }

    @Test
    void pollShouldRemoveRetrievedElementFromTheQueue() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);
        q.add(new TestPrioritizable("a", 1));
        q.poll();
        assertNull(q.poll(), "Poll should remove the retrieved element from the queue.");
    }

    @Test
    void peekShouldReturnNullIfQueueEmpty() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);
        assertNull(q.peek(), "Peek should return null if the queue is empty.");
    }

    @Test
    void peekShouldNotRemoveRetrievedElement() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);
        q.add(new TestPrioritizable("a", 1));
        q.peek();
        assertNotNull(q.peek(), "Peek should not remove the retrieved element from the queue.");
    }

    @Test
    void updateShouldCorrectlyUpdateHighestPriorityElement() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);
        TestPrioritizable t7 = new TestPrioritizable("g", 7);
        TestPrioritizable t2 = new TestPrioritizable("b", 2);
        q.add(t7);
        q.add(t2);
        q.update(t7, 1);

        assertEquals(t2, q.poll(), "Update should correctly update the highest priority element.");
    }

    @Test
    void updateShouldCorrectlyUpdateLowestPriorityElement() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);
        TestPrioritizable t7 = new TestPrioritizable("g", 7);
        TestPrioritizable t2 = new TestPrioritizable("b", 2);
        q.add(t7);
        q.add(t2);
        q.update(t2, 9);
        assertEquals(t2, q.poll(), "Update should correctly update the lowest priority element.");
    }

    @Test
    void updateShouldThrowExceptionWhenElementNotFound() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);
        TestPrioritizable t7 = new TestPrioritizable("g", 7);
        TestPrioritizable t2 = new TestPrioritizable("b", 2);
        TestPrioritizable t5 = new TestPrioritizable("c", 5);
        q.add(t7);
        q.add(t2);

        Executable updateOperation = () -> q.update(t5, 3);

        assertThrows(NoSuchElementException.class, updateOperation, "NoSuchElementException should be thrown" +
                "if the element is not found");
    }

    @Test
    void updateShouldThrowExceptionWhenQueueIsEmpty() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);

        Executable updateOperation = () -> q.update(new TestPrioritizable("g", 7), 3);

        assertThrows(NoSuchElementException.class, updateOperation, "NoSuchElementException should be thrown" +
                "if the queue is empty");
    }

    @Test
    void updateShouldWorkIfNewElementHasSamePriority() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);
        TestPrioritizable t7 = new TestPrioritizable("g", 7);
        TestPrioritizable t2 = new TestPrioritizable("b", 2);
        q.add(t7);
        q.add(t2);

        q.update(t7, 7);
        assertEquals(t7, q.poll(), "Update should work correctly if the priority remains the same");
    }

    @Test
    void queueShouldRespectPriorityBounds() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);
        TestPrioritizable tMaxAllowed = new TestPrioritizable("a", 10);
        q.add(tMaxAllowed);
        assertEquals(tMaxAllowed, q.poll());

        Executable insertLarger = () -> q.add(new TestPrioritizable("b", 11));
        assertThrows(IllegalStateException.class, insertLarger, "Elements with larger priority should not be allowed.");

        TestPrioritizable tMinAllowed = new TestPrioritizable("a", 1);
        q.add(tMinAllowed);
        assertEquals(tMinAllowed, q.poll());

        Executable insertSmaller = () -> q.add(new TestPrioritizable("b", 0));
        assertThrows(IllegalStateException.class, insertSmaller, "Elements with priority < 0 should not be allowed");

    }

    @Test
    void queueShouldAlwaysReturnHighestPriorityElement() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);

        List<TestPrioritizable> values = new ArrayList<>();
        new Random()
                .ints(20, 1, 10)
                .forEach(p -> values.add(new TestPrioritizable(String.valueOf(p), p)));

        values.forEach(q::add);

        //order inserted elements in order of priority
        Collections.sort(values);
        Collections.reverse(values);

        //retrieve all the elements from the queue
        List retrievedValues = new ArrayList();
        while (q.peek() != null) {
            retrievedValues.add(q.poll());
        }

        assertEquals(values, retrievedValues);
    }
}
