package com.akavrt.csp.core;

import com.akavrt.csp.core.metadata.ProblemMetadata;
import com.google.common.collect.Sets;

import java.util.*;

/**
 * <p>Utility class to help assemble orders and rolls into valid instance of Problem with ease. A
 * number of validity checks was implemented to ensure uniqueness of order and roll ids.</p>
 *
 * <p>It's highly encouraged to create Problem instances using this builder rather then
 * instantiating them manually.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
// TODO replace IllegalArgumentException with own checked Exception
public class ProblemBuilder {
    private List<Order> builderOrders;
    private List<Roll> builderRolls;
    private int allowedCutsNumber;
    private ProblemMetadata metadata;

    /**
     * <p>Creates an instance of builder with empty properly initialized list of orders and
     * list
     * of rolls.</p>
     */
    public ProblemBuilder() {
        builderOrders = new ArrayList<Order>();
        builderRolls = new ArrayList<Roll>();
    }

    /**
     * <p>Set the list of orders.</p>
     *
     * @param orders The list of orders.
     */
    public void setOrders(List<Order> orders) {
        builderOrders.clear();

        Set<String> ids = Sets.newHashSet();
        for (Order order : orders) {
            // orders with ill-defined ids will be quietly ignored
            if (order != null && order.getId() != null && order.getId().trim().length() > 0) {
                String id = order.getId();
                if (ids.contains(id)) {
                    // id clash, inconsistent problem definition
                    ids.clear();
                    builderOrders.clear();
                    throw new IllegalArgumentException("Uniqueness of order's ids is broken.");
                } else {
                    ids.add(id);
                    builderOrders.add(order);
                }
            }
        }
    }

    /**
     * <p>Add order to the list of orders.</p>
     *
     * @param order The added order.
     */
    public void addOrder(Order order) {
        if (order != null && order.getId() != null && order.getId().trim().length() > 0) {
            String id = order.getId();

            boolean repeatedIdFound = false;
            for (int i = 0; i < builderOrders.size() && !repeatedIdFound; i++) {
                repeatedIdFound = builderOrders.get(i).getId().equals(id);
            }

            if (repeatedIdFound) {
                // id clash, inconsistent problem definition
                throw new IllegalArgumentException("Uniqueness of order's ids is broken.");
            } else {
                builderOrders.add(order);
            }
        } else {
            throw new IllegalArgumentException(
                    "Ill-defined order can't be added to the problem.");
        }
    }

    /**
     * <p>Add orders from the list.</p>
     *
     * @param orders The list of orders.
     */
    public void addOrders(List<Order> orders) {
        for (Order order : orders) {
            addOrder(order);
        }
    }

    /**
     * <p>Set the stock defined as a list of rolls.</p>
     *
     * @param rolls The list of rolls.
     */
    public void setRolls(List<Roll> rolls) {
        builderRolls.clear();

        Set<Integer> ids = Sets.newHashSet();
        for (Roll roll : rolls) {
            // rolls with ill-defined ids will be quietly ignored
            if (roll != null && roll.getId() != null && roll.getId().trim().length() > 0) {
                int internalId = roll.getInternalId();
                if (ids.contains(internalId)) {
                    // id clash, inconsistent problem definition
                    ids.clear();
                    builderRolls.clear();
                    throw new IllegalArgumentException(
                            "Uniqueness of ids within list of stock rolls is broken.");
                } else {
                    ids.add(internalId);
                    builderRolls.add(roll);
                }
            }
        }
    }

    /**
     * <p>Add roll to the list of rolls.</p>
     *
     * @param roll The added roll.
     */
    public void addRoll(Roll roll) {
        if (roll != null && roll.getId() != null && roll.getId().trim().length() > 0) {
            int internalId = roll.getInternalId();

            boolean repeatedIdFound = false;
            for (int i = 0; i < builderRolls.size() && !repeatedIdFound; i++) {
                repeatedIdFound = internalId == builderRolls.get(i).getInternalId();
            }

            if (repeatedIdFound) {
                // id clash, inconsistent problem definition
                throw new IllegalArgumentException(
                        "Uniqueness of ids within list of stock rolls is broken.");
            } else {
                builderRolls.add(roll);
            }
        } else {
            throw new IllegalArgumentException(
                    "Ill-defined roll can't be added to the problem.");
        }
    }

    /**
     * <p>Add a group of rolls with the same size.</p>
     *
     * <p>A predefined number of copies of reference roll will be created and added to the
     * problem. The number of added rolls is equal to the size of the group.</p>
     *
     * @param roll     The reference roll.
     * @param quantity Size of the group.
     */
    public void addRolls(Roll roll, int quantity) {
        if (roll == null || roll.getId() == null || roll.getId().trim().length() == 0) {
            throw new IllegalArgumentException(
                    "Ill-defined roll can't be added to the problem.");
        }

        for (int i = 1; i <= quantity; i++) {
            Roll copy = new Roll(roll.getId(), i, roll.getLength(), roll.getWidth());
            copy.setMetadata(roll.getMetadata());

            try {
                addRoll(copy);
            } catch (IllegalArgumentException e) {
                // TODO add logger statement
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>Add rolls from the list.</p>
     *
     * @param rolls The list of rolls.
     */
    public void addRolls(List<Roll> rolls) {
        for (Roll roll : rolls) {
            addRoll(roll);
        }
    }

    /**
     * <p>Set the upper bound for total number of cuts allowed within one pattern.
     * Use zero if constraint isn't used.</p>
     *
     * @param allowedCutsNumber The maximum number of cuts allowed within one pattern or zero
     *                          if constraint isn't used.
     */
    public void setAllowedCutsNumber(int allowedCutsNumber) {
        this.allowedCutsNumber = allowedCutsNumber;
    }

    /**
     * <p>Set additional information about the problem.</p>
     *
     * @param metadata Additional information about the problem.
     * @return This Builder object to allow for chaining of calls to set methods.
     */
    public void setMetadata(ProblemMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * <p>Create a Problem with the arguments supplied to this builder.<p/>
     *
     * @param sortOrders Sort orders in ascending order of width.
     * @param sortRolls  Sort rolls in ascending order of width.
     */
    public Problem build(boolean sortOrders, boolean sortRolls) {
        if (sortOrders) {
            Collections.sort(builderOrders, new Comparator<Order>() {
                @Override
                public int compare(Order lhs, Order rhs) {
                    return lhs.getWidth() < rhs.getWidth() ? -1 :
                            (lhs.getWidth() > rhs.getWidth() ? 1 : 0);
                }
            });
        }

        if (sortRolls) {
            Collections.sort(builderRolls, new Comparator<Roll>() {
                @Override
                public int compare(Roll lhs, Roll rhs) {
                    return lhs.getWidth() < rhs.getWidth() ? -1 :
                            (lhs.getWidth() > rhs.getWidth() ? 1 : 0);
                }
            });
        }

        Problem problem = new Problem(builderOrders, builderRolls, allowedCutsNumber);
        problem.setMetadata(metadata);

        return problem;
    }

    /**
     * <p>Create a Problem with the arguments supplied to this builder.<p/>
     */
    public Problem build() {
        return build(false, false);
    }

}

