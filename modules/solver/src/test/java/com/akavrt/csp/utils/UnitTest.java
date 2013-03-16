package com.akavrt.csp.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 14.03.13
 * Time: 03:58
 */
public class UnitTest {
    private static final double DELTA = 1e-15;

    @Test
    public void multiplier() {
        assertEquals(1, Unit.METER.getMultiplier(), DELTA);
        assertEquals(0.01, Unit.CENTIMETER.getMultiplier(), DELTA);
        assertEquals(0.001, Unit.MILLIMETER.getMultiplier(), DELTA);
        assertEquals(0.0254, Unit.INCH.getMultiplier(), DELTA);
    }

    @Test
    public void conversion() {
        double dimension = 100;

        assertEquals(100, Unit.METER.toMeters(dimension), DELTA);
        assertEquals(1, Unit.CENTIMETER.toMeters(dimension), DELTA);
        assertEquals(0.1, Unit.MILLIMETER.toMeters(dimension), DELTA);
        assertEquals(Unit.MILLIMETER.toMeters(25.4), Unit.INCH.toMeters(1), DELTA);
    }
}
