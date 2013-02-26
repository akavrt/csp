package com.akavrt.csp.core;

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
    private List<Order> orders;
    private List<Roll> rolls;
    private int allowedCutsNumber;

    /**
     * <p>List of orders.</p>
     *
     * @return the list of orders or null if no orders was previously set
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * <p>Set the list of orders.</p>
     *
     * @param orders The list of orders
     */
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * <p>List of rolls presenting stock.</p>
     *
     * @return the list of rolls or null if no rolls was previously set
     */
    public List<Roll> getRolls() {
        return rolls;
    }

    /**
     * <p>Set the stock for this problem defined as a list of rolls.</p>
     *
     * @param rolls The list of rolls
     */
    public void setRolls(List<Roll> rolls) {
        this.rolls = rolls;
    }

    /**
     * <p>Inclusive upper bound for total number of cuts allowed within one pattern.
     * Zero is treated as unconstrained number of cuts.</p>
     *
     * @return the maximum number of cuts allowed within one pattern or zero if no constraint was
     *         set
     */
    public int getAllowedCutsNumber() {
        return allowedCutsNumber;
    }

    /**
     * <p>Set the upper bound for total number of cuts allowed within one pattern.
     * Use zero if constraint isn't used.</p>
     *
     * @param allowedCutsNumber the maximum number of cuts allowed within one pattern or zero if
     *                          constraint isn't used
     */
    public void setAllowedCutsNumber(int allowedCutsNumber) {
        this.allowedCutsNumber = allowedCutsNumber;
    }

    /**
     * <p>Check whether constraint for total number of cuts allowed within one pattern is
     * active.</p>
     *
     * @return true if constraint is active, false otherwise
     */
    public boolean isCutsNumberResctricted() {
        return allowedCutsNumber > 0;
    }
}
