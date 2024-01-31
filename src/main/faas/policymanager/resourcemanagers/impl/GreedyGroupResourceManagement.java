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
    public List<Invoker> assignInvokers(List<String> actions, List<Invoker> availableInvokers, List<Integer> memory) {
        List<Invoker> assignedInvokers = new ArrayList<>();

        for (Invoker invoker : availableInvokers) {
            for (String action : actions) {
                if (memory.get(actions.indexOf(action)) <= invoker.getFreeMemoryMB()) {
                    assignedInvokers.add(invoker);
                }
            }
        }

        return assignedInvokers;
    }



}
