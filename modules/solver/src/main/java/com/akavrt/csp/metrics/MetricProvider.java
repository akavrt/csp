package com.akavrt.csp.metrics;

/**
 * <p>Enumerates all important characteristics of the cutting plan. No information about internal
 * structure of the plan is exposed. This way we are breaking the ties between implementation
 * details and evaluation of the cutting plans' quality.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface MetricProvider {
    /**
     * <p>Calculate total wasted area for cutting plan as a whole. Measured in abstract square
     * units.</p>
     *
     * @return The wasted area for cutting plan.
     */
    double getTrimArea();

    /**
     * <p>Calculate ratio between amount of material wasted on trim and total amount of material
     * used according to the cutting plan.</p>
     *
     * @return Fractional ratio as a measure showing what part of stock material is wasted on trim.
     */
    double getTrimRatio();

    /**
     * <p>Calculate number of active patterns contained in a cutting plan.</p>
     *
     * <p>Pattern is active if it has a roll attached to it and non-zero width (at least one cut
     * will be made when pattern is applied to the roll).</p>
     *
     * @return Number of active patterns.
     */
    int getActivePatternsCount();

    /**
     * <p>Number of unique pattern used in a cutting plan equals to the number of times we need to
     * setup equipment (circular shears, in particular) to cut rolls in a way defined by that
     * cutting plan.</p>
     *
     * @return Number of unique cutting patterns used in a plan.
     */
    int getUniquePatternsCount();

    /**
     * <p>Calculate the amount of strip produced according to the plan, find unfulfilled orders and
     * compare the missing amount of strip with the amount specified in problem definition.</p>
     *
     * @return Average underproduction defined as fractional ratio.
     */
    double getAverageUnderProductionRatio();

    /**
     * <p>Calculate the amount of strip produced according to the plan, find unfulfilled orders and
     * compare the missing amount of strip with the amount specified in problem definition. Maximum
     * underproduction found is returned in a form of a fractional ratio.</p>
     *
     * @return Maximum underproduction defined as fractional ratio.
     */
    public double getMaximumUnderProductionRatio();

    /**
     * <p>Calculate the excessive amount of strip produced according to the plan and compare it
     * with the amount specified in problem definition.</p>
     *
     * @return Average overproduction defined as fractional ratio.
     */
    double getAverageOverProductionRatio();

    /**
     * <p>Calculate the excessive amount of strip produced according to the plan and compare it
     * with the amount specified in problem definition. Maximum overproduction found is returned in
     * a form of a fractional ratio.</p>
     *
     * @return Maximum overproduction defined as fractional ratio.
     */
    public double getMaximumOverProductionRatio();

}
