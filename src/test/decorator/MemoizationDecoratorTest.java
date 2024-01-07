package test.decorator;

import main.faas.decorator.MemoizationDecorator;
import main.faas.invoker.impl.InvokerImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MemoizationDecoratorTest {

    private MemoizationDecorator memoizationDecorator;
    private InvokerImpl testInvoker;

    @Before
    public void setUp() {
        testInvoker = new InvokerImpl(0, null, "testInvoker") {
            @Override
            public Object invokeAction(String actionName, Object params) {
                // Devuelve un resultado basado en la acción y los parámetros
                return actionName + ":" + params;
            }
        };
        memoizationDecorator = new MemoizationDecorator(testInvoker);
    }

    @Test
    public void testInvokeAction_CachedResult() throws Exception {
        String actionName = "testAction";
        Object params = "testParams";

        // Primera llamada para almacenar el resultado en caché
        Object firstResult = memoizationDecorator.invokeAction(actionName, params);

        // Segunda llamada debe devolver el mismo resultado desde la caché
        Object cachedResult = memoizationDecorator.invokeAction(actionName, params);

        assertEquals(firstResult, cachedResult);
    }

    @Test
    public void testInvokeAction_NewComputation() throws Exception {
        String actionName = "testAction";
        Object params = "newParams";

        // Primera llamada con nuevos parámetros
        Object result = memoizationDecorator.invokeAction(actionName, params);

        assertEquals(actionName + ":" + params, result);
    }

    @Test
    public void testInvokeAction_UpdateCacheWithDifferentParams() throws Exception {
        String actionName = "testAction";
        Object params1 = "params1";
        Object params2 = "params2";

        // Llamada con el primer conjunto de parámetros
        Object result1 = memoizationDecorator.invokeAction(actionName, params1);

        // Llamada con un segundo conjunto de parámetros
        Object result2 = memoizationDecorator.invokeAction(actionName, params2);

        assertNotEquals(result1, result2);
        assertEquals(actionName + ":" + params2, result2);
    }

}

