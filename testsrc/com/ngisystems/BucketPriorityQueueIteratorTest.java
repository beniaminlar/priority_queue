package com.ngisystems;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class BucketPriorityQueueIteratorTest {

    @Test
    void iteratorShouldIterateAllElements() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);

        int insertedValues = 10;
        new Random()
                .ints(insertedValues, 1, 10)
                .forEach(p -> q.add(new TestPrioritizable("value", p)));
        Iterator<Prioritizable> iterator = q.iterator();
        int iteratedValues = 0;
        while (iterator.hasNext()) {
            iteratedValues++;
            iterator.next();
        }

        assertEquals(insertedValues, iteratedValues, "Iterator should iterate all queue elements");
    }

    @Test
    void nextShouldThrowExceptionWhenHasNextFalse() {
        BucketsPriorityQueue q = new BucketsPriorityQueue(10);

        Iterator<Prioritizable> iterator = q.iterator();
        assertFalse(iterator.hasNext());

        Executable nextOperation = iterator::next;
        assertThrows(NoSuchElementException.class, nextOperation, "Iterator#next should throw exception " +
                "if no more elements exist");
    }

    @Test
    /**
     * In this test multiple threads will try to iterate over an existing queue and replace all even elements with
     * uneven elements.
     * At the end the queue should be consistent.
     */
    void iteratorShouldAllowConcurrentModificationAndKeepConsistency() {
        BucketsPriorityQueue<TestPrioritizable> q = new BucketsPriorityQueue<>(10);
        String[] values = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j" };
        new Random()
                .ints(10, 1, 10)
                .forEach(p -> q.add(new TestPrioritizable(values[p], p)));

        Runnable worker = () -> {
            Iterator<TestPrioritizable> iterator = q.iterator();
            while (iterator.hasNext()) {
                TestPrioritizable nextElement = iterator.next();
                if(ThreadLocalRandom.current().nextInt(0, 1000) % 2 == 0) {
                    try {
                        q.update(nextElement, ThreadLocalRandom.current().nextInt(1, 10));
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

        List<Integer> retrievedElements = new ArrayList<>();
        while (q.peek() != null) {
            retrievedElements.add(q.poll().getPriority());
        }

        List<Integer> sortedElements = new ArrayList<>(retrievedElements);
        Collections.sort(sortedElements);
        Collections.reverse(sortedElements);

        assertEquals(sortedElements, retrievedElements);
    }
}
