package com.alexecollins.concurrent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.*;

/**
 * @author: alexec (alex.e.c@gmail.com)
 */
@RunWith(Parameterized.class)
public class BoundedPriorityBlockingQueueTest {

    @Parameterized.Parameters
    public static List<Object[]> data() {
        return Arrays.asList(
                new Object[]{ PriorityBlockingQueue.class},
                new Object[]{ BoundedPriorityBlockingQueue.class}
        )     ;
    }

    private final BlockingQueue<ComparableStub> sut;

    public BoundedPriorityBlockingQueueTest(Class<? extends BlockingQueue> clazz) {
        this.sut = clazz.equals(PriorityBlockingQueue.class) ? new PriorityBlockingQueue<ComparableStub>() :
                new BoundedPriorityBlockingQueue<ComparableStub>(3, 3);
    }

    @Test
    public void testIterator() throws Exception {
        assertNotNull(sut.iterator());
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(0, sut.size());

        sut.add(new ComparableStub(1));
        sut.add(new ComparableStub(2));
        sut.add(new ComparableStub(0));

        assertEquals(3, sut.size());

        new Thread() {
            @Override
            public void run() {
                try {
                    sut.put(new ComparableStub(0));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.start();

        Thread.sleep(10);

        assertEquals(sut instanceof BoundedPriorityBlockingQueue ? 3 : 4, sut.size());

        sut.take();
        assertEquals(3, sut.size());

        sut.take();
        assertEquals(2, sut.size());
    }

    @Test(expected = NullPointerException.class)
    public void testPutNPE() throws Exception {
        sut.put(null);
    }

    @Test
    public void testPut() throws Exception {
        sut.put(new ComparableStub(0));
        sut.put(new ComparableStub(0));
        sut.put(new ComparableStub(0));

        assertEquals(3, sut.size());

        new Thread() {
            @Override
            public void run() {
                try {
                    sut.put(new ComparableStub(0));
                } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
                }
            }
        }.start();

        Thread.sleep(10);

        assertEquals(sut instanceof BoundedPriorityBlockingQueue ? 3 : 4, sut.size());

        sut.take();
        assertEquals(3, sut.size());

        sut.take();
        assertEquals(2, sut.size());
    }


    @Test
    public void testTake() throws Exception {
        sut.put(new ComparableStub(1));
        sut.put(new ComparableStub(3));
        sut.put(new ComparableStub(2));

        assertEquals(3, sut.size());

        new Thread() {
            @Override
            public void run() {
                try {
                    sut.put(new ComparableStub(0));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.start();

        Thread.sleep(10);

        assertEquals(new ComparableStub(sut instanceof PriorityBlockingQueue ? 0 : 1), sut.take());
        assertEquals(new ComparableStub(sut instanceof PriorityBlockingQueue ? 1 : 0), sut.take());
        assertEquals(new ComparableStub(2), sut.take());
        assertEquals(new ComparableStub(3), sut.take());
    }

    @Test
    public void testRemainingCapacity() throws Exception {
        assertTrue(sut.remainingCapacity() > 0);

        if (sut instanceof  BoundedPriorityBlockingQueue) {
            int r = sut.remainingCapacity();

             sut.put(new ComparableStub(0));

            assertEquals(r - 1, sut.remainingCapacity());
        }
    }

    @Test                         (expected = NullPointerException.class)
    public void testDrainToNotNull() throws Exception {
        sut.drainTo(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDrainToThis() throws Exception {
        sut.drainTo(sut);
    }

    @Test
    public void testDrainTo() throws Exception {
        Collection<ComparableStub> drain = new LinkedList<ComparableStub>()    ;

        sut.drainTo(drain);

        assertEquals(0, drain.size());

        sut.put(new ComparableStub(0));

        sut.drainTo(drain);

        assertEquals(1, drain.size());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testDrainToThisMaxElements() throws Exception {
        sut.drainTo(sut, 3);
    }

    @Test
    public void testDrainToMaxElements() throws Exception {
        Collection<ComparableStub> drain = new LinkedList<ComparableStub>()    ;

        sut.put(new ComparableStub(0));
        sut.put(new ComparableStub(1));

        assertEquals(1, sut.drainTo(drain, 1));

        assertEquals(1, drain.size());
    }

    @Test(expected = NullPointerException.class)
    public void testOfferNull() throws Exception {
        sut.offer(null);
    }

    @Test
    public void testOffer() throws Exception {
        assertTrue(sut.offer(new ComparableStub(0)));
        assertTrue(sut.offer(new ComparableStub(0)));
        assertTrue(sut.offer(new ComparableStub(0)));

        boolean b = sut.offer(new ComparableStub(0));

        assertEquals(b, !(sut instanceof BoundedPriorityBlockingQueue));
    }

    @Test
    public void testPoll() throws Exception {
        assertNull(sut.poll());

        sut.put(new ComparableStub(0));

        assertEquals(new ComparableStub(0), sut.poll());

    }


    @Test
    public void testPollTime() throws Exception {
        assertNull(sut.poll(1, TimeUnit.SECONDS));

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                    sut.put(new ComparableStub(0));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }.start();

        assertEquals(new ComparableStub(0), sut.poll(1, TimeUnit.SECONDS));
    }


    @Test
    public void testPeek() throws Exception {
       assertNull(sut.peek());

        sut.put(new ComparableStub(0));

        assertNotNull(sut.peek());
    }
}
