package test.future;

import main.faas.future.impl.ResultFutureImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ResultFutureImplTest {

    private ResultFutureImpl<String> resultFuture;

    @Before
    public void setUp() {
        resultFuture = new ResultFutureImpl<>();
    }

    @Test
    public void testIsDone_InitiallyFalse() {
        assertFalse(resultFuture.isDone());
    }

    @Test
    public void testSet_SingleValue() {
        String testValue = "test";
        resultFuture.set(testValue);

        assertTrue(resultFuture.isDone());
    }

    @Test
    public void testGet_BlocksUntilValueSet() throws InterruptedException {
        String testValue = "test";

        Thread producer = new Thread(() -> {
            try {
                Thread.sleep(100); // Simula algún procesamiento
                resultFuture.set(testValue);
            } catch (InterruptedException e) {
                fail("Interrupción inesperada");
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                List<String> results = resultFuture.get();
                assertEquals(1, results.size());
                assertEquals(testValue, results.get(0));
            } catch (InterruptedException e) {
                fail("Interrupción inesperada");
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }

    @Test
    public void testAdd_AdditionalValues() {
        String value1 = "test1";
        String value2 = "test2";

        resultFuture.add(value1);
        resultFuture.set(value2);

        try {
            List<String> results = resultFuture.get();
            assertEquals(2, results.size());
            assertTrue(results.contains(value1));
            assertTrue(results.contains(value2));
        } catch (InterruptedException e) {
            fail("Interrupción inesperada");
        }
    }
}

