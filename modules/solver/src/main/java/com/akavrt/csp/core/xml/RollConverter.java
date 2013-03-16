package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.core.metadata.RollMetadata;
import org.jdom2.Element;

/**
 * <p>Converter class used to transform an instance of Roll to its XML representation and vice
 * versa.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class RollConverter extends StripConverter<Roll> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRootTag() {
        return XmlTags.ROLL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Roll createStrip(String id, double length, double width) {
        return new Roll(id, length, width);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element prepareMetadata(Roll roll) {
        return roll.getMetadata() == null ? null : roll.getMetadata().save();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Roll extract(Element rootElm) {
        Roll roll = super.extract(rootElm);

        // handle metadata
        Element metadataElm = rootElm.getChild(XmlTags.METADATA);
        if (metadataElm != null) {
            RollMetadata metadata = new RollMetadata();
            metadata.load(metadataElm);
            roll.setMetadata(metadata);
        }

        return roll;
    }

    private interface XmlTags {
        String ROLL = "roll";
        String METADATA = "metadata";
    }
}

