package main.faas.policymanager.resourcemanagers.impl;

import main.faas.invoker.Invoker;
import main.faas.policymanager.resourcemanagers.ResourceManagementStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la estrategia de gestión de recursos que asigna funciones a los invocadores
 * basándose en un enfoque voraz (greedy). Esta estrategia selecciona siempre el invocador
 * con la mayor cantidad de memoria libre disponible para cada acción.
 */
public class GreedyGroupResourceManagement implements ResourceManagementStrategy {

    /**
     * Asigna funciones a los invocadores disponibles basándose en la cantidad de memoria libre.
     * Selecciona el invocador con más memoria libre para cada función.
     *
     * @param actions Lista de nombres de acciones a asignar.
     * @param availableInvokers Lista de invocadores disponibles.
     * @return Lista de invocadores asignados a cada acción.
     */
    @Override
    public List<Invoker> assignFunctions(List<String> actions, List<Invoker> availableInvokers) {
        List<Invoker> assignedInvokers = new ArrayList<>();
        List<Invoker> sortedInvokers = sortInvokersByFreeMemory(availableInvokers);

        for (String action : actions) {
            Invoker bestInvoker = selectBestInvoker(sortedInvokers);

            if (bestInvoker != null) {
                assignedInvokers.add(bestInvoker);
            }
        }

        return assignedInvokers;
    }

    /**
     * Ordena los invocadores disponibles por la cantidad de memoria libre en orden descendente.
     *
     * @param invokers Lista de invocadores a ordenar.
     * @return Lista de invocadores ordenada.
     */
    private List<Invoker> sortInvokersByFreeMemory(List<Invoker> invokers) {
        List<Invoker> sortedInvokers = new ArrayList<>(invokers);
        sortedInvokers.sort((invoker1, invoker2) -> Integer.compare(invoker2.getFreeMemoryMB(), invoker1.getFreeMemoryMB()));
        return sortedInvokers;
    }

    /**
     * Selecciona el mejor invocador para una acción, basándose en la cantidad de memoria libre.
     *
     * @param invokers Lista de invocadores entre los que elegir.
     * @return El invocador seleccionado con la mayor cantidad de memoria libre.
     */
    private Invoker selectBestInvoker(List<Invoker> invokers) {
        Invoker bestInvoker = null;
        int bestRemainingMemory = 0;

        for (Invoker invoker : invokers) {
            int remainingMemory = invoker.getFreeMemoryMB();
            if (remainingMemory >= bestRemainingMemory) {
                bestInvoker = invoker;
                bestRemainingMemory = remainingMemory;
            }
        }

        return bestInvoker;
    }

}
