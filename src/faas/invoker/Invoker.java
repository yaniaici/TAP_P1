package faas.invoker;

import java.util.function.Function;

public interface Invoker {

    void registerAction(String actionName, Function<Object, Object> action, int memoryMB);

    Object invokeAction(String actionName, Object params) throws Exception;

    int getFreeMemoryMB();

    boolean hasAction(String actionName);
}
