package com.akavrt.csp.core.metadata;

import com.akavrt.csp.xml.XmlCompatible;
import org.jdom2.Element;

/**
 * <p>This class can hold any meaningful data about the roll. It is responsible for saving and
 * loading that data to and from XML.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class RollMetadata implements XmlCompatible {

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(Element element) {

    }
}
