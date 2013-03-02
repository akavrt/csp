package com.akavrt.csp.xml;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Solution;
import org.jdom2.Element;

import java.util.List;

/**
 * User: akavrt
 * Date: 03.03.13
 * Time: 01:22
 */
public class SolutionConverter implements XmlConverter<Solution> {
    private final static String PATTERN_ID_TEMPLATE = "pattern%d";

    private final PatternConverter patternConverter;

    public SolutionConverter(List<Order> orders) {
        patternConverter = new PatternConverter(orders);
    }

    @Override
    public Element convert(Solution solution) {
        Element solutionElm = new Element(SolutionTags.SOLUTION);

        List<Pattern> patterns = solution.getPatterns();
        if (patterns != null && patterns.size() > 0) {
            Element patternsElm = new Element(SolutionTags.PATTERNS);
            solutionElm.addContent(patternsElm);

            for (int i = 0; i < patterns.size(); i++) {
                String patternId = String.format(PATTERN_ID_TEMPLATE, i + 1);

                Element patternElm = patternConverter.convert(patterns.get(i));
                patternElm.setAttribute(SolutionTags.ID, patternId);
                patternsElm.addContent(patternElm);
            }
        }

        return solutionElm;
    }

    public interface SolutionTags {
        String SOLUTION = "solution";
        String PATTERNS = "patterns";
        String ID = "id";
    }
}
