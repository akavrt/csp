package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Roll;
import com.akavrt.csp.core.metadata.RollMetadata;
import org.jdom2.Element;

/**
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class RollConverter extends StripConverter<Roll> {

    @Override
    public String getRootTag() {
        return XmlTags.ROLL;
    }

    @Override
    public Roll createStrip(String id, double length, double width) {
        return new Roll(id, length, width);
    }

    @Override
    public Element prepareMetadata(Roll roll) {
        return roll.getMetadata() == null ? null : roll.getMetadata().save();
    }

    @Override
    public Roll extract(Element rootElm) {
        Roll roll  = super.extract(rootElm);

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

