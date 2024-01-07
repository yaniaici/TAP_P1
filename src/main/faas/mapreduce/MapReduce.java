package main.faas.mapreduce;

import main.faas.controller.Controller;
import main.faas.future.impl.ResultFutureImpl;
import main.faas.invoker.Invoker;
import main.faas.invoker.impl.InvokerImpl;
import main.faas.policymanager.PolicyManager;
import main.faas.policymanager.resourcemanagers.impl.RoundRobinResourceManagement;

import java.util.*;
import java.util.function.Function;

public class MapReduce {
    public Map<String,Integer> wordCount(String text, int threads) {
        //Dividimos el texto en palabras y eliminaos los caracteres especiales
        List<List<String>> partials = divideParts(text, threads);
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
        Controller controller = getController("wordscount", wordscount, 256);
        Map<String,Integer> results = new HashMap<>();
        try {
            for (int i = 0; i < threads; i++) {
                ResultFutureImpl<Object> resultat = controller.invoke_async("wordscount", Map.of("x", partials.get(i)));
                if(i == 0){
                    results = (Map<String, Integer>) resultat.get().get(0);
                }else{
                    Map<String, Integer> aux = (Map<String, Integer>) resultat.get().get(0);
                    results = mergeMaps(results, aux);
                }
            }
            controller.displayExecutionTimeByInvoker();
            controller.displayExecutionTimeStats();
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String,Integer> countWords(String text, int threads) {
        //Dividimos el texto en palabras y eliminaos los caracteres especiales
        List<List<String>> partials = divideParts(text, threads);
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
        Controller controller = getController("countwords", countwords, 128);
        Map<String,Integer> results = new HashMap<>();
        try {
            for (int i = 0; i < threads; i++) {
                ResultFutureImpl<Object> resultat = controller.invoke_async("countwords", Map.of("x", partials.get(i)));
                if(i == 0){
                    results = (Map<String, Integer>) resultat.get().get(0);
                }else{
                    Map<String, Integer> aux = (Map<String, Integer>) resultat.get().get(0);
                    results = mergeMaps(results, aux);
                }
            }
            controller.displayExecutionTimeStats();
            controller.displayExecutionTimeByInvoker();
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Integer> mergeMaps(Map<String, Integer> map1, Map<String, Integer> map2) {
        Map<String, Integer> result = new HashMap<>(map1);

        for (Map.Entry<String, Integer> entry : map2.entrySet()) {
            result.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        return result;
    }

    private Controller getController(String actionName, Function<Object, Object> action, int memoryMB){
        Controller controller = new Controller();
        Invoker invoker = new InvokerImpl(2048,controller,"2");
        PolicyManager policyManager = new PolicyManager(new RoundRobinResourceManagement());
        controller.setPolicyManager(policyManager);
        controller.setInvokers(Collections.singletonList(invoker));
        controller.registerAction(actionName, action, memoryMB);
        return controller;
    }

    private List<List<String>> divideParts(String text, int threads){
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
}
