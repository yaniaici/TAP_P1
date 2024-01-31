package main.faas.mapreduce;

import main.faas.controller.Controller;
import main.faas.future.impl.ResultFutureImpl;
import main.faas.invoker.Invoker;
import main.faas.invoker.impl.InvokerImpl;
import main.faas.policymanager.PolicyManager;
import main.faas.policymanager.resourcemanagers.impl.GreedyGroupResourceManagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class MainMapReduce {
    public static void main(String[] args) {
        Controller controller = new Controller();
        Invoker invoker = new InvokerImpl(2048,controller,"2");
        Invoker invoker2 = new InvokerImpl(2048,controller,"3");
        PolicyManager policyManager = new PolicyManager(new GreedyGroupResourceManagement());
        controller.setPolicyManager(policyManager);
        List<Invoker> invokersList = new ArrayList<>();
        invokersList.add(invoker);
        invokersList.add(invoker2);
        controller.setInvokers(invokersList);
        int threads = 4;    //Partes en las que vamos a dividir el texto i numero de invokaciones distintas que vamos a hacer.

        Function<Object, Object> countwords = x -> {
            if (x instanceof Map<?, ?> map) {
                List<String> partial = (List<String>) map.get("x");
                Map<String, Integer> result = new HashMap<>();
                int count = 0;
                for (String o : partial){
                    count++;
                }
                result.put("Palabras totales", count);
                return result;
            }
            return null;
        };

        Function<Object, Object> wordscount = x -> {
            if (x instanceof Map<?, ?> map) {
                List<String> partial = (List<String>) map.get("x");
                Map<String, Integer> result = new HashMap<>();
                for (String o : partial){
                    result.put(o, result.getOrDefault(o, 0) + 1);
                }
                return result;
            }
            return null;
        };

        controller.registerAction("countwords", countwords, 512);
        controller.registerAction("wordscount", wordscount, 512);


        try {
            String txt = leer();
            //Dividimos el texto en palabras y eliminaos los caracteres especiales
            List<List<String>> partials = divideParts(txt, threads);
            Map<String,Integer> results = new HashMap<>();
            for (int i = 0; i < threads; i++) {
                ResultFutureImpl<Object> resultat = controller.invoke_async("wordscount", Map.of("x", partials.get(i)));
                if (i == 0) {
                    results = (Map<String, Integer>) resultat.get().getFirst();
                } else {
                    Map<String, Integer> aux = (Map<String, Integer>) resultat.get().getFirst();
                    results = mergeMaps(results, aux);
                }
            }

            Map<String,Integer> results2 = new HashMap<>();
            for (int i = 0; i < threads; i++) {
                ResultFutureImpl<Object> resultat = controller.invoke_async("countwords", Map.of("x", partials.get(i)));
                if (i == 0) {
                    results2 = (Map<String, Integer>) resultat.get().getFirst();
                } else {
                    Map<String, Integer> aux = (Map<String, Integer>) resultat.get().getFirst();
                    results2 = mergeMaps(results2, aux);
                }
            }

            System.out.println(results);
            System.out.println(results2);
            controller.displayExecutionTimeStats();
            controller.displayExecutionTimeByInvoker();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    public static String leer() throws IOException {
        // Ruta del archivo
        String txtRoute = "src/main/faas/booksForMapReduce/what_the_wind_did.txt";

        BufferedReader reader = new BufferedReader(new FileReader(txtRoute));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        // Leemos línea por línea y agregamos al StringBuilder
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        // Cerramos el BufferedReader
        reader.close();


        // Devolvemos el contenido
        return stringBuilder.toString();
    }

    public static List<List<String>> divideParts(String text, int threads){
        List<String> all = Arrays.stream(text.split(" "))
                .map(word -> word.replaceAll("[\",.?\\n]", ""))
                .toList();
        int wordsPerThread = all.size() / threads;
        List<List<String>> partials = new ArrayList<>();
        //Por cada uno de los threads le asignamos un rango de palabras
        for (int i = 0; i < threads; i++) {
            int start = i * wordsPerThread;
            int end = (i + 1) * wordsPerThread;
            List<String> partial = new ArrayList<>();
            for (int j = start; j < end; j++) {
                String word = all.get(j);
                partial.add(word);
                if(i==threads-1 && j==end-1){
                    for (int k = end; k < all.size(); k++) {
                        word = all.get(k);
                        partial.add(word);
                    }
                }
            }
            partials.add(partial);
        }
        return partials;
    }
    public static Map<String, Integer> mergeMaps(Map<String, Integer> map1, Map<String, Integer> map2) {
        Map<String, Integer> result = new HashMap<>(map1);

        for (Map.Entry<String, Integer> entry : map2.entrySet()) {
            result.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        return result;
    }

}
