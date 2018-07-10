package com.ngisystems;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConcurrentDynamicPriorityQueueIteratorTest {

    @Test
    void iteratorShouldIterateAllElements() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();

        int insertedValues = 10;
        new Random()
                .ints(insertedValues, 1, 1000)
                .forEach(q::add);
        Iterator<Integer> iterator = q.iterator();
        int iteratedValues = 0;
        while (iterator.hasNext()) {
            iteratedValues++;
            iterator.next();
        }

        assertEquals(insertedValues, iteratedValues, "Iterator should iterate all queue elements");
    }

    @Test
    void nextShouldThrowExceptionWhenHasNextFalse() {
        ConcurrentDynamicPriorityQueue<Integer> q = new ConcurrentDynamicPriorityQueue<>();

        Iterator<Integer> iterator = q.iterator();
        assertFalse(iterator.hasNext());

        Executable nextOperation = iterator::next;
        assertThrows(NoSuchElementException.class, nextOperation, "Iterator#next should throw exception " +
                "if no more elements exist");
    }
}
