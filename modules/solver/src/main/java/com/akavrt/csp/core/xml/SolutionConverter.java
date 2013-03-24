package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.*;
import com.akavrt.csp.core.metadata.SolutionMetadata;
import com.akavrt.csp.xml.XmlConverter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jdom2.Element;

import java.util.*;

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

            // group patterns
            List<PatternGroup> groups = preparePatternGroups(patterns);
            PatternGroupConverter converter = new PatternGroupConverter(problem);
            int i = 0;
            for (PatternGroup group : groups) {
                String patternId = String.format(PATTERN_ID_TEMPLATE, ++i);

                Element patternElm = converter.export(group);
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

        // process patterns
        Element patternsElm = rootElm.getChild(XmlTags.PATTERNS);
        if (patternsElm != null) {
            // instance of PatternGroupConverter can't be reused to process different solutions
            // stock handling (extraction of attached rolls) must be done separately for each solution
            PatternGroupConverter converter = new PatternGroupConverter(problem);

            // extracting pattern groups one by one
            for (Element patternElm : patternsElm.getChildren(XmlTags.PATTERN)) {
                PatternGroup group = converter.extract(patternElm);

                for (Roll roll : group.getRolls()) {
                    Pattern pattern = new Pattern(problem.getOrders());
                    pattern.setCuts(group.getCuts());
                    pattern.setRoll(roll);

                    solution.addPattern(pattern);
                }
            }
        }

        return solution;
    }

    private List<PatternGroup> preparePatternGroups(List<Pattern> patterns) {
        // group patterns
        Map<Integer, PatternGroup> groups = Maps.newHashMap();
        for (Pattern pattern : patterns) {
            int hash = pattern.getCutsHashCode();
            PatternGroup group = groups.get(hash);
            if (group == null) {
                // first occurrence
                group = new PatternGroup(pattern.getCuts());
                group.addRoll(pattern.getRoll());

                groups.put(hash, group);
            } else {
                // no need to create new group
                group.addRoll(pattern.getRoll());
            }
        }

        List<PatternGroup> patternGroups = Lists.newArrayList(groups.values());

        // sort groups in descending order of pattern usage
        Collections.sort(patternGroups, new Comparator<PatternGroup>() {
            @Override
            public int compare(PatternGroup lhs, PatternGroup rhs) {
                return lhs.getRolls().size() > rhs.getRolls().size() ? -1 :
                        (lhs.getRolls().size() < rhs.getRolls().size() ? 1 : 0);
            }
        });

        return patternGroups;
    }

    private interface XmlTags {
        String SOLUTION = "solution";
        String METADATA = "metadata";
        String PATTERNS = "patterns";
        String PATTERN = "pattern";
        String ID = "id";
    }
}
