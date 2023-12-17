package faas.reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ActionProxy implements InvocationHandler {
    private Object realObject;

    public static Object newInstance(Object target){
        Class targetClass = target.getClass();
        Class interfaces[] = targetClass.getInterfaces();
        return Proxy.newProxyInstance(targetClass.getClassLoader(),
                interfaces, new ActionProxy(target));
    }
    private ActionProxy(Object realObject) {
        this.realObject = realObject;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            // Realiza acciones adicionales antes de invocar el método
            System.out.println("Antes de invocar el metodo " + method.getName() + " escribo esto\n");

            // Invoca el método en el objeto real
            result = method.invoke(realObject, args);

            // Realiza acciones adicionales después de invocar el método
            System.out.println("Después de invocar al metodo " + method.getName() + " escribo esto\n");
        } catch (Exception e) {
            System.err.println("Fallo la invocacion del metodo " + method.getName());
        }
        return result;
    }

}
