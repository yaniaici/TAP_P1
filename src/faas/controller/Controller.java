package faas.controller;

import faas.invoker.Invoker;
import faas.policymanager.PolicyManager;

import java.util.ArrayList;
import java.util.Collections;
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
}
