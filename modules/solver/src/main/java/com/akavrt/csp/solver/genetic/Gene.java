package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Roll;

import java.util.Arrays;

/**
 * User: akavrt
 * Date: 27.03.13
 * Time: 16:01
 */
public class Gene {
    private int[] pattern;
    private Roll roll;

    public Gene(int[] pattern, Roll roll) {
        this.pattern = pattern;
        this.roll = roll;
    }

    public Gene(Pattern solutionPattern) {
        pattern = new int[solutionPattern.getCuts().size()];
        for (int i = 0; i < pattern.length; i++) {
            pattern[i] = solutionPattern.getCuts().get(i).getQuantity();
        }

        roll = solutionPattern.getRoll();
    }

    public int[] getPattern() {
        return pattern;
    }

    public void setPattern(int[] pattern) {
        // TODO implement a mechanism to reset cached values
        // in chromosome's metric provider on pattern change
        this.pattern = pattern;
    }

    public Roll getRoll() {
        return roll;
    }

    public void setRoll(Roll roll) {
        this.roll = roll;
    }

    public double getTrim(GeneticExecutionContext context) {
        if (roll == null || pattern == null) {
            return 0;
        }

        double totalWidth = 0;
        for (int i = 0; i < pattern.length; i++) {
            totalWidth += pattern[i] * context.getOrderWidth(i);
        }

        return roll.getWidth() - totalWidth;
    }

    public double getTrimArea(GeneticExecutionContext context) {
        if (roll == null || pattern == null) {
            return 0;
        }

        return roll.getLength() * getTrim(context);
    }

    public double getProductionLengthForOrder(int index) {
        if (roll == null || pattern == null) {
            return 0;
        }

        return roll.getLength() * pattern[index];
    }

    public int getPatternHashCode() {
        return Arrays.hashCode(pattern);
    }

}
