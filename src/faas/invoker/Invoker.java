package faas.invoker;

import java.util.function.Function;

public interface Invoker<T,R> {
    void registerAction(String actionName, Function<T, R> action, int memoryMB);
    int getFreeMemoryMB();
    boolean hasAction(String actionName);
    R invokeAction(String actionName, T params) throws Exception;

}
