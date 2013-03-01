package com.akavrt.csp.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: akavrt
 * Date: 28.02.13
 * Time: 17:43
 */
public class RollTest {
    private static final double DELTA = 1e-15;
    private double totalLength = 500;
    private double totalWidth = 50;

    @Test
    public void rollWithoutTrim() {
        Roll roll = new Roll("roll1", totalLength, totalWidth);

        assertEquals(totalLength, roll.getLength(), DELTA);
        assertEquals(totalWidth, roll.getWidth(), DELTA);
        assertEquals(totalWidth * totalLength, roll.getArea(), DELTA);
    }

    @Test
    public void invalidRoll() {
        Roll roll = new Roll("roll1", 0, 0);
        assertFalse(roll.isValid());
    }
}
