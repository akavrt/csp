package com.akavrt.csp.tester.params;

import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;

/**
 * User: akavrt
 * Date: 13.04.13
 * Time: 19:07
 */
public class PatternGeneratorParametersReader extends
        AbstractParametersReader<PatternGeneratorParameters> {

    @Override
    protected String getRootElementName() {
        return "pattern";
    }

    @Override
    protected PatternGeneratorParameters createParameterSet() {
        return new PatternGeneratorParameters();
    }
}
