package main.faas.decorator;

/**
 * La clase Remember se utiliza para almacenar pares de parámetros y resultados.
 * Es utilizada principalmente para la implementación de la memoización,
 * permitiendo almacenar y recuperar rápidamente resultados previos de invocaciones de acciones.
 */
public class Remember {
    /**
     * Los parámetros utilizados en la invocación de una acción.
     */
    Object parameters;

    /**
     * El resultado obtenido de la invocación de la acción.
     */
    Object result;

    /**
     * Construye un objeto Remember con los parámetros y el resultado de una invocación.
     *
     * @param params Los parámetros utilizados en la invocación de la acción.
     * @param result El resultado de la invocación de la acción.
     */
    public Remember(Object params, Object result) {
        this.parameters = params;
        this.result = result;
    }
}
