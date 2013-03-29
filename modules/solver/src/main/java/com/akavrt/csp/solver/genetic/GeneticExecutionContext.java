package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.ExecutionContext;
import com.akavrt.csp.solver.pattern.PatternGenerator;

/**
 * User: akavrt
 * Date: 27.03.13
 * Time: 20:25
 */
public interface GeneticExecutionContext extends ExecutionContext {
    GeneticAlgorithmParameters getMethodParameters();
    PatternGenerator getPatternGenerator();
    Metric getObjectiveFunction();
    BinaryOperator getCrossover();
    UnaryOperator getMutation();
    double getOrderWidth(int index);
    double getOrderLength(int index);
    int getOrdersSize();
}
