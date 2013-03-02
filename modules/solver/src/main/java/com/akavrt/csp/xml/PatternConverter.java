package com.akavrt.csp.xml;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Pattern;
import org.jdom2.Element;

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
        for (int i = 0; i < multipliers.length; i++) {
            Element cutElm = prepareCut(i, multipliers[i]);
            cutsElm.addContent(cutElm);
        }

        return patternElm;
    }

    private Element prepareCut(int index, int quantity) {
        Element cutElm = new Element(PatternTags.CUT);
        cutElm.setAttribute(PatternTags.QUANTITY, Integer.toString(quantity));

        Element orderElm = new Element(PatternTags.ORDER);
        cutElm.addContent(orderElm);

        Element refElm = new Element(PatternTags.REF);
        refElm.setAttribute(PatternTags.ID, orders.get(index).getId());
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
}
