package faas.future;

import java.util.List;

public interface ResultFuture<V> {
    boolean isDone();
    List<V> get() throws InterruptedException;
    void set(V value);
}
