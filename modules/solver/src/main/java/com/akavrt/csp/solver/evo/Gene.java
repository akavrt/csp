package com.akavrt.csp.solver.evo;

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
 * <p>All implemented evolutionary algorithms are based on a pretty straightforward representation
 * scheme: genes corresponds to cutting patterns with rolls attached to them (see Pattern), while
 * chromosomes corresponds to cutting plans (see Solution) - i.e. no additional coding is used.</p>
 *
 * <p>The main benefit from introduction of a separate Gene class (Pattern can be used to represent
 * genes with some minor changes) is substantial speed up when doing different checks and metric
 * evaluations.</p>
 *
 * <p>Current representation of a cutting pattern employed in Gene is based on a fixed order of
 * elements within list of orders provided by the problem. Exactly as in classical ILP formal model
 * of CSP we can use simple arrays of fixed length containing integer values to represent cutting
 * patterns in a consistent way. This eliminates the need to deal with unique order ids and makes
 * enumeration of cuts associated with different orders a whole lot easier.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Gene {
    private int[] pattern;
    private Roll roll;

    /**
     * <p>Creates an instance of Gene representing cutting pattern with roll attached to it.</p>
     *
     * @param pattern Cutting pattern defined by array of integer multipliers. The value of each
     *                multiplier equals to the number of times corresponding order must be cut from
     *                the roll.
     * @param roll    Roll attached to the pattern.
     */
    public Gene(int[] pattern, Roll roll) {
        this.pattern = pattern;
        this.roll = roll;
    }

    /**
     * <p>Converts an instance of Pattern into new instance of Gene.</p>
     *
     * @param solutionPattern Pattern to be converted into gene.
     */
    public Gene(Pattern solutionPattern) {
        pattern = new int[solutionPattern.getCuts().size()];
        for (int i = 0; i < pattern.length; i++) {
            pattern[i] = solutionPattern.getCuts().get(i).getQuantity();
        }

        roll = solutionPattern.getRoll();
    }

    /**
     * <p>Copy constructor.</p>
     *
     * @param gene Gene to be copied.
     */
    public Gene(Gene gene) {
        this(gene.pattern.clone(), gene.roll);
    }

    /**
     * <p>Converts gene into equivalent instance of Pattern.</p>
     *
     * @param context Context provides access to the problem definition.
     * @return An instance of Pattern equivalent to the current gene.
     */
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

    /**
     * <p>Returns array representing cutting pattern.</p>
     *
     * @return Pattern represented as an array of integers.
     */
    public int[] getPattern() {
        return pattern;
    }

    /**
     * <p>Sets cutting pattern.</p>
     *
     * @param pattern Pattern represented as an array of integers.
     */
    public void setPattern(int[] pattern) {
        this.pattern = pattern;
    }

    /**
     * <p>Pattern will be applied to this roll when cutting plan is executed.</p>
     *
     * @return The roll attached to pattern or null.
     */
    public Roll getRoll() {
        return roll;
    }

    /**
     * <p>Attach one of the stock rolls to the pattern.</p>
     *
     * @param roll The roll being attached to the pattern.
     */
    public void setRoll(Roll roll) {
        this.roll = roll;
    }

    /**
     * <p>Total width of the roll being used to cut orders when pattern is applied to it. Measured
     * in abstract units.</p>
     *
     * @param context Context provides access to the problem definition.
     * @return The total width of the orders being cut from the roll according to the pattern.
     */
    public double getWidth(EvolutionaryExecutionContext context) {
        if (pattern == null) {
            return 0;
        }

        double totalWidth = 0;
        for (int i = 0; i < pattern.length; i++) {
            totalWidth += pattern[i] * context.getOrderWidth(i);
        }

        return totalWidth;
    }

    /**
     * <p>Wasted width of the roll which will be left unused after cutting. Measured in abstract
     * units.</p>
     *
     * @param context Context provides access to the problem definition.
     * @return The unused width of the roll.
     */
    public double getTrimWidth(EvolutionaryExecutionContext context) {
        if (roll == null || pattern == null) {
            return 0;
        }

        return roll.getWidth() - getWidth(context);
    }

    /**
     * <p>Wasted area of the roll which will be left unused after cutting. Measured in abstract
     * square units.</p>
     *
     * @param context Context provides access to the problem definition.
     * @return The unused area of the roll.
     */
    public double getTrimArea(EvolutionaryExecutionContext context) {
        if (roll == null || pattern == null) {
            return 0;
        }

        return roll.getLength() * getTrimWidth(context);
    }

    /**
     * <p>Calculate the length of the finished product we will get for specific order after
     * executing pattern. Measured in abstract units.</p>
     *
     * @param index Position of the order in the list of orders provided by problem definition.
     * @return The length of produced strip.
     */
    public double getProductionLengthForOrder(int index) {
        if (roll == null || pattern == null) {
            return 0;
        }

        return roll.getLength() * pattern[index];
    }

    /**
     * <p>Calculate total number of cuts defined within pattern.</p>
     *
     * @return Total number of cuts.
     */
    private int getTotalNumberOfCuts() {
        int totalCuts = 0;

        for (int cuts : pattern) {
            totalCuts += cuts;
        }

        return totalCuts;
    }

    /**
     * <p>Check whether cutting pattern is feasible or not. Infeasible patterns can't be cut from
     * rolls due to exceeded width or technical limitation.</p>
     *
     * @param context Context provides access to the problem definition.
     * @return true if pattern is valid, false otherwise.
     */
    public boolean isFeasible(EvolutionaryExecutionContext context) {
        int allowedCutsNumber = context.getProblem().getAllowedCutsNumber();
        boolean isCutsValid = allowedCutsNumber == 0 || getTotalNumberOfCuts() <= allowedCutsNumber;

        boolean isPatternWidthValid = true;
        if (roll != null) {
            isPatternWidthValid = DoubleMath.fuzzyCompare(getWidth(context), roll.getWidth(),
                                                          Constants.TOLERANCE) <= 0;
        }

        return isCutsValid && isPatternWidthValid;
    }

    /**
     * <p>Utility method which is used to discover repeating patterns in cutting plan.</p>
     *
     * @return The content-based hash code for array of multipliers.
     */
    public int getPatternHashCode() {
        return Arrays.hashCode(pattern);
    }

    /**
     * <p>Calculation of hash code takes into account cutting pattern and textual id of the roll
     * (only if there is any roll attached to a pattern).</p>
     *
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(Arrays.hashCode(pattern), roll == null ? null : roll.getId());
    }

}
