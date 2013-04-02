package com.akavrt.csp.tester.tracer;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.core.Solution;

/**
 * User: akavrt
 * Date: 22.03.13
 * Time: 14:46
 */
public class TraceUtils {
    public static String traceProblem(Problem problem, boolean printOrders, boolean printRolls) {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("PROBLEM: %d orders; %d rolls.",
                                     problem.getOrders().size(),
                                     problem.getRolls().size()));
        if (printOrders) {
            builder.append("\n  ORDERS:");

            int digits = (int) Math.floor(Math.log10(problem.getOrders().size())) + 1;
            String format = "\n    #%" + digits + "d  %s";
            int i = 0;
            for (Order order : problem.getOrders()) {
                builder.append(String.format(format, ++i, order));
            }
        }

        if (printRolls) {
            builder.append("\n  ROLLS:");

            int digits = (int) Math.floor(Math.log10(problem.getRolls().size())) + 1;
            String format = "\n    #%" + digits + "d  %s";
            int i = 0;
            for (Roll roll : problem.getRolls()) {
                builder.append(String.format(format, ++i, roll));
            }
        }

        return builder.toString();
    }

    /**
     * <p>Provide human-readable description of whether solution is valid or not. Information about
     * constraint violation is printed out for invalid solutions only.</p>
     *
     * @param solution Solution to investigate.
     * @param problem  The problem against which validity of the solution is being tested.
     * @return Textual trace of the solution validity.
     */
    public static String traceSolutionValidity(Solution solution, Problem problem) {
        StringBuilder builder = new StringBuilder();

        boolean isValid = solution.isValid(problem);
        if (isValid) {
            builder.append("  VALID.");
        } else {
            builder.append("  INVALID:");
            if (!solution.isPatternsValid(problem)) {
                builder.append("\n      infeasible patterns found;");
            }

            if (!solution.isRollsValid()) {
                builder.append("\n      repeated rolls found;");
            }

            if (!solution.isOrdersFulfilled(problem.getOrders())) {
                builder.append("\n      unfulfilled orders found;");
            }
        }

        return builder.toString();
    }

    public static String traceSolution(Solution solution, Problem problem, Tracer<Solution> tracer,
                                       boolean printPatterns) {
        StringBuilder builder = new StringBuilder();

        builder.append(tracer.trace(solution));
        builder.append("\n");
        builder.append(traceSolutionValidity(solution, problem));

        if (printPatterns) {
            builder.append("\nPATTERNS:\n");
            builder.append(solution);
        }

        return builder.toString();
    }

}
