package faas.controller.impl;

import faas.controller.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ControllerImpl<T,R> implements Controller<T,R>{
    private Map<String, Function<T, R>> actions = new HashMap<>();

    @Override
    public void registerAction(String actionName, Function<T, R> action, int memoryMB) {

        actions.put(actionName, action);

        // TODO: Tomar en cuenta la memoria RAM necesaria para la acción y gestionar los recursos.

    }


    @Override
    public R invokeAction(String actionName, T params) throws Exception {
        Function<T, R> action = actions.get(actionName);
        if (action == null) {
            throw new NoSuchElementException("La acción '" + actionName + "' no está registrada.");
        }
        try {
            return action.apply(params);
        } catch (Exception e) {
            throw new Exception("Error al ejecutar la acción '" + actionName + "': " + e.getMessage(), e);
        }
    }


}
