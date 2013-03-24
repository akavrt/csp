package com.akavrt.csp.tester.tracer;

/**
 * <p>Sometimes object properties cannot be thoroughly investigated without resorting to external
 * dependencies. For instance, check out Solution and Metric - to reveal inner logical structure of
 * the given solution we need to apply a metric to it. In testing environment Metric could be
 * acting as a tracer and supply us with valuable details about solutions structure.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface Tracer<T> {
    /**
     * <p>Provide extended description of the object in a human-readable format.</p>
     *
     * @param traceable The solution to be described.
     * @return A human-readable description of the object.
     */
    String trace(T traceable);
}