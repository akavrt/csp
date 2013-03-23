package com.akavrt.csp.solver.pattern;

import org.jdom2.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 15.03.13
 * Time: 00:43
 */
public class PatternGeneratorParametersTest {

    @Test
    public void defaults() {
        // no custom values were provided,
        // default values should be converted to XML and then retrieved back
        Element element = new PatternGeneratorParameters().save();
        PatternGeneratorParameters actual = new PatternGeneratorParameters();
        actual.load(element);

        PatternGeneratorParameters expected = new PatternGeneratorParameters();
        assertEquals(expected.getGenerationTrialsLimit(), actual.getGenerationTrialsLimit());
    }

    @Test
    public void conversion() {
        PatternGeneratorParameters expected = new PatternGeneratorParameters();
        expected.setGenerationTrialsLimit(500);

        Element element = expected.save();
        PatternGeneratorParameters actual = new PatternGeneratorParameters();
        actual.load(element);

        assertEquals(expected.getGenerationTrialsLimit(), actual.getGenerationTrialsLimit());
    }

}
