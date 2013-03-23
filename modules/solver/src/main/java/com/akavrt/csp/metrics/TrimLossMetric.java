package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Solution;

/**
 * <p>This metric corresponds to the trim loss minimization objective.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class TrimLossMetric extends MinimizationMetric {
    /**
     * <p>Calculate trim loss ratio.</p>
     *
     * <p>Evaluated value may vary from 0 to 1. The less is better.<p/>
     *
     * @param solution The evaluated solution.
     * @return Trim loss fractional ratio.
     */
    @Override
    public double evaluate(Solution solution) {
        double trimArea = 0;
        double totalArea = 0;

        for (Pattern pattern : solution.getPatterns()) {
            if (pattern.isActive()) {
                trimArea += pattern.getTrimArea();
                totalArea += pattern.getRoll().getArea();
            }
        }

        return totalArea == 0 ? 0 : trimArea / totalArea;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "TL";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Trim loss fractional ratio";
    }

}
