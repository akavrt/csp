package com.akavrt.csp.xml;

import org.jdom2.Element;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 15:25
 */
public class Utils {

    public static double getDoubleFromText(Element rootElm, String targetTag) {
        double value = 0;
        String valueString = rootElm.getChildText(targetTag);
        if (valueString != null) {
            try {
                value = Double.parseDouble(valueString);
            } catch (NumberFormatException e) {
                // TODO add logger statement
                e.printStackTrace();
            }
        }
        return value;
    }

    public static int getIntegerFromText(Element rootElm, String targetTag) {
        int value = 0;
        String valueString = rootElm.getChildText(targetTag);
        if (valueString != null) {
            try {
                value = Integer.parseInt(valueString);
            } catch (NumberFormatException e) {
                // TODO add logger statement
                e.printStackTrace();
            }
        }

        return value;
    }

    public static int getIntegerFromAttribute(Element targetElm, String targetAttr,
                                              int defaultValue) {
        int value = defaultValue;
        String valueString = targetElm.getAttributeValue(targetAttr);
        if (valueString != null) {
            try {
                value = Integer.parseInt(valueString);
            } catch (NumberFormatException e) {
                // TODO add logger statement
                e.printStackTrace();
            }
        }

        return value;
    }
}
