package com.akavrt.csp.xml;

import com.akavrt.csp.core.MultiCut;
import com.akavrt.csp.core.Order;
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
        Element cutElm = new Element(MultiCutTags.CUT);
        cutElm.setAttribute(MultiCutTags.QUANTITY, Integer.toString(cut.getQuantity()));

        Element orderElm = new Element(MultiCutTags.ORDER);
        cutElm.addContent(orderElm);

        Element refElm = new Element(MultiCutTags.REF);
        refElm.setAttribute(MultiCutTags.ID, cut.getOrder().getId());
        orderElm.addContent(refElm);

        return cutElm;
    }

    @Override
    public MultiCut extract(Element rootElm) {
        int quantity = Utils.getIntegerFromAttribute(rootElm, MultiCutTags.QUANTITY, 0);

        // extracting order reference and trying to find referenced order
        Order order = null;
        Element orderElm = rootElm.getChild(MultiCutTags.ORDER);
        if (orderElm != null && orderElm.getChild(MultiCutTags.REF) != null) {
            Element refElm = orderElm.getChild(MultiCutTags.REF);
            String orderId = refElm.getAttributeValue(MultiCutTags.ID);

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

    public interface MultiCutTags {
        String REF = "ref";
        String ID = "id";
        String CUT = "cut";
        String QUANTITY = "quantity";
        String ORDER = "order";
    }

}
