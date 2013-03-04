package com.akavrt.csp.xml;

import com.akavrt.csp.core.*;
import com.google.common.collect.Lists;
import org.jdom2.Element;

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
    private final List<Roll> rolls;
    private final MultiCutConverter cutConverter;

    public PatternConverter(Problem problem) {
        orders = problem.getOrders();

        rolls = Lists.newArrayList();
        rolls.addAll(problem.getRolls());

        cutConverter = new MultiCutConverter(problem.getOrders());
    }

    @Override
    public Element export(Pattern pattern) {
        Element patternElm = new Element(PatternTags.PATTERN);

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

    @Override
    public Pattern extract(Element rootElm) {
        // extracting cuts
        Element cutsElm = rootElm.getChild(PatternTags.CUTS);
        List<MultiCut> cuts = Lists.newArrayList();
        if (cutsElm != null) {
            for (Element cutElm : cutsElm.getChildren(PatternTags.CUT)) {
                MultiCut cut = cutConverter.extract(cutElm);
                if (cut != null) {
                    cuts.add(cut);
                }
            }
        }

        // extracting reference and trying to find referenced roll
        Roll roll = null;
        Element rollElm = rootElm.getChild(PatternTags.ROLL);
        if (rollElm != null) {
            roll = retrieveRoll(rollElm);
        }

        Pattern pattern = new Pattern(orders);
        pattern.setCuts(cuts);
        pattern.setRoll(roll);

        return pattern;
    }

    private Element prepareRollReference(Roll roll) {
        Element rollElm = new Element(PatternTags.ROLL);

        Element refElm = new Element(PatternTags.REF);
        refElm.setAttribute(PatternTags.ID, roll.getId());
        rollElm.addContent(refElm);

        return rollElm;
    }

    private Element prepareCuts(Pattern pattern) {
        Element cutsElm = new Element(PatternTags.CUTS);
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
        Roll roll = null;

        Element refElm = rollElm.getChild(PatternTags.REF);
        if (refElm != null && refElm.getAttributeValue(PatternTags.ID) != null) {
            String rollId = refElm.getAttributeValue(PatternTags.ID);
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

    public interface PatternTags {
        String PATTERN = "pattern";
        String ROLL = "roll";
        String REF = "ref";
        String ID = "id";
        String CUTS = "cuts";
        String CUT = "cut";
    }
}
