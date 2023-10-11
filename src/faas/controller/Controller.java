package faas.controller;

import faas.invoker.Invoker;

import java.util.List;
import java.util.function.Function;

public interface Controller<T,R> {

     void registerAction(String actionName, Function<T, R> action, int memoryMB);
    void setInvokers(List<Invoker<T, R>> invokers);
     R invoke(String actionName, T params) throws Exception;


}
