package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.*;
import com.akavrt.csp.xml.XmlConverter;
import com.google.common.collect.Lists;
import org.jdom2.Element;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>Converter class used to transform an instance of Pattern to its XML representation and vice
 * versa.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class PatternConverter implements XmlConverter<Pattern> {
    private final List<Order> orders;
    private final List<Roll> rolls;
    private final MultiCutConverter cutConverter;

    /**
     * <p>Create a reusable instance of PatternConverter tied with problem definition. Valid
     * conversion can be done only for patterns which resides within single solution applied to the
     * problem specified during creation of the converter.</p>
     *
     * <p>The latter restriction is needed to properly recreate references to orders and rolls
     * while extracting pattern from XML.</p>
     */
    public PatternConverter(Problem problem) {
        orders = problem.getOrders();

        rolls = Lists.newArrayList();
        rolls.addAll(problem.getRolls());

        cutConverter = new MultiCutConverter(problem.getOrders());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element export(Pattern pattern) {
        Element patternElm = new Element(XmlTags.PATTERN);

        // converting attached roll to a reference
        if (pattern.getRoll() != null) {
            Element rollElm = prepareRollReference(pattern.getRoll());
            patternElm.addContent(rollElm);
        }

        // converting cuts
        Element cutsElm = prepareCuts(pattern);
        patternElm.addContent(cutsElm);

        return patternElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pattern extract(Element rootElm) {
        // extracting cuts
        Element cutsElm = rootElm.getChild(XmlTags.CUTS);
        List<MultiCut> cuts = Lists.newArrayList();
        if (cutsElm != null) {
            for (Element cutElm : cutsElm.getChildren(XmlTags.CUT)) {
                MultiCut cut = cutConverter.extract(cutElm);
                if (cut != null) {
                    cuts.add(cut);
                }
            }
        }

        // extracting reference and trying to find referenced roll
        Roll roll = null;
        Element rollElm = rootElm.getChild(XmlTags.ROLL);
        if (rollElm != null) {
            roll = retrieveRoll(rollElm);
        }

        Pattern pattern = new Pattern(orders);
        pattern.setCuts(cuts);
        pattern.setRoll(roll);

        return pattern;
    }

    private Element prepareRollReference(Roll roll) {
        /*
        Element rollElm = new Element(XmlTags.ROLL);

        Element refElm = new Element(XmlTags.REF);
        refElm.setAttribute(XmlTags.ID, roll.getId());
        rollElm.addContent(refElm);

        return rollElm;
        */

        Element rollElm = new Element(XmlTags.ROLL);
        rollElm.setAttribute(XmlTags.REF, roll.getId());

        return rollElm;
    }

    private Element prepareCuts(Pattern pattern) {
        Element cutsElm = new Element(XmlTags.CUTS);
        List<MultiCut> cuts = pattern.getCuts();

        // sort cuts using user-defined order id's
        // same order is used as in <problem /> section, see ProblemConverter
        Collections.sort(cuts, new Comparator<MultiCut>() {
            @Override
            public int compare(MultiCut lhs, MultiCut rhs) {
                return lhs.getOrder().getId().compareTo(rhs.getOrder().getId());
            }
        });

        // converting cuts
        for (MultiCut cut : cuts) {
            Element cutElm = cutConverter.export(cut);
            cutsElm.addContent(cutElm);
        }

        return cutsElm;
    }

    private Roll retrieveRoll(Element rollElm) {
        /*
        Roll roll = null;

        Element refElm = rollElm.getChild(XmlTags.REF);
        if (refElm != null && refElm.getAttributeValue(XmlTags.ID) != null) {
            String rollId = refElm.getAttributeValue(XmlTags.ID);
            for (int i = 0; i < rolls.size(); i++) {
                Roll rollToTest = rolls.get(i);
                if (rollToTest.getId().equals(rollId)) {
                    // referenced roll found
                    roll = rollToTest;
                    rolls.remove(i);
                    break;
                }
            }
        }

        return roll;
        */

        Roll roll = null;
        String rollId = rollElm.getAttributeValue(XmlTags.REF);
        if (rollId != null) {
            for (int i = 0; i < rolls.size(); i++) {
                Roll rollToTest = rolls.get(i);
                if (rollToTest.getId().equals(rollId)) {
                    // referenced roll found
                    roll = rollToTest;
                    rolls.remove(i);
                    break;
                }
            }
        }

        return roll;
    }

    private interface XmlTags {
        String PATTERN = "pattern";
        String ROLL = "roll";
        String REF = "ref";
        String ID = "id";
        String CUTS = "cuts";
        String CUT = "cut";
    }
}
