package main.faas.policymanager.resourcemanagers.impl;

import main.faas.invoker.Invoker;
import main.faas.policymanager.resourcemanagers.ResourceManagementStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la estrategia de gestión de recursos que asigna acciones a los invocadores
 * en grandes grupos. Cada grupo de acciones es asignado a un único invocador hasta que se alcanza
 * el tamaño del grupo especificado.
 */
public class BigGroupResourceManagement implements ResourceManagementStrategy {
    private final int groupSize;
    /**
     * Constructor que inicializa la estrategia de gestión de recursos con un tamaño de grupo específico.
     *
     * @param groupSize El tamaño máximo de cada grupo de acciones asignado a un invocador.
     */
    public BigGroupResourceManagement(int groupSize) {
        this.groupSize = groupSize;
    }

    /**
     * Asigna funciones a los invocadores disponibles agrupándolas en grandes grupos.
     * Cada grupo de funciones es asignado a un invocador hasta alcanzar el tamaño del grupo.
     *
     * @param actions Lista de nombres de acciones a asignar.
     * @param availableInvokers Lista de invocadores disponibles.
     * @return Lista de invocadores con las funciones asignadas.
     */
    @Override
    public List<Invoker> assignFunctions(List<String> actions, List<Invoker> availableInvokers) {
        List<Invoker> assignedInvokers = new ArrayList<>();
        int totalFunctions = actions.size();
        int totalGroups = (totalFunctions + groupSize - 1) / groupSize; // Redondea hacia arriba para incluir grupos parciales

        int currentGroup = 0;
        int currentFunctionIndex = 0;
        int currentInvokerIndex = 0;

        while (currentGroup < totalGroups) {
            Invoker invoker = availableInvokers.get(currentInvokerIndex % availableInvokers.size());
            int functionsToAssign = Math.min(groupSize, totalFunctions - currentFunctionIndex);

            for (int j = 0; j < functionsToAssign; j++) {
                assignedInvokers.add(invoker);
                currentFunctionIndex++;
            }

            currentGroup++;
            currentInvokerIndex++;
        }

        return assignedInvokers;
    }
}
