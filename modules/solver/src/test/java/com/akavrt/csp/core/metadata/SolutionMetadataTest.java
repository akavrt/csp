package com.akavrt.csp.core.metadata;

import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import org.jdom2.Element;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * User: akavrt
 * Date: 14.03.13
 * Time: 17:37
 */
public class SolutionMetadataTest {
    @Test
    public void conversionDescription() {
        SolutionMetadata expected = new SolutionMetadata();
        expected.setDescription("Test conversion");

        Element element = expected.save();
        SolutionMetadata actual = new SolutionMetadata();
        actual.load(element);

        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void conversionDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1984, Calendar.JULY, 28, 0, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        SolutionMetadata expected = new SolutionMetadata();
        expected.setDate(calendar.getTime());

        Element element = expected.save();
        SolutionMetadata actual = new SolutionMetadata();
        actual.load(element);

        assertEquals(expected.getDate().getTime(), actual.getDate().getTime());
    }

    @Test
    public void conversionParameters() {
        SolutionMetadata expected = new SolutionMetadata();

        Element element = expected.save();
        Element paramsElm = element.getChild(XmlTags.PARAMETERS);
        assertNull(paramsElm);

        // add parameter
        PatternGeneratorParameters params = new PatternGeneratorParameters();
        params.setGenerationTrialsLimit(10);
        expected.addParameters(params);

        element = expected.save();
        paramsElm = element.getChild(XmlTags.PARAMETERS);
        assertNotNull(paramsElm);

        // clear parameters
        expected.clearParameters();
        element = expected.save();
        paramsElm = element.getChild(XmlTags.PARAMETERS);
        assertNull(paramsElm);
    }

    @Test
    public void emptyMetadata() {
        SolutionMetadata expected = new SolutionMetadata();

        Element element = expected.save();
        Element paramsElm = element.getChild(XmlTags.PARAMETERS);
        assertNull(paramsElm);

        SolutionMetadata actual = new SolutionMetadata();
        actual.load(element);

        assertNull(actual.getDescription());
        assertNull(actual.getDate());
    }

    private interface XmlTags {
        String PARAMETERS = "parameters";
    }

}
