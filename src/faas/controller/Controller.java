package faas.controller;

import faas.invoker.Invoker;
import faas.policymanager.PolicyManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class Controller {

    private List<Invoker> invokers;
    private PolicyManager policyManager;

    public void setInvokers(List<Invoker> invokers) {
        this.invokers = invokers;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    public List<Object> invoke(String actionName, Object params) throws Exception {
        checkPolicyManagerConfigured();

        List<String> actionNames = convertActionNameToList(actionName);
        List<Object> paramList = convertParamsToList(params);

        List<Invoker> assignedInvokers = policyManager.assignFunctions(actionNames, invokers);

        return executeAssignedActions(assignedInvokers, actionNames, paramList);
    }

    private void checkPolicyManagerConfigured() {
        if (policyManager == null) {
            throw new IllegalStateException("El PolicyManager no ha sido configurado.");
        }
    }

    private List<String> convertActionNameToList(String actionName) {
        return Collections.singletonList(actionName);
    }

    private List<Object> convertParamsToList(Object params) {
        if (params instanceof List) {
            return (List<Object>) params;
        } else {
            return Collections.singletonList(params);
        }
    }

    private List<Object> executeAssignedActions(List<Invoker> assignedInvokers, List<String> actionNames, List<Object> paramList) throws Exception {
        List<Object> results = new ArrayList<>();

        Iterator<String> actionNameIterator = actionNames.iterator();
        Iterator<Object> paramIterator = paramList.iterator();

        for (Invoker assignedInvoker : assignedInvokers) {
            if (!actionNameIterator.hasNext() || !paramIterator.hasNext()) {
                break; // Si no quedan acciones o parámetros, salimos del bucle.
            }

            String action = actionNameIterator.next();
            Object param = paramIterator.next();

            results.add(assignedInvoker.invokeAction(action, param));
        }

        return results;
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
}
