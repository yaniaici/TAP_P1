package main.faas.observer;
/**
 * La clase Metrics se utiliza para almacenar y proporcionar detalles sobre la ejecución de una acción,
 * incluyendo el identificador del invocador, el tiempo de ejecución y el uso de memoria.
 */
public class Metrics {
    private final String invokerId;
    private final long executionTime;
    private final int memoryUsage;

    /**
     * Constructor que inicializa una instancia de Metrics.
     *
     * @param invokerId El identificador del invocador que ejecutó la acción.
     * @param executionTime El tiempo de ejecución de la acción en milisegundos.
     * @param memoryUsage La cantidad de memoria utilizada por la acción en MB.
     */
    public Metrics (String invokerId, long excecutionTime, int memoryUsage) {
        this.invokerId = invokerId;
        this.executionTime = excecutionTime;
        this.memoryUsage = memoryUsage;
    }

    /**
     * Obtiene el identificador del invocador.
     *
     * @return El identificador del invocador.
     */
    public String getInvokerId() {
        return invokerId;
    }

    /**
     * Obtiene el tiempo de ejecución de la acción.
     *
     * @return El tiempo de ejecución en milisegundos.
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * Obtiene la cantidad de memoria utilizada por la acción.
     *
     * @return La cantidad de memoria en MB.
     */
    public int getMemoryUsage() {
        return memoryUsage;
    }

    /**
     * Proporciona una representación en cadena de la instancia de Metrics.
     *
     * @return Una cadena que representa la instancia de Metrics.
     */
    @Override
    public String toString() {
        return "Metrics{" +
                "invokerId='" + invokerId + '\'' +
                ", executionTime=" + executionTime +
                ", memoryUsage=" + memoryUsage +
                '}';
    }


}
