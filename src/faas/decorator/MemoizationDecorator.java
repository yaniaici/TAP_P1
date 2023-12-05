package faas.decorator;

import faas.invoker.impl.InvokerImpl;

import java.util.HashMap;

public class MemoizationDecorator extends InvokerImpl {
    InvokerImpl client;
    HashMap<Integer, Object> cache = new HashMap<Integer, Object>();

    public MemoizationDecorator(InvokerImpl invoker){
        super(0);
        client = invoker;
    }

    public Object invokeAction(String actionName, Object params) throws Exception {
        int key = 0;
        for (int i = 0; i < actionName.length(); i++) {
            char c = actionName.charAt(i);
            key = key + (int) c - 'a' + 1;  // Valor numérico de la letra + acumulado
        }
        key = key+params.hashCode();
        if(cache.containsKey(key)){
            Remember r = (Remember) cache.get(key);
            if(r.parameters.equals(params)) {
                return r.result;
            }else{
                Object result = client.invokeAction(actionName, params);
                cache.put(key, (new Remember(params,result)));
                return result;
            }
        }else{
            Object result = client.invokeAction(actionName, params);
            cache.put(key, new Remember(params,result));
            return result;
        }
    }
}
