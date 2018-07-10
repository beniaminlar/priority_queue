package com.ngisystems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ConcurrentDynamicPriorityQueueIteratorConcurrencyTest {

    ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();

    @BeforeEach
    void makeConcurrentModifications() {
        new Random()
                .ints(100, 1, 1000)
                .forEach(q::add);

        Runnable worker = () -> {
            Iterator<Integer> iterator = q.iterator();
            while (iterator.hasNext()) {
                Integer nextElement = iterator.next();
                if(nextElement % 2 == 0) {
                    try {
                        q.update(nextElement, nextElement + 1);
                    } catch (NoSuchElementException e) {
                        //the element no longer exists in the live queue
                    }
                }
            }
        };

        Thread t1 = new Thread(worker);
        Thread t2 = new Thread(worker);
        Thread t3 = new Thread(worker);
        Thread t4 = new Thread(worker);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            //Wait for threads to finish.
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            fail("Execution thread interrupted");
        }
    }


    @Test
    /**
     * In this test multiple threads will try to iterate over an existing queue and replace all even elements with
     * uneven elements.
     * At the end the queue should contain only uneven elements.
     */
    void iteratorShouldAllowConcurrentModification() {
        while (q.peek() != null) {
            assertTrue(q.poll() % 2 != 0);
        }
    }

    @Test
    /**
     * In this test multiple threads will try to iterate over an existing queue and replace all even elements with
     * uneven elements.
     * At the end the queue should contain only uneven elements.
     */
    void iteratorShouldAllowConcurrentModificationAndKeepConsistency() {
        List<Integer> retrievedElements = new ArrayList<>();
        while (q.peek() != null) {
            retrievedElements.add(q.poll());
        }

        List<Integer> sortedElements = new ArrayList<>(retrievedElements);
        Collections.sort(sortedElements);
        Collections.reverse(sortedElements);

        assertEquals(sortedElements, retrievedElements);
    }
}
