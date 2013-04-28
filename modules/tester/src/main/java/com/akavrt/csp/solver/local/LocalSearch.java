package com.akavrt.csp.solver.local;

import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.metadata.SolutionMetadata;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.metrics.complex.ScalarMetric;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.ExecutionContext;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.EvolutionaryContext;
import com.akavrt.csp.solver.evo.EvolutionaryExecutionContext;
import com.akavrt.csp.solver.evo.EvolutionaryOperator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.utils.ParameterSet;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * User: akavrt
 * Date: 24.04.13
 * Time: 14:56
 */
public class LocalSearch implements Algorithm {
    private static final Logger LOGGER = LogManager.getLogger(LocalSearch.class);
    private static final String METHOD_NAME = "Local search based on SHP";
    private static final String SHORT_METHOD_NAME = "LS-SHP";
    private final Algorithm constructiveProcedure;
    private final Metric objectiveFunction;
    private final EvolutionaryOperator searchStep;

    public LocalSearch(Algorithm constructiveProcedure, PatternGenerator generator,
                       Metric objectiveFunction) {
        this.constructiveProcedure = constructiveProcedure;
        this.objectiveFunction = objectiveFunction;

        searchStep = new GroupReplacementOperator(generator, objectiveFunction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return METHOD_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ParameterSet> getParameters() {
        // TODO for now only empty list is returned
        // TODO replace with real parameters
        return Lists.newArrayList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Solution> execute(ExecutionContext context) {
        if (context.getProblem() == null) {
            return null;
        }

        EvolutionaryContext geneticContext = new EvolutionaryContext(context);
        Solution solution = search(geneticContext);

        return Lists.newArrayList(solution);
    }

    private Solution search(EvolutionaryExecutionContext context) {
        Solution result = null;
        List<Solution> solutions = constructiveProcedure.execute(context);

        if (solutions != null && solutions.size() > 0 && solutions.get(0) != null) {
            Chromosome best = new Chromosome(context, solutions.get(0));

            Metric traceMetric = new ScalarMetric();

            int step = 0;
            int stuck = 0;
            while (!context.isCancelled() && step < 100 && stuck < 10) {
                if (LOGGER.isInfoEnabled()) {
                    String formatted = String.format("%.3f", traceMetric.evaluate(best));
                    LOGGER.info("Local search step #{}, best is {}", step, formatted);
                }

                Chromosome current = searchStep.apply(best);

                if (objectiveFunction.compare(current, best) > 0) {
                    best = current;
                    stuck = 0;
                } else {
                    LOGGER.info("Local search stuck on step #{}", step);
                    stuck++;
                }

                step++;
            }

            result = best.convert();
            result.setMetadata(prepareMetadata());
        }

        return result;
    }

    private SolutionMetadata prepareMetadata() {
        SolutionMetadata metadata = new SolutionMetadata();
        metadata.setDescription("Solution obtained with " + SHORT_METHOD_NAME + ".");
        metadata.setDate(new Date());
        metadata.setParameters(getParameters());

        return metadata;
    }

}
