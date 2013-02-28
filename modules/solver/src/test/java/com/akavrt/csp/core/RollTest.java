package com.akavrt.csp.core;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: akavrt
 * Date: 28.02.13
 * Time: 17:43
 */
public class RollTest {
    private static final double DELTA = 1e-15;
    private int id = 1;
    private double totalLength = 500;
    private double totalWidth = 50;
    private double startTrimLength = 10;
    private double endTrimLength = 20;
    private double leftTrimWidth = 2;
    private double rightTrimWidth = 5;

    @Test
    public void rollWithoutTrim() {

        Roll.Builder builder = new Roll.Builder();
        Roll roll = builder.setId(id).setLength(totalLength).setWidth(totalWidth).build();

        assertEquals(totalLength, roll.getUsableLength(), DELTA);
        assertEquals(totalWidth, roll.getUsableWidth(), DELTA);
        assertEquals(totalWidth * totalLength, roll.getUsableArea(), DELTA);
    }

    @Test
    public void rollWithTrim() {

        Roll.Builder builder = new Roll.Builder();
        Roll roll = builder.setId(id)
                           .setLength(totalLength).setStartTrimLength(startTrimLength)
                           .setEndTrimLength(endTrimLength).setWidth(totalWidth)
                           .setLeftTrimWidth(leftTrimWidth).setRightTrimWidth(rightTrimWidth)
                           .build();

        double usableLength = totalLength - startTrimLength - endTrimLength;
        assertEquals(usableLength, roll.getUsableLength(), DELTA);

        double usableWidth = totalWidth - leftTrimWidth - rightTrimWidth;
        assertEquals(usableWidth, roll.getUsableWidth(), DELTA);

        assertEquals(usableLength * usableWidth, roll.getUsableArea(), DELTA);
    }

    @Test
    public void invalidRoll() {
        Roll.Builder builder;
        Roll roll;

        builder = new Roll.Builder();
        roll = builder.setId(id)
                      .setLength(totalLength).setStartTrimLength(startTrimLength)
                      .setEndTrimLength(endTrimLength).setWidth(totalWidth)
                      .setLeftTrimWidth(leftTrimWidth).setRightTrimWidth(rightTrimWidth)
                      .build();
        assertTrue(roll.isValid());

        builder = new Roll.Builder();
        roll = builder.setId(id)
                      .setLength(200).setStartTrimLength(100).setEndTrimLength(100)
                      .setWidth(totalWidth).setLeftTrimWidth(leftTrimWidth)
                      .setRightTrimWidth(rightTrimWidth)
                      .build();
        assertFalse(roll.isValid());

        builder = new Roll.Builder();
        roll = builder.setId(id)
                      .setLength(200).setStartTrimLength(0).setEndTrimLength(205)
                      .setWidth(totalWidth).setLeftTrimWidth(leftTrimWidth)
                      .setRightTrimWidth(rightTrimWidth)
                      .build();
        assertFalse(roll.isValid());

        builder = new Roll.Builder();
        roll = builder.setId(id)
                      .setLength(totalLength).setStartTrimLength(startTrimLength)
                      .setEndTrimLength(endTrimLength)
                      .setWidth(50).setLeftTrimWidth(25)
                      .setRightTrimWidth(25)
                      .build();
        assertFalse(roll.isValid());

        builder = new Roll.Builder();
        roll = builder.setId(id)
                      .setLength(totalLength).setStartTrimLength(startTrimLength)
                      .setEndTrimLength(endTrimLength)
                      .setWidth(50).setLeftTrimWidth(0)
                      .setRightTrimWidth(55)
                      .build();
        assertFalse(roll.isValid());

    }
}
