package com.akavrt.csp.xml;


import org.jdom2.Element;

/**
 * User: akavrt
 * Date: 02.03.13
 * Time: 22:58
 */
public interface XmlConverter<T> {
    public Element convert(T value);
}
