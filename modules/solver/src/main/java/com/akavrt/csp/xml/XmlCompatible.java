package com.akavrt.csp.xml;

import org.jdom2.Element;

/**
 * <p>In contrast to XML serialization of core classes, see XmlConverter interface, conversion of
 * metadata to and from XML is handled inside corresponding classes, see package
 * com.akavrt.csp.core.metadata. Any class from this package can be easily extended to support
 * additional content which should be stored in metadata.</p>
 *
 * <p>Thereby, converter classes implementing XmlConverter interface defines basic structure of
 * output XML, while classes implementing XmlCompatible interface should be used as a hooks to
 * support custom metadata which content and format are both defined by the user (inside client
 * code).</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface XmlCompatible {
    /**
     * <p>Convert state into XML represented as org.jdom2.Element.</p>
     *
     * @return XML representation of the state.
     */
    public Element save();

    /**
     * <p>Retrieve internal state from XML represented as org.jdom2.Element.</p>
     */
    public void load(Element element);

}
