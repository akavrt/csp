package com.akavrt.csp.solver;

import com.akavrt.csp.core.Solution;

import java.util.List;

/**
 * User: akavrt
 * Date: 08.03.13
 * Time: 13:57
 */
public interface Algorithm {
    List<Solution> execute(ExecutionContext context);
}
