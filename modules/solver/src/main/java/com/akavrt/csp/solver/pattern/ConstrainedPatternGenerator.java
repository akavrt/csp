package com.akavrt.csp.solver.pattern;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * <p>Modification of the pattern generation procedure proposed by Vahrenkamp in this
 * <a href="http://dx.doi.org/10.1016/0377-2217(95)00198-0">paper</a>. Constraint on the maximum
 * number of cuts allowed within one pattern can be imposed.</p>
 *
 * <p>This implementation is not thread safe.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ConstrainedPatternGenerator implements PatternGenerator {
    private final Random rGen;
    private double[] widths;
    private int allowedCutsNumber;
    private PatternGeneratorParameters params;
    private int[] currentPattern;
    private int[] bestPattern;
    private double unusedWidth;

    /**
     * <p>Create instance of constrained pattern generator with default set of parameters.</p>
     */
    public ConstrainedPatternGenerator() {
        this(null, new PatternGeneratorParameters());
    }

    /**
     * <p>Create instance of constrained pattern generator configured with a set of parameters
     * provided.</p>
     *
     * @param params Parameters of pattern generator.
     */
    public ConstrainedPatternGenerator(PatternGeneratorParameters params) {
        this(null, params);
    }

    /**
     * <p>Create instance of constrained pattern generator with default set of parameters tied to
     * specific problem.</p>
     *
     * @param problem Problem used to retrieve width of each order and set of constraints.
     */
    public ConstrainedPatternGenerator(Problem problem) {
        this(problem, new PatternGeneratorParameters());
    }

    /**
     * <p>Create instance of constrained pattern generator configured with a set of parameters
     * provided tied to specific problem.</p>
     *
     * @param problem Problem used to retrieve width of each order and set of constraints.
     * @param params  Parameters of pattern generator.
     */
    public ConstrainedPatternGenerator(Problem problem, PatternGeneratorParameters params) {
        if (problem != null) {
            setOrders(problem.getOrders());
            setAllowedCutsNumber(problem.getAllowedCutsNumber());
        }

        this.params = params;
        rGen = new Random();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PatternGeneratorParameters getParameters() {
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Problem problem) {
        setOrders(problem.getOrders());
        setAllowedCutsNumber(problem.getAllowedCutsNumber());
    }

    /**
     * <p>Information about orders enumerated within problem definition is used in pattern
     * generation process.</p>
     *
     * @param orders The list of orders defined within problem.
     */
    private void setOrders(List<Order> orders) {
        widths = new double[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            widths[i] = orders.get(i).getWidth();
        }

        currentPattern = new int[widths.length];
        bestPattern = new int[widths.length];
    }

    /**
     * <p>Set the upper bound for total number of cuts allowed within one pattern. Use zero if
     * constraint isn't used.</p>
     *
     * @param allowedCutsNumber The maximum number of cuts allowed within one pattern or zero if
     *                          constraint isn't used.
     */
    private void setAllowedCutsNumber(int allowedCutsNumber) {
        this.allowedCutsNumber = allowedCutsNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] generate(double rollWidth, int[] demand, double allowedTrimRatio) {
        if (widths == null) {
            return null;
        }

        int totalItems = 0;
        double minUnfulfilledWidth = 0;
        for (int i = 0; i < widths.length; i++) {
            totalItems += demand[i];

            if ((minUnfulfilledWidth == 0 || widths[i] < minUnfulfilledWidth) && demand[i] > 0) {
                minUnfulfilledWidth = widths[i];
            }
        }

        if (minUnfulfilledWidth == 0 || minUnfulfilledWidth > rollWidth || totalItems == 0) {
            return null;
        }

        // check whether simple greedy placement is possible
        double totalWidth = 0;
        for (int i = 0; i < widths.length; i++) {
            totalWidth += widths[i] * demand[i];
        }

        if (totalWidth <= rollWidth) {
            // use greedy placement
            greedyPlacement(demand);
        } else {
            // use randomized generation procedure
            int trialCounter = 0;
            double bestTrim = rollWidth;
            double allowedTrim = rollWidth * allowedTrimRatio;

            do {
                trial(rollWidth, demand, totalItems);

                // if new pattern is better than the current best,
                // replace latter one with new pattern
                if (unusedWidth < bestTrim) {
                    bestTrim = unusedWidth;
                    System.arraycopy(currentPattern, 0, bestPattern, 0, bestPattern.length);
                }

                trialCounter++;
            }
            while (trialCounter < params.getGenerationTrialsLimit() && allowedTrim > bestTrim);
        }

        return bestPattern.clone();
    }

    private void greedyPlacement(int[] demand) {
        Arrays.fill(bestPattern, 0);
        int addedItems = 0;
        boolean isCutsUnconstrained = allowedCutsNumber == 0;
        int i = widths.length - 1;
        while (i >= 0 && (isCutsUnconstrained || addedItems < allowedCutsNumber)) {
            int j = 1;
            while (j <= demand[i] && (isCutsUnconstrained || addedItems < allowedCutsNumber)) {
                bestPattern[i]++;
                addedItems++;

                j++;
            }

            i--;
        }
    }

    private void trial(double rollWidth, int[] demand, int totalItems) {
        // reset pattern
        Arrays.fill(currentPattern, 0);

        int addedItems = 0;
        unusedWidth = rollWidth;
        boolean cutCanBeMade = true;

        // generate pattern
        do {
            int index = rGen.nextInt(widths.length);
            if (widths[index] <= unusedWidth && demand[index] - currentPattern[index] > 0) {
                currentPattern[index]++;
                unusedWidth -= widths[index];
                addedItems++;

                // pattern was changed,
                // check if any unfulfilled order can be cut from remained width
                cutCanBeMade = false;
                for (int i = 0; i < widths.length; i++) {
                    if (demand[i] - currentPattern[i] > 0 && widths[i] <= unusedWidth) {
                        cutCanBeMade = true;
                        break;
                    }
                }
            }

        }
        while (cutCanBeMade && (allowedCutsNumber == 0 || addedItems < allowedCutsNumber) &&
                totalItems - addedItems > 0);
    }
}
