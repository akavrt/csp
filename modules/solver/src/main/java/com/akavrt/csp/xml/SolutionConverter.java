package com.akavrt.csp.xml;

import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.google.common.collect.Lists;
import org.jdom2.Element;

import java.util.List;

/**
 * User: akavrt
 * Date: 03.03.13
 * Time: 01:22
 */
public class SolutionConverter implements XmlConverter<Solution> {
    private final static String PATTERN_ID_TEMPLATE = "pattern%d";
    private final Problem problem;

    public SolutionConverter(Problem problem) {
        this.problem = problem;
    }

    @Override
    public Element export(Solution solution) {
        Element solutionElm = new Element(SolutionTags.SOLUTION);

        List<Pattern> patterns = solution.getPatterns();
        if (patterns != null && patterns.size() > 0) {
            Element patternsElm = new Element(SolutionTags.PATTERNS);
            solutionElm.addContent(patternsElm);

            // converting patterns
            PatternConverter patternConverter = new PatternConverter(problem);
            for (int i = 0; i < patterns.size(); i++) {
                String patternId = String.format(PATTERN_ID_TEMPLATE, i + 1);

                Element patternElm = patternConverter.export(patterns.get(i));
                patternElm.setAttribute(SolutionTags.ID, patternId);
                patternsElm.addContent(patternElm);
            }
        }

        return solutionElm;
    }

    @Override
    public Solution extract(Element rootElm) {
        // instance of PatternConverter can't be reused to extract different solutions
        // stock handling (extraction of attached rolls) must be done separately for each solution
        PatternConverter patternConverter = new PatternConverter(problem);

        Element patternsElm = rootElm.getChild(SolutionTags.PATTERNS);
        List<Pattern> patterns = Lists.newArrayList();
        if (patternsElm != null) {
            // extracting patterns one by one
            for (Element patternElm : patternsElm.getChildren(SolutionTags.PATTERN)) {
                Pattern pattern = patternConverter.extract(patternElm);
                patterns.add(pattern);
            }
        }

        return new Solution(patterns);
    }

    public interface SolutionTags {
        String SOLUTION = "solution";
        String PATTERNS = "patterns";
        String PATTERN = "pattern";
        String ID = "id";
    }
}
