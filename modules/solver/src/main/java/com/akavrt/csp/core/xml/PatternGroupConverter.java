package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.MultiCut;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.utils.Utils;
import com.akavrt.csp.xml.XmlConverter;
import com.akavrt.csp.xml.XmlUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jdom2.Element;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * <p>Patterns representing exactly the same cuts are grouped together in order to make XML
 * notation less clunky. Given two or more identical patterns applied to different rolls we write
 * each unique pattern only once along with the list of rolls being cutted using this specific
 * pattern:</p>
 *
 * <pre>
 * {@code
 * <patterns>
 *     <pattern id="pattern1">
 *         <rolls>
 *             <roll ref="roll1" quantity="1" />
 *             <roll ref="roll2" quantity="2" />
 *         </rolls>
 *
 *         <cuts>
 *             <cut ref="order2" quantity="1" />
 *             <cut ref="order3" quantity="1" />
 *         </cuts>
 *     </pattern>
 *
 *     <pattern id="pattern2">
 *         <rolls>
 *             <roll ref="roll3" quantity="3" />
 *         </rolls>
 *
 *         <cuts>
 *             <cut ref="order1" quantity="1" />
 *             <cut ref="order2" quantity="2" />
 *             <cut ref="order3" quantity="2" />
 *         </cuts>
 *     </pattern>
 *     ...
 * </patterns>
 * }
 * </pre>
 *
 * <p>In the example shown above 'pattern1' is applied 3 times: once with 'roll1' and twice with
 * 'roll2'; while 'pattern2' is applied 3 times only with 'roll3'.</p>
 *
 * <p>This implementation of Converter used to transform a fully prepared instance of PatternGroup
 * to its XML representation and vice versa.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class PatternGroupConverter implements XmlConverter<PatternGroup> {
    private final List<Roll> rolls;
    private final MultiCutConverter cutConverter;

    /**
     * <p>Create an instance of PatternGroupConverter tied with problem definition. Specific
     * instance can't be reused to extract different solutions from XML: for each solution
     * to be extracted from XML you have to create and use new instance of
     * PatternGroupConverter.</p>
     *
     * <p>The latter restriction is imposed to properly recreate references to orders and rolls
     * while extracting patterns from XML.</p>
     */
    public PatternGroupConverter(Problem problem) {
        rolls = Lists.newArrayList();
        rolls.addAll(problem.getRolls());

        cutConverter = new MultiCutConverter(problem.getOrders());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element export(PatternGroup patternGroup) {
        Element patternElm = new Element(XmlTags.PATTERN);

        // group and convert attached rolls
        if (patternGroup.getRolls() != null) {
            Element rollsElm = prepareRolls(patternGroup.getRolls());
            patternElm.addContent(rollsElm);
        }

        // convert cuts
        Element cutsElm = prepareCuts(patternGroup.getCuts());
        patternElm.addContent(cutsElm);

        return patternElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PatternGroup extract(Element rootElm) {
        // extract cuts
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

        PatternGroup patternGroup = new PatternGroup(cuts);

        // extract references and try to find referenced rolls
        Element rollsElm = rootElm.getChild(XmlTags.ROLLS);
        if (rollsElm != null) {
            for (Element rollElm : rollsElm.getChildren(XmlTags.ROLL)) {
                // unique id used to identify group of rolls
                String rollId = rollElm.getAttributeValue(XmlTags.REF);

                // number of rolls of the same size to be cut using current pattern
                int quantity = XmlUtils.getIntegerFromAttribute(rollElm, XmlTags.QUANTITY, 0);

                if (!Utils.isEmpty(rollId) && quantity > 0) {
                    for (int i = 0; i < quantity; i++) {
                        Roll roll = findRoll(rollId);
                        if (roll != null) {
                            patternGroup.addRoll(roll);
                        }
                    }
                }
            }

        }

        return patternGroup;
    }

    private Element prepareRolls(List<Roll> rolls) {
        // group rolls
        Map<String, RollGroup> rollGroups = Maps.newHashMap();
        for (Roll roll : rolls) {
            String id = roll.getId();
            RollGroup rollGroup = rollGroups.get(id);
            if (rollGroup == null) {
                // first occurrence
                rollGroup = new RollGroup(roll);
                rollGroups.put(id, rollGroup);
            } else {
                // no need to create new group
                rollGroup.incQuantity();
            }
        }

        // sort rolls using user-defined id's
        List<RollGroup> sorted = Lists.newArrayList(rollGroups.values());
        Collections.sort(sorted, new Comparator<RollGroup>() {
            @Override
            public int compare(RollGroup lhs, RollGroup rhs) {
                return lhs.getRoll().getId().compareTo(rhs.getRoll().getId());
            }
        });

        Element rollsElm = new Element(XmlTags.ROLLS);
        for (RollGroup rollGroup : sorted) {
            Element rollElm = new Element(XmlTags.ROLL);
            rollElm.setAttribute(XmlTags.QUANTITY, Integer.toString(rollGroup.getQuantity()));
            rollElm.setAttribute(XmlTags.REF, rollGroup.getRoll().getId());

            rollsElm.addContent(rollElm);
        }

        return rollsElm;
    }

    private Element prepareCuts(List<MultiCut> cuts) {
        Element cutsElm = new Element(XmlTags.CUTS);

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
            if (cut.getQuantity() > 0) {
                Element cutElm = cutConverter.export(cut);
                cutsElm.addContent(cutElm);
            }
        }

        return cutsElm;
    }

    private Roll findRoll(String rollId) {
        Roll roll = null;
        for (int i = 0; i < rolls.size(); i++) {
            Roll rollToTest = rolls.get(i);
            if (rollToTest.getId().equals(rollId)) {
                // referenced roll found
                roll = rollToTest;
                rolls.remove(i);
                break;
            }
        }

        return roll;
    }

    private interface XmlTags {
        String PATTERN = "pattern";
        String ROLLS = "rolls";
        String ROLL = "roll";
        String REF = "ref";
        String QUANTITY = "quantity";
        String CUTS = "cuts";
        String CUT = "cut";
    }

}
