package faas;


import faas.invoker.Invoker;
import faas.policymanager.PolicyManager;

import java.util.List;
import java.util.function.Function;

public interface DynamicProxy {
    void setInvokers(List<Invoker> invokers);
    void setPolicyManager(PolicyManager policyManager);
    Object invoke(String actionName, Object params) throws Exception;

    Object invoke_async(String actionName, Object parameters);

    void registerAction(String actionName, Function<Object,Object> action, int memoryMB);


}
