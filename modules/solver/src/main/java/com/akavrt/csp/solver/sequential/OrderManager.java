package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Roll;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * <p>Problem provides immutable list of orders. Given that order of elements in that list is fixed
 * and can't be changed during execution of the optimization routine, we can use simplified
 * definition of the cutting pattern represented as a gene array of integer multipliers. Value
 * of each multiplier equals to the number of times corresponding order must be cut from the
 * roll.</p>
 *
 * <p>Order manager helps to track production of the strip and calculate different ratios used by
 * sequential procedure.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class OrderManager {
    private final List<MutableOrder> orders;

    /**
     * <p>Create manager and associate it with initial list of orders, enumerated within the
     * problem definition.</p>
     *
     * @param problemOrders The list of the orders.
     */
    public OrderManager(List<Order> problemOrders) {
        orders = Lists.newArrayList();
        for (Order problemOrder : problemOrders) {
            orders.add(new MutableOrder(problemOrder));
        }
    }

    /**
     * <p>Calculate total area of the strip, which is need to be produced to meet demand.</p>
     *
     * @return Total area of the missing strip.
     */
    public double getUnfulfilledOrdersArea() {
        double area = 0;
        for (MutableOrder order : orders) {
            area += order.getUnfulfilledArea();
        }

        return area;
    }

    /**
     * <p>Calculate demand constraints for pattern generation based on the total length of rolls
     * combined in a group and unfulfilled length of each order.</p>
     *
     * @param group List of rolls to be cut with same pattern.
     * @return An array of integer multipliers, each multiplier defines upper bound for a number of
     *         times corresponding order could be cut from each roll in a group.
     */
    public int[] calcGroupDemand(List<Roll> group) {
        double groupLength = 0;
        for (Roll roll : group) {
            groupLength += roll.getLength();
        }

        if (groupLength == 0) {
            return null;
        }

        int[] demand = new int[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            if (!orders.get(i).isFulfilled()) {
                double unfulfilledLength = orders.get(i).getUnfulfilledLength();
                double ratio = unfulfilledLength / groupLength;

                // rounding multiplier up is allowed only if size of the group equals to one
                // this rule is used to implicitly control overproduction
                if (group.size() > 1) {
                    demand[i] = (int) Math.floor(ratio);
                } else {
                    demand[i] = ratio < 1 ? 1 : (int) Math.floor(ratio);
                }
            }
        }

        return demand;
    }

    /**
     * <p>Calculate total width of the pattern provided.</p>
     *
     * @param pattern Pattern defined by an array of integer multipliers.
     * @return Total width of the pattern, measured in abstract units.
     */
    public double getPatternWidth(int[] pattern) {
        if (pattern == null) {
            return 0;
        }

        double patternWidth = 0;
        for (int i = 0; i < orders.size(); i++) {
            patternWidth += pattern[i] * orders.get(i).getOrder().getWidth();
        }

        return patternWidth;
    }

    /**
     * <p>Calculate trim loss fractional ratio for the pattern provided. Zero value of the ratio
     * means that there is no trim loss at all.</p>
     *
     * @param group   List of rolls to be cut with pattern provided.
     * @param pattern Pattern defined by an array of integer multipliers.
     * @return Trim loss fractional ratio.
     */
    public double getPatternTrimRatio(List<Roll> group, int[] pattern) {
        if (pattern == null) {
            return 0;
        }

        double totalArea = 0;
        double unusedArea = 0;
        double patternWidth = getPatternWidth(pattern);
        for (Roll roll : group) {
            totalArea += roll.getArea();
            unusedArea += (roll.getWidth() - patternWidth) * roll.getLength();
        }

        return unusedArea / totalArea;
    }

    /**
     * <p>Check the status of production.</p>
     *
     * @return true if all orders are fulfilled, false otherwise.
     */
    public boolean isOrdersFulfilled() {
        boolean isFulfilled = true;
        for (int i = 0; i < orders.size() && isFulfilled; i++) {
            isFulfilled = orders.get(i).isFulfilled();
        }

        return isFulfilled;
    }

    /**
     * <p>Update production of strip with new pattern added to the partial solution.</p>
     *
     * @param group   List of rolls to be cut with pattern provided.
     * @param pattern Pattern defined by an array of integer multipliers.
     */
    public void updateProduction(List<Roll> group, int[] pattern) {
        if (pattern == null) {
            return;
        }

        double groupLength = 0;
        for (Roll roll : group) {
            groupLength += roll.getLength();
        }

        for (int i = 0; i < orders.size(); i++) {
            orders.get(i).addProducedLength(groupLength * pattern[i]);
        }
    }

}
