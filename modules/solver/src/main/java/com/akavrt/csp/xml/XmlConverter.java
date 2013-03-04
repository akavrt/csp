package com.akavrt.csp.xml;


import org.jdom2.Element;

/**
 * User: akavrt
 * Date: 02.03.13
 * Time: 22:58
 */
public interface XmlConverter<T> {
    // export to XML
    public Element export(T value);
    // extract from XML
    public T extract(Element element);
}
