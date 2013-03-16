package com.akavrt.csp.solver.sequential;

import org.jdom2.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 15.03.13
 * Time: 00:44
 */
public class HaesslerProcedureParametersTest {
    private static final double DELTA = 1e-15;

    @Test
    public void defaults() {
        // no custom values were provided,
        // default values should be converted to XML and then retrieved back
        Element element = new HaesslerProcedureParameters().save();
        HaesslerProcedureParameters actual = new HaesslerProcedureParameters();
        actual.load(element);

        HaesslerProcedureParameters expected = new HaesslerProcedureParameters();
        assertEquals(expected.getTrimRatioRelaxDelta(), actual.getTrimRatioRelaxDelta(), DELTA);
        assertEquals(expected.getPatternUsageRelaxDelta(), actual.getPatternUsageRelaxDelta());
    }

    @Test
    public void conversion() {
        HaesslerProcedureParameters expected = new HaesslerProcedureParameters();
        expected.setTrimRatioRelaxDelta(0.5);
        expected.setPatternUsageRelaxDelta(5);

        Element element = expected.save();
        HaesslerProcedureParameters actual = new HaesslerProcedureParameters();
        actual.load(element);

        assertEquals(expected.getTrimRatioRelaxDelta(), actual.getTrimRatioRelaxDelta(), DELTA);
        assertEquals(expected.getPatternUsageRelaxDelta(), actual.getPatternUsageRelaxDelta());
    }
}
