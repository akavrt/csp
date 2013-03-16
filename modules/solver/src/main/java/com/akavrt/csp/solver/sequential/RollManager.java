package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.Roll;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>The list of rolls stored in Problem is immutable. To build solution in a sequential manner we
 * need iteratively revise stock and remove used roll to avoid repeatable usage of the same rolls.
 * Roll manager simplifies these tasks and most of the time is used as a provider of utility
 * methods used by sequential procedure.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class RollManager {
    private final List<Roll> rolls;

    /**
     * <p>Create manager and associate it with initial list of stock rolls, enumerated within the
     * problem definition.</p>
     *
     * @param problemRolls The list of the rolls available in stock.
     */
    public RollManager(List<Roll> problemRolls) {
        rolls = Lists.newArrayList(problemRolls);

        // sort rolls in ascending order of widths
        Collections.sort(rolls, new Comparator<Roll>() {
            @Override
            public int compare(Roll lhs, Roll rhs) {
                return lhs.getWidth() < rhs.getWidth() ? -1 :
                        (lhs.getWidth() > rhs.getWidth() ? 1 : 0);
            }
        });
    }

    /**
     * <p>Number of unused rolls left in stock.</p>
     *
     * @return Size of the stock.
     */
    public int size() {
        return rolls.size();
    }

    /**
     * <p>Find roll with minimal area within list of rolls.</p>
     *
     * @return The area of the roll with minimal area within list of rolls.
     */
    public double getMinRollArea() {
        double minRollArea = 0;
        for (Roll roll : rolls) {
            if (minRollArea == 0 || roll.getArea() < minRollArea) {
                minRollArea = roll.getArea();
            }
        }

        return minRollArea;
    }

    /**
     * <p>Search for a largest group of rolls. Group clustering is decided based on width of the
     * roll.</p>
     *
     * @param toleranceRatio Positive ratio allows to add rolls with different width of strip to
     *                       the same group. Deviation of the width within group is controlled by
     *                       fraction tolerance ratio.
     * @return Size of the largest group found.
     */
    public int getLargestGroupSize(double toleranceRatio) {
        int maxGroupSize = 0;
        for (int i = 0; i < rolls.size(); i++) {
            double anchorWidth = rolls.get(i).getWidth();
            int groupSize = 0;
            for (int j = 0; j < rolls.size(); j++) {
                double delta = 1 - anchorWidth / rolls.get(j).getWidth();
                if (delta >= 0 && delta <= toleranceRatio) {
                    groupSize++;
                }
            }

            if (groupSize > maxGroupSize) {
                maxGroupSize = groupSize;
            }
        }

        return maxGroupSize;
    }

    /**
     * <p>Calculate size of the group formed by a specific roll used as an anchor.</p>
     *
     * @param anchorIndex    Index of the roll used as an anchor within list of rolls.
     * @param toleranceRatio Positive ratio allows to add rolls with different width of strip to
     *                       the same group. Deviation of the width within group is controlled by
     *                       fraction tolerance ratio.
     * @return The size of the group.
     */
    public int getGroupSize(int anchorIndex, double toleranceRatio) {
        double anchorWidth = rolls.get(anchorIndex).getWidth();
        int groupSize = 0;
        for (Roll roll : rolls) {
            double delta = 1 - anchorWidth / roll.getWidth();
            if (delta >= 0 && delta <= toleranceRatio) {
                groupSize++;
            }
        }

        return groupSize;
    }

    /**
     * <p>Create group of the rolls formed by a specific roll used as an anchor.</p>
     *
     * @param anchorIndex    Index of the roll used as an anchor within list of rolls.
     * @param toleranceRatio Positive ratio allows to add rolls with different width of strip to
     *                       the same group. Deviation of the width within group is controlled by
     *                       fraction tolerance ratio.
     * @return The group of the rolls.
     */
    public List<Roll> getGroup(int anchorIndex, double toleranceRatio) {
        List<Roll> group = Lists.newArrayList();

        double anchorWidth = rolls.get(anchorIndex).getWidth();
        for (Roll roll : rolls) {
            double delta = 1 - anchorWidth / roll.getWidth();
            if (delta >= 0 && delta <= toleranceRatio) {
                group.add(roll);
            }
        }

        return group;
    }

    /**
     * <p>Remove used rolls from stock.</p>
     *
     * @param group Group of rolls to be removed from stock.
     */
    public void updateStock(List<Roll> group) {
        for (Roll usedRoll : group) {
            int i = 0;
            while (i < rolls.size()) {
                Roll stockRoll = rolls.get(i);
                if (usedRoll.getInternalId() == stockRoll.getInternalId()) {
                    rolls.remove(i);
                } else {
                    i++;
                }
            }
        }

    }
}
