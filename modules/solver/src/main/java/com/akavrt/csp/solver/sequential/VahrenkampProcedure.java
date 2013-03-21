package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.xml.XmlCompatible;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * <p>Reference implementation of Vahrenkamp's modification of the sequential heuristic procedure.
 * For detailed explanation of the method check out this paper:
 *
 * Vahrenkamp, R. Random search in the one-dimensional cutting stock problem [Text] / R. Vahrenkamp
 * // European Journal of Operational Research. — 1996. — Vol. 95, N 1. — P. 191—200.
 * <a href="http://dx.doi.org/10.1016/0377-2217(95)00198-0">link</a>.
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class VahrenkampProcedure extends SequentialProcedure {
    public static final String METHOD_NAME = "Vahrenkamp's sequential heuristic procedure";
    private static final String SHORT_METHOD_NAME = "Vahrenkamp's SHP";
    private static final Logger LOGGER = LogManager.getFormatterLogger(VahrenkampProcedure.class);
    private final VahrenkampProcedureParameters params;
    private final Random rGen;

    /**
     * <p>Create instance of Algorithm implementing modification of the sequential heuristic
     * procedure proposed by Vahrenkamp. Default set of parameters will be used.</p>
     *
     * @param generator Pattern generator.
     */
    public VahrenkampProcedure(PatternGenerator generator) {
        this(generator, new VahrenkampProcedureParameters());
    }

    /**
     * <p>Create instance of Algorithm implementing modification of the sequential heuristic
     * procedure proposed by Vahrenkamp. Algorithm is configured with a set of parameters
     * provided.</p>
     *
     * @param generator Pattern generator.
     * @param params    Parameters of sequential heuristic procedure.
     */
    public VahrenkampProcedure(PatternGenerator generator, VahrenkampProcedureParameters params) {
        super(generator);
        this.params = params;

        rGen = new Random();
    }

    @Override
    protected String getShortMethodName() {
        return SHORT_METHOD_NAME;
    }

    @Override
    protected XmlCompatible getMethodParams() {
        return params;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return METHOD_NAME;
    }

    @Override
    protected Solution search() {
        Solution solution = new Solution();

        double allowedTrimRatio = 0;
        int patternUsage = evaluatePatternUsage(allowedTrimRatio);

        while (!orderManager.isOrdersFulfilled() && rollManager.size() > 0) {
            LOGGER.debug("#TRIM_AL: %.2f  #PU_AL: %d", allowedTrimRatio, patternUsage);

            // search for the next suitable pattern
            // to be added to the partial solution
            BuildingBlock block = rollGroupStep(allowedTrimRatio, patternUsage);

            if (block != null) {
                // adjust production and stock
                orderManager.updateProduction(block.group, block.pattern);
                rollManager.updateStock(block.group);

                // add pattern to the partial solution
                for (Roll roll : block.group) {
                    Pattern pattern = orderManager.prepareSolutionPattern(block.pattern);
                    pattern.setRoll(roll);

                    solution.addPattern(pattern);
                }

                // reset aspiration levels
                allowedTrimRatio = 0;
                patternUsage = evaluatePatternUsage(allowedTrimRatio);
                LOGGER.debug("  RESET  #TRIM_AL: %.2f  #PU_AL: %d", allowedTrimRatio, patternUsage);
            } else {
                // relax one of the aspiration levels
                // which level to relax is decided based on a draw
                double draw = rGen.nextDouble();
                if (draw > params.getGoalmix()) {
                    // relax pattern usage
                    if (patternUsage > 1) {
                        patternUsage -= params.getPatternUsageRelaxDelta();
                        LOGGER.debug("  RELAX  #PU_AL to %d", patternUsage);
                    }
                } else {
                    // relax trim
                    if (allowedTrimRatio < 1) {
                        allowedTrimRatio += params.getTrimRatioRelaxDelta();
                        LOGGER.debug("  RELAX  #TRIM_AL to %.2f", allowedTrimRatio);
                    }
                }
            }
        }

        return solution;
    }

}
