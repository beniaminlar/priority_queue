package com.ngisystems;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentDynamicPriorityQueueTest {

    @Test
    void pollShouldReturnNullIfQueueEmpty() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();
        assertNull(q.poll(), "Poll should return null if the queue is empty.");
    }

    @Test
    void pollShouldRemoveRetrievedElementFromTheQueue() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();
        q.add(1);
        q.poll();
        assertNull(q.poll(), "Poll should remove the retrieved element from the queue.");
    }

    @Test
    void peekShouldReturnNullIfQueueEmpty() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();
        assertNull(q.peek(), "Peek should return null if the queue is empty.");
    }

    @Test
    void peekShouldNotRemoveRetrievedElement() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();
        q.add(1);
        q.peek();
        assertNotNull(q.peek(), "Peek should not remove the retrieved element from the queue.");
    }

    @Test
    void updateShouldCorrectlyUpdateHighestPriorityElement() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();
        q.add(7);
        q.add(2);
        q.update(7, 5);
        assertEquals((Integer) 5, q.poll(), "Update should correctly update the highest priority element.");
    }

    @Test
    void updateShouldCorrectlyUpdateLowestPriorityElement() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();
        q.add(7);
        q.add(2);
        q.update(2, 9);
        assertEquals((Integer) 9, q.poll(), "Update should correctly update the lowest priority element.");
    }

    @Test
    void updateShouldThrowExceptionWhenElementNotFound() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();
        q.add(7);
        q.add(2);

        Executable updateOperation = () -> q.update(1, 3);

        assertThrows(NoSuchElementException.class, updateOperation, "NoSuchElementException should be thrown" +
                "if the element is not found");
    }

    @Test
    void updateShouldThrowExceptionWhenQueueIsEmpty() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();

        Executable updateOperation = () -> q.update(1, 3);

        assertThrows(NoSuchElementException.class, updateOperation, "NoSuchElementException should be thrown" +
                "if the queue is empty");
    }

    @Test
    void updateShouldWorkIfNewElementHasSamePriority() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();
        q.add(7);
        q.add(2);

        q.update(7, 7);
        assertEquals((Integer) 7, q.poll(), "Update should work correctly if the priority remains the same");
    }



    @Test
    void queueShouldAlwaysReturnHighestPriorityElement() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();

        List<Integer> values = new ArrayList<>();
        new Random()
                .ints(20, 1, 1000)
                .forEach(values::add);

        values.forEach(q::add);

        //order inserted elements in order of priority
        Collections.sort(values);
        Collections.reverse(values);

        //retrieve all the elements from the queue
        List<Integer> retrievedValues = new ArrayList<>();
        while (q.peek() != null) {
            retrievedValues.add(q.poll());
        }

        assertEquals(retrievedValues, values);
    }

    @Test
    void queueShouldMaintainConsistencyInMultithreadedEnvironment() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();

        Runnable worker = () -> {
            for(int i = 0; i < 1000; i++) {
                q.add(ThreadLocalRandom.current().nextInt(0, 1000));
                if(i % 10 == 0) {
                    q.peek();
                }
                if(i % 5 == 0) {
                    q.poll();
                }
            }
        };

        Thread t1 = new Thread(worker);
        Thread t2 = new Thread(worker);
        Thread t3 = new Thread(worker);

        t1.start();
        t2.start();
        t3.start();

        try {
            //Wait for threads to finish.
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            fail("Execution thread interrupted");
        }

        //verify that the queue is still consistent
        Integer lastPollResult = q.poll();
        while (q.peek() != null) {
            assertTrue(lastPollResult >= q.peek(), "Elements should be retrieved in order of priority" +
                    " after multithreaded use");
            lastPollResult = q.poll();
        }
    }
}
