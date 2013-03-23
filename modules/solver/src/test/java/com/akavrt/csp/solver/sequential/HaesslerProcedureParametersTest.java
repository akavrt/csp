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
        Element element = new SequentialProcedureParameters().save();
        SequentialProcedureParameters actual = new SequentialProcedureParameters();
        actual.load(element);

        SequentialProcedureParameters expected = new SequentialProcedureParameters();
        assertEquals(expected.getTrimRatioRelaxStep(), actual.getTrimRatioRelaxStep(), DELTA);
        assertEquals(expected.getPatternUsageRelaxStep(), actual.getPatternUsageRelaxStep());
    }

    @Test
    public void conversion() {
        SequentialProcedureParameters expected = new SequentialProcedureParameters();
        expected.setTrimRatioRelaxStep(0.5);
        expected.setPatternUsageRelaxStep(5);

        Element element = expected.save();
        SequentialProcedureParameters actual = new SequentialProcedureParameters();
        actual.load(element);

        assertEquals(expected.getTrimRatioRelaxStep(), actual.getTrimRatioRelaxStep(), DELTA);
        assertEquals(expected.getPatternUsageRelaxStep(), actual.getPatternUsageRelaxStep());
    }
}
