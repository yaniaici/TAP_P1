import faas.controller.Controller;
import faas.controller.impl.ControllerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FaasApplication {

    public static void main(String[] args) {

        ControllerImpl<Map<String, Integer>, Integer> controller = new ControllerImpl<>();

        Function<Map<String, Integer>, Integer> f = x -> x.get("x") - x.get("y");
        controller.registerAction("addAction", f, 256);
        Map<String, Integer> params = new HashMap<>();
        params.put("x", 10);
        params.put("y", 5);

        try {
            Integer result = controller.invokeAction("addAction", params);
            System.out.println("Resultado: " + result);
        } catch (Exception e) {
            System.out.println("ERROR");
        }

    }
}
