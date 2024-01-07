package main.faas.future.impl;

import main.faas.future.ResultFuture;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de ResultFuture que proporciona una manera de acceder a un resultado futuro.
 * Permite a los hilos esperar de manera segura por un resultado que será proporcionado por otro hilo.
 *
 * @param <V> El tipo de dato del resultado.
 */
public class ResultFutureImpl<V> implements ResultFuture<V> {
    private List<V> result;
    private boolean isDone = false;

    /**
     * Constructor que inicializa la lista de resultados.
     */
    public ResultFutureImpl() {
        result = new ArrayList<>();
    }

    /**
     * Verifica si el resultado ya está disponible.
     *
     * @return Verdadero si el resultado está disponible, falso en caso contrario.
     */
    @Override
    public synchronized boolean isDone() {
        return isDone;
    }

    /**
     * Añade un resultado a la lista de resultados.
     * Este método se puede usar para añadir resultados de manera incremental.
     *
     * @param result El resultado a añadir.
     */
    public synchronized void add(V result) {
        this.result.add(result);
    }

    /**
     * Obtiene el resultado esperado. Si aún no está disponible, bloquea el hilo hasta que lo esté.
     *
     * @return La lista de resultados.
     * @throws InterruptedException Si el hilo es interrumpido mientras espera.
     */
    @Override
    public synchronized List<V> get() throws InterruptedException {
        while (!isDone) {
            wait();
        }
        return result;
    }

    /**
     * Establece el valor del resultado y notifica a todos los hilos que están esperando que el resultado ya está disponible.
     * Si el resultado ya fue establecido previamente, este método no hace nada.
     *
     * @param value El valor a establecer como resultado.
     */
    @Override
    public synchronized void set(V value) {
        if (!isDone) {
            this.result.add(value);
            this.isDone = true;
            notifyAll();
        }
    }
}
