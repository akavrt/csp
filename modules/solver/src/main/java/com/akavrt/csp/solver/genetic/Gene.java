package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.solver.ExecutionContext;
import com.akavrt.csp.utils.Constants;
import com.google.common.base.Objects;
import com.google.common.math.DoubleMath;

import java.util.Arrays;
import java.util.List;

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

    public Gene(Gene gene) {
        this(gene.pattern.clone(), gene.roll);
    }

    public Pattern convert(ExecutionContext context) {
        Pattern solutionPattern = new Pattern(context.getProblem());

        // add cuts
        List<Order> orders = context.getProblem().getOrders();
        for (int i = 0; i < orders.size(); i++) {
            solutionPattern.setCut(orders.get(i), pattern[i]);
        }

        // set roll
        solutionPattern.setRoll(roll);

        return solutionPattern;
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

    public double getWidth(GeneticExecutionContext context) {
        if (pattern == null) {
            return 0;
        }

        double totalWidth = 0;
        for (int i = 0; i < pattern.length; i++) {
            totalWidth += pattern[i] * context.getOrderWidth(i);
        }

        return totalWidth;
    }

    public double getTrimWidth(GeneticExecutionContext context) {
        if (roll == null || pattern == null) {
            return 0;
        }

        return roll.getWidth() - getWidth(context);
    }

    public double getTrimArea(GeneticExecutionContext context) {
        if (roll == null || pattern == null) {
            return 0;
        }

        return roll.getLength() * getTrimWidth(context);
    }

    public double getProductionLengthForOrder(int index) {
        if (roll == null || pattern == null) {
            return 0;
        }

        return roll.getLength() * pattern[index];
    }

    public int getTotalNumberOfCuts() {
        int totalCuts = 0;

        for (int cuts : pattern) {
            totalCuts += cuts;
        }

        return totalCuts;
    }

    public boolean isValid(GeneticExecutionContext context) {
        int allowedCutsNumber = context.getProblem().getAllowedCutsNumber();
        boolean isCutsValid = allowedCutsNumber == 0 || getTotalNumberOfCuts() <= allowedCutsNumber;

        boolean isPatternWidthValid = true;
        if (roll != null) {
            isPatternWidthValid = DoubleMath.fuzzyCompare(getWidth(context), roll.getWidth(),
                                                          Constants.TOLERANCE) <= 0;
        }

        return isCutsValid && isPatternWidthValid;
    }

    public int getPatternHashCode() {
        return Arrays.hashCode(pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Arrays.hashCode(pattern), roll == null ? null : roll.getId());
    }

}
