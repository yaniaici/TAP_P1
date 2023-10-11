package faas.controller;

import java.util.Map;
import java.util.function.Function;

public interface Controller<T,R> {

     void registerAction(String actionName, Function<T, R> action, int memoryMB);

     R invokeAction(String actionName, T params) throws Exception;

}
