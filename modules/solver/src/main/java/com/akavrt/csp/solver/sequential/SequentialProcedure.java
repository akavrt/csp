package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.metadata.SolutionMetadata;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.ExecutionContext;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.utils.Utils;
import com.akavrt.csp.xml.XmlCompatible;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * User: akavrt
 * Date: 21.03.13
 * Time: 02:12
 */
public abstract class SequentialProcedure implements Algorithm {
    private final PatternGenerator patternGenerator;
    protected RollManager rollManager;
    protected OrderManager orderManager;

    public SequentialProcedure(PatternGenerator generator) {
        this.patternGenerator = generator;
    }

    protected abstract String getShortMethodName();

    protected abstract XmlCompatible getMethodParams();

    protected abstract Solution search();

    protected abstract Logger getLogger();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Solution> execute(ExecutionContext context) {
        if (patternGenerator == null || context.getProblem() == null) {
            return null;
        }

        Problem problem = context.getProblem();
        patternGenerator.initialize(problem);

        rollManager = new RollManager(problem.getRolls());
        orderManager = new OrderManager(problem.getOrders());

        Solution solution = search();

        // prepare metadata
        solution.setMetadata(prepareMetadata());

        List<Solution> solutions = Lists.newArrayList();
        solutions.add(solution);

        return solutions;
    }

    private SolutionMetadata prepareMetadata() {
        SolutionMetadata metadata = new SolutionMetadata();
        metadata.setDescription("Solution obtained with " + getShortMethodName() + ".");
        metadata.setDate(new Date());
        metadata.addParameters(getMethodParams());
        metadata.addParameters(patternGenerator.getParameters());

        return metadata;
    }

    protected int evaluatePatternUsage(double allowedTrimRatio) {
        // find roll with minimal area
        double minRollArea = rollManager.getMinRollArea();

        // calculate total area of ordered strip yet to be produced
        double ordersArea = orderManager.getUnfulfilledOrdersArea();

        // let's evaluate maximum possible pattern usage
        int patternUsage = (int) Math.ceil(ordersArea / minRollArea);

        // search for a largest group of rolls
        // group clustering is decided based on width
        int maxGroupSize = rollManager.getLargestGroupSize(allowedTrimRatio);

        // adjust expectations
        if (patternUsage > maxGroupSize) {
            patternUsage = maxGroupSize;
        }

        return patternUsage;
    }

    protected BuildingBlock rollGroupStep(double allowedTrimRatio, int patternUsage) {
        BuildingBlock block = null;

        int anchorIndex = 0;
        while (anchorIndex < rollManager.size() && block == null) {
            // check whether we can find group of sufficient size
            if (rollManager.getGroupSize(anchorIndex, allowedTrimRatio) >= patternUsage) {
                // if suitable group exists, try to generate pattern
                block = patternGeneration(allowedTrimRatio, patternUsage, anchorIndex);
            }

            anchorIndex++;
        }

        return block;
    }

    protected BuildingBlock patternGeneration(double allowedTrimRatio,
                                              int patternUsage,
                                              int anchorIndex) {
        BuildingBlock block = null;

        // let's process current group and try to generate suitable pattern
        List<Roll> group = rollManager.getGroup(anchorIndex, allowedTrimRatio, patternUsage);
        int[] demand = orderManager.calcGroupDemand(group);

        // rolls are sorted in ascending order of width
        // the narrowest roll is always the first roll in the group
        double baseRollWidth = group.get(0).getWidth();

        int[] pattern = patternGenerator.generate(baseRollWidth, demand, allowedTrimRatio);
        if (pattern != null
                && orderManager.getPatternTrimRatio(group, pattern) <= allowedTrimRatio) {
            // pattern search succeeded
            block = new BuildingBlock(pattern, group);

            if (getLogger().isDebugEnabled()) {
                debugPatternGeneration(anchorIndex, demand, block);
            }
        }

        return block;
    }

    private void debugPatternGeneration(int anchorIndex, int[] demand, BuildingBlock block) {
        Roll anchorRoll = block.group.get(0);
        double trimRatio = orderManager.getPatternTrimRatio(block.group, block.pattern);

        getLogger().debug("      ANC_IDX: %d -> base roll: '%s', #%d",
                          anchorIndex, anchorRoll.getId(), anchorRoll.getInternalId());
        getLogger().debug(Utils.convertIntArrayToString("       demand", demand));
        getLogger().debug(Utils.convertIntArrayToString("      pattern", block.pattern));
        getLogger().debug("      TL ratio = %.4f", trimRatio);
    }

    protected static class BuildingBlock {
        public final int[] pattern;
        public final List<Roll> group;

        public BuildingBlock(int[] pattern, List<Roll> group) {
            this.pattern = pattern;
            this.group = group;
        }
    }

}
