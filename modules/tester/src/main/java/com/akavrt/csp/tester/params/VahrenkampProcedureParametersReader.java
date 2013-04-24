package com.akavrt.csp.tester.params;

import com.akavrt.csp.solver.sequential.VahrenkampProcedureParameters;

/**
 * User: akavrt
 * Date: 24.04.13
 * Time: 19:08
 */
public class VahrenkampProcedureParametersReader extends
        AbstractParametersReader<VahrenkampProcedureParameters> {

    @Override
    protected String getRootElementName() {
        return "sequential";
    }

    @Override
    protected VahrenkampProcedureParameters createParameterSet() {
        return new VahrenkampProcedureParameters();
    }
}

