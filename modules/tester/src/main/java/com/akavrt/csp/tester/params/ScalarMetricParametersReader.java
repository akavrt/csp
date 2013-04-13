package com.akavrt.csp.tester.params;

import com.akavrt.csp.metrics.ScalarMetricParameters;

/**
 * User: akavrt
 * Date: 13.04.13
 * Time: 19:10
 */
public class ScalarMetricParametersReader extends
        AbstractParametersReader<ScalarMetricParameters> {

    @Override
    protected String getRootElementName() {
        return "scalar";
    }

    @Override
    protected ScalarMetricParameters createParameterSet() {
        return new ScalarMetricParameters();
    }
}
