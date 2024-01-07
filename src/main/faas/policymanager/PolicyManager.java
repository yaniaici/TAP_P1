package main.faas.policymanager;

import main.faas.invoker.Invoker;
import main.faas.policymanager.resourcemanagers.ResourceManagementStrategy;

import java.util.List;

/**
 * PolicyManager es responsable de manejar la estrategia de asignación de recursos a los invocadores.
 * Utiliza una estrategia definida de gestión de recursos para asignar funciones a los invocadores disponibles.
 */
public class PolicyManager {
    private ResourceManagementStrategy currentStrategy;


    /**
     * Constructor que inicializa el PolicyManager con una estrategia de gestión de recursos específica.
     *
     * @param strategy La estrategia inicial de gestión de recursos a utilizar.
     */
    public PolicyManager(ResourceManagementStrategy strategy) {
        this.currentStrategy = strategy;
    }

    /**
     * Establece la estrategia de gestión de recursos que será utilizada por este PolicyManager.
     *
     * @param strategy La nueva estrategia de gestión de recursos a establecer.
     */
    public void setResourceManagementStrategy(ResourceManagementStrategy strategy) {
        this.currentStrategy = strategy;
    }

    /**
     * Asigna funciones a los invocadores disponibles utilizando la estrategia actual de gestión de recursos.
     *
     * @param functions Lista de nombres de funciones a asignar.
     * @param availableInvokers Lista de invocadores disponibles.
     * @return Lista de invocadores asignados a cada función.
     */
    public List<Invoker> assignFunctions(List<String> functions, List<Invoker> availableInvokers) {
        return currentStrategy.assignFunctions(functions, availableInvokers);
    }
}
