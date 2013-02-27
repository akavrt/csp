package com.akavrt.csp.core;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * <p>Typical MSS CSP (Multiple Stock-Size Cutting Stock Problem, for detailed explanation see
 * <a href="http://dx.doi.org/10.1016/j.ejor.2005.12.047">G. Wascher et al., 2007</a>) is defined
 * by a two lists used for explicit enumeration of both stock (raw material) and orders (finished
 * product) supplemented with a set of additional constraints (if needed).</p>
 *
 * <p>In this class we are introducing these lists. Also we are adding one important constraint to
 * limit the total number of cuts in a single cutting pattern.<p/>
 *
 * <p>In previous implementation metadata was stored along with orders and stock.
 * Here we will try to strip classes from such data and keep them lightweight.<p/>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Problem {
    private ImmutableList<Order> orders;
    private ImmutableList<Roll> rolls;
    private int allowedCutsNumber;

    /**
     * <p>Universal constructor which can be used to create problems with constraint imposed on
     * maximum number of cuts allowed within one pattern.</p>
     *
     * @param orders            The list of orders.
     * @param rolls             The list of rolls.
     * @param allowedCutsNumber The maximum number of cuts allowed within one pattern or zero
     *                          if constraint isn't used.
     */
    public Problem(List<Order> orders, List<Roll> rolls, int allowedCutsNumber) {
        this.orders = ImmutableList.copyOf(orders);
        this.rolls = ImmutableList.copyOf(rolls);
        this.allowedCutsNumber = allowedCutsNumber;
    }

    /**
     * <p>Constructor for creation of problems with no additional constraints.</p>
     *
     * @param orders The list of orders.
     * @param rolls  The list of rolls.
     */
    public Problem(List<Order> orders, List<Roll> rolls) {
        this(orders, rolls, 0);
    }

    /**
     * <p>Immutable list of orders, add operation is not supported.</p>
     *
     * @return The list of orders or null if no orders was previously set.
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * <p>Immutable list of rolls presenting stock, add operation is not supported.</p>
     *
     * @return The list of rolls or null if no rolls was previously set.
     */
    public List<Roll> getRolls() {
        return rolls;
    }

    /**
     * <p>Inclusive upper bound for total number of cuts allowed within one pattern.
     * Zero is treated as unconstrained number of cuts.</p>
     *
     * @return The maximum number of cuts allowed within one pattern or zero if no constraint was
     *         set.
     */
    public int getAllowedCutsNumber() {
        return allowedCutsNumber;
    }

    /**
     * <p>Check whether constraint for total number of cuts allowed within one pattern is
     * active.</p>
     *
     * @return true if constraint is active, false otherwise.
     */
    public boolean isCutsNumberResctricted() {
        return allowedCutsNumber > 0;
    }

    // TODO extend this builder to support real materials with physical properties like
    // thickness, density, etc.
    public static class Builder {
        private List<Order> orders;
        private List<Roll> rolls;
        private int allowedCutsNumber;

        /**
         * <p>Set the list of orders.</p>
         *
         * @param orders The list of orders.
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setOrders(List<Order> orders) {
            this.orders = orders;
            return this;
        }

        /**
         * <p>Set the stock defined as a list of rolls.</p>
         *
         * @param rolls The list of rolls.
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setRolls(List<Roll> rolls) {
            this.rolls = rolls;
            return this;
        }

        /**
         * <p>Set the upper bound for total number of cuts allowed within one pattern.
         * Use zero if constraint isn't used.</p>
         *
         * @param allowedCutsNumber The maximum number of cuts allowed within one pattern or zero
         *                          if constraint isn't used.
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setAllowedCutsNumber(int allowedCutsNumber) {
            this.allowedCutsNumber = allowedCutsNumber;
            return this;
        }

        /**
         * <p>Creates a Problem with the arguments supplied to this builder.<p/>
         */
        public Problem build() {
            return new Problem(orders, rolls, allowedCutsNumber);
        }

    }
}
