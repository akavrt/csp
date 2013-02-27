package com.akavrt.csp.params;

/**
 * <p>Parameters used with objective function obtained through linear scalarization.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ObjectiveFunctionParameters {
    private double trimFactor;
    private double patternsFactor;
    private double productionFactor;

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

    public static class Builder {
        private double trimFactor;
        private double patternsFactor;
        private double productionFactor;

        public Builder setTrimFactor(double trimFactor) {
            this.trimFactor = trimFactor;
            return this;
        }

        public Builder setPatternsFactor(double patternsFactor) {
            this.patternsFactor = patternsFactor;
            return this;
        }

        public Builder setProductionFactor(double productionFactor) {
            this.productionFactor = productionFactor;
            return this;
        }

        public ObjectiveFunctionParameters build() {
            ObjectiveFunctionParameters params = new ObjectiveFunctionParameters();
            params.trimFactor = trimFactor;
            params.patternsFactor = patternsFactor;
            params.productionFactor = productionFactor;

            return params;
        }
    }
}
