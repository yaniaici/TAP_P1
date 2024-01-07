package main.faas.reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ActionProxy es un handler de invocación que se utiliza para crear un proxy dinámico para cualquier objeto.
 * Intercepta todas las llamadas a métodos del objeto proxy y puede realizar acciones adicionales antes y después
 * de invocar el método en el objeto real.
 */
public class ActionProxy implements InvocationHandler {
    private Object realObject;

    /**
     * Crea una nueva instancia de proxy para un objeto dado.
     *
     * @param target El objeto objetivo para el cual se creará el proxy.
     * @return Un objeto proxy que implementa las interfaces del objeto objetivo.
     */
    public static Object newInstance(Object target){
        Class targetClass = target.getClass();
        Class interfaces[] = targetClass.getInterfaces();
        return Proxy.newProxyInstance(targetClass.getClassLoader(),
                interfaces, new ActionProxy(target));
    }

    /**
     * Constructor privado para ActionProxy.
     *
     * @param realObject El objeto real para el cual este proxy actuará como handler.
     */
    private ActionProxy(Object realObject) {
        this.realObject = realObject;
    }

    /**
     * Intercepta todas las llamadas a métodos del objeto proxy.
     * Realiza acciones antes y después de la invocación del método en el objeto real.
     *
     * @param proxy El objeto proxy en el que se invocó el método.
     * @param method El método que fue invocado.
     * @param args Argumentos del método invocado.
     * @return El resultado de la invocación del método en el objeto real.
     * @throws Throwable Si la invocación del método produce una excepción.
     */
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
