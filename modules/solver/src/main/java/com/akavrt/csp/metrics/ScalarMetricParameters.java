package com.akavrt.csp.metrics;

/**
 * <p>Parameters used with objective function obtained through linear scalarization.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ScalarMetricParameters {
    private static final double DEFAULT_TRIM_FACTOR = 0.33;
    private static final double DEFAULT_PATTERNS_FACTOR = 0.33;
    private static final double DEFAULT_PRODUCTION_FACTOR = 0.33;

    private double trimFactor = DEFAULT_TRIM_FACTOR;
    private double patternsFactor = DEFAULT_PATTERNS_FACTOR;
    private double productionFactor = DEFAULT_PRODUCTION_FACTOR;

    public double getTrimFactor() {
        return trimFactor;
    }

    public void setTrimFactor(double trimFactor) {
        this.trimFactor = trimFactor;
    }

    public double getPatternsFactor() {
        return patternsFactor;
    }

    public void setPatternsFactor(double patternsFactor) {
        this.patternsFactor = patternsFactor;
    }

    public double getProductionFactor() {
        return productionFactor;
    }

    public void setProductionFactor(double productionFactor) {
        this.productionFactor = productionFactor;
    }

}
