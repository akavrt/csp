package com.akavrt.csp.core.xml;

import com.akavrt.csp.xml.XmlUtils;
import org.jdom2.Element;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * User: akavrt
 * Date: 14.03.13
 * Time: 01:00
 */
public class UtilsTest {
    private static final double DELTA = 1e-15;

    @Test
    public void formattingDouble() {
        double value = 5;
        String formatted = XmlUtils.formatDouble(value);
        assertEquals("5", formatted);

        value = 5.23;
        formatted = XmlUtils.formatDouble(value);
        assertEquals("5.23", formatted);

        value = 5.238;
        formatted = XmlUtils.formatDouble(value);
        assertEquals("5.238", formatted);

        // only three fraction digits is saved
        value = 5.2386;
        formatted = XmlUtils.formatDouble(value);
        assertEquals("5.239", formatted);
    }

    @Test
    public void doubleFromText() {
        Element rootElm = new Element("root");

        double defaultValue = 55.2;
        double value;

        // element contains no data
        value = XmlUtils.getDoubleFromText(rootElm, defaultValue);
        assertEquals(defaultValue, value, DELTA);

        // element contains wrong data
        rootElm.setText("not a double value");
        value = XmlUtils.getDoubleFromText(rootElm, defaultValue);
        assertEquals(defaultValue, value, DELTA);

        // element contains double value with dot as a decimal separator
        rootElm.setText("19.84");
        value = XmlUtils.getDoubleFromText(rootElm, defaultValue);
        assertFalse(defaultValue == value);
        assertEquals(19.84, value, DELTA);

        // element contains double value spoiled with text
        // this should be ignored
        rootElm.setText("19.84double");
        value = XmlUtils.getDoubleFromText(rootElm, defaultValue);
        assertEquals(defaultValue, value, DELTA);

        // element contains double value with comma as a decimal separator
        // this should be ignored, the only decimal separator allowed is dot
        rootElm.setText("7,28");
        value = XmlUtils.getDoubleFromText(rootElm, defaultValue);
        assertEquals(defaultValue, value, DELTA);
    }

    @Test
    public void doubleFromChildText() {
        Element rootElm = new Element("root");

        double defaultValue = 55.2;
        double value;

        // child is not exist
        value = XmlUtils.getDoubleFromText(rootElm, "child", defaultValue);
        assertEquals(defaultValue, value, DELTA);

        Element childElm = new Element("child");
        rootElm.addContent(childElm);

        // child exists but contains no data
        value = XmlUtils.getDoubleFromText(rootElm, "child", defaultValue);
        assertEquals(defaultValue, value, DELTA);

        // child exists, contains wrong data
        childElm.setText("not a double value");
        value = XmlUtils.getDoubleFromText(rootElm, "child", defaultValue);
        assertEquals(defaultValue, value, DELTA);

        // child exists, contains double value with dot as a decimal separator
        childElm.setText("19.84");
        value = XmlUtils.getDoubleFromText(rootElm, "child", defaultValue);
        assertFalse(defaultValue == value);
        assertEquals(19.84, value, DELTA);

        // child exists, contains double value spoiled with text
        // this should be ignored
        childElm.setText("19.84double");
        value = XmlUtils.getDoubleFromText(rootElm, "child", defaultValue);
        assertEquals(defaultValue, value, DELTA);

        // child exists, contains double value with comma as a decimal separator
        // this should be ignored, the only decimal separator allowed is dot
        childElm.setText("7,28");
        value = XmlUtils.getDoubleFromText(rootElm, "child", defaultValue);
        assertEquals(defaultValue, value, DELTA);
    }

    @Test
    public void intFromText() {
        Element rootElm = new Element("root");

        int defaultValue = 55;
        int value;

        // element contains no data
        value = XmlUtils.getIntegerFromText(rootElm, defaultValue);
        assertEquals(defaultValue, value);

        // element contains wrong data
        rootElm.setText("not an int value");
        value = XmlUtils.getIntegerFromText(rootElm, defaultValue);
        assertEquals(defaultValue, value);

        // element contains valid integer value
        rootElm.setText("19");
        value = XmlUtils.getIntegerFromText(rootElm, defaultValue);
        assertFalse(defaultValue == value);
        assertEquals(19, value);

        // element contains integer value spoiled with text
        // this should be ignored
        rootElm.setText("19int");
        value = XmlUtils.getIntegerFromText(rootElm, defaultValue);
        assertEquals(defaultValue, value);

        // element contains integer value with comma as a group separator
        // this should be ignored
        rootElm.setText("7,28");
        value = XmlUtils.getIntegerFromText(rootElm, defaultValue);
        assertEquals(defaultValue, value);
    }

    @Test
    public void intFromChildText() {
        Element rootElm = new Element("root");

        int defaultValue = 55;
        int value;

        // child is not exist
        value = XmlUtils.getIntegerFromText(rootElm, "child", defaultValue);
        assertEquals(defaultValue, value, DELTA);

        Element childElm = new Element("child");
        rootElm.addContent(childElm);

        // child exists but contains no data
        value = XmlUtils.getIntegerFromText(rootElm, "child", defaultValue);
        assertEquals(defaultValue, value);

        // child exists, contains wrong data
        childElm.setText("not an int value");
        value = XmlUtils.getIntegerFromText(rootElm, "child", defaultValue);
        assertEquals(defaultValue, value);

        // child exists, contains valid integer value
        childElm.setText("19");
        value = XmlUtils.getIntegerFromText(rootElm, "child", defaultValue);
        assertFalse(defaultValue == value);
        assertEquals(19, value);

        // child exists, contains integer value spoiled with text
        // this should be ignored
        childElm.setText("19int");
        value = XmlUtils.getIntegerFromText(rootElm, "child", defaultValue);
        assertEquals(defaultValue, value);

        // child exists, contains integer value with comma as a group separator
        // this should be ignored
        childElm.setText("7,28");
        value = XmlUtils.getIntegerFromText(rootElm, "child", defaultValue);
        assertEquals(defaultValue, value);
    }

    @Test
    public void intFromAttributeText() {
        Element rootElm = new Element("root");

        int defaultValue = 55;
        int value;

        // attribute is not exist
        value = XmlUtils.getIntegerFromAttribute(rootElm, "attr", defaultValue);
        assertEquals(defaultValue, value, DELTA);

        // attribute exists but contains no data
        rootElm.setAttribute("attr", "");
        value = XmlUtils.getIntegerFromAttribute(rootElm, "attr", defaultValue);
        assertEquals(defaultValue, value);

        // attribute exists, contains wrong data
        rootElm.setAttribute("attr", "not an int value");
        value = XmlUtils.getIntegerFromAttribute(rootElm, "attr", defaultValue);
        assertEquals(defaultValue, value);

        // attribute exists, contains valid integer value
        rootElm.setAttribute("attr", "19");
        value = XmlUtils.getIntegerFromAttribute(rootElm, "attr", defaultValue);
        assertFalse(defaultValue == value);
        assertEquals(19, value);

        // attribute exists, contains integer value spoiled with text
        // this should be ignored
        rootElm.setAttribute("attr", "19int");
        value = XmlUtils.getIntegerFromAttribute(rootElm, "attr", defaultValue);
        assertEquals(defaultValue, value);

        // attribute exists, contains integer value with comma as a group separator
        // this should be ignored
        rootElm.setAttribute("attr", "7,28");
        value = XmlUtils.getIntegerFromAttribute(rootElm, "attr", defaultValue);
        assertEquals(defaultValue, value);
    }

    @Test
    public void formattingDate() {
        Calendar calendar = Calendar.getInstance();

        Date date;
        String formatted;

        calendar.set(1984, Calendar.JULY, 28, 0, 0);
        date = calendar.getTime();
        formatted = XmlUtils.formatDate(date);
        assertEquals("1984-07-28 00:00", formatted);

        calendar.set(1984, Calendar.JULY, 28, 4, 7);
        date = calendar.getTime();
        formatted = XmlUtils.formatDate(date);
        assertEquals("1984-07-28 04:07", formatted);
    }

    @Test
    public void dateFromText() {
        Element rootElm = new Element("root");

        Calendar expected = Calendar.getInstance();
        expected.set(1984, Calendar.JULY, 28, 0, 0);
        expected.set(Calendar.SECOND, 0);
        expected.set(Calendar.MILLISECOND, 0);

        Calendar actual = Calendar.getInstance();
        Date value;

        // element contains no data
        value = XmlUtils.getDateFromText(rootElm);
        assertNull(value);

        // element contains wrong data
        rootElm.setText("not a correctly formatted date");
        value = XmlUtils.getDateFromText(rootElm);
        assertNull(value);

        // element contains correctly formatted date
        rootElm.setText("1984-07-28 00:00");
        value = XmlUtils.getDateFromText(rootElm);
        assertNotNull(value);

        actual.setTime(value);
        assertEquals(expected.getTimeInMillis(), actual.getTimeInMillis());

        // element contains correctly formatted date spoiled with text
        // this should be ignored
        rootElm.setText("1984-07-28 00:00 ignore me");
        value = XmlUtils.getDateFromText(rootElm);
        assertNull(value);
    }

}
