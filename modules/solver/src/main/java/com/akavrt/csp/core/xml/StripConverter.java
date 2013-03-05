package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Strip;
import com.akavrt.csp.xml.XmlConverter;
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
    public abstract Element prepareMetadata(T strip);

    @Override
    public Element export(T strip) {
        Element rootElm = new Element(getRootTag());
        rootElm.setAttribute(XmlTags.ID, strip.getId());

        Element metadataElm = prepareMetadata(strip);
        if (metadataElm != null) {
            rootElm.addContent(metadataElm);
        }

        Element stripElm = prepareStrip(strip);
        rootElm.addContent(stripElm);

        return rootElm;
    }

    @Override
    public T extract(Element rootElm) {
        T strip = null;

        double length = 0;
        double width = 0;

        String id = rootElm.getAttributeValue(XmlTags.ID);
        Element stripElm = rootElm.getChild(XmlTags.STRIP);
        if (stripElm != null) {
            length = Utils.getDoubleFromText(stripElm, XmlTags.LENGTH);
            width = Utils.getDoubleFromText(stripElm, XmlTags.WIDTH);
        }

        if (length > 0 && width > 0) {
            strip = createStrip(id, length, width);
        }

        return strip;
    }

    private Element prepareStrip(T strip) {
        Element stripElm = new Element(XmlTags.STRIP);

        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(',');
        DecimalFormat format = new DecimalFormat("#.##", formatSymbols);

        // length of the strip
        Element lengthElm = new Element(XmlTags.LENGTH);
        lengthElm.setText(format.format(strip.getLength()));
        stripElm.addContent(lengthElm);

        // width of the strip
        Element widthElm = new Element(XmlTags.WIDTH);
        widthElm.setText(format.format(strip.getWidth()));
        stripElm.addContent(widthElm);

        return stripElm;
    }

    private interface XmlTags {
        String ID = "id";
        String STRIP = "strip";
        String LENGTH = "length";
        String WIDTH = "width";
    }

}
