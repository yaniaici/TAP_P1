package main.faas.policymanager.resourcemanagers.impl;

import main.faas.invoker.Invoker;
import main.faas.policymanager.resourcemanagers.ResourceManagementStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la estrategia de gestión de recursos que asigna funciones a los invocadores
 * utilizando un enfoque de grupos uniformes. Esta estrategia distribuye las funciones en grupos
 * de tamaño fijo entre todos los invocadores disponibles.
 */
public class UniformGroupResourceManager implements ResourceManagementStrategy {

    private final int groupSize;

    /**
     * Constructor que inicializa la estrategia de gestión de recursos con un tamaño de grupo específico.
     *
     * @param groupSize El tamaño máximo de cada grupo de acciones asignado a un invocador.
     */
    public UniformGroupResourceManager (int groupSize) {
        this.groupSize = groupSize;
    }

    /**
     * Asigna funciones a los invocadores disponibles agrupándolas en grupos de tamaño uniforme.
     * Cada grupo de funciones es asignado a un invocador hasta alcanzar el tamaño del grupo.
     *
     * @param actions Lista de nombres de acciones a asignar.
     * @param availableInvokers Lista de invocadores disponibles.
     * @return Lista de invocadores asignados a cada acción.
     */
    @Override
    public List<Invoker> assignFunctions(List<String> actions, List<Invoker> availableInvokers) {
        List<Invoker> assignedInvokers = new ArrayList<>();

        int totalFunctions = actions.size();
        int index = 0;

        while (index < totalFunctions) {
            for (Invoker invoker : availableInvokers) {
                for (int i = 0; i < groupSize; i++) {
                    if (index < totalFunctions) {
                        assignedInvokers.add(invoker);
                        index++;
                    } else {
                        break;
                    }
                }
            }
        }

        return assignedInvokers;
    }
}
