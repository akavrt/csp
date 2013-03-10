package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.ExecutionContext;

import java.util.List;

/**
 * User: akavrt
 * Date: 08.03.13
 * Time: 13:59
 */
public class HaesslerShp implements Algorithm {
    private Problem problem;

    public List<Solution> execute(ExecutionContext context) {
        problem = context.getProblem();

        return null;
    }

}
