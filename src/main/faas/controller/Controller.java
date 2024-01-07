package main.faas.controller;

import main.faas.future.impl.ResultFutureImpl;
import main.faas.invoker.Invoker;
import main.faas.observer.Metrics;
import main.faas.policymanager.PolicyManager;
import main.faas.reflection.DynamicProxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * La clase Controller actúa como un intermediario entre los clientes y los invocadores,
 * gestionando la invocación de acciones y la asignación de recursos.
 */
public class Controller implements DynamicProxy {


    /**
     * Lista de invocadores disponibles para ejecutar acciones.
     */
    private List<Invoker> invokers;

    /**
     * Gestor de políticas para asignar funciones a los invocadores.
     */
    private PolicyManager policyManager;

    /**
     * Servicio executor para ejecutar acciones de manera asíncrona.
     */
    private ExecutorService executor = Executors.newFixedThreadPool(8);

    /**
     * Lista de métricas recolectadas de las ejecuciones de acciones.
     */
    private List<Metrics> metricsList = new ArrayList<>();



    /**
     * Establece los invocadores disponibles para el controlador.
     *
     * @param invokers Lista de invocadores.
     */
    public void setInvokers(List<Invoker> invokers) {
        this.invokers = invokers;
    }

    /**
     * Establece el gestor de políticas para asignar funciones a los invocadores.
     *
     * @param policyManager El gestor de políticas a asignar.
     */
    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    /**
     * Invoca una acción de manera síncrona.
     *
     * @param actionName Nombre de la acción a invocar.
     * @param params Parámetros para la acción.
     * @return Lista de objetos resultantes de la invocación de la acción.
     * @throws Exception Si ocurre un error durante la invocación.
     */
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

    /**
     * Invoca una acción de manera asíncrona.
     *
     * @param actionName Nombre de la acción a invocar.
     * @param parameters Parámetros para la acción.
     * @return Un futuro que eventualmente contendrá el resultado de la acción.
     */
    public ResultFutureImpl<Object> invoke_async(String actionName, Object parameters) {
        // Encuentra el Invoker para la acción dada
        checkPolicyManagerConfigured();

        List<String> actionNames = convertActionNameToList(actionName);
        System.out.println(actionNames);


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
                System.out.println("Error al ejecutar la acción '" + actionName + "': " + e.getMessage());
            }
        });

        return futureResult;
    }

    /**
     * Recibe y almacena las métricas de una ejecución de acción.
     *
     * @param metrics Las métricas a almacenar.
     */
    public synchronized void receiveMetrics(Metrics metrics) {
        metricsList.add(metrics);
    }


    /**
     * Comprueba si el PolicyManager ha sido configurado.
     *
     * @throws IllegalStateException Si el PolicyManager no está configurado.
     */
    private void checkPolicyManagerConfigured() {
        if (policyManager == null) {
            throw new IllegalStateException("El PolicyManager no ha sido configurado.");
        }
    }

    /**
     * Convierte el nombre de una acción en una lista que contiene ese nombre.
     * Utilizado para estandarizar la manera de procesar nombres de acciones.
     *
     * @param actionName El nombre de la acción.
     * @return Una lista conteniendo el nombre de la acción.
     */
    private List<String> convertActionNameToList(String actionName) {
        return Collections.singletonList(actionName);
    }





    /**
     * Registra una nueva acción en uno de los invocadores disponibles.
     * La acción se registra solo si hay un invocador con suficiente memoria disponible.
     *
     * @param actionName Nombre de la acción a registrar.
     * @param action La función que representa la acción.
     * @param memoryMB La cantidad de memoria requerida para la acción.
     */
    public void registerAction(String actionName, Function<Object, Object> action, int memoryMB) {
        Invoker targetInvoker = findAvailableInvoker(memoryMB);

        if (targetInvoker == null) {
            System.out.println("No hay Invokers disponibles con suficiente memoria para registrar la acción '" + actionName + "'.");
            return;
        }

        // Registra la acción en el Invoker seleccionado.
        targetInvoker.registerAction(actionName, action, memoryMB);
    }

    /**
     * Busca un invocador disponible que tenga suficiente memoria libre.
     *
     * @param requiredMemoryMB La cantidad de memoria requerida.
     * @return El invocador disponible que cumple con el requisito de memoria, o null si no se encuentra ninguno.
     */
    private Invoker findAvailableInvoker(int requiredMemoryMB) {
        // Itera a través de los Invokers y selecciona el primero que tenga suficiente memoria disponible.
        System.out.println(invokers);
        for (Invoker invoker : invokers) {
            if (invoker.getFreeMemoryMB() >= requiredMemoryMB) {
                return invoker;
            }
        }

        // Si no se encuentra ningún Invoker disponible, devuelve null.
        return null;
    }

    /**
     * Muestra las estadísticas de tiempo de ejecución de todas las acciones ejecutadas por cada invocador.
     * Incluye el tiempo máximo, mínimo y promedio de ejecución.
     */
    public void displayExecutionTimeStats() {
        metricsList.stream()
                .collect(Collectors.groupingBy(Metrics::getInvokerId))
                .forEach((action, metrics) -> {
                    LongSummaryStatistics stats = metrics.stream()
                            .mapToLong(Metrics::getExecutionTime)
                            .summaryStatistics();

                    System.out.println("Invoker: " + action +
                            ", Max Time: " + stats.getMax() +
                            ", Min Time: " + stats.getMin() +
                            ", Avg Time: " + stats.getAverage());
                });
    }

    /**
     * Muestra el tiempo total de ejecución por cada invocador.
     */
    public void displayExecutionTimeByInvoker() {
        metricsList.stream()
                .collect(Collectors.groupingBy(Metrics::getInvokerId,
                        Collectors.summingLong(Metrics::getExecutionTime)))
                .forEach((invokerId, totalTime) ->
                        System.out.println("Invoker: " + invokerId + ", Total Execution Time: " + totalTime + "ms"));
    }

    /**
     * Proporciona una representación en cadena del estado actual del controlador.
     *
     * @return Una cadena que describe el estado actual del controlador.
     */
    @Override
    public String toString() {
        return "Controller{" +
                "invokers=" + invokers +
                ", policyManager=" + policyManager +
                ", executor=" + executor +
                ", metricsList=" + metricsList +
                '}';
    }
}
