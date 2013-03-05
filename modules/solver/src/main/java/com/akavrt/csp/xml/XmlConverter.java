package com.akavrt.csp.xml;


import org.jdom2.Element;

/**
 * <p>XML serialization for all core classes is implemented on a separate layer. Each core class
 * has its converter counterpart, for example see classes com.akavrt.csp.core.Problem and
 * com.akavrt.csp.core.xml.ProblemConverter. Converter classes implementing this interface are able
 * to convert an instance of corresponding core class to org.jdom2.Element (export to XML) and vice
 * versa (import from XML).</p>
 *
 * <p>XML serialization was built using JDOM library, for more information check out this
 * <a href="http://www.jdom.org/">resource</a>.<p/>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface XmlConverter<T> {
    /**
     * <p>Convert an instance of T to XML represented by org.jdom2.Element.</p>
     *
     * @param value The object which is being converted to XML.
     * @return XML representation of an object.
     */
    public Element export(T value);

    /**
     * <p>Convert XML representation provided as an instance of org.jdom2.Element into object.</p>
     *
     * @param element XML representation of an object.
     * @return The extracted instance of T.
     */
    public T extract(Element element);
}
