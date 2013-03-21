package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.xml.XmlCompatible;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>Reference implementation of Haessler's sequential heuristic procedure. For detailed
 * explanation of the method check out these publications:
 *
 * <ol>
 * <li>
 * Haessler, R. W. Controlling cutting pattern changes in one-dimensional trim problems [Text] /
 * R. W. Haessler // Operations Research. — 1975. — Vol. 23, No. 3. — pp. 483—493.
 * <a href="http://dx.doi.org/10.1287/opre.23.3.483">link</a>;
 * </li>
 * <li>
 * Haessler, R. W. A procedure for solving the 1.5-dimensional coil slitting problem [Text] /
 * R. W. Haessler // AIIE Transactions. — 1978. — Vol. 10, No. 1. — pp. 70—75.
 * <a href="http://dx.doi.org/10.1080/05695557808975185">link</a>.
 * </li>
 * </ol>
 * </p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class HaesslerProcedure extends SequentialProcedure {
    public static final String METHOD_NAME = "Haessler's sequential heuristic procedure";
    private static final String SHORT_METHOD_NAME = "Haessler's SHP";
    private static final Logger LOGGER = LogManager.getFormatterLogger(HaesslerProcedure.class);
    private final HaesslerProcedureParameters params;

    /**
     * <p>Create instance of Algorithm implementing Haessler's sequential heuristic procedure.
     * Default set of parameters will be used.</p>
     *
     * @param generator Pattern generator.
     */
    public HaesslerProcedure(PatternGenerator generator) {
        this(generator, new HaesslerProcedureParameters());
    }

    /**
     * <p>Create instance of Algorithm implementing Haessler's sequential heuristic procedure.
     * Algorithm is configured with a set of parameters provided.</p>
     *
     * @param generator Pattern generator.
     * @param params    Parameters of sequential heuristic procedure.
     */
    public HaesslerProcedure(PatternGenerator generator, HaesslerProcedureParameters params) {
        super(generator);
        this.params = params;
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

        while (!orderManager.isOrdersFulfilled() && rollManager.size() > 0) {
            BuildingBlock block = trimStep();

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
            }
        }

        return solution;
    }

    private BuildingBlock trimStep() {
        BuildingBlock block = null;

        double allowedTrimRatio = 0;

        while (allowedTrimRatio < 1 && block == null) {
            LOGGER.debug("#TRIM_AL: %.2f", allowedTrimRatio);

            block = patternUsageStep(allowedTrimRatio);

            // relax requirements for trim loss
            allowedTrimRatio += params.getTrimRatioRelaxDelta();
        }

        return block;
    }

    private BuildingBlock patternUsageStep(double allowedTrimRatio) {
        BuildingBlock block = null;

        // let's evaluate maximum possible pattern usage
        int patternUsage = evaluatePatternUsage(allowedTrimRatio);

        while (patternUsage > 0 && block == null) {
            LOGGER.debug("#TRIM_AL: %.2f  ##PU_AL: %d", allowedTrimRatio, patternUsage);

            block = rollGroupStep(allowedTrimRatio, patternUsage);

            // relax requirements for pattern usage
            patternUsage -= params.getPatternUsageRelaxDelta();
        }

        return block;
    }

}