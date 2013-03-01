package com.akavrt.csp.core;

import com.akavrt.csp.params.ObjectiveParameters;

import java.util.List;

/**
 * <p>Implementation of the objective function used in a previous version. Based on a linear
 * scalarization when three partial objectives (minimization of trim loss, pattern reduction and
 * minimization of product deviation) are substituted with a single objective and a set of weights
 * used to express importance of each partial objective.<p/>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ScalarMetric implements Metric {
    private final Problem problem;
    private final ObjectiveParameters params;

    /**
     * <p>Creates an instance of evaluator. Can be reused to evaluate different solutions against
     * problem and objective function provided as a parameters to the constructor.</p>
     *
     * @param problem The problem in question.
     * @param params  Parameters of objective function.
     */
    public ScalarMetric(Problem problem, ObjectiveParameters params) {
        this.problem = problem;
        this.params = params;
    }

    /**
     * <p>Calculates trim loss ratio.</p>
     *
     * <p>Evaluated value may vary from 0 to 1. The less is better.<p/>
     *
     * @param solution The evaluated solution.
     * @return Calculated trim loss ratio.
     */
    public double getTrimRatio(Solution solution) {
        double trimArea = 0;
        double totalArea = 0;

        for (Pattern pattern : solution.getPatterns()) {
            if (pattern.isActive()) {
                trimArea += pattern.getTrimArea(problem.getOrders());
                totalArea += pattern.getRoll().getUsableArea();
            }
        }

        return totalArea == 0 ? 0 : trimArea / totalArea;
    }

    /**
     * <p>Calculates pattern reduction ratio.</p>
     *
     * <p>If only one roll is used, then the fixed value of zero will be returned. Otherwise
     * evaluated value may vary from 0 to 1. The less is better.</p>
     *
     * @param solution The evaluated solution.
     * @return Calculated pattern reduction ratio.
     */
    public double getPatternsRatio(Solution solution) {
        double ratio = 0;

        int activePatternsCount = 0;
        for (Pattern pattern : solution.getPatterns()) {
            if (pattern.isActive()) {
                activePatternsCount++;
            }
        }

        if (activePatternsCount > 1) {
            ratio = (solution.getUniquePatternsCount() - 1) / (double) (activePatternsCount - 1);
        }

        return ratio;
    }

    /**
     * <p>Calculates product deviation ratio.</p>
     *
     * <p>Evaluated value may vary from 0 to 1. The less is better: zero means that there is no
     * overproduction or underproduction.</p>
     *
     * @param solution The evaluated solution.
     * @return Calculated pattern reduction ratio.
     */
    public double getProductionRatio(Solution solution) {
        double totalProductDeviation = 0;

        List<Order> orders = problem.getOrders();
        for (int i = 0; i < orders.size(); i++) {
            double productDeviation = Math.abs(orders.get(i).getLength() -
                    solution.getProductionLengthForOrder(i));
            totalProductDeviation += productDeviation / orders.get(i).getLength();
        }

        return totalProductDeviation / orders.size();
    }

    /**
     * <p>Using simple objective function of the following form:</p>
     *
     * <p>Z = C1 * trimLossRatio + C2 * patternsRatio + C3 * productDeviationRation,</p>
     *
     * <p>where C1 + C2 + C3 = 1.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public double evaluate(Solution solution) {
        return params.getTrimFactor() * getTrimRatio(solution) +
                params.getPatternsFactor() * getPatternsRatio(solution) +
                params.getProductionFactor() * getProductionRatio(solution);
    }
}
