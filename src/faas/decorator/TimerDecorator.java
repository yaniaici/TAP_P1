package faas.decorator;

import faas.invoker.impl.InvokerImpl;

public class TimerDecorator extends InvokerImpl {
    long inicio;
    long fin;
    InvokerImpl client;

    public TimerDecorator(InvokerImpl invoker){
         super(0, invoker.getController(), invoker.getInvokerId());
        client = invoker;
    }

    public Object invokeAction(String actionName, Object params) throws Exception {
        inicio = System.currentTimeMillis();
        Object result = client.invokeAction(actionName, params);
        fin = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución: " + (fin - inicio) + " milisegundos");
        return result;
    }
}
