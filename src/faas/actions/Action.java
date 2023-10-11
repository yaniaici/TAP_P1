package faas.actions;

public interface Action<T, R> {
    R run(T arg) throws Exception;
}
