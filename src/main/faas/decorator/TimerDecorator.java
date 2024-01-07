package main.faas.decorator;

import main.faas.invoker.impl.InvokerImpl;

/**
 * El TimerDecorator es un decorador para InvokerImpl que agrega funcionalidad
 * para medir y mostrar el tiempo de ejecución de las acciones invocadas.
 */
public class TimerDecorator extends InvokerImpl {
    /**
     * Tiempo de inicio de la invocación.
     */
    private long inicio;

    /**
     * Tiempo de finalización de la invocación.
     */
    private long fin;

    /**
     * Cliente InvokerImpl que será decorado con la funcionalidad de temporización.
     */
    private InvokerImpl client;

    /**
     * Construye un TimerDecorator envolviendo un InvokerImpl existente.
     *
     * @param invoker El InvokerImpl que será decorado con la funcionalidad de temporización.
     */
    public TimerDecorator(InvokerImpl invoker) {
        super(0, invoker.getController(), invoker.getInvokerId());
        this.client = invoker;
    }

    /**
     * Invoca una acción utilizando el cliente interno y mide el tiempo que tarda la ejecución.
     * Muestra el tiempo de ejecución en milisegundos.
     *
     * @param actionName El nombre de la acción a invocar.
     * @param params Los parámetros para la acción.
     * @return El resultado de la acción.
     * @throws Exception Si ocurre un error durante la invocación de la acción.
     */
    public Object invokeAction(String actionName, Object params) throws Exception {
        inicio = System.currentTimeMillis();
        Object result = client.invokeAction(actionName, params);
        fin = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución: " + (fin - inicio) + " milisegundos");
        return result;
    }
}
