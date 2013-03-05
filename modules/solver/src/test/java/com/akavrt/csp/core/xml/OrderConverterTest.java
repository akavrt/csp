package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.xml.OrderConverter;
import org.jdom2.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 18:38
 */
public class OrderConverterTest {
    private static final double DELTA = 1e-15;

    @Test
    public void conversion() {
        String id = "order";
        double length = 500.28;
        double width = 300.355;

        Order order = new Order(id, length, width);

        OrderConverter converter = new OrderConverter();
        Element orderElm = converter.export(order);
        // dot is forced to be used as a decimal separator
        assertEquals("500.28", orderElm.getChild("strip").getChildText("length"));

        Order extracted = converter.extract(orderElm);

        assertEquals(id, extracted.getId());
        assertEquals(length, extracted.getLength(), DELTA);
        // only two fractional digits is saved in XML conversion
        assertEquals(300.36, extracted.getWidth(), DELTA);
    }
}
