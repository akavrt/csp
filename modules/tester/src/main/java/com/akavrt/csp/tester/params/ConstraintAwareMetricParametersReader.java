package com.akavrt.csp.tester.params;

import com.akavrt.csp.metrics.ConstraintAwareMetricParameters;

/**
 * User: akavrt
 * Date: 20.04.13
 * Time: 22:17
 */
public class ConstraintAwareMetricParametersReader extends
        AbstractParametersReader<ConstraintAwareMetricParameters> {

    @Override
    protected ConstraintAwareMetricParameters createParameterSet() {
        return new ConstraintAwareMetricParameters();
    }
}
