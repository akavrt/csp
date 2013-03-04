package com.akavrt.csp.xml;

import com.akavrt.csp.core.Strip;
import org.jdom2.Element;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 14:59
 */
public abstract class StripConverter<T extends Strip> implements XmlConverter<T> {

    public abstract String getRootTag();
    public abstract T createStrip(String id, double length, double width);

    @Override
    public Element export(T strip) {
        Element rootElm = new Element(getRootTag());
        rootElm.setAttribute(StripTags.ID, strip.getId());

        Element stripElm = prepareStrip(strip);
        rootElm.addContent(stripElm);

        return rootElm;
    }

    @Override
    public T extract(Element rootElm) {
        T strip = null;

        double length = 0;
        double width = 0;

        String id = rootElm.getAttributeValue(StripTags.ID);
        Element stripElm = rootElm.getChild(StripTags.STRIP);
        if (stripElm != null) {
            length = Utils.getDoubleFromText(stripElm, StripTags.LENGTH);
            width = Utils.getDoubleFromText(stripElm, StripTags.WIDTH);
        }

        if (length > 0 && width > 0) {
            strip = createStrip(id, length, width);
        }

        return strip;
    }

    private Element prepareStrip(T strip) {
        Element stripElm = new Element(StripTags.STRIP);

        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(',');
        DecimalFormat format = new DecimalFormat("#.##", formatSymbols);

        // length of the strip
        Element lengthElm = new Element(StripTags.LENGTH);
        lengthElm.setText(format.format(strip.getLength()));
        stripElm.addContent(lengthElm);

        // width of the strip
        Element widthElm = new Element(StripTags.WIDTH);
        widthElm.setText(format.format(strip.getWidth()));
        stripElm.addContent(widthElm);

        return stripElm;
    }

    public interface StripTags {
        String ID = "id";
        String STRIP = "strip";
        String LENGTH = "length";
        String WIDTH = "width";
    }

}
