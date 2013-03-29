package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.ExecutionContext;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.utils.ParameterSet;

import java.util.List;

/**
 * User: akavrt
 * Date: 27.03.13
 * Time: 16:11
 */
public class GeneticAlgorithm implements Algorithm, GeneticExecutionContext {
    protected ExecutionContext context;
    private GeneticAlgorithmParameters parameters;
    private PatternGenerator patternGenerator;
    private BinaryOperator crossover;
    private UnaryOperator mutation;
    private UnaryOperator heuristic;
    private Population population;
    private Metric objectiveFunction;
    private double[] orderWidth;
    private double[] orderLength;

    @Override
    public String name() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Solution> execute(ExecutionContext context) {

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ParameterSet> getParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GeneticAlgorithmParameters getMethodParameters() {
        return parameters;
    }

    @Override
    public PatternGenerator getPatternGenerator() {
        return patternGenerator;
    }

    @Override
    public Metric getObjectiveFunction() {
        return objectiveFunction;
    }

    @Override
    public BinaryOperator getCrossover() {
        return null;
    }

    @Override
    public UnaryOperator getMutation() {
        return null;
    }

    @Override
    public double getOrderWidth(int index) {
//        return orderWidth == null || index >= orderWidth.length ? 0 : orderWidth[index];
        return orderWidth[index];
    }

    @Override
    public double getOrderLength(int index) {
        //        return orderLength == null || index >= orderLength.length ? 0 : orderLength[index];
        return orderLength[index];
    }

    @Override
    public int getOrdersSize() {
//      return orderWidth == null ? 0 : orderWidth.length;
        return orderWidth.length;
    }

    @Override
    public Problem getProblem() {
        return context == null ? null : context.getProblem();
    }

    @Override
    public boolean isCancelled() {
        return context == null ? null : context.isCancelled();
    }
}
