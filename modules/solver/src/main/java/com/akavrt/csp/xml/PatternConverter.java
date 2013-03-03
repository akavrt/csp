package com.akavrt.csp.xml;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Pattern;
import com.google.common.collect.Lists;
import org.jdom2.Element;

import javax.swing.text.DefaultEditorKit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: akavrt
 * Date: 03.03.13
 * Time: 00:46
 */
public class PatternConverter implements XmlConverter<Pattern> {
    private final List<Order> orders;

    public PatternConverter(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public Element convert(Pattern pattern) {
        Element patternElm = new Element(PatternTags.PATTERN);

        if (pattern.getRoll() != null) {
            Element rollElm = new Element(PatternTags.ROLL);
            patternElm.addContent(rollElm);

            Element refElm = new Element(PatternTags.REF);
            refElm.setAttribute(PatternTags.ID, pattern.getRoll().getId());
            rollElm.addContent(refElm);
        }

        Element cutsElm = new Element(PatternTags.CUTS);
        patternElm.addContent(cutsElm);
        int[] multipliers = pattern.getMultipliers();
        List<Cut> cuts = Lists.newArrayList();
        for (int i = 0; i < multipliers.length; i++) {
            Cut cut = new Cut(multipliers[i], orders.get(i).getId());
            cuts.add(cut);
        }

        // sort cuts using user-defined order id's
        // same order is used as in <problem /> section, see ProblemConverter
        Collections.sort(cuts, new Comparator<Cut>() {
            @Override
            public int compare(Cut lhs, Cut rhs) {
                return lhs.getOrderId().compareTo(rhs.getOrderId());
            }
        });

        for (Cut cut : cuts) {
            Element cutElm = prepareCut(cut);
            cutsElm.addContent(cutElm);
        }

        return patternElm;
    }

    private Element prepareCut(Cut cut) {
        Element cutElm = new Element(PatternTags.CUT);
        cutElm.setAttribute(PatternTags.QUANTITY, Integer.toString(cut.getQuantity()));

        Element orderElm = new Element(PatternTags.ORDER);
        cutElm.addContent(orderElm);

        Element refElm = new Element(PatternTags.REF);
        refElm.setAttribute(PatternTags.ID, cut.getOrderId());
        orderElm.addContent(refElm);

        return cutElm;
    }

    public interface PatternTags {
        String PATTERN = "pattern";
        String ROLL = "roll";
        String REF = "ref";
        String ID = "id";
        String CUTS = "cuts";
        String CUT = "cut";
        String QUANTITY = "quantity";
        String ORDER = "order";
    }

    private static class Cut {
        private final int quantity;
        private final String orderId;

        public Cut(int quantity, String orderId) {
            this.quantity = quantity;
            this.orderId = orderId;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getOrderId() {
            return orderId;
        }

    }
}
