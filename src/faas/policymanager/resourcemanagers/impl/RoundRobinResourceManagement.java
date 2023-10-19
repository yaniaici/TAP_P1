package faas.policymanager.resourcemanagers.impl;

import faas.invoker.Invoker;
import faas.policymanager.resourcemanagers.ResourceManagementStrategy;

import java.util.ArrayList;
import java.util.List;

public class RoundRobinResourceManagement implements ResourceManagementStrategy {
    private int currentIndex = 0;

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

