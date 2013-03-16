package com.akavrt.csp.solver.pattern;

import com.akavrt.csp.core.xml.Utils;
import com.akavrt.csp.xml.XmlCompatible;
import org.jdom2.Element;

/**
 * <p>Parameters of the randomized multistart pattern generation procedure, see
 * ConstrainedPatternGenerator for more details.</p>
 *
 * <p>An instance of this class can be saved to XML and extracted from it using methods defined in
 * XmlCompatible interface.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class PatternGeneratorParameters implements XmlCompatible {
    private static final int DEFAULT_GENERATION_TRIALS_LIMIT = 100;
    private int generationTrialsLimit = DEFAULT_GENERATION_TRIALS_LIMIT;

    /**
     * <p>Maximum number of trials can be used by procedure while generating pattern.</p>
     */
    public int getGenerationTrialsLimit() {
        return generationTrialsLimit;
    }

    /**
     * <p>Set the maximum number of trials can be used by procedure while generating pattern.</p>
     *
     * @param generationTrialsLimit Maximum number of generation trials.
     */
    public void setGenerationTrialsLimit(int generationTrialsLimit) {
        this.generationTrialsLimit = generationTrialsLimit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element paramsElm = new Element(XmlTags.PARAMETERS);

        Element generationTrialsLimitElm = new Element(XmlTags.GENERATION_TRIALS_LIMIT);
        generationTrialsLimitElm.setText(Integer.toString(getGenerationTrialsLimit()));
        paramsElm.addContent(generationTrialsLimitElm);

        return paramsElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(Element rootElm) {
        Element limitElm = rootElm.getChild(XmlTags.GENERATION_TRIALS_LIMIT);
        if (limitElm != null) {
            int limit = Utils.getIntegerFromText(limitElm, DEFAULT_GENERATION_TRIALS_LIMIT);
            setGenerationTrialsLimit(limit);
        }
    }

    public interface XmlTags {
        String PARAMETERS = "pattern-generation";
        String GENERATION_TRIALS_LIMIT = "generation-trials-limit";
    }

}