package main.faas.observer;

public class Metrics {
    private final String invokerId;
    private final long executionTime;
    private final int memoryUsage;

    public Metrics (String invokerId, long excecutionTime, int memoryUsage) {
        this.invokerId = invokerId;
        this.executionTime = excecutionTime;
        this.memoryUsage = memoryUsage;
    }

    public String getInvokerId() {
        return invokerId;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public int getMemoryUsage() {
        return memoryUsage;
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "invokerId='" + invokerId + '\'' +
                ", executionTime=" + executionTime +
                ", memoryUsage=" + memoryUsage +
                '}';
    }


}
