package faas.decorator;

public class Remember {
    Object parameters;
    Object result;
    public Remember(Object params, Object result) {
        parameters = params;
        this.result = result;
    }
}
