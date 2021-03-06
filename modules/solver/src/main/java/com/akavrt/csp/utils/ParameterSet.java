package com.akavrt.csp.utils;

import com.akavrt.csp.xml.XmlCompatible;
import org.jdom2.Element;

/**
 * <p>All classes representing parameter sets should implement this interface and thus provide easy
 * means to save parameter values in external source (XML in this case).</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface ParameterSet extends XmlCompatible {
    /**
     * <p>Optional description of the parameter set.</p>
     *
     * @return Textual description of the parameter set.
     */
    String getDescription();

    /**
     * <p>Sometimes we need to describe what component of the algorithm is configured with specific
     * parameter set. For instance, this is the case when several instances of the same component
     * is used within optimization routine - additional description allows us to differentiate
     * corresponding parameter sets in XML.</p>
     *
     * @param description Textual description of the parameter set.
     */
    void setDescription(String description);

    /**
     * <p>Retrieve internal state from XML. Corresponding XML element containing exported state
     * must be an immediate child of the given element provided as a parameter.</p>
     *
     * <p>Name of the child element defined by the ParameterSet itself.</p>
     */
    void loadFromChildElement(Element element);

}
