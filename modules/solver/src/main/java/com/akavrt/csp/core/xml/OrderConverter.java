package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.metadata.OrderMetadata;
import org.jdom2.Element;

/**
 * <p>Converter class used to transform an instance of Order to its XML representation and vice
 * versa.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class OrderConverter extends StripConverter<Order> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRootTag() {
        return XmlTags.ORDER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order createStrip(String id, double length, double width) {
        return new Order(id, length, width);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element prepareMetadata(Order order) {
        return order.getMetadata() == null ? null : order.getMetadata().save();
    }

    /**
     * {@inheritDoc}
     */
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
