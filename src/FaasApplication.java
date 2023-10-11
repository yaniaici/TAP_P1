import faas.controller.Controller;
import faas.controller.impl.ControllerImpl;
import faas.invoker.Invoker;
import faas.invoker.impl.InvokerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FaasApplication {

    public static void main(String[] args) throws Exception {

        List<Invoker<Integer, Integer>> invokers = new ArrayList<>();

        // Agregar Invokers a la lista
        invokers.add(new InvokerImpl<>(1024));  // Ejemplo de un Invoker con 1024 MB de memoria
        invokers.add(new InvokerImpl<>(2048));  // Ejemplo de otro Invoker con 2048 MB de memoria

        // Crear un Controller y asignarle la lista de Invokers
        Controller<Integer, Integer> controller = new ControllerImpl<>();
        controller.setInvokers(invokers);
        System.out.println(invokers);

        // Registrar una acción de prueba en el Controller
        Function<Integer, Integer> testAction = x -> x * 2;  // Ejemplo de una acción de prueba
        controller.registerAction("testAction", testAction, 256);
        System.out.println(invokers);

        System.out.println(controller.invoke("testAction", 2));

    }
}
