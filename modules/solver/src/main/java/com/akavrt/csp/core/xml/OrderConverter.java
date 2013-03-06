package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.metadata.OrderMetadata;
import org.jdom2.Element;

/**
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class OrderConverter extends StripConverter<Order> {

    @Override
    public String getRootTag() {
        return XmlTags.ORDER;
    }

    @Override
    public Order createStrip(String id, double length, double width) {
        return new Order(id, length, width);
    }

    @Override
    public Element prepareMetadata(Order order) {
        return order.getMetadata() == null ? null : order.getMetadata().save();
    }

    @Override
    public Order extract(Element rootElm) {
        Order order = super.extract(rootElm);

        // handle metadata
        Element metadataElm = rootElm.getChild(XmlTags.METADATA);
        if (metadataElm != null) {
            OrderMetadata metadata = new OrderMetadata();
            metadata.load(metadataElm);
            order.setMetadata(metadata);
        }

        return order;
    }

    private interface XmlTags {
        String ORDER = "order";
        String METADATA = "metadata";
    }
}
