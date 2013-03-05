package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.MultiCut;
import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.xml.MultiCutConverter;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 21:30
 */
public class MultiCutConverterTest {
    private List<Order> orders;
    private MultiCutConverter converter;

    @Before
    public void setUpProblem() {
        // preparing orders
        orders = new ArrayList<Order>();
        orders.add(new Order("order1", 500, 50));
        orders.add(new Order("order2", 400, 40));
        orders.add(new Order("order3", 300, 30));
        orders.add(new Order("order4", 200, 20));

        converter = new MultiCutConverter(orders);
    }

    @Test
    public void conversion() {
        MultiCut cut = new MultiCut(orders.get(1), 5);
        Element cutElm = converter.export(cut);
        MultiCut extracted = converter.extract(cutElm);

        assertEquals(cut.getOrder().getId(), extracted.getOrder().getId());
        assertEquals(cut.getQuantity(), extracted.getQuantity());
    }

}
