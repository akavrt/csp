package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Strip;
import com.akavrt.csp.xml.XmlConverter;
import org.jdom2.Element;

/**
 * <p>This abstract class defines basic contract for conversion between any instance of Strip and
 * its XML representation.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public abstract class StripConverter<T extends Strip> implements XmlConverter<T> {

    /**
     * @return Name of the root element.
     */
    public abstract String getRootTag();

    /**
     * <p>Factory-like method used to hide details about the concrete class when creating an
     * instance of it.</p>
     *
     * @param id     The identifier for the strip.
     * @param length The length of the strip, in abstract units.
     * @param width  The width of the strip, in abstract units.
     * @return An instance of concrete class extending Strip.
     */
    public abstract T createStrip(String id, double length, double width);

    /**
     * <p>Inherited classes can use metadata of different types. In base class we define only the
     * order of elements in XML: metadata comes first, strip description comes second if metadata
     * is present.</p>
     *
     * @param strip An instance of class inherited from Strip, which contains metadata.
     * @return Metadata converted to XML, represented by an instance of org.jdom2.Element or null.
     */
    public abstract Element prepareMetadata(T strip);

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public T extract(Element rootElm) {
        T strip = null;

        double length = 0;
        double width = 0;

        String id = rootElm.getAttributeValue(XmlTags.ID);
        Element stripElm = rootElm.getChild(XmlTags.STRIP);
        if (stripElm != null) {
            length = XmlUtils.getDoubleFromText(stripElm, XmlTags.LENGTH, 0);
            width = XmlUtils.getDoubleFromText(stripElm, XmlTags.WIDTH, 0);
        }

        if (length > 0 && width > 0) {
            strip = createStrip(id, length, width);
        }

        return strip;
    }

    private Element prepareStrip(T strip) {
        Element stripElm = new Element(XmlTags.STRIP);

        // length of the strip
        Element lengthElm = new Element(XmlTags.LENGTH);
        lengthElm.setText(XmlUtils.formatDouble(strip.getLength()));
        stripElm.addContent(lengthElm);

        // width of the strip
        Element widthElm = new Element(XmlTags.WIDTH);
        widthElm.setText(XmlUtils.formatDouble(strip.getWidth()));
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
