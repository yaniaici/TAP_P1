package main;

import main.faas.controller.Controller;
import main.faas.invoker.Invoker;
import main.faas.invoker.impl.InvokerImpl;
import main.faas.policymanager.PolicyManager;
import main.faas.policymanager.resourcemanagers.impl.RoundRobinResourceManagement;

import java.util.*;
import java.util.function.Function;

public class FaasApplication {


    public static void main(String[] args) {
        // Crear Invoker de prueba con 1024 MB de memoria

        // Crear Controller y asignarle el Invoker
        Controller controller = new Controller();
        Invoker invoker1 = new InvokerImpl(256, controller, "8");
        Invoker invoker2 = new InvokerImpl(256, controller, "9");
        List<Invoker> invokerList = new ArrayList<>();
        invokerList.add(invoker1);
        invokerList.add(invoker2);
        PolicyManager policyManager = new PolicyManager(new RoundRobinResourceManagement());
        controller.setPolicyManager(policyManager);
        controller.setInvokers(invokerList);

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
                Map.of("x", 6, "y", 3),
                Map.of("x", 9, "y", 7),
                Map.of("x", 8, "y", 0)
        );

        try {
            List<Object> results = controller.invoke("testAction", input);
            System.out.println("Resultados: " + results);

            for (Object result : results) {
                if (result instanceof Integer) {
                    System.out.println("Resultado: " + (int) result);
                }
            }
        } catch (Exception e) {
            System.out.println("Error durante la invocación grupal: " + e.getMessage());
        }

        /*
        // Prueba individual decorators
        Function<Object, Object> testFactorial = x -> {
            if (x instanceof Map<?, ?> map) {
                Object xValue = map.get("x");
                if (xValue instanceof Integer) {
                    BigInteger result = new BigInteger("1");

                    for (int i = 1; i <= (int)xValue; i++) {
                        result = result.multiply(BigInteger.valueOf(i));
                    }

                    return result;
                }
            }
            return null;
        };
        InvokerImpl invk = new InvokerImpl(512, controller, "2");
        invk.registerAction("testFactorial", testFactorial, 256);
        InvokerImpl decorator = new TimerDecorator(invk);
        try {
            System.out.println(decorator.invokeAction("testFactorial", Map.of("x", 5000)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        InvokerImpl decorator2 = new MemoizationDecorator(decorator);
        try {
            System.out.println(decorator2.invokeAction("testFactorial", Map.of("x", 6000)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            System.out.println(decorator2.invokeAction("testFactorial", Map.of("x", 6000)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        DynamicProxy dController = (DynamicProxy) ActionProxy.newInstance(controller);
        dController.registerAction("testFactorial", testFactorial, 256);
        try {
            System.out.println(dController.invoke("testFactorial", Map.of("x", 100)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        */
        // Prueba individual

    }

}

