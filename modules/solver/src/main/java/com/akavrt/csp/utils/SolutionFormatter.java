package com.akavrt.csp.utils;

import com.akavrt.csp.core.MultiCut;
import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Solution;

/**
 * <p>Utility class used to format console output.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class SolutionFormatter {
    private static final String FIRST_LEVEL_INDENT = "  ";
    private static final String SECOND_LEVEL_INDENT = "    ";

    public static String format(Solution solution) {
        return format(solution, null, null, true);
    }

    public static String format(Solution solution, boolean printPatterns) {
        return format(solution, null, null, printPatterns);
    }

    public static String format(Solution solution, String caption, boolean printPatterns) {
        return format(solution, caption, null, printPatterns);
    }

    public static String format(Solution solution, String caption, String trace, boolean printPatterns) {
        StringBuilder builder = new StringBuilder();

        if (!Utils.isEmpty(caption)) {
            builder.append(caption);
            builder.append("\n");
        } else {
            builder.append("*** SOLUTION:");
            builder.append("\n");
        }

        if (!Utils.isEmpty(trace)) {
            builder.append(trace);
            builder.append("\n");
        }

        builder.append(formatFeasibility(solution));

        if (printPatterns) {
            int maxQuantity = 0;
            for (Pattern pattern : solution.getPatterns()) {
                for (MultiCut cut : pattern.getCuts()) {
                    if (maxQuantity == 0 || cut.getQuantity() > maxQuantity) {
                        maxQuantity = cut.getQuantity();
                    }
                }
            }

            int quantityDigits = (int) Math.floor(Math.log10(maxQuantity)) + 1;
            String patternQuantityFormat = " %" + quantityDigits + "d ";

            int patternDigits = (int) Math.floor(Math.log10(solution.getPatterns().size())) + 1;
            String indexFormat = SECOND_LEVEL_INDENT + "#%" + patternDigits + "d: ";

            builder.append("\n");
            builder.append(FIRST_LEVEL_INDENT);
            builder.append("PATTERNS:");

            builder.append("\n");
            builder.append(FIRST_LEVEL_INDENT);
            builder.append("{");
            int i = 0;
            for (Pattern pattern : solution.getPatterns()) {
                builder.append("\n");
                builder.append(FIRST_LEVEL_INDENT);

                String index = String.format(indexFormat, ++i);
                builder.append(index);

                builder.append(formatPattern(pattern, patternQuantityFormat));
            }
            builder.append("\n");
            builder.append(FIRST_LEVEL_INDENT);
            builder.append("}");
        }

        return builder.toString();
    }

    private static String formatPattern(Pattern pattern, String quantityFormat) {
        StringBuilder builder = new StringBuilder();

        builder.append("[");
        for (MultiCut cut : pattern.getCuts()) {
            builder.append(String.format(quantityFormat, cut.getQuantity()));
        }
        builder.append("]");

        if (pattern.getRoll() != null) {
            builder.append(" -> ").append(pattern.getRoll().getId());
        } else {
            builder.append(" -> unset");
        }

        return builder.toString();
    }

    private static String formatFeasibility(Solution solution) {
        StringBuilder builder = new StringBuilder();

        boolean isValid = solution.isFeasible();
        if (isValid) {
            builder.append(FIRST_LEVEL_INDENT);
            builder.append("FEASIBLE.");
        } else {
            builder.append(FIRST_LEVEL_INDENT);
            builder.append("INFEASIBLE:");
            if (!solution.isPatternsFeasible()) {
                builder.append("\n");
                builder.append(FIRST_LEVEL_INDENT);
                builder.append(SECOND_LEVEL_INDENT);
                builder.append("infeasible patterns found;");
            }

            if (!solution.isRollUsageFeasible()) {
                builder.append("\n");
                builder.append(FIRST_LEVEL_INDENT);
                builder.append(SECOND_LEVEL_INDENT);
                builder.append("repeated rolls found;");
            }

            if (!solution.isOrdersFulfilled()) {
                builder.append("\n");
                builder.append(FIRST_LEVEL_INDENT);
                builder.append(SECOND_LEVEL_INDENT);
                builder.append("unfulfilled orders found;");
            }
        }

        return builder.toString();
    }

}
