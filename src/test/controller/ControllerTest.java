package test.controller;

import main.faas.controller.Controller;
import main.faas.invoker.Invoker;
import main.faas.invoker.impl.InvokerImpl;
import main.faas.policymanager.PolicyManager;
import main.faas.policymanager.resourcemanagers.impl.RoundRobinResourceManagement;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ControllerTest {

    private Controller controller;
    private Invoker invoker;
    private PolicyManager policyManager;
    private Function<Object, Object> testAction;

    @Before
    public void setUp() {
        // Crear Controller y asignarle el Invoker
         controller = new Controller();
         invoker = new InvokerImpl(1024, controller, "1");
         policyManager = new PolicyManager(new RoundRobinResourceManagement());
        controller.setPolicyManager(policyManager);
        controller.setInvokers(Collections.singletonList(invoker));

        // Registrar una acción de prueba en el Controller
        testAction = x -> {
            if (x instanceof Map<?, ?> map) {
                Object xValue = map.get("x");
                Object yValue = map.get("y");
                if (xValue instanceof Integer && yValue instanceof Integer) {
                    return (int) xValue - (int) yValue;
                }
            }
            return null;
        };
        controller.registerAction("testAction", testAction, 256);
    }

    @Test
    public void testInvokeWithSingleParam() {
        // Suponiendo que tu Invoker y PolicyManager dummies están configurados para manejar esta solicitud
        Object param = "testParam";
        String actionName = "testAction";



        try {
            List<Object> results = controller.invoke(actionName, param);
            assertNotNull(results);
            // Añadir más aserciones según la lógica esperada
        } catch (Exception e) {
            fail("La invocación lanzó una excepción inesperada: " + e.getMessage());
        }
    }

    @Test
    public void testInvokeWithNullPolicyManager() {
        controller.setPolicyManager(null);

        try {
            controller.invoke("testAction", "testParam");
            fail("Debería haber lanzado una IllegalStateException");
        } catch (IllegalStateException e) {
            // Prueba pasada
        } catch (Exception e) {
            fail("Tipo de excepción incorrecto lanzado: " + e.getClass().getSimpleName());
        }
    }


}

