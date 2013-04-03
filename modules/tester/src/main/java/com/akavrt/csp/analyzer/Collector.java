package com.akavrt.csp.analyzer;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;

/**
 * User: akavrt
 * Date: 23.03.13
 * Time: 00:35
 */
public interface Collector {
    void collect(Solution solution);
    void collect(Solution solution, long millis);
    void clear();
    void process(Problem problem);
    boolean isGlobal();
}
