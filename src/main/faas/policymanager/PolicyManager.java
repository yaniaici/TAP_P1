package main.faas.policymanager;

import main.faas.invoker.Invoker;
import main.faas.policymanager.resourcemanagers.ResourceManagementStrategy;

import java.util.List;

public class PolicyManager {
    private ResourceManagementStrategy currentStrategy;

    public PolicyManager(ResourceManagementStrategy strategy) {
        this.currentStrategy = strategy;
    }

    public void setResourceManagementStrategy(ResourceManagementStrategy strategy) {
        this.currentStrategy = strategy;
    }

    public List<Invoker> assignFunctions(List<String> functions, List<Invoker> availableInvokers) {
        return currentStrategy.assignFunctions(functions, availableInvokers);
    }
}
