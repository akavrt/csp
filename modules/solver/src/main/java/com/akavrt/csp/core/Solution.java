package com.akavrt.csp.core;

import com.akavrt.csp.core.metadata.SolutionMetadata;
import com.akavrt.csp.metrics.MetricProvider;
import com.akavrt.csp.utils.Constants;
import com.google.common.collect.Sets;
import com.google.common.math.DoubleMath;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>Solution to the cutting stock problem (CSP) is represented as a cutting plan. Cutting plan
 * explicitly enumerates patterns and rolls used to produce orders of sufficient length.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Solution implements Plan {
    private final SolutionMetricProvider metricProvider;
    private final Problem problem;
    private List<Pattern> patterns;
    private SolutionMetadata metadata;

    /**
     * <p>Create an instance of Solution with empty array of patterns</p>.
     */
    public Solution(Problem problem) {
        this.problem = problem;

        patterns = new ArrayList<Pattern>();
        metricProvider = new SolutionMetricProvider(problem, this);
    }

    /**
     * <p>Create an instance of Solution with array of patterns provided as a parameter.</p>
     *
     * @param patterns The list of patters assigned to the solution.
     */
    public Solution(Problem problem, List<Pattern> patterns) {
        this.problem = problem;
        this.patterns = patterns;

        metricProvider = new SolutionMetricProvider(problem, this);
    }

    /**
     * <p>Add new pattern to the solution.</p>
     *
     * @param pattern The new pattern added to the solution.
     */
    public void addPattern(Pattern pattern) {
        patterns.add(pattern);
    }

    /**
     * <p>Returns a list of patterns used in cutting plan.</p>
     *
     * @return The list of patterns.
     */
    public List<Pattern> getPatterns() {
        return patterns;
    }

    /**
     * <p>Set prepared list of patterns.</p>
     *
     * @param patterns The list of patters assigned to the solution.
     */
    public void setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    /**
     * <p>Remove all previously added patterns.</p>
     */
    public void clear() {
        patterns.clear();
    }

    /**
     * <p>Calculate the length of the finished product we will get for specific order after
     * executing cutting plan. Measured in abstract units.</p>
     *
     * @param order The order in question.
     * @return The length of produced strip.
     */
    public double getProductionLengthForOrder(Order order) {
        double productionLength = 0;

        for (Pattern pattern : patterns) {
            productionLength += pattern.getProductionLengthForOrder(order);
        }

        return productionLength;
    }

    /**
     * <p>Check feasibility of used patterns.</p>
     *
     * @return true if plan consists of feasible patterns only, false otherwise.
     */
    public boolean isPatternsFeasible() {
        boolean isPatternFeasible = true;

        // exit on the first infeasible pattern
        int allowedCutsNumber = problem.getAllowedCutsNumber();
        for (Pattern pattern : patterns) {
            if (!pattern.isFeasible(allowedCutsNumber)) {
                isPatternFeasible = false;
                break;
            }
        }

        return isPatternFeasible;
    }

    /**
     * <p>Feasible plans can cut each roll (attach it to the pattern) only once. Multiple usage of
     * the rolls isn't allowed.</p>
     *
     * @return true if plan doesn't contain rolls which are used more than once, false otherwise.
     */
    public boolean isRollUsageFeasible() {
        Set<Integer> rollIds = Sets.newHashSet();
        boolean isRepeatedRollFound = false;
        for (Pattern pattern : patterns) {
            if (pattern.isActive() && !rollIds.add(pattern.getRoll().getInternalId())) {
                // we can't use same roll twice
                isRepeatedRollFound = true;
                break;
            }
        }

        return !isRepeatedRollFound;
    }

    /**
     * <p>Check whether all requirements for order's length are met. If any order with insufficient
     * length can be found, solution should be treated as infeasible one.</p>
     *
     * @return true if strip of sufficient length will be produced for all orders, false otherwise.
     */
    public boolean isOrdersFulfilled() {
        boolean isOrderFulfilled = true;

        // exit on the first unfulfilled order
        for (Order order : problem.getOrders()) {
            isOrderFulfilled = DoubleMath.fuzzyCompare(getProductionLengthForOrder(order),
                                                       order.getLength(),
                                                       Constants.TOLERANCE) >= 0;
            if (!isOrderFulfilled) {
                break;
            }
        }

        return isOrderFulfilled;
    }

    /**
     * <p>Check all characteristics of the solution to determine whether it is feasible or not.</p>
     *
     * @return true if solution is feasible, false otherwise.
     */
    public boolean isFeasible() {
        return isPatternsFeasible() && isRollUsageFeasible() && isOrdersFulfilled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetricProvider getMetricProvider() {
        return metricProvider;
    }

    /**
     * <p>Provide extended description of the solution.</p>
     *
     * @return Additional information about the solution.
     */
    public SolutionMetadata getMetadata() {
        return metadata;
    }

    /**
     * <p>Set additional information about the solution.</p>
     *
     * @param metadata Additional information about the solution.
     */
    public void setMetadata(SolutionMetadata metadata) {
        this.metadata = metadata;
    }

}
