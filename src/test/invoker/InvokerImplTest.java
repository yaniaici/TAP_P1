package test.invoker;

import main.faas.controller.Controller;
import main.faas.invoker.impl.InvokerImpl;
import main.faas.policymanager.PolicyManager;
import main.faas.policymanager.resourcemanagers.impl.RoundRobinResourceManagement;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.junit.Assert.*;

public class InvokerImplTest {

    private InvokerImpl invoker;
    private Controller dummyController;
    private PolicyManager policyManager;
    private Function<Object, Object> testAction;


    @Before
    public void setUp() {
        dummyController = new Controller();
        invoker = new InvokerImpl(1024, dummyController, "1");
        policyManager = new PolicyManager(new RoundRobinResourceManagement());
        dummyController.setPolicyManager(policyManager);
        dummyController.setInvokers(Collections.singletonList(invoker));
        invoker = new InvokerImpl(100, dummyController, "testInvoker");
    }

    @Test
    public void testRegisterActionWithSufficientMemory() {
        invoker.registerAction("testAction", params -> "result", 50);
        assertTrue(invoker.hasAction("testAction"));
    }

    @Test
    public void testRegisterActionInsufficientMemory() {
        invoker.registerAction("testAction", params -> "result", 150);
        assertFalse(invoker.hasAction("testAction"));
    }

    @Test
    public void testInvokeRegisteredAction() throws Exception {
        Function<Object, Object> action = params -> "result";
        invoker.registerAction("testAction", action, 50);
        Object result = invoker.invokeAction("testAction", null);
        assertEquals("result", result);
    }

    @Test(expected = NoSuchElementException.class)
    public void testInvokeUnregisteredAction() throws Exception {
        invoker.invokeAction("nonExistentAction", null);
    }

    // Pruebas adicionales según sea necesario...
}

