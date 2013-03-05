package com.akavrt.csp.xml;

import org.jdom2.Element;

/**
 * User: akavrt
 * Date: 05.03.13
 * Time: 01:35
 */
public interface MetadataConverter {
    // save state to XML
    public Element save();

    // load state from XML
    public void load(Element element);

}
