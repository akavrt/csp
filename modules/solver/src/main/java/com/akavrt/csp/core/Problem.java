package com.akavrt.csp.core;

import com.akavrt.csp.core.metadata.ProblemMetadata;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * <p>In previous implementation metadata was stored along with orders and stock. Here we will try
 * to strip classes from such data and keep them lightweight.<p/>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Problem {
    private ImmutableList<Order> orders;
    private ImmutableList<Roll> rolls;
    private int allowedCutsNumber;
    private ProblemMetadata metadata;

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
    public boolean isCutsNumberRestricted() {
        return allowedCutsNumber > 0;
    }

    /**
     * <p>Provide extended description of the problem.</p>
     *
     * @return Additional information about the problem.
     */
    public ProblemMetadata getMetadata() {
        return metadata;
    }

    /**
     * <p>Set additional information about the problem.</p>
     *
     * @param metadata Additional information about the problem.
     */
    public void setMetadata(ProblemMetadata metadata) {
        this.metadata = metadata;
    }

    public static class Builder {
        private List<Order> orders;
        private List<Roll> rolls;
        private boolean sortOrders;
        private boolean sortRolls;
        private int allowedCutsNumber;
        private ProblemMetadata metadata;

        /**
         * <p>Creates an instance of builder with empty properly initialized list of orders and
         * list
         * of rolls.</p>
         */
        public Builder() {
            orders = new ArrayList<Order>();
            rolls = new ArrayList<Roll>();
        }

        /**
         * <p>Set the list of orders.</p>
         *
         * @param orders The list of orders.
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setOrders(List<Order> orders) {
            this.orders.clear();
            this.orders.addAll(orders);

            return this;
        }

        /**
         * <p>Add order to the list of orders.</p>
         *
         * @param order The added order.
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder addOrder(Order order) {
            orders.add(order);
            return this;
        }

        /**
         * <p>Set the stock defined as a list of rolls.</p>
         *
         * @param rolls The list of rolls.
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setRolls(List<Roll> rolls) {
            this.rolls.clear();
            this.rolls.addAll(rolls);

            return this;
        }

        /**
         * <p>Add roll to the list of rolls.</p>
         *
         * @param roll The added roll.
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder addRoll(Roll roll) {
            rolls.add(roll);
            return this;
        }

        /**
         * <p>Add a group of rolls with the same size.</p>
         *
         * <p>A predefined number of copies of reference roll will be created and added to the
         * problem. The number of added rolls is equal to the size of the group.</p>
         *
         * @param roll     The reference roll.
         * @param quantity Size of the group.
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder addRolls(Roll roll, int quantity) {
            for (int i = 1; i <= quantity; i++) {
                Roll copy = new Roll(roll.getId(), i, roll.getLength(), roll.getWidth());
                copy.setMetadata(roll.getMetadata());

                rolls.add(copy);
            }
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
         * <p>Set additional information about the problem.</p>
         *
         * @param metadata Additional information about the problem.
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setMetadata(ProblemMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * <p>Sort orders in ascending order of width.</p>
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder rearrangeOrders() {
            sortOrders = true;
            return this;
        }

        /**
         * <p>Sort rolls in ascending order of width.</p>
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder rearrangeRolls() {
            sortRolls = true;
            return this;
        }

        /**
         * <p>Create a Problem with the arguments supplied to this builder.<p/>
         */
        public Problem build() {
            if (sortOrders) {
                Collections.sort(orders, new Comparator<Order>() {
                    @Override
                    public int compare(Order lhs, Order rhs) {
                        return lhs.getWidth() < rhs.getWidth() ? -1 :
                                (lhs.getWidth() > rhs.getWidth() ? 1 : 0);
                    }
                });
            }

            if (sortRolls) {
                Collections.sort(rolls, new Comparator<Roll>() {
                    @Override
                    public int compare(Roll lhs, Roll rhs) {
                        return lhs.getWidth() < rhs.getWidth() ? -1 :
                                (lhs.getWidth() > rhs.getWidth() ? 1 : 0);
                    }
                });
            }

            Problem problem = new Problem(orders, rolls, allowedCutsNumber);
            problem.setMetadata(metadata);

            return problem;
        }

    }
}
