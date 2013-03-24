package com.akavrt.csp.xml;

import com.akavrt.csp.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

import java.text.*;
import java.util.Date;
import java.util.Locale;

/**
 * <p>Collection of utility methods used to format data in XML export and to parse data while
 * importing it from XML.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class XmlUtils {
    private static final Logger LOGGER = LogManager.getLogger(XmlUtils.class);
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    private static final DecimalFormat DECIMAL_FORMAT;
    private static DateFormat DATE_FORMAT;

    static {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(',');

        DECIMAL_FORMAT = new DecimalFormat();
        DECIMAL_FORMAT.setDecimalFormatSymbols(formatSymbols);
        DECIMAL_FORMAT.setGroupingUsed(false);

        DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.ENGLISH);
    }

    /**
     * <p>Converts double value to String with special format applied. Dot is used as decimal
     * separator, group separator isn't used.</p>
     *
     * @param value Value to format.
     * @return Formatted value.
     */
    public static String formatDouble(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    /**
     * <p>Convert the textual content directly held under element to double.</p>
     *
     * <p>Only dot can be used as decimal separator, group separator isn't allowed.</p>
     *
     * @param element      Element with textual content to convert.
     * @param defaultValue If conversion fails, default value is returned.
     * @return Double value parsed from textual content or default value, if conversion fails.
     */
    public static double getDoubleFromText(Element element, double defaultValue) {
        double value = defaultValue;
        String valueString = element.getText();
        if (!Utils.isEmpty(valueString)) {
            ParsePosition pp = new ParsePosition(0);
            Number number = DECIMAL_FORMAT.parse(valueString, pp);
            if (number != null && valueString.length() == pp.getIndex()) {
                value = number.doubleValue();
            }
        }
        return value;
    }

    /**
     * <p>Convert the textual content directly held under child element to double.</p>
     *
     * <p>Only dot can be used as decimal separator, group separator isn't allowed.</p>
     *
     * @param parent       Immediate parent for child element with textual content to convert.
     * @param childName    Name of the child element.
     * @param defaultValue If conversion fails, default value is returned.
     * @return Double value parsed from textual content or default value, if conversion fails.
     */
    public static double getDoubleFromText(Element parent, String childName, double defaultValue) {
        double value = defaultValue;
        String valueString = parent.getChildText(childName);
        if (!Utils.isEmpty(valueString)) {
            ParsePosition pp = new ParsePosition(0);
            Number number = DECIMAL_FORMAT.parse(valueString, pp);
            if (number != null && valueString.length() == pp.getIndex()) {
                value = number.doubleValue();
            }
        }

        return value;
    }

    /**
     * <p>Convert the textual content directly held under element to integer.</p>
     *
     * @param element      Element with textual content to convert.
     * @param defaultValue If conversion fails, default value is returned.
     * @return Integer value parsed from textual content or default value, if conversion fails.
     */
    public static int getIntegerFromText(Element element, int defaultValue) {
        int value = defaultValue;
        String valueString = element.getText();
        if (!Utils.isEmpty(valueString)) {
            try {
                value = Integer.parseInt(valueString);
            } catch (NumberFormatException e) {
                LOGGER.catching(e);
            }
        }

        return value;
    }

    /**
     * <p>Convert the textual content directly held under child element to integer.</p>
     *
     * @param parent       Immediate parent for child element with textual content to convert.
     * @param childName    Name of the child element.
     * @param defaultValue If conversion fails, default value is returned.
     * @return Integer value parsed from textual content or default value, if conversion fails.
     */
    public static int getIntegerFromText(Element parent, String childName, int defaultValue) {
        int value = defaultValue;
        String valueString = parent.getChildText(childName);
        if (!Utils.isEmpty(valueString)) {
            try {
                value = Integer.parseInt(valueString);
            } catch (NumberFormatException e) {
                LOGGER.catching(e);
            }
        }

        return value;
    }

    /**
     * <p>Convert the textual content held as attribute value to integer.</p>
     *
     * @param element       Element containing attribute with textual content to convert.
     * @param attributeName Name of the attribute.
     * @param defaultValue  If conversion fails, default value is returned.
     * @return Integer value parsed from textual content or default value, if conversion fails.
     */
    public static int getIntegerFromAttribute(Element element, String attributeName,
                                              int defaultValue) {
        int value = defaultValue;
        String valueString = element.getAttributeValue(attributeName);
        if (!Utils.isEmpty(valueString)) {
            try {
                value = Integer.parseInt(valueString);
            } catch (NumberFormatException e) {
                LOGGER.catching(e);
            }
        }

        return value;
    }

    /**
     * <p>Converts an instance of Date to String with special format applied.</p>
     *
     * <p>Pattern 'yyyy-MM-dd HH:mm' is used to format date.</p>
     *
     * @param date Date to format.
     * @return Formatted date.
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * <p>Convert the textual content directly held under element to Date.</p>
     *
     * <p>The only formatting pattern supported is 'yyyy-MM-dd HH:mm'. Other formats will be
     * ignored.</p>
     *
     * @param element Element with textual content to convert.
     * @return Date parsed from textual content or null, if conversion fails.
     */
    public static Date getDateFromText(Element element) {
        Date date = null;

        String valueString = element.getText();
        if (!Utils.isEmpty(valueString)) {
            ParsePosition pp = new ParsePosition(0);
            date = DATE_FORMAT.parse(valueString, pp);
            if (valueString.length() != pp.getIndex()) {
                date = null;
            }
        }

        return date;
    }

}
