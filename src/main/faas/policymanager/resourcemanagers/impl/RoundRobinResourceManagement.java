package main.faas.policymanager.resourcemanagers.impl;

import main.faas.invoker.Invoker;
import main.faas.policymanager.resourcemanagers.ResourceManagementStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la estrategia de gestión de recursos que asigna funciones a los invocadores
 * utilizando un enfoque de Round Robin. Esta estrategia distribuye las funciones de manera equitativa
 * entre los invocadores disponibles, rotando secuencialmente a través de ellos.
 */
public class RoundRobinResourceManagement implements ResourceManagementStrategy {
    private int currentIndex = 0;

    /**
     * Asigna funciones a los invocadores disponibles utilizando un enfoque de Round Robin.
     * Esto asegura una distribución equitativa de las funciones entre todos los invocadores.
     *
     * @param actions Lista de nombres de acciones a asignar.
     * @param availableInvokers Lista de invocadores disponibles.
     * @return Lista de invocadores asignados a cada acción.
     */
    @Override
    public List<Invoker> assignFunctions(List<String> actions, List<Invoker> availableInvokers) {
        List<Invoker> assignedInvokers = new ArrayList<>();

        for (String ignored : actions) {
            if (currentIndex >= availableInvokers.size()) {
                currentIndex = 0; // Ciclo de nuevo al primer Invoker si es necesario
            }

            Invoker invoker = availableInvokers.get(currentIndex);
            assignedInvokers.add(invoker);
            currentIndex++;
        }


        return assignedInvokers;
    }
}

