package com.akavrt.csp.solver.pattern;

import com.akavrt.csp.core.Problem;

/**
 * <p>Pattern generation is widely used in sequential heuristic procedures and a number of other
 * approximate methods designed to solve complex real-world CSPs. Here we define minimalistic
 * interface for both deterministic and non-deterministic randomized procedures used for pattern
 * generation purposes by approximate methods.</p>
 *
 * <p>It's supposed that an instance of PatternGenerator can be reused, thus in concrete
 * implementations we should provide a handy way to initialize pattern generator with fixed list of
 * orders and fixed set of constraints (like a maximum number of cuts allowed within one pattern)
 * during instantiation or through setters.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface PatternGenerator {
    /**
     * <p>Generate a valid pattern conforming to the provided set of constraints.</p>
     *
     * <p>List of orders (the width of each order is used in generation process) and a selection of
     * constraints imposed on patterns are prefixed (any reasonable way to do this may be used,
     * either constructor or setter-based) and thus there is no need to provide them repeatedly
     * through parameters.</p>
     *
     * <p>In some cases patterns with trim loss less or equals to some predefined level can be
     * accepted. This can speed up generation process if multistart procedure is used: it will be
     * terminated shortly after the pattern with desired characteristics was found.</p>
     *
     * @param rollWidth        The width of the roll. Total width of the generated pattern have to
     *                         be less or equal to the width of the roll.
     * @param demand           Upper bound for a number of strips which can be placed within
     *                         pattern for each order.
     * @param allowedTrimRatio Defines acceptable level of trim loss as a fractional ratio, value
     *                         can vary from 0 to 1 (zero means that trim loss is not allowed).
     * @return Generated pattern defined by array of integer multipliers. The value of each
     *         multiplier equals to the number of times corresponding order must be cut from the
     *         roll.
     */
    int[] generate(double rollWidth, int[] demand, double allowedTrimRatio);

    /**
     * <p>Pattern generator can (and should) be configured with suitable to the context set of
     * parameters.</p>
     *
     * @return Current set of parameters used by pattern generator.
     */
    PatternGeneratorParameters getParameters();

    /**
     * <p>Associate pattern generator with specific problem.</p>
     *
     * <p>Information about orders characteristics and constraints defined within problem will be
     * used in pattern generation process.</p>
     *
     * <p>Initialization of the pattern generator can be done either during instantiation or using
     * this method. Uninitialized pattern generator (with no problem linked to it) can't be used to
     * produce valid patterns.</p>
     *
     * @param problem The problem which is being solved.
     */
    public void initialize(Problem problem);

}
