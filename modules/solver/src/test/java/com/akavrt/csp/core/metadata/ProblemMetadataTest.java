package com.akavrt.csp.core.metadata;

import com.akavrt.csp.utils.Unit;
import org.jdom2.Element;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: akavrt
 * Date: 14.03.13
 * Time: 17:38
 */
public class ProblemMetadataTest {

    @Test
    public void conversionName() {
        ProblemMetadata expected = new ProblemMetadata();
        expected.setName("Test problem");

        Element element = expected.save();
        ProblemMetadata actual = new ProblemMetadata();
        actual.load(element);

        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void conversionAuthor() {
        ProblemMetadata expected = new ProblemMetadata();
        expected.setAuthor("Victor Balabanov");

        Element element = expected.save();
        ProblemMetadata actual = new ProblemMetadata();
        actual.load(element);

        assertEquals(expected.getAuthor(), actual.getAuthor());
    }

    @Test
    public void conversionDescription() {
        ProblemMetadata expected = new ProblemMetadata();
        expected.setDescription("Test conversion");

        Element element = expected.save();
        ProblemMetadata actual = new ProblemMetadata();
        actual.load(element);

        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    public void conversionDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1984, Calendar.JULY, 28, 0, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        ProblemMetadata expected = new ProblemMetadata();
        expected.setDate(calendar.getTime());

        Element element = expected.save();
        ProblemMetadata actual = new ProblemMetadata();
        actual.load(element);

        assertEquals(expected.getDate().getTime(), actual.getDate().getTime());
    }

    @Test
    public void conversionUnits() {
        ProblemMetadata expected = new ProblemMetadata();
        expected.setUnits(Unit.INCH);

        Element element = expected.save();
        ProblemMetadata actual = new ProblemMetadata();
        actual.load(element);

        assertEquals(expected.getUnits(), actual.getUnits());
    }

    @Test
    public void emptyMetadata() {
        ProblemMetadata expected = new ProblemMetadata();

        Element element = expected.save();
        ProblemMetadata actual = new ProblemMetadata();
        actual.load(element);

        assertNull(actual.getName());
        assertNull(actual.getAuthor());
        assertNull(actual.getDescription());
        assertNull(actual.getDate());
        assertNull(actual.getUnits());
    }
}
