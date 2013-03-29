package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.solver.pattern.PatternGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>Simplified version of Haessler's sequential heuristic procedure with pattern usage goal
 * removed from the search algorithm (only trim loss is considered).</p>
 *
 * <p>See HaesslerProcedure for more details.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class SimplifiedProcedure extends HaesslerProcedure {
    private static final String METHOD_NAME = "Simplified sequential heuristic procedure";
    private static final String SHORT_METHOD_NAME = "Simplified SHP";
    private static final Logger LOGGER = LogManager.getFormatterLogger(SimplifiedProcedure.class);

    /**
     * <p>Create instance of Algorithm implementing simplified version of Haessler's sequential
     * heuristic procedure. Default set of parameters will be used.</p>
     *
     * @param generator Pattern generator.
     */
    public SimplifiedProcedure(PatternGenerator generator) {
        super(generator, new SequentialProcedureParameters());
    }

    /**
     * <p>Create instance of Algorithm implementing simplified version of Haessler's sequential
     * heuristic procedure. Algorithm is configured with a set of parameters provided.</p>
     *
     * @param generator Pattern generator.
     * @param params    Parameters of sequential heuristic procedure.
     */
    public SimplifiedProcedure(PatternGenerator generator, SequentialProcedureParameters params) {
        super(generator, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getShortMethodName() {
        return SHORT_METHOD_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return METHOD_NAME;
    }

    @Override
    protected BuildingBlock trimStep() {
        BuildingBlock block = null;

        double allowedTrimRatio = getMethodParameters().getTrimRatioLowerBound();

        // in simplified version pattern usage is prefixed to 1
        int patternUsage = 1;

        while (!context.isCancelled() && allowedTrimRatio < 1 && block == null) {
            LOGGER.debug("#TRIM_AL: %.2f", allowedTrimRatio);

            block = rollGroupStep(allowedTrimRatio, patternUsage);

            // relax requirements for trim loss
            allowedTrimRatio += getMethodParameters().getTrimRatioRelaxStep();
        }

        return block;
    }

}
