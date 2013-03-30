package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.solver.ExecutionContext;

/**
 * User: akavrt
 * Date: 27.03.13
 * Time: 20:25
 */
public interface GeneticExecutionContext extends ExecutionContext {
    double getOrderWidth(int index);
    double getOrderLength(int index);
    int getOrdersSize();
}
