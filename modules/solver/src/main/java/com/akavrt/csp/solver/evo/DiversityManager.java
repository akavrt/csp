package com.akavrt.csp.solver.evo;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * User: akavrt
 * Date: 27.04.13
 * Time: 01:22
 */
public class DiversityManager {
    private static final int RETRY_BOUND = 10;
    private final Set<Integer> hashes;
    private int retryCount;

    public DiversityManager() {
        hashes = Sets.newHashSet();
    }

    public void reset() {
        hashes.clear();
        retryCount = 0;
    }

    public void add(Chromosome chromosome) {
        hashes.add(chromosome.hashCode());
    }

    public boolean isAdded(Chromosome chromosome) {
        return hashes.contains(chromosome.hashCode());
    }

    public int getMeasure() {
        return hashes.size();
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void incRetryCount() {
        retryCount++;
    }

    public int getRetryBound() {
        return RETRY_BOUND;
    }

}
