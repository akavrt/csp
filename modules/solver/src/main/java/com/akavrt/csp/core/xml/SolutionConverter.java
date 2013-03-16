package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.metadata.SolutionMetadata;
import com.akavrt.csp.xml.XmlConverter;
import com.google.common.collect.Lists;
import org.jdom2.Element;

import java.util.List;

/**
 * <p>Converter class used to transform an instance of Solution to its XML representation and vice
 * versa.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class SolutionConverter implements XmlConverter<Solution> {
    private final static String PATTERN_ID_TEMPLATE = "pattern%d";
    private final Problem problem;

    /**
     * <p>Create reusable instance of SolutionConverter. Reuse is limited to the set of solutions
     * applied to the problem specified during creation of the converter.</p>
     */
    public SolutionConverter(Problem problem) {
        this.problem = problem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element export(Solution solution) {
        Element solutionElm = new Element(XmlTags.SOLUTION);

        // process metadata
        if (solution.getMetadata() != null) {
            Element metadataElm = solution.getMetadata().save();
            if (metadataElm != null) {
                solutionElm.addContent(metadataElm);
            }
        }

        // process patterns
        List<Pattern> patterns = solution.getPatterns();
        if (patterns != null && patterns.size() > 0) {
            Element patternsElm = new Element(XmlTags.PATTERNS);
            solutionElm.addContent(patternsElm);

            // converting patterns
            PatternConverter patternConverter = new PatternConverter(problem);
            for (int i = 0; i < patterns.size(); i++) {
                String patternId = String.format(PATTERN_ID_TEMPLATE, i + 1);

                Element patternElm = patternConverter.export(patterns.get(i));
                patternElm.setAttribute(XmlTags.ID, patternId);
                patternsElm.addContent(patternElm);
            }
        }

        return solutionElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Solution extract(Element rootElm) {
        Solution solution = new Solution();

        // process metadata
        Element metadataElm = rootElm.getChild(XmlTags.METADATA);
        if (metadataElm != null) {
            SolutionMetadata metadata = new SolutionMetadata();
            metadata.load(metadataElm);
            solution.setMetadata(metadata);
        }

        // instance of PatternConverter can't be reused to process different solutions
        // stock handling (extraction of attached rolls) must be done separately for each solution
        PatternConverter patternConverter = new PatternConverter(problem);

        // process patterns
        Element patternsElm = rootElm.getChild(XmlTags.PATTERNS);
        if (patternsElm != null) {
            List<Pattern> patterns = Lists.newArrayList();

            // extracting patterns one by one
            for (Element patternElm : patternsElm.getChildren(XmlTags.PATTERN)) {
                Pattern pattern = patternConverter.extract(patternElm);
                patterns.add(pattern);
            }

            solution.setPatterns(patterns);
        }

        return solution;
    }

    private interface XmlTags {
        String SOLUTION = "solution";
        String METADATA = "metadata";
        String PATTERNS = "patterns";
        String PATTERN = "pattern";
        String ID = "id";
    }
}
