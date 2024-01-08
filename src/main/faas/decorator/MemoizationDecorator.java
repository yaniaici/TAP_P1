package main.faas.decorator;

import main.faas.invoker.impl.InvokerImpl;
import java.util.HashMap;

/**
 * El decorador MemoizationDecorator extiende la funcionalidad de un InvokerImpl,
 * añadiendo una capa de memoización para mejorar la eficiencia de la invocación de acciones.
 * Almacena los resultados de las invocaciones de acciones para evitar cálculos repetidos.
 */
public class MemoizationDecorator extends InvokerImpl {
    private InvokerImpl client;
    private HashMap<Integer, Object> cache = new HashMap<>();

    /**
     * Construye un MemoizationDecorator envolviendo un InvokerImpl existente.
     *
     * @param invoker El InvokerImpl que será decorado con memoización.
     */
    public MemoizationDecorator(InvokerImpl invoker) {
        super(0, invoker.getController(), invoker.getInvokerId());
        this.client = invoker;
    }

    /**
     * Invoca una acción utilizando el cliente interno y almacena su resultado en caché.
     * Si una acción con los mismos parámetros ya fue invocada, retorna el resultado de la caché.
     *
     * @param actionName El nombre de la acción a invocar.
     * @param params Los parámetros para la acción.
     * @return El resultado de la acción.
     * @throws Exception Si ocurre un error durante la invocación de la acción.
     */
    public Object invokeAction(String actionName, Object params) throws Exception {
        int key = generateCacheKey(actionName, params);
        if (cache.containsKey(key)) {
            Remember r = (Remember) cache.get(key);
            if (r.parameters.equals(params)) {
                return r.result;
            } else {
                return cacheResultAndInvoke(actionName, params, key);
            }
        } else {
            return cacheResultAndInvoke(actionName, params, key);
        }
    }

    /**
     * Genera una clave única para la caché basada en el nombre de la acción y los parámetros.
     *
     * @param actionName El nombre de la acción.
     * @param params Los parámetros de la acción.
     * @return La clave generada para la caché.
     */
    private int generateCacheKey(String actionName, Object params) {
        int key = 0;
        for (int i = 0; i < actionName.length(); i++) {
            char c = actionName.charAt(i);
            key += (int) c - 'a' + 1;
        }
        key += params.hashCode();
        return key;
    }

    /**
     * Invoca la acción utilizando el cliente, almacena el resultado en la caché y lo devuelve.
     *
     * @param actionName El nombre de la acción.
     * @param params Los parámetros de la acción.
     * @param key La clave para almacenar el resultado en la caché.
     * @return El resultado de la acción.
     * @throws Exception Si ocurre un error durante la invocación.
     */
    private Object cacheResultAndInvoke(String actionName, Object params, int key) throws Exception {
        Object result = client.invokeAction(actionName, params);
        cache.put(key, new Remember(params, result));
        return result;
    }

}