package faas.controller.impl;

import faas.controller.Controller;
import faas.invoker.Invoker;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ControllerImpl<T,R> implements Controller<T,R>{

    private List<Invoker<T,R>> invokers;

    public void setInvokers(List<Invoker<T, R>> invokers) {
        this.invokers = invokers;
    }

    @Override
    public R invoke(String actionName, T params) throws Exception {
        Invoker<T,R> selectedInvoker = findInvoker(actionName);
        return selectedInvoker.invokeAction(actionName, params);
    }


    @Override
    public void registerAction(String actionName, Function<T, R> action, int memoryMB) {

        Invoker<T,R> targetInvoker = findAvailableInvoker(memoryMB);

        if(targetInvoker == null) {
            System.out.println("No hay Invokers disponibles con suficiente memoria para registrar la acción '" + actionName + "'.");
            return;
        }

        // Registra la acción en el Invoker seleccionado.
        targetInvoker.registerAction(actionName, action, memoryMB);
    }

    private Invoker<T,R> findAvailableInvoker(int requiredMemoryMB) {
        // Itera a través de los Invokers y selecciona el primero que tenga suficiente memoria disponible.
        for (Invoker<T,R> invoker : invokers) {
            if (invoker.getFreeMemoryMB() >= requiredMemoryMB) {
                return invoker;
            }
        }

        // Si no se encuentra ningún Invoker disponible, devuelve null.
        return null;
    }

    private Invoker<T, R> findInvoker(String actionName) {

        for (Invoker<T, R> invoker : invokers) {
            if (invoker.hasAction(actionName)) {
                return invoker;
            }
        }



        throw new NoSuchElementException("Invoker no encontrado");
    }


}
