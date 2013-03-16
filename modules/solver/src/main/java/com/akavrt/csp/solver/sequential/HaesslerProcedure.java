package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.metadata.SolutionMetadata;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.ExecutionContext;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * <p>Reference implementation of Haessler's sequential heuristic procedure. For detailed
 * explanation of the method check out these publications:
 *
 * <ol>
 * <li>
 *     <a href="http://dx.doi.org/10.1287/opre.23.3.483">[R. W. Haessler, 1975];</a>
 * </li>
 * <li>
 *     <a href="http://dx.doi.org/10.1080/05695557808975185">[R. W. Haessler, 1978].</a>
 * </li>
 * </ol>
 * </p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class HaesslerProcedure implements Algorithm {
    public static final String METHOD_NAME = "Haessler's sequential heuristic procedure";
    private HaesslerProcedureParameters params;
    private PatternGenerator patternGenerator;
    private RollManager rollManager;
    private OrderManager orderManager;
    private Solution currentSolution;

    /**
     * <p>Create instance of Algorithm implementing Haessler's sequential heuristic procedure.
     * Default set of parameters will be used.</p>
     */
    public HaesslerProcedure() {
        this(new HaesslerProcedureParameters());
    }

    /**
     * <p>Create instance of Algorithm implementing Haessler's sequential heuristic procedure.
     * Algorithm is configured with a set of parameters provided.</p>
     *
     * @param params Parameters of sequential heuristic procedure.
     */
    public HaesslerProcedure(HaesslerProcedureParameters params) {
        this.params = params;
    }

    /**
     * Current set of parameters used by the method. It could be saved to XML if needed.
     */
    public HaesslerProcedureParameters getParameters() {
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return METHOD_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Solution> execute(ExecutionContext context) {
        if (context.getPatternGenerator() == null) {
            return null;
        }

        Problem problem = context.getProblem();
        patternGenerator = context.getPatternGenerator();

        rollManager = new RollManager(problem.getRolls());
        orderManager = new OrderManager(problem.getOrders());

        currentSolution = new Solution();
        while (!orderManager.isOrdersFulfilled() && rollManager.size() > 0) {
            trimStep();
        }

        // prepare metadata
        currentSolution.setMetadata(prepareMetadata());

        List<Solution> solutions = Lists.newArrayList();
        solutions.add(currentSolution);

        return solutions;
    }

    private boolean trimStep() {
        double allowedTrimRatio = 0;
        boolean isPatternFound = false;
        while (allowedTrimRatio < 1 && !isPatternFound) {
            isPatternFound = patternUsageStep(allowedTrimRatio);

            // relax requirements for trim loss
            allowedTrimRatio += params.getTrimRatioRelaxDelta();
        }

        return isPatternFound;
    }

    private boolean patternUsageStep(double allowedTrimRatio) {
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

        boolean isPatternFound = false;
        while (patternUsage > 0 && !isPatternFound) {
            isPatternFound = rollGroupStep(allowedTrimRatio, patternUsage);

            // relax requirements for pattern usage
            patternUsage -= params.getPatternUsageRelaxDelta();
        }

        return isPatternFound;
    }

    private boolean rollGroupStep(double allowedTrimRatio, int patternUsage) {
        boolean isPatternFound = false;

        int anchorIndex = 0;
        while (anchorIndex < rollManager.size() && !isPatternFound) {
            // check whether we can find group of sufficient size
            if (rollManager.getGroupSize(anchorIndex, allowedTrimRatio) >= patternUsage) {
                // if suitable group exists, try to generate pattern
                isPatternFound = patternGeneration(allowedTrimRatio, anchorIndex);
            }

            anchorIndex++;
        }

        return isPatternFound;
    }

    private boolean patternGeneration(double allowedTrimRatio, int anchorIndex) {
        boolean isPatternFound = false;

        // let's process current group and try to generate suitable pattern
        List<Roll> group = rollManager.getGroup(anchorIndex, allowedTrimRatio);
        int[] demand = orderManager.calcGroupDemand(group);

        // rolls are sorted in ascending order of width
        // the narrowest roll is always the first roll in the group
        double baseRollWidth = group.get(0).getWidth();

        int[] pattern = patternGenerator.generate(baseRollWidth, demand, allowedTrimRatio);
        if (pattern != null
                && orderManager.getPatternTrimRatio(group, pattern) <= allowedTrimRatio) {
            // pattern search succeeded

            // adjust production and stock
            orderManager.updateProduction(group, pattern);
            rollManager.updateStock(group);

            // add patterns to the partial solution
            for (Roll roll : group) {
                Pattern solutionPattern = orderManager.prepareSolutionPattern(pattern);
                solutionPattern.setRoll(roll);

                currentSolution.addPattern(solutionPattern);
            }

            isPatternFound = true;
        }

        return isPatternFound;
    }

    private SolutionMetadata prepareMetadata() {
        SolutionMetadata metadata = new SolutionMetadata();
        metadata.setDescription("Solution obtained with " + getName() + ".");
        metadata.setDate(new Date());
        metadata.addParameters(getParameters());
        metadata.addParameters(patternGenerator.getParameters());

        return metadata;
    }
}