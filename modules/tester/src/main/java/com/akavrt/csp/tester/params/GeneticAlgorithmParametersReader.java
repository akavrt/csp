package com.akavrt.csp.tester.params;

import com.akavrt.csp.solver.genetic.GeneticAlgorithmParameters;

/**
 * User: akavrt
 * Date: 13.04.13
 * Time: 18:39
 */
public class GeneticAlgorithmParametersReader extends
        AbstractParametersReader<GeneticAlgorithmParameters> {

    @Override
    protected String getRootElementName() {
        return "genetic";
    }

    @Override
    protected GeneticAlgorithmParameters createParameterSet() {
        return new GeneticAlgorithmParameters();
    }
}
