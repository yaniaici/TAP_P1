package main.faas.policymanager.resourcemanagers.impl;

import main.faas.invoker.Invoker;
import main.faas.policymanager.resourcemanagers.ResourceManagementStrategy;

import java.util.ArrayList;
import java.util.List;

public class BigGroupResourceManagement implements ResourceManagementStrategy {
    private final int groupSize;

    public BigGroupResourceManagement(int groupSize) {
        this.groupSize = groupSize;
    }

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
