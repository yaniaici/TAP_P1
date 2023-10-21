import faas.controller.Controller;
import faas.invoker.Invoker;
import faas.invoker.impl.InvokerImpl;
import faas.future.impl.ResultFutureImpl;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;

public class FaasApplication {


    public static void main(String[] args) throws Exception {
        // Crear Invoker de prueba con 1024 MB de memoria
        Invoker invoker = new InvokerImpl(1024);

        // Crear Controller y asignarle el Invoker
        Controller controller = new Controller();
        controller.setInvokers(Collections.singletonList(invoker));

        // Registrar una acción de prueba en el Controller
        Function<Object, Object> testAction = x -> {
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

        // Prueba grupal
        List<Map<String, Integer>> input = Arrays.asList(
                Map.of("x", 2, "y", 3),
                Map.of("x", 9, "y", 1),
                Map.of("x", 8, "y", 8)
        );

        try {
            List<Object> results = controller.invoke("testAction", input);
            for (Object result : results) {
                if (result instanceof Integer) {
                    System.out.println("Resultado: " + (int) result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Function<Object, Object> sleep = s -> {
            try {
                Thread.sleep(Duration.ofSeconds(5));
                return "Done!";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        controller.registerAction("sleepAction", sleep, 256);
        ResultFutureImpl<Object> fut1 = controller.invoke_async("sleepAction", 5);
        ResultFutureImpl<Object> fut2 = controller.invoke_async("sleepAction", 5);
        ResultFutureImpl<Object> fut3 = controller.invoke_async("sleepAction", 5);
        fut1.get();
        fut2.get();
        fut3.get();
        System.out.println("Fut 1: " + fut1.get());
        System.out.println("Fut 2: " + fut2.get());
        System.out.println("Fut 3: " + fut3.get());
    }
    }

