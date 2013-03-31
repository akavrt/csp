package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Plan;

import java.util.Collections;
import java.util.Comparator;

/**
 * <p>This class defines ordering of solutions in minimization problems. Should be used as a parent
 * class for metrics corresponding to minimization problems.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public abstract class MinimizationMetric implements Metric {
    /**
     * <p>We are dealing with minimization problem: given two solutions, solution with smaller
     * value of objective function will be better.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public int compare(Plan p1, Plan p2) {
        double s1eval = evaluate(p1);
        double s2eval = evaluate(p2);

        return s1eval > s2eval ? -1 : (s1eval < s2eval ? 1 : 0);
    }

    /**
     * <p>Natural ordering defined in the following way: given two cutting plans p1 and p2, plan p2
     * will be preceded by plan p1 in a sorted collection if plan p2 is better than plan p1, i.e.
     * evaluated objective function for p2 gives smaller value (minimization problem).</p>
     *
     * @return A comparator which imposes natural ordering on a collection of cutting plans.
     */
    public Comparator<Plan> getComparator() {
        return new Comparator<Plan>() {

            @Override
            public int compare(Plan lhs, Plan rhs) {
                return MinimizationMetric.this.compare(lhs, rhs);
            }
        };
    }

    /**
     * <p>The reverse of the natural ordering defined in the following way: given two cutting plans
     * p1 and p2, plan p2 will be preceded by plan p1 in a sorted collection if plan p2 is worse
     * than plan p1, i.e. evaluated objective function for p2 gives larger value (minimization
     * problem).</p>
     *
     * @return A comparator that imposes the reverse of the natural ordering on a collection of
     *         cutting plans.
     */
    public Comparator<Plan> getReverseComparator() {
        return Collections.reverseOrder(getComparator());
    }

}
