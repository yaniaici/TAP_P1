package main.faas.future.impl;

import main.faas.future.ResultFuture;

import java.util.ArrayList;
import java.util.List;

public class ResultFutureImpl<V> implements ResultFuture<V> {
    private List<V> result;
    private boolean isDone = false;

    public ResultFutureImpl() {
        result = new ArrayList<>();
    }

    @Override
    public synchronized boolean isDone() {
        return isDone;
    }

    public synchronized void add(V result) {
        this.result.add(result);
    }

    //Obtenemos el valor si ya lo tenemos, usamos syncronized para evitar que se escriban valores a la vez que se leen
    @Override
    public synchronized List<V> get() throws InterruptedException {
        while (!isDone) {
            wait();
        }
        return result;
    }

    //Si no se ha escrito el valor, lo escribimos y notificamos a los hilos que esten esperando
    @Override
    public synchronized void set(V value) {
        if (!isDone) {
            this.result.add(value);
            this.isDone = true;
            notifyAll();
        }
    }
}
