package main.faas.policymanager.resourcemanagers;

import main.faas.invoker.Invoker;

import java.util.List;

public interface ResourceManagementStrategy {
    List<Invoker> assignFunctions(List<String> actions, List<Invoker> availableInvokers);
}
