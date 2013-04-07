package com.akavrt.csp.utils;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Roll;

/**
 * <p>Utility class used to format console output.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ProblemFormatter {
    private static final String FIRST_LEVEL_INDENT = "  ";
    private static final String SECOND_LEVEL_INDENT = "    ";

    public static String format(Problem problem) {
        return format(problem, true, true);
    }

    public static String format(Problem problem, boolean printOrders, boolean printRolls) {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("PROBLEM: %d orders; %d rolls.",
                                     problem.getOrders().size(),
                                     problem.getRolls().size()));
        if (printOrders) {
            builder.append("\n");
            builder.append(FIRST_LEVEL_INDENT);
            builder.append("ORDERS:");

            int digits = (int) Math.floor(Math.log10(problem.getOrders().size())) + 1;
            String format = "\n" + SECOND_LEVEL_INDENT + "#%" + digits + "d  %s";
            int i = 0;
            for (Order order : problem.getOrders()) {
                builder.append(String.format(format, ++i, order));
            }
        }

        if (printRolls) {
            builder.append("\n");
            builder.append(FIRST_LEVEL_INDENT);
            builder.append("ROLLS:");

            int digits = (int) Math.floor(Math.log10(problem.getRolls().size())) + 1;
            String format = "\n" + SECOND_LEVEL_INDENT + "#%" + digits + "d  %s";
            int i = 0;
            for (Roll roll : problem.getRolls()) {
                builder.append(String.format(format, ++i, roll));
            }
        }

        return builder.toString();
    }

}
