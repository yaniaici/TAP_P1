package main.faas.policymanager.resourcemanagers.impl;

import main.faas.invoker.Invoker;
import main.faas.policymanager.resourcemanagers.ResourceManagementStrategy;

import java.util.ArrayList;
import java.util.List;

public class GreedyGroupResourceManagement implements ResourceManagementStrategy {

    @Override
    public List<Invoker> assignFunctions(List<String> actions, List<Invoker> availableInvokers) {
        List<Invoker> assignedInvokers = new ArrayList<>();
        List<Invoker> sortedInvokers = sortInvokersByFreeMemory(availableInvokers);

        for (String action : actions) {
            Invoker bestInvoker = selectBestInvoker(sortedInvokers);

            if (bestInvoker != null) {
                assignedInvokers.add(bestInvoker);
            }
        }

        return assignedInvokers;
    }

    private List<Invoker> sortInvokersByFreeMemory(List<Invoker> invokers) {
        List<Invoker> sortedInvokers = new ArrayList<>(invokers);
        sortedInvokers.sort((invoker1, invoker2) -> Integer.compare(invoker2.getFreeMemoryMB(), invoker1.getFreeMemoryMB()));
        return sortedInvokers;
    }

    private Invoker selectBestInvoker(List<Invoker> invokers) {
        Invoker bestInvoker = null;
        int bestRemainingMemory = 0;

        for (Invoker invoker : invokers) {
            int remainingMemory = invoker.getFreeMemoryMB();
            if (remainingMemory >= bestRemainingMemory) {
                bestInvoker = invoker;
                bestRemainingMemory = remainingMemory;
            }
        }

        return bestInvoker;
    }

}
