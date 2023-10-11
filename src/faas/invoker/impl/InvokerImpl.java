package faas.invoker.impl;

import faas.invoker.Invoker;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class InvokerImpl implements Invoker {

    private final int totalMemoryMB;
    private int usedMemoryMB;
    private final Map<String, Function<Object, Object>> actions;

    public InvokerImpl(int totalMemoryMB) {
        this.totalMemoryMB = totalMemoryMB;
        this.usedMemoryMB = 0;
        this.actions = new HashMap<>();
    }

    @Override
    public void registerAction(String actionName, Function<Object, Object> action, int memoryMB) {
        if (usedMemoryMB + memoryMB <= totalMemoryMB) {
            actions.put(actionName, action);
            usedMemoryMB += memoryMB;
        } else {
            System.out.println("No hay suficiente memoria para registrar la acción '" + actionName + "'.");
        }
    }

    public Object invokeAction(String actionName, Object params) throws Exception {
        Function<Object, Object> action = actions.get(actionName);

        if (hasAction(actionName)) {
            try {
                return action.apply(params);
            } catch (Exception e) {
                throw new Exception("Error al ejecutar la acción '" + actionName + "': " + e.getMessage(), e);
            }
        }

        throw new NoSuchElementException("Acción no disponible");
    }

    @Override
    public int getFreeMemoryMB() {
        return totalMemoryMB - usedMemoryMB;
    }

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
}
