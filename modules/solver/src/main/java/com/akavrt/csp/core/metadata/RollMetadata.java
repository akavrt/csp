package com.akavrt.csp.core.metadata;

import com.akavrt.csp.xml.MetadataConverter;
import org.jdom2.Element;

/**
 * <p>This class can hold any meaningful data about the roll. It is responsible for saving and
 * loading that date to and from XML.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class RollMetadata implements MetadataConverter {

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
