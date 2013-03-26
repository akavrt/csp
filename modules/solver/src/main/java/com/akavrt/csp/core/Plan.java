package com.akavrt.csp.core;

import com.akavrt.csp.metrics.MetricProvider;

/**
 * <p>This interface is introduced to close the gap between different implementations of cutting
 * plan. Mainly used in metric evaluations.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface Plan {
    /**
     * <p>Return an instance of MetricProvider specific to this implementation of Plan.</p>
     */
    MetricProvider getMetricProvider();
}
