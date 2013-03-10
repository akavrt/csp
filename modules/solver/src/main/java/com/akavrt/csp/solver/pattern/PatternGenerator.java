package com.akavrt.csp.solver.pattern;

/**
 * User: akavrt
 * Date: 10.03.13
 * Time: 18:44
 */
public interface PatternGenerator {
    int[] generate(double rollWidth, int[] demand, double allowedTrimRatio);
}
