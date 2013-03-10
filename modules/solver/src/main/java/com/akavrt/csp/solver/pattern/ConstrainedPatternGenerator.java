package com.akavrt.csp.solver.pattern;

import java.util.Arrays;
import java.util.Random;

/**
 * <p>Modification of the pattern generation procedure proposed by Vahrenkamp. Constraint on the
 * maximum number of cuts allowed within one pattern can be imposed.</p>
 *
 * <p>This implementation is not thread safe.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ConstrainedPatternGenerator implements PatternGenerator {
    private final double[] widths;
    private final int allowedCutsNumber;
    private final int allowedTrialsCount;
    private final Random rGen;
    private int[] currentPattern;
    private int[] bestPattern;
    private double unusedWidth;

    public ConstrainedPatternGenerator(double[] orderWidths, int trials) {
        this(orderWidths, 0, trials);
    }

    public ConstrainedPatternGenerator(double[] orderWidths, int cuts, int trials) {
        this.widths = orderWidths;
        this.allowedCutsNumber = cuts;
        this.allowedTrialsCount = trials;

        rGen = new Random();
        currentPattern = new int[orderWidths.length];
        bestPattern = new int[orderWidths.length];
    }

    @Override
    public int[] generate(double rollWidth, int[] demand, double allowedTrimRatio) {
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
        while (trialCounter < allowedTrialsCount && allowedTrim > bestTrim);

        return bestPattern.clone();
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
