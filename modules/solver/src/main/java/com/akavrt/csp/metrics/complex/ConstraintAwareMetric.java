package com.akavrt.csp.metrics.complex;

import com.akavrt.csp.core.Plan;
import com.akavrt.csp.metrics.Metric;

import java.util.Collections;
import java.util.Comparator;

/**
 * User: akavrt
 * Date: 20.04.13
 * Time: 19:35
 */
public class ConstraintAwareMetric implements Metric {
    private final Metric patternsMetric;
    private final ConstraintAwareMetricParameters params;

    public ConstraintAwareMetric() {
        this(new ConstraintAwareMetricParameters());
    }

    public ConstraintAwareMetric(ConstraintAwareMetricParameters params) {
        this.params = params;

        patternsMetric = new PatternReductionMetric();
    }

    @Override
    public double evaluate(Plan plan) {
        return params.getAggregatedTrimFactor() * plan.getMetricProvider().getAggregatedTrimRatio()
                + params.getPatternsFactor() * patternsMetric.evaluate(plan);
    }

    @Override
    public int compare(Plan p1, Plan p2) {
        double firstUnderProduction = p1.getMetricProvider().getAverageUnderProductionRatio();
        double secondUnderProduction = p2.getMetricProvider().getAverageUnderProductionRatio();

        int result;
        if (firstUnderProduction == 0 && secondUnderProduction == 0) {
            // dealing with two feasible solutions,
            // minimization problem is implied
            double s1eval = evaluate(p1);
            double s2eval = evaluate(p2);

            result = s1eval > s2eval ? -1 : (s1eval < s2eval ? 1 : 0);
        } else if (firstUnderProduction > 0 && secondUnderProduction > 0) {
            // dealing with two infeasible solutions
            result = firstUnderProduction > secondUnderProduction ? -1
                    : (firstUnderProduction < secondUnderProduction ? 1 : 0);
        } else {
            // only one solution is feasible
            result = firstUnderProduction > 0 ? -1 : 1;
        }

        return result;
    }

    @Override
    public Comparator<Plan> getComparator() {
        return new Comparator<Plan>() {

            @Override
            public int compare(Plan lhs, Plan rhs) {
                return ConstraintAwareMetric.this.compare(lhs, rhs);
            }
        };
    }

    @Override
    public Comparator<Plan> getReverseComparator() {
        return Collections.reverseOrder(getComparator());
    }

    @Override
    public String abbreviation() {
        return "SCALAR-X";
    }

    @Override
    public String name() {
        return "Constraint aware parameter-less metric";
    }

}
