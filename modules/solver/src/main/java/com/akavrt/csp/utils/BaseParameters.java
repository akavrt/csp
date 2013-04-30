package com.akavrt.csp.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

/**
 * <p>Utility class which should be used as base class for classes representing parameter sets with
 * description defined as a gene textual field.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public abstract class BaseParameters implements ParameterSet {
    private static final Logger LOGGER = LogManager.getLogger(BaseParameters.class);
    private String description;

    /**
     * <p>Returns name of the root element enclosing corresponding set of parameters.</p>
     *
     * <p>Used in XML import.</p>
     *
     * @return Name of the root element enclosing corresponding set of parameters.
     */
    protected abstract String getRootElementName();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromChildElement(Element element) {
        Element parameterSetElm = element.getChild(getRootElementName());
        if (parameterSetElm != null) {
            load(parameterSetElm);
        } else {
            LOGGER.warn("<{}> element wasn't found.", getRootElementName());
        }
    }
}
