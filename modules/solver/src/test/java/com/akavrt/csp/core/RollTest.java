package com.akavrt.csp.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: akavrt
 * Date: 28.02.13
 * Time: 17:43
 */
public class RollTest {
    private static final double DELTA = 1e-15;

    @Test
    public void createRoll() {
        String id = "roll";
        double length = 400;
        double width = 30;

        Roll roll = new Roll(id, length, width);

        assertEquals(id, roll.getId());
        assertEquals(length, roll.getLength(), DELTA);
        assertEquals(width, roll.getWidth(), DELTA);
    }

    @Test
    public void calculateRollArea() {
        String id = "roll";
        double length = 400;
        double width = 30;

        Roll roll = new Roll(id, length, width);
        assertEquals(length * width, roll.getArea(), DELTA);
    }

    @Test
    public void rollValidity() {
        String id = "roll";
        double length = 400;
        double width = 30;

        Roll roll = new Roll(id, length, width);
        assertTrue(roll.isValid());

        roll = new Roll(id, 0, width);
        assertFalse(roll.isValid());

        roll = new Roll(id, length, 0);
        assertFalse(roll.isValid());

        roll = new Roll(id, 0, 0);
        assertFalse(roll.isValid());
    }

    @Test
    public void internalIdReproducibility() {
        String id = "roll";
        double length = 400;
        double width = 30;

        Roll firstRoll = new Roll(id, length, width);
        Roll secondRoll = new Roll(id, length, width);
        assertEquals(firstRoll.getInternalId(), secondRoll.getInternalId());

        // let's add additional integer id (unique within group)
        firstRoll = new Roll(id, 1, length, width);
        secondRoll = new Roll(id, 1, length, width);
        assertTrue(firstRoll.getInternalId() == secondRoll.getInternalId());

        firstRoll = new Roll(id, 1, length, width);
        secondRoll = new Roll(id, 2, length, width);
        assertFalse(firstRoll.getInternalId() == secondRoll.getInternalId());
    }

}
