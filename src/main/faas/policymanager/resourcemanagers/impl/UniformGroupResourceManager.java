package main.faas.policymanager.resourcemanagers.impl;

import main.faas.invoker.Invoker;
import main.faas.policymanager.resourcemanagers.ResourceManagementStrategy;

import java.util.ArrayList;
import java.util.List;

public class UniformGroupResourceManager implements ResourceManagementStrategy {

    private final int groupSize;

    public UniformGroupResourceManager (int groupSize) {
        this.groupSize = groupSize;
    }

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
