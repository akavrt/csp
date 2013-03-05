package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.MultiCut;
import com.akavrt.csp.core.Order;
import com.akavrt.csp.xml.XmlConverter;
import com.google.common.collect.Maps;
import org.jdom2.Element;

import java.util.List;
import java.util.Map;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 16:11
 */
public class MultiCutConverter implements XmlConverter<MultiCut> {
    private final Map<String, Order> mappedOrders;

    public MultiCutConverter(List<Order> orders) {
        mappedOrders = Maps.newHashMap();
        for (Order order : orders) {
            mappedOrders.put(order.getId(), order);
        }
    }

    @Override
    public Element export(MultiCut cut) {
        Element cutElm = new Element(XmlCutTags.CUT);
        cutElm.setAttribute(XmlCutTags.QUANTITY, Integer.toString(cut.getQuantity()));

        Element orderElm = new Element(XmlCutTags.ORDER);
        cutElm.addContent(orderElm);

        Element refElm = new Element(XmlCutTags.REF);
        refElm.setAttribute(XmlCutTags.ID, cut.getOrder().getId());
        orderElm.addContent(refElm);

        return cutElm;
    }

    @Override
    public MultiCut extract(Element rootElm) {
        int quantity = Utils.getIntegerFromAttribute(rootElm, XmlCutTags.QUANTITY, 0);

        // extracting order reference and trying to find referenced order
        Order order = null;
        Element orderElm = rootElm.getChild(XmlCutTags.ORDER);
        if (orderElm != null && orderElm.getChild(XmlCutTags.REF) != null) {
            Element refElm = orderElm.getChild(XmlCutTags.REF);
            String orderId = refElm.getAttributeValue(XmlCutTags.ID);

            if (orderId != null) {
                order = mappedOrders.get(orderId);
            }
        }

        MultiCut cut = null;
        if (order != null) {
            cut = new MultiCut(order, quantity);
        }

        return cut;
    }

    private interface XmlCutTags {
        String REF = "ref";
        String ID = "id";
        String CUT = "cut";
        String QUANTITY = "quantity";
        String ORDER = "order";
    }

}
