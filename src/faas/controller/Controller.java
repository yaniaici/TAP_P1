package faas.controller;

import faas.future.impl.ResultFutureImpl;
import faas.invoker.Invoker;
import faas.observer.Metrics;
import faas.policymanager.PolicyManager;
import faas.reflection.DynamicProxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Controller implements DynamicProxy {

    private List<Invoker> invokers;
    private PolicyManager policyManager;

    private ExecutorService executor = Executors.newFixedThreadPool(8);
    private List<Metrics> metricsList = new ArrayList<>();





    public void setInvokers(List<Invoker> invokers) {
        this.invokers = invokers;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    public List<Object> invoke(String actionName, Object params) throws Exception {
        checkPolicyManagerConfigured();

        List<String> actionNames = convertActionNameToList(actionName);

        List<Invoker> assignedInvokers = policyManager.assignFunctions(actionNames, invokers);
        Invoker selectedInvoker = assignedInvokers.get(0);

        if(params instanceof List) {
            // Invocación grupal
            List<Object> paramList = (List<Object>) params;
            List<Object> results = new ArrayList<>();
            for(Object param : paramList) {
                results.add(selectedInvoker.invokeAction(actionName, param));
            }

            return results;
        }else {
            // Individual
            List<Object> result = new ArrayList<>(Collections.emptyList());
            result.add(selectedInvoker.invokeAction(actionName, params));
            return result;
        }
    }

    public ResultFutureImpl<Object> invoke_async(String actionName, Object parameters) {
        // Encuentra el Invoker para la acción dada
        checkPolicyManagerConfigured();

        List<String> actionNames = convertActionNameToList(actionName);

        List<Invoker> assignedInvokers = policyManager.assignFunctions(actionNames, invokers);
        Invoker selectedInvoker = assignedInvokers.get(0);

        // Crea un objeto ResultFutureImpl para representar el resultado futuro
        ResultFutureImpl<Object> futureResult = new ResultFutureImpl<>();

        // Usa el ExecutorService para ejecutar la acción de manera asíncrona
        executor.submit(() -> {
            try {
                Object result;
                if (parameters instanceof List) {
                    List<Object> paramList = (List<Object>) parameters;
                    List<Object> results = new ArrayList<>();
                    for (Object param : paramList) {
                        results.add(selectedInvoker.invokeAction(actionName, param));
                    }
                    result = results;
                } else {
                    result = selectedInvoker.invokeAction(actionName, parameters);
                }

                // Establece el resultado en el future
                futureResult.set(result);
            } catch (Exception e) {
                // Handle exceptions, you can set an exception in your future or handle it differently
                System.out.println("Error al ejecutar la acción '" + actionName + "': " + e.getMessage());
            }
        });

        return futureResult;
    }

    public synchronized void receiveMetrics(Metrics metrics) {
        metricsList.add(metrics);
    }



    private void checkPolicyManagerConfigured() {
        if (policyManager == null) {
            throw new IllegalStateException("El PolicyManager no ha sido configurado.");
        }
    }

    private List<String> convertActionNameToList(String actionName) {
        return Collections.singletonList(actionName);
    }






    public void registerAction(String actionName, Function<Object, Object> action, int memoryMB) {
        Invoker targetInvoker = findAvailableInvoker(memoryMB);

        if (targetInvoker == null) {
            System.out.println("No hay Invokers disponibles con suficiente memoria para registrar la acción '" + actionName + "'.");
            return;
        }

        // Registra la acción en el Invoker seleccionado.
        targetInvoker.registerAction(actionName, action, memoryMB);
    }

    private Invoker findAvailableInvoker(int requiredMemoryMB) {
        // Itera a través de los Invokers y selecciona el primero que tenga suficiente memoria disponible.
        for (Invoker invoker : invokers) {
            if (invoker.getFreeMemoryMB() >= requiredMemoryMB) {
                return invoker;
            }
        }

        // Si no se encuentra ningún Invoker disponible, devuelve null.
        return null;
    }

    public void displayExecutionTimeStats() {
        metricsList.stream()
                .collect(Collectors.groupingBy(Metrics::getInvokerId))
                .forEach((action, metrics) -> {
                    LongSummaryStatistics stats = metrics.stream()
                            .mapToLong(Metrics::getExecutionTime)
                            .summaryStatistics();

                    System.out.println("Action: " + action +
                            ", Max Time: " + stats.getMax() +
                            ", Min Time: " + stats.getMin() +
                            ", Avg Time: " + stats.getAverage());
                });
    }

    public void displayExecutionTimeByInvoker() {
        metricsList.stream()
                .collect(Collectors.groupingBy(Metrics::getInvokerId,
                        Collectors.groupingBy(Metrics::getExecutionTime)))
                .forEach((action, totalTime) ->
                        System.out.println("Action: " + action + ", Total Time: " + totalTime + "ms"));
    }
}
