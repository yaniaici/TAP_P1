package main.faas.invoker.impl;

import main.faas.controller.Controller;
import main.faas.invoker.Invoker;
import main.faas.observer.Metrics;
import main.faas.observer.Observer;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Implementación de la interfaz Invoker que gestiona la invocación de acciones y la asignación de memoria.
 */
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

    /**
     * Crea un nuevo InvokerImpl con memoria y controlador especificados.
     *
     * @param totalMemoryMB La memoria total disponible para este invocador.
     * @param controller El controlador asociado a este invocador.
     * @param invokerId El identificador único para este invocador.
     */
    public InvokerImpl(int totalMemoryMB, Controller controller, String invokerId) {
        this.totalMemoryMB = totalMemoryMB;
        this.usedMemoryMB = 0;
        this.actions = new HashMap<>();
        this.controller = controller;
        this.invokerId = invokerId;
    }

    /**
     * Registra una nueva acción, si hay memoria suficiente disponible.
     *
     * @param actionName Nombre de la acción a registrar.
     * @param action La función que representa la acción.
     * @param memoryMB La cantidad de memoria requerida para la acción.
     */
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

    /**
     * Invoca una acción especificada, si está registrada.
     *
     * @param actionName El nombre de la acción a invocar.
     * @param params Los parámetros para la acción.
     * @return El resultado de la acción invocada.
     * @throws Exception Si la acción no está registrada o si ocurre un error en la invocación.
     */
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

    /**
     * Obtiene la cantidad de memoria libre disponible en el invocador.
     *
     * @return La cantidad de memoria libre en MB.
     */
    @Override
    public int getFreeMemoryMB() {
        return totalMemoryMB - usedMemoryMB;
    }

    /**
     * Obtiene la cantidad de memoria usada en el invocador.
     *
     * @return La cantidad de memoria usada en MB.
     */
    @Override
    public int getUsedMemoryMB(){return usedMemoryMB;}

    /**
     * Verifica si una acción específica está registrada en el invocador.
     *
     * @param actionName El nombre de la acción a verificar.
     * @return Verdadero si la acción está registrada, falso en caso contrario.
     */
    @Override
    public boolean hasAction(String actionName) {
        return actions.containsKey(actionName);
    }

    /**
     * Proporciona una representación en cadena del estado actual del invocador.
     *
     * @return Una cadena que describe el estado actual del invocador.
     */
    @Override
    public String toString() {
        return "InvokerImpl{" +
                "totalMemoryMB=" + totalMemoryMB +
                ", usedMemoryMB=" + usedMemoryMB +
                ", actions=" + actions +
                '}';
    }

    /**
     * Actualiza las métricas en el controlador asociado basándose en la acción invocada.
     *
     * @param metrics Las métricas a actualizar en el controlador.
     */
    @Override
    public void updateMetrics(Metrics metrics) {
        controller.receiveMetrics(metrics);
    }
}
