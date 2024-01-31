package main.faas.policymanager.resourcemanagers;

import main.faas.invoker.Invoker;

import java.util.List;
import java.util.function.Function;

public interface ResourceManagementStrategy {
    List<Invoker> assignInvokers(List<String> actionNames, List<Invoker> availableInvokers, List<Integer> memory);
}
