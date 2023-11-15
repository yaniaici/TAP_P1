package faas.decorator;

import faas.invoker.impl.InvokerImpl;

import java.util.HashMap;
import java.util.List;

public class MemoizationDecorator extends InvokerImpl {
    InvokerImpl client;
    HashMap<String, List<Object>> cache = new HashMap<String, List<Object>>();

    public MemoizationDecorator(InvokerImpl invoker){
        super(0);
        client = invoker;
    }

    public Object invokeAction(String actionName, Object params) throws Exception {
        if(cache.containsKey(actionName)){
            Remember r = (Remember) cache.get(actionName);
            if(r.parameters.equals(params)) {
                return r.result;
            }else{
                Object result = client.invokeAction(actionName, params);
                cache.put(actionName, (new Remember(params,result)));
                return result;
            }
        }else{
            Object result = client.invokeAction(actionName, params);
            cache.put(actionName, (new Remember(params,result)));
            return result;
        }
    }
}
