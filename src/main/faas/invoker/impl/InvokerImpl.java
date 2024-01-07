package main.faas.invoker.impl;

import main.faas.controller.Controller;
import main.faas.invoker.Invoker;
import main.faas.observer.Metrics;
import main.faas.observer.Observer;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class InvokerImpl implements Invoker, Observer {

    private final String invokerId;
    private final int totalMemoryMB;
    private int usedMemoryMB;
    private final Map<String, Function<Object, Object>> actions;

    public String getInvokerId() {
        return invokerId;
    }

    public Controller getController() {
        return controller;
    }

    private final Controller controller;

    public InvokerImpl(int totalMemoryMB, Controller controller, String invokerId) {
        this.totalMemoryMB = totalMemoryMB;
        this.usedMemoryMB = 0;
        this.actions = new HashMap<>();
        this.controller = controller;
        this.invokerId = invokerId;
    }

    @Override
    public void registerAction(String actionName, Function<Object, Object> action, int memoryMB) {
        if (usedMemoryMB + memoryMB <= totalMemoryMB) {
            actions.put(actionName, action);
            usedMemoryMB += memoryMB;
            System.out.println(usedMemoryMB);
        } else {
            System.out.println("No hay suficiente memoria para registrar la acción '" + actionName + "'.");
        }
    }

    public Object invokeAction(String actionName, Object params) throws Exception {
        if(!hasAction(actionName)) {
            throw new NoSuchElementException("Acción no disponible: " + actionName);
        }

        long startTime = System.currentTimeMillis();
        Function<Object, Object> action = actions.get(actionName);
        Object result;

            try {
                result = action.apply(params);
            } catch (Exception e) {
                throw new Exception("Error al ejecutar la acción '" + actionName + "': " + e.getMessage(), e);
            } finally {
                long endTime = System.currentTimeMillis();
                Metrics metrics = new Metrics(invokerId, endTime - startTime, usedMemoryMB);
                updateMetrics(metrics);
            }

            return result;

    }

    @Override
    public int getFreeMemoryMB() {
        return totalMemoryMB - usedMemoryMB;
    }

    @Override
    public int getUsedMemoryMB(){return usedMemoryMB;}

    @Override
    public boolean hasAction(String actionName) {
        return actions.containsKey(actionName);
    }

    @Override
    public String toString() {
        return "InvokerImpl{" +
                "totalMemoryMB=" + totalMemoryMB +
                ", usedMemoryMB=" + usedMemoryMB +
                ", actions=" + actions +
                '}';
    }

    @Override
    public void updateMetrics(Metrics metrics) {
        controller.receiveMetrics(metrics);
    }
}
