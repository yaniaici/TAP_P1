package faas.controller;

import java.util.Map;
import java.util.function.Function;

public interface Controller {
    // Registra una nueva acción con un identificador y la cantidad de memoria
    void registerAction(String actionName, Function<?,?> action, int memoryMB);

    // Lista todas las acciones registradas en el sistema
    Map<String, Function<?, Integer>> listActions();

    // Invoca una acción registrada proporcionando un id y parámetros de entrada (luego haremos excepciones personalizadas)
    int invokeAction(String actionName, Map<String, ?> params) throws Exception;

    // Elimina una acción registrada por su identificador
    void deleteAction(String actionName);

}
