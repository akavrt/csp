package com.akavrt.csp.xml;

import com.akavrt.csp.core.Order;
import org.jdom2.Element;

import java.text.DecimalFormat;

/**
 * User: akavrt
 * Date: 02.03.13
 * Time: 22:57
 */
public class OrderConverter implements XmlConverter<Order> {

    @Override
    public Element convert(Order order) {
        Element orderElm = new Element(OrderTags.ORDER);
        orderElm.setAttribute(OrderTags.ID, order.getId());

        Element stripElm = new Element(OrderTags.STRIP);
        orderElm.addContent(stripElm);

        DecimalFormat format = new DecimalFormat("#.##");

        // length of the order
        Element lengthElm = new Element(OrderTags.LENGTH);
        lengthElm.setText(format.format(order.getLength()));
        stripElm.addContent(lengthElm);

        // width of the order
        Element widthElm = new Element(OrderTags.WIDTH);
        widthElm.setText(format.format(order.getWidth()));
        stripElm.addContent(widthElm);

        return orderElm;
    }

    public interface OrderTags {
        String ORDER = "order";
        String ID = "id";
        String STRIP = "strip";
        String LENGTH = "length";
        String WIDTH = "width";
    }
}
