package com.akavrt.csp.core;

import com.akavrt.csp.core.metadata.SolutionMetadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Solution to the cutting stock problem (CSP) is represented as a cutting plan. Cutting plan
 * explicitly enumerates patterns and rolls used to produce orders of sufficient length.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Solution {
    private List<Pattern> patterns;
    private SolutionMetadata metadata;

    /**
     * <p>Create an instance of Solution with empty array of patterns</p>.
     */
    public Solution() {
        patterns = new ArrayList<Pattern>();
    }

    /**
     * <p>Create an instance of Solution with array of patterns provided as a parameter.</p>.
     *
     * @param patterns The list of patters assigned to the solution.
     */
    public Solution(List<Pattern> patterns) {
        this.patterns = patterns;
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
     * <p>Calculate total wasted area for cutting plan as a whole. Measured in abstract square
     * units.</p>
     *
     * @return The wasted area for cutting plan.
     */
    public double getTrimArea() {
        double trimArea = 0;
        for (Pattern pattern : patterns) {
            trimArea += pattern.getTrimArea();
        }

        return trimArea;
    }

    /**
     * <p>Number of unique pattern used in this solution equals to the number of times we need to
     * setup equipment (circular shears, in particular) to cut rolls in a way defined by this
     * cutting plan.</p>
     *
     * @return Number of unique cutting patterns used in solution.
     */
    public int getUniquePatternsCount() {
        Set<Integer> set = new HashSet<Integer>();

        for (Pattern pattern : patterns) {
            if (pattern.isActive()) {
                set.add(pattern.getCutsHashCode());
            }
        }

        return set.size();
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
     * <p>Check validity of used patterns.</p>
     *
     * @param problem The problem contains information about orders and constrains needed for
     *                evaluation.
     * @return true if plan consists of valid patterns only, false otherwise.
     */
    private boolean isPatternsValid(Problem problem) {
        boolean isPatternValid = true;

        // exit on the first invalid pattern
        for (int i = 0; i < patterns.size() && isPatternValid; i++) {
            isPatternValid = patterns.get(i).isValid(problem.getAllowedCutsNumber());
        }

        return isPatternValid;
    }

    /**
     * <p>Valid plans can cut each roll (attach it to the pattern) only once. Multiple usage of the
     * rolls isn't allowed.</p>
     *
     * @return true if plan doesn't contain rolls which are used more than once, false otherwise.
     */
    private boolean isRollsValid() {
        Set<Integer> rollIds = new HashSet<Integer>();
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
     * length can be found, solution should be treated as invalid one.</p>
     *
     * @param orders The list of orders is needed for evaluation.
     * @return true if strip of sufficient length will be produced for all orders, false otherwise.
     */
    public boolean isOrdersFulfilled(List<Order> orders) {
        boolean isOrderFulfilled = true;

        // exit on the first unfulfilled order
        for (int i = 0; i < orders.size() && isOrderFulfilled; i++) {
            Order order = orders.get(i);
            isOrderFulfilled = getProductionLengthForOrder(order) >= order.getLength();
        }

        return isOrderFulfilled;
    }

    /**
     * <p>Check all characteristics of the solution to determine whether it is valid or not.</p>
     *
     * @param problem The problem against which validity of the solution is being tested.
     * @return true if solution is valid, false otherwise.
     */
    public boolean isValid(Problem problem) {
        return isPatternsValid(problem) && isRollsValid() && isOrdersFulfilled(problem.getOrders());
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        int digits = (int) Math.floor(Math.log10(patterns.size())) + 1;
        String indexFormat = "\n    #%" + digits + "d: ";

        builder.append("{");
        int i = 0;
        for (Pattern pattern : patterns) {
            String index = String.format(indexFormat, ++i);
            builder.append(index);
            builder.append(pattern);
        }
        builder.append("\n}");

        return builder.toString();
    }

}
