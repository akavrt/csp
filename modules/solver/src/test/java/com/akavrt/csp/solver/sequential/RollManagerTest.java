package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.Roll;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: akavrt
 * Date: 15.03.13
 * Time: 19:58
 */
public class RollManagerTest {
    private static final double DELTA = 1e-15;
    private List<Roll> rolls;
    private RollManager manager;

    @Before
    public void setUpManager() {
        rolls = Lists.newArrayList();
        rolls.add(new Roll("roll1", 1000, 100));
        rolls.add(new Roll("roll2", 1000, 120));
        rolls.add(new Roll("roll3", 1000, 140));
        rolls.add(new Roll("roll4", 1000, 160));
        rolls.add(new Roll("roll5", 1000, 180));
        rolls.add(new Roll("roll6", 1000, 200));

        // group of rolls with same length and width
        rolls.add(new Roll("roll7", 1, 1000, 220));
        rolls.add(new Roll("roll7", 2, 1000, 220));
        rolls.add(new Roll("roll7", 3, 1000, 220));
        rolls.add(new Roll("roll8", 1000, 230));

        manager = new RollManager(rolls);
    }

    @Test
    public void size() {
        assertEquals(10, manager.size());
    }

    @Test
    public void getMinRollArea() {
        assertEquals(1000 * 100, manager.getMinRollArea(), DELTA);
    }

    @Test
    public void getLargestGroupSize() {
        int groupSize;

        // 180 X 3
        groupSize = manager.getLargestGroupSize(0);
        assertEquals(3, groupSize);

        // 200, 220 X3
        groupSize = manager.getLargestGroupSize(0.1);
        assertEquals(4, groupSize);

        // 200, 220 X3, 230
        groupSize = manager.getLargestGroupSize(0.15);
        assertEquals(5, groupSize);
    }

    @Test
    public void getGroupSize() {
        int groupSize;

        // 100
        groupSize = manager.getGroupSize(0, 0);
        assertEquals(1, groupSize);

        // 100, 120
        groupSize = manager.getGroupSize(0, 0.2);
        assertEquals(2, groupSize);

        // 100, 120, 140
        groupSize = manager.getGroupSize(0, 0.29);
        assertEquals(3, groupSize);

        // 180
        groupSize = manager.getGroupSize(4, 0);
        assertEquals(1, groupSize);

        // 180
        groupSize = manager.getGroupSize(4, 0.09);
        assertEquals(1, groupSize);

        // 180, 200
        groupSize = manager.getGroupSize(4, 0.1);
        assertEquals(2, groupSize);

        // 180, 200
        groupSize = manager.getGroupSize(4, 0.18);
        assertEquals(2, groupSize);

        // 180, 200, 220 X 3
        groupSize = manager.getGroupSize(4, 0.19);
        assertEquals(5, groupSize);

        // 220 X 3
        groupSize = manager.getGroupSize(7, 0);
        assertEquals(3, groupSize);

        // 220 X 3, 230
        groupSize = manager.getGroupSize(7, 0.05);
        assertEquals(4, groupSize);
    }

    @Test
    public void getGroup() {
        List<Roll> group;

        // let's test our helper
        group = Lists.newArrayList();
        group.add(rolls.get(0));
        assertTrue(isAllRollsFound(group, rolls.get(0).getInternalId()));

        group.add(rolls.get(1));
        assertFalse(isAllRollsFound(group,
                                    rolls.get(0).getInternalId(),
                                    rolls.get(1).getInternalId(),
                                    rolls.get(2).getInternalId()));

        // actual test

        // 100
        group = manager.getGroup(0, 0);
        assertTrue(isAllRollsFound(group, rolls.get(0).getInternalId()));

        // 100, 120
        group = manager.getGroup(0, 0.2);
        assertTrue(isAllRollsFound(group,
                                   rolls.get(0).getInternalId(),
                                   rolls.get(1).getInternalId()));

        // 100, 120, 140
        group = manager.getGroup(0, 0.29);
        assertTrue(isAllRollsFound(group,
                                   rolls.get(0).getInternalId(),
                                   rolls.get(1).getInternalId(),
                                   rolls.get(2).getInternalId()));


        // 180
        group = manager.getGroup(4, 0);
        assertTrue(isAllRollsFound(group,
                                   rolls.get(4).getInternalId()));

        // 180
        group = manager.getGroup(4, 0.09);
        assertTrue(isAllRollsFound(group,
                                   rolls.get(4).getInternalId()));

        // 180, 200
        group = manager.getGroup(4, 0.1);
        assertTrue(isAllRollsFound(group,
                                   rolls.get(4).getInternalId(),
                                   rolls.get(5).getInternalId()));

        // 180, 200
        group = manager.getGroup(4, 0.18);
        assertTrue(isAllRollsFound(group,
                                   rolls.get(4).getInternalId(),
                                   rolls.get(5).getInternalId()));

        // 180, 200, 220 X 3
        group = manager.getGroup(4, 0.19);
        assertTrue(isAllRollsFound(group,
                                   rolls.get(4).getInternalId(),
                                   rolls.get(5).getInternalId(),
                                   rolls.get(6).getInternalId(),
                                   rolls.get(7).getInternalId(),
                                   rolls.get(8).getInternalId()));

        // 220 X 3
        group = manager.getGroup(7, 0);
        assertTrue(isAllRollsFound(group,
                                   rolls.get(6).getInternalId(),
                                   rolls.get(7).getInternalId(),
                                   rolls.get(8).getInternalId()));

        // 220 X 3, 230
        group = manager.getGroup(7, 0.05);
        assertTrue(isAllRollsFound(group,
                                   rolls.get(6).getInternalId(),
                                   rolls.get(7).getInternalId(),
                                   rolls.get(8).getInternalId(),
                                   rolls.get(9).getInternalId()));
    }

    private boolean isAllRollsFound(List<Roll> rolls, int... expectedInternalIds) {
        Map<Integer, Boolean> found = Maps.newHashMap();
        for (int id : expectedInternalIds) {
            found.put(id, false);
        }

        for (Roll roll : rolls) {
            boolean knownRoll = found.containsKey(roll.getInternalId());

            if (knownRoll) {
                found.put(roll.getInternalId(), true);
            } else {
                return false;
            }
        }

        boolean isAllRollsFound = true;
        for (Boolean isRollFound : found.values()) {
            isAllRollsFound = isAllRollsFound && isRollFound;
            if (!isAllRollsFound) {
                return false;
            }
        }

        return true;
    }

    @Test
    public void updateStock() {
        List<Roll> group;

        group = Lists.newArrayList();

        // empty group
        assertEquals(10, manager.size());
        manager.updateStock(group);
        assertEquals(10, manager.size());

        // nonempty group
        group.add(rolls.get(0));
        group.add(rolls.get(3));
        group.add(rolls.get(6));
        group.add(rolls.get(7));
        group.add(rolls.get(8));

        assertEquals(10, manager.size());
        manager.updateStock(group);
        assertEquals(5, manager.size());
    }

    @Test
    public void emptyStock() {
        List<Roll> stock = Lists.newArrayList();
        RollManager emptyManager = new RollManager(stock);

        assertEquals(0, emptyManager.getMinRollArea(), DELTA);
        assertEquals(0, emptyManager.getLargestGroupSize(0));
    }
}
